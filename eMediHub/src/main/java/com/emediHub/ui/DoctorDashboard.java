package com.emediHub.ui;

import com.emediHub.hospital.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class DoctorDashboard extends JFrame {

    private DoctorDao  doctorDAO  = new DoctorDao();
    private PatientDao patientDAO = new PatientDao();
    private String doctorName;
    private int doctorId;
    private DefaultTableModel patientModel;
    private JLabel statusBadge;

    public DoctorDashboard(String doctorName, int doctorId) {
        this.doctorName = doctorName;
        this.doctorId   = doctorId;

        setTitle("eMediHub — Doctor Panel | " + doctorName);
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        loadPatients();
        loadMyStatus();
    }

    // ─────────── HEADER ───────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(39, 174, 96));
        header.setBorder(BorderFactory.createEmptyBorder( 12, 20, 12, 20));

        // Left — welcome + status badge
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        JLabel welcome = new JLabel( "Welcome, " + doctorName);
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setForeground(Color.WHITE);

        statusBadge = new JLabel("● Available");
        statusBadge.setFont( new Font("Arial", Font.BOLD, 13));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setOpaque(true);
        statusBadge.setBackground(new Color(39, 174, 96));
        statusBadge.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
                ));

        leftPanel.add(welcome);
        leftPanel.add(statusBadge);
        header.add(leftPanel, BorderLayout.WEST);

        // Right — status change + logout buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton statusBtn = new JButton( "Update My Status");
        statusBtn.setBackground(new Color(52, 73, 94));
        statusBtn.setForeground(Color.WHITE);
        statusBtn.setFont( new Font("Arial", Font.BOLD, 13));
        statusBtn.setFocusPainted(false);
        statusBtn.setBorderPainted(false);
        statusBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        statusBtn.addActionListener(
            e -> updateMyStatus());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont( new Font("Arial", Font.BOLD, 13));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            // Set to off duty on logout
            doctorDAO.updateStatus(doctorId, "Off Duty");
            dispose();
            new LoginScreen().setVisible(true);
        });

        rightPanel.add(statusBtn);
        rightPanel.add(logoutBtn);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // ─────────── BODY ───────────
    private JPanel buildBody() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        tabs.addTab("Patient List",    buildPatientTab());
        tabs.addTab("Doctor Status",   buildStatusTab());
        tabs.addTab("My Profile",      buildProfileTab());
        tabs.addTab("Payments",        buildPaymentTab());
        tabs.addTab("Chatbot",         buildChatTab());
        tabs.addTab("Prescription",    buildPrescriptionTab());
        tabs.addTab("Medical History", buildHistoryTab());
        tabs.addTab("Appointments",    buildAppointmentsTab());
        
        JPanel body = new JPanel(new BorderLayout());
        body.add(tabs);
        return body;
    }

    // ─────────── PATIENT TAB ───────────
    private JPanel buildPatientTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder( 15, 15, 15, 15));

        // Search bar
        JPanel searchBar = new JPanel( new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setBorder( BorderFactory.createTitledBorder( "Search Patients"));

        JTextField searchField = new JTextField(20);
        JButton searchBtn  = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");

        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        showAllBtn.setBackground(new Color(149, 165, 166));
        showAllBtn.setForeground(Color.WHITE);
        showAllBtn.setFocusPainted(false);

        searchBar.add(new JLabel("Patient name:"));
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchBar.add(showAllBtn);

        // Table
        String[] cols = { "ID", "Name", "Age", "Gender", "Phone", "Disease"};
        patientModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(patientModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(39, 174, 96));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));

        // Info label
        JLabel infoLabel = new JLabel("  View only — doctors cannot "+ "edit patient records");
        infoLabel.setFont( new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground( new Color(150, 150, 150));

        // Actions
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            if (kw.isEmpty()) {
                loadPatients();
                return;
            }
            patientModel.setRowCount(0);
            for (Patient p : patientDAO.searchPatient(kw)) {
                patientModel.addRow(new Object[]{
                    p.getId(),    p.getName(),
                    p.getAge(),   p.getGender(),
                    p.getPhone(), p.getDisease()
                });
            }
        });

        showAllBtn.addActionListener(
            e -> loadPatients());

        searchField.addActionListener(
            e -> searchBtn.doClick());

        panel.add(searchBar,BorderLayout.NORTH);
        panel.add(new JScrollPane(table),BorderLayout.CENTER);
        panel.add(infoLabel,BorderLayout.SOUTH);

        return panel;
    }

    // ─────────── DOCTOR STATUS TAB ───────────
    private JPanel buildStatusTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder( 15, 15, 15, 15));

        // All doctors status table
        String[] cols = {"ID", "Doctor Name","Specialization", "Status"};
        DefaultTableModel statusModel =new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

        JTable statusTable = new JTable(statusModel);
        statusTable.setRowHeight(32);
        statusTable.setFont(new Font("Arial", Font.PLAIN, 13));
        statusTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        statusTable.getTableHeader().setBackground(new Color(44, 62, 80));
        statusTable.getTableHeader().setForeground(Color.WHITE);

        // Status column color renderer
        statusTable.getColumnModel().getColumn(3)
            .setCellRenderer(
                new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent( JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                    JLabel lbl = new JLabel();
                    lbl.setOpaque(true);
                    lbl.setHorizontalAlignment(CENTER);
                    lbl.setFont(new Font( "Arial", Font.BOLD, 12));
                    String s = v != null ? v.toString() : "";
                    switch (s) {
                        case "Available":
                            lbl.setText( "● Available");
                            lbl.setForeground( new Color(39,174,96));
                            lbl.setBackground( new Color(212,247,229));
                            break;
                        case "Busy":
                            lbl.setText("● Busy");
                            lbl.setForeground(new Color(231,76,60));
                            lbl.setBackground(new Color(252,220,216));
                            break;
                        case "On Leave":
                            lbl.setText("● On Leave");
                            lbl.setForeground(new Color(243,156,18));
                            lbl.setBackground(new Color(254,240,212));
                            break;
                        case "Off Duty":
                            lbl.setText("● Off Duty");
                            lbl.setForeground(new Color(127,140,141));
                            lbl.setBackground(new Color(235,237,239));
                            break;
                        default:
                            lbl.setText(s);
                            lbl.setForeground(Color.BLACK);
                            lbl.setBackground(Color.WHITE);
                    }
                    if (sel) {
                        lbl.setBackground(new Color(52,152,219));
                        lbl.setForeground(Color.WHITE);
                    }
                    return lbl;
                }
            });

        // Load all doctors into table
        for (Doctor d : doctorDAO.getAllDoctors()) {
            statusModel.addRow(new Object[]{
                d.getId(),
                d.getName(),
                d.getSpecialization(),
                d.getStatus()
            });
        }

        // Refresh button
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton refreshBtn = new JButton("⟳ Refresh Status");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> {
            statusModel.setRowCount(0);
            for (Doctor d :doctorDAO.getAllDoctors()) {
                statusModel.addRow(new Object[]{
                    d.getId(),
                    d.getName(),
                    d.getSpecialization(),
                    d.getStatus()
                });
            }
        });

        JButton myStatusBtn = new JButton("Update My Status");
        myStatusBtn.setBackground(new Color(39, 174, 96));
        myStatusBtn.setForeground(Color.WHITE);
        myStatusBtn.setFocusPainted(false);
        myStatusBtn.addActionListener(
            e -> updateMyStatus());

        bottomBar.add(refreshBtn);
        bottomBar.add(myStatusBtn);

        panel.add(new JScrollPane(statusTable),BorderLayout.CENTER);
        panel.add(bottomBar,BorderLayout.SOUTH);

        return panel;
    }

    // ─────────── MY PROFILE TAB ───────────
    private JPanel buildProfileTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel info = new JPanel(new GridLayout(6, 2, 12, 14));
        info.setBorder(BorderFactory.createTitledBorder("My Profile"));

        JLabel nameLbl   = new JLabel("-");
        JLabel specLbl   = new JLabel("-");
        JLabel phoneLbl  = new JLabel("-");
        JLabel emailLbl  = new JLabel("-");
        JLabel statusLbl = new JLabel("-");

        Font bold = new Font("Arial", Font.BOLD, 14);
        nameLbl.setFont(bold);
        specLbl.setFont(bold);
        phoneLbl.setFont(bold);
        emailLbl.setFont(bold);
        statusLbl.setFont(bold);

        // Load doctor profile
        for (Doctor d : doctorDAO.getAllDoctors()) {
            if (d.getId() == doctorId) {
                nameLbl.setText(d.getName());
                specLbl.setText(d.getSpecialization());
                phoneLbl.setText(d.getPhone());
                emailLbl.setText(d.getEmail());
                statusLbl.setText(d.getStatus());

                // Color status label
                switch (d.getStatus()) {
                    case "Available":
                        statusLbl.setForeground(new Color(39, 174, 96));
                        break;
                    case "Busy":
                        statusLbl.setForeground(new Color(231, 76, 60));
                        break;
                    case "On Leave":
                        statusLbl.setForeground(new Color(243, 156, 18));
                        break;
                    default:
                        statusLbl.setForeground(new Color(127, 140, 141));
                }
                break;
            }
        }

        info.add(makeLabel("Name:"));
        info.add(nameLbl);
        info.add(makeLabel("Specialization:"));
        info.add(specLbl);
        info.add(makeLabel("Phone:"));
        info.add(phoneLbl);
        info.add(makeLabel("Email:"));
        info.add(emailLbl);
        info.add(makeLabel("Current Status:"));
        info.add(statusLbl);

        JButton updateBtn = new JButton("Update My Status");
        updateBtn.setBackground( new Color(39, 174, 96));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont( new Font("Arial", Font.BOLD, 14));
        updateBtn.setFocusPainted(false);
        updateBtn.setPreferredSize( new Dimension(200, 40));
        updateBtn.addActionListener(e -> {
            updateMyStatus();
            // Refresh profile tab labels
            for (Doctor d : doctorDAO.getAllDoctors()) {
                if (d.getId() == doctorId) {
                    statusLbl.setText(d.getStatus());
                    switch (d.getStatus()) {
                        case "Available":
                            statusLbl.setForeground( new Color(39,174,96));
                            break;
                        case "Busy":
                            statusLbl.setForeground( new Color(231,76,60));
                            break;
                        case "On Leave":
                            statusLbl.setForeground( new Color(243,156,18));
                            break;
                        default:
                            statusLbl.setForeground( new Color(127,140,141));
                    }
                    break;
                }
            }
        });

        panel.add(info, BorderLayout.NORTH);
        panel.add(updateBtn,  BorderLayout.SOUTH);
        return panel;
    }

    // ─────────── PAYMENT TAB ───────────
    private JPanel buildPaymentTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton openBtn = new JButton("Open Payment Center");
        openBtn.setFont(new Font("Arial", Font.BOLD, 15));
        openBtn.setBackground(new Color(142, 68, 173));
        openBtn.setForeground(Color.WHITE);
        openBtn.setFocusPainted(false);
        openBtn.setPreferredSize(new Dimension(220, 44));
        openBtn.addActionListener(e ->
            new PaymentPanel(doctorName).setVisible(true));

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.add(openBtn);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    // ─────────── CHATBOT TAB ───────────
    private JPanel buildChatTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton openBtn = new JButton("Open Chatbot");
        openBtn.setFont(new Font("Arial", Font.BOLD, 15));
        openBtn.setBackground(new Color(52, 152, 219));
        openBtn.setForeground(Color.WHITE);
        openBtn.setFocusPainted(false);
        openBtn.setPreferredSize(new Dimension(200, 44));
        openBtn.addActionListener(e ->
            new ChatBotPanel().setVisible(true));

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.add(openBtn);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    // ─────────── FOOTER ───────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(44, 62, 80));

        JLabel lbl = new JLabel("  eMediHub — Doctor Panel  " + "|  Developed by Mahak Gupta");
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.add(lbl);
        return footer;
    }

    // ─────────── UPDATE STATUS ───────────
    private void updateMyStatus() {
        String[] options = {
            "Available",
            "Busy",
            "On Leave",
            "Off Duty"
        };

        String selected =(String) JOptionPane.showInputDialog(
                this,
                "Select your current status:",
                "Update My Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (selected != null) {
            if (doctorDAO.updateStatus(doctorId, selected)) {
                updateStatusBadge(selected);
                JOptionPane.showMessageDialog(this,
                    "Your status updated to: "
                    + selected + "!",
                    "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update status!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ─────────── UPDATE BADGE IN HEADER ───────────
    private void updateStatusBadge(String status) {
        switch (status) {
            case "Available":
                statusBadge.setText("● Available");
                statusBadge.setBackground(new Color(39, 174, 96));
                break;
            case "Busy":
                statusBadge.setText("● Busy");
                statusBadge.setBackground(new Color(231, 76, 60));
                break;
            case "On Leave":
                statusBadge.setText("● On Leave");
                statusBadge.setBackground(new Color(243, 156, 18));
                break;
            case "Off Duty":
                statusBadge.setText("● Off Duty");
                statusBadge.setBackground(new Color(127, 140, 141));
                break;
        }
        statusBadge.revalidate();
        statusBadge.repaint();
    }

    // ─────────── LOAD MY STATUS ───────────
    private void loadMyStatus() {
        for (Doctor d : doctorDAO.getAllDoctors()) {
            if (d.getId() == doctorId) {
                updateStatusBadge(d.getStatus());
                break;
            }
        }
    }

    // ─────────── LOAD PATIENTS ───────────
    private void loadPatients() {
        patientModel.setRowCount(0);
        for (Patient p :patientDAO.getAllPatients()) {
            patientModel.addRow(new Object[]{
                p.getId(),    p.getName(),
                p.getAge(),   p.getGender(),
                p.getPhone(), p.getDisease()
            });
        }
    }

    // ─────────── HELPER ───────────
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        return lbl;
    }
    
    private JPanel buildPrescriptionTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton openBtn = new JButton("Open Prescription Module");
        openBtn.setFont(new Font("Arial", Font.BOLD, 15));
        openBtn.setBackground(new Color(39, 174, 96));
        openBtn.setForeground(Color.WHITE);
        openBtn.setFocusPainted(false);
        openBtn.setPreferredSize(new Dimension(240, 44));
        openBtn.addActionListener(e ->
            new PrescriptionPanel(doctorName, "doctor").setVisible(true));
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.add(openBtn);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel buildHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton openBtn = new JButton("Open Medical History");
        openBtn.setFont(new Font("Arial", Font.BOLD, 15));
        openBtn.setBackground(new Color(41, 128, 185));
        openBtn.setForeground(Color.WHITE);
        openBtn.setFocusPainted(false);
        openBtn.setPreferredSize(new Dimension(240, 44));
        openBtn.addActionListener(e ->
            new MedicalHistoryPanel(doctorName, "doctor").setVisible(true));

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.add(openBtn);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel buildAppointmentsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton openBtn = new JButton("Open Appointments");
        openBtn.setFont(new Font("Arial", Font.BOLD, 15));
        openBtn.setBackground(new Color(22, 160, 133));
        openBtn.setForeground(Color.WHITE);
        openBtn.setFocusPainted(false);
        openBtn.addActionListener(e ->
            new AppointmentPanel(doctorName, "doctor").setVisible(true));
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.add(openBtn);
        panel.add(center,BorderLayout.CENTER);
        return panel;
    }
    
}