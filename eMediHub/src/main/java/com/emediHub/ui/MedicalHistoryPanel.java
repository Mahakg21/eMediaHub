package com.emediHub.ui;

import com.emediHub.db.DBConnection;
import com.emediHub.history.*;
import com.emediHub.hospital.PatientDao;
import com.emediHub.notification.MedicalHistoryEmailService;
import com.emediHub.hospital.Patient;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class MedicalHistoryPanel extends JFrame {

    private MedicalHistoryDao dao = new MedicalHistoryDao();
    private PatientDao patientDAO = new PatientDao();

    private String doctorName;
    private String role;

    // Form fields
    private JComboBox<String> patientBox;
    private JTextField visitDateField;
    private JTextField diagnosisField;
    private JTextField treatmentField;
    private JTextArea  symptomsArea;
    private JTextField bpField;
    private JTextField tempField;
    private JTextField weightField;
    private JTextArea  notesArea;
    private JTextField followUpField;

    // Table
    private DefaultTableModel tableModel;
    private JTable historyTable;

    // Stats
    private JLabel totalLbl;
    private JLabel followUpLbl;

    public MedicalHistoryPanel(String doctorName, String role) {
        this.doctorName = doctorName;
        this.role       = role;

        setTitle("eMediHub — Medical History");
        setSize(1150, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        loadHistory();
        loadStats();
    }

    // ─────────── HEADER ───────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("  Medical History");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        // Stats chips in header
        JPanel statsPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 12, 0));
        statsPanel.setOpaque(false);

        totalLbl = makeChip("Total Visits: 0",new Color(52, 152, 219));
        followUpLbl = makeChip("Follow Ups Today: 0",new Color(231, 76, 60));

        JButton refreshBtn =new JButton("⟳ Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.addActionListener(e -> {
            loadHistory();
            loadStats();
        });

        statsPanel.add(totalLbl);
        statsPanel.add(followUpLbl);
        statsPanel.add(refreshBtn);

        header.add(title, BorderLayout.WEST);
        header.add(statsPanel,BorderLayout.EAST);
        return header;
    }

    private JLabel makeChip(String text, Color color) {
        JLabel lbl = new JLabel( "  " + text + "  ");
        lbl.setFont(new Font( "Arial", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(color);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return lbl;
    }

    // ─────────── BODY ───────────
    private JSplitPane buildBody() {
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            buildFormPanel(),
            buildTablePanel());
        split.setDividerLocation(440);
        split.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return split;
    }

    // ─────────── FORM PANEL ───────────
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Scrollable form
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBorder(BorderFactory.createTitledBorder( "Add Visit Record"));

        // Patient dropdown
        patientBox = new JComboBox<>();
        patientBox.addItem( "-- Select Patient --");
        for (Patient p : patientDAO.getAllPatients()) {
            patientBox.addItem(p.getName());
        }
        patientBox.setFont(new Font( "Arial", Font.PLAIN, 13));

        // Visit date
        visitDateField = new JTextField(java.time.LocalDate.now().toString());

        // Diagnosis
        diagnosisField = new JTextField();

        // Treatment
        treatmentField = new JTextField();

        // Symptoms
        symptomsArea = new JTextArea(3, 15);
        symptomsArea.setLineWrap(true);
        symptomsArea.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

        // Vitals
        bpField     = new JTextField("120/80");
        tempField   = new JTextField("98.6°F");
        weightField = new JTextField("65 kg");

        // Notes
        notesArea = new JTextArea(2, 15);
        notesArea.setLineWrap(true);
        notesArea.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

        // Follow up date
        followUpField = new JTextField();

        form.add(makeLabel("Patient:"));
        form.add(patientBox);
        form.add(makeLabel("Visit Date:"));
        form.add(visitDateField);
        form.add(makeLabel("Diagnosis:"));
        form.add(diagnosisField);
        form.add(makeLabel("Treatment:"));
        form.add(treatmentField);
        form.add(makeLabel("Blood Pressure:"));
        form.add(bpField);
        form.add(makeLabel("Temperature:"));
        form.add(tempField);
        form.add(makeLabel("Weight:"));
        form.add(weightField);
        form.add(makeLabel("Follow Up Date:"));
        form.add(followUpField);

        // Symptoms — full width
        JPanel sympPanel = new JPanel(new BorderLayout(5, 5));
        sympPanel.setBorder(BorderFactory.createTitledBorder("Symptoms"));
        sympPanel.add(new JScrollPane(symptomsArea));

        // Notes — full width
        JPanel notesPanel = new JPanel(new BorderLayout(5, 5));
        notesPanel.setBorder(BorderFactory.createTitledBorder("Doctor Notes"));
        notesPanel.add(new JScrollPane(notesArea));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));

        JButton saveBtn =new JButton("Save Record");
        saveBtn.setBackground(new Color(39, 174, 96));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(160, 38));
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(
            e -> saveRecord());

        JButton clearBtn =new JButton("Clear");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        clearBtn.setPreferredSize(new Dimension(90, 38));
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(
            e -> clearForm());

        btnPanel.add(saveBtn);
        btnPanel.add(clearBtn);

        // Combine all parts
        JPanel topPart = new JPanel(new BorderLayout(6, 6));
        topPart.add(form, BorderLayout.NORTH);
        topPart.add(sympPanel,BorderLayout.CENTER);
        topPart.add(notesPanel,BorderLayout.SOUTH);

        panel.add(new JScrollPane(topPart),BorderLayout.CENTER);
        panel.add(btnPanel,BorderLayout.SOUTH);

        return panel;
    }

    // ─────────── TABLE PANEL ───────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Tab panel — History + Follow Ups
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));

        tabs.addTab("All Visit History",buildHistoryTab());
        tabs.addTab("Follow Up Schedule",buildFollowUpTab());

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    // All visits tab
    private JPanel buildHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JTextField searchField =new JTextField(16);
        JButton searchBtn =new JButton("Search");
        JButton showAllBtn =new JButton("Show All");

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
        String[] cols = {
            "ID", "Patient", "Doctor",
            "Visit Date", "Diagnosis",
            "BP", "Temp", "Follow Up"};
        tableModel =
            new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(30);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 12));
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        historyTable.getTableHeader().setBackground(new Color(44,62,80));
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.setSelectionBackground(new Color(52, 152, 219));
        historyTable.setSelectionForeground(Color.WHITE);
        historyTable.setGridColor(new Color(220, 220, 220));

        // Color follow up column
        historyTable.getColumnModel()
            .getColumn(7)
            .setCellRenderer(
                new DefaultTableCellRenderer(){
                public Component
                        getTableCellRendererComponent(
                        JTable t, Object v,
                        boolean sel,
                        boolean foc,
                        int r, int c) {
                    super
                    .getTableCellRendererComponent(
                        t,v,sel,foc,r,c);
                    String val = v != null ? v.toString() : "";
                    if (!val.isEmpty() && !val.equals("-")) {
                        setForeground(new Color(231,76,60));
                        setFont(new Font("Arial",Font.BOLD, 12));
                    }
                    return this;
                }
            });

        // Column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        // Bottom buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JButton viewBtn =new JButton("View Full Details");
        JButton deleteBtn =new JButton("Delete");
        JButton refreshBtn =new JButton("Refresh");

        viewBtn.setBackground(new Color(52, 152, 219));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setFocusPainted(false);
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);

        btnPanel.add(viewBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        // Actions
        searchBtn.addActionListener(e -> {
            String kw =searchField.getText().trim();
            if (!kw.isEmpty())
                populateTable(dao.getByPatient(kw));
        });

        searchField.addActionListener(
            e -> searchBtn.doClick());

        showAllBtn.addActionListener(
            e -> loadHistory());

        viewBtn.addActionListener(
            e -> viewFullDetails());

        deleteBtn.addActionListener(e -> {
            int row = historyTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,"Please select a record!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete this record?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.delete(id)) {
                    JOptionPane.showMessageDialog( this,"Record deleted!");
                    loadHistory();
                    loadStats();
                }
            }
        });

        refreshBtn.addActionListener(e -> {
            loadHistory();
            loadStats();
        });

        panel.add(searchBar,BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable),BorderLayout.CENTER);
        panel.add(btnPanel,BorderLayout.SOUTH);

        return panel;
    }

    // Follow up tab
    private JPanel buildFollowUpTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {
            "ID", "Patient", "Doctor",
            "Last Visit", "Diagnosis",
            "Follow Up Date"};
        DefaultTableModel followModel =
            new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

        JTable followTable =new JTable(followModel);
        followTable.setRowHeight(30);
        followTable.setFont(new Font( "Arial", Font.PLAIN, 12));
        followTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        followTable.getTableHeader().setBackground(new Color(231, 76, 60));
        followTable.getTableHeader().setForeground(Color.WHITE);

        // Load follow ups
        loadFollowUps(followModel);

        JButton refreshFollowBtn =new JButton("⟳ Refresh");
        refreshFollowBtn.setBackground(new Color(231, 76, 60));
        refreshFollowBtn.setForeground(Color.WHITE);
        refreshFollowBtn.setFocusPainted(false);
        refreshFollowBtn.addActionListener(
            e -> loadFollowUps(followModel));

        panel.add(new JScrollPane(followTable),BorderLayout.CENTER);
        panel.add(refreshFollowBtn,BorderLayout.SOUTH);

        return panel;
    }

    // ─────────── FOOTER ───────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(44, 62, 80));
        JLabel lbl = new JLabel(
            "  eMediHub Medical History  "
            + "| Developed by Mahak Gupta  | " + doctorName
            + "  |  Role: " + role);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.add(lbl);
        return footer;
    }

    // ─────────── SAVE RECORD ───────────
    private void saveRecord() {
        String patient =patientBox.getSelectedItem().toString();

        if (patient.equals("-- Select Patient --")) {
            JOptionPane.showMessageDialog(this,"Please select a patient!");
            return;
        }
        if (diagnosisField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please enter diagnosis!");
            return;
        }

        MedicalHistory h =new MedicalHistory();
        h.setPatientName(patient);
        h.setDoctorName(doctorName);
        h.setVisitDate(visitDateField.getText().trim());
        h.setDiagnosis(diagnosisField.getText().trim());
        h.setTreatment(treatmentField.getText().trim());
        h.setSymptoms(symptomsArea.getText().trim());
        h.setBloodPressure(bpField.getText().trim());
        h.setTemperature(tempField.getText().trim());
        h.setWeight(weightField.getText().trim());
        h.setNotes(notesArea.getText().trim());
        h.setFollowUpDate(followUpField.getText().trim());


        if (dao.addHistory(h)) {

            // ✅ Email bhejne ka popup
            int choice = JOptionPane.showConfirmDialog(this,
                    "Medical history saved!\n\n"
                    + "Send record to patient "
                    + "on email?",
                    "Send Email",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                sendHistoryEmail(h);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Medical history saved "
                    + "successfully!",
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            clearForm();
            loadHistory();
            loadStats();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to save record!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────── VIEW FULL DETAILS ───────────
    private void viewFullDetails() {
        int row =historyTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,"Please select a record!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        MedicalHistory h = dao.getById(id);
        if (h == null) return;

        String details =
            "═".repeat(42) + "\n"
            + "     MEDICAL VISIT RECORD\n"
            + "═".repeat(42) + "\n\n"
            + "Record ID   : #" + h.getId()
            + "\n"
            + "Visit Date  : "
            + h.getVisitDate() + "\n"
            + "─".repeat(42) + "\n"
            + "Patient     : "
            + h.getPatientName() + "\n"
            + "Doctor      : Dr."
            + h.getDoctorName() + "\n"
            + "─".repeat(42) + "\n\n"
            + "DIAGNOSIS\n"
            + h.getDiagnosis() + "\n\n"
            + "SYMPTOMS\n"
            + safe(h.getSymptoms()) + "\n\n"
            + "TREATMENT\n"
            + safe(h.getTreatment()) + "\n\n"
            + "─".repeat(42) + "\n"
            + "VITALS\n"
            + "Blood Pressure : "
            + safe(h.getBloodPressure())
            + "\n"
            + "Temperature    : "
            + safe(h.getTemperature()) + "\n"
            + "Weight         : "
            + safe(h.getWeight()) + "\n"
            + "─".repeat(42) + "\n\n"
            + "DOCTOR'S NOTES\n"
            + safe(h.getNotes()) + "\n\n"
            + "─".repeat(42) + "\n"
            + "Follow Up Date : "
            + (h.getFollowUpDate() != null
                && !h.getFollowUpDate()
                    .isEmpty()
                ? h.getFollowUpDate()
                : "Not scheduled") + "\n"
            + "═".repeat(42);

        JTextArea area =new JTextArea(details);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(new Color(245, 245, 245));
        area.setRows(22);

        JOptionPane.showMessageDialog(
            this,
            new JScrollPane(area),
            "Medical History — #" + id,
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────── LOAD DATA ───────────
    private void loadHistory() {
        if (role.equals("doctor"))
            populateTable(dao.getByDoctor(doctorName));
        else
            populateTable(dao.getAll());
    }

    private void populateTable(List<MedicalHistory> list) {
        tableModel.setRowCount(0);
        for (MedicalHistory h : list) {
            tableModel.addRow(new Object[]{
                h.getId(),
                h.getPatientName(),
                h.getDoctorName(),
                h.getVisitDate(),
                h.getDiagnosis(),
                safe(h.getBloodPressure()),
                safe(h.getTemperature()),
                (h.getFollowUpDate() != null && !h.getFollowUpDate()
                .isEmpty()) ? h.getFollowUpDate() : "-"
            });
        }
    }

    private void loadFollowUps(DefaultTableModel model) {
        model.setRowCount(0);
        for (MedicalHistory h : dao.getUpcomingFollowUps()) {
            model.addRow(new Object[]{
                h.getId(),
                h.getPatientName(),
                h.getDoctorName(),
                h.getVisitDate(),
                h.getDiagnosis(),
                h.getFollowUpDate()
            });
        }
    }

    private void loadStats() {
        totalLbl.setText(
            "  Total Visits: "
            + dao.countAll() + "  ");
        followUpLbl.setText(
            "  Follow Ups Today: "
            + dao.countFollowUpsToday()
            + "  ");
    }

    // ─────────── CLEAR FORM ───────────
    private void clearForm() {
        patientBox.setSelectedIndex(0);
        visitDateField.setText(java.time.LocalDate.now().toString());
        diagnosisField.setText("");
        treatmentField.setText("");
        symptomsArea.setText("");
        bpField.setText("120/80");
        tempField.setText("98.6°F");
        weightField.setText("65 kg");
        notesArea.setText("");
        followUpField.setText("");
    }

    // ─────────── HELPERS ───────────
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        return lbl;
    }

    private String safe(String val) {
        return (val != null && !val.isEmpty()) ? val : "-";
    }
    
 // ─────────── SEND EMAIL ───────────
    private void sendHistoryEmail(MedicalHistory h) {

        // DB se email fetch karo
        String email = getPatientEmail(h.getPatientName());

        // Nahi mili toh manually lo
        if (email.isEmpty()) {
            email = JOptionPane.showInputDialog(this,
                "Patient email not found.\n"
                + "Enter email address:",
                "Patient Email", JOptionPane.QUESTION_MESSAGE);
        }

        // Skip kiya
        if (email == null || email.trim().isEmpty())
            return;

        // Validate
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,"Invalid email address!","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        final String finalEmail = email.trim();
        final MedicalHistory finalH = h;

        // Loading dialog
        JDialog dlg = new JDialog(this,"Sending Email...",false);
        JPanel dlgPanel = new JPanel(new BorderLayout(10, 10));
        dlgPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        JLabel lbl = new JLabel(
            "Sending medical record to "
            + finalEmail + "...",
            SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        dlgPanel.add(lbl,BorderLayout.CENTER);
        dlgPanel.add(bar,BorderLayout.SOUTH);
        dlg.add(dlgPanel);
        dlg.setSize(400, 110);
        dlg.setLocationRelativeTo(this);

        // Background thread mein send karo
        new Thread(() -> {

            SwingUtilities.invokeLater(
                () -> dlg.setVisible(true));

            boolean sent = MedicalHistoryEmailService.send(finalEmail, finalH);

            SwingUtilities.invokeLater(() -> {
                dlg.dispose();

                if (sent) {
                    JOptionPane.showMessageDialog(this,
                            "Medical history sent!\n\n"
                            + "Sent to: "
                            + finalEmail + "\n"
                            + "Patient: "
                            + finalH
                                .getPatientName()
                            + "\n"
                            + "Visit Date: "
                            + finalH
                                .getVisitDate(),
                            "Email Sent",
                            JOptionPane
                            .INFORMATION_MESSAGE);
                } else {
                    int retry = JOptionPane.showConfirmDialog(this,
                            "Email failed!\n\n"
                            + "Check:\n"
                            + "1. Internet connection\n"
                            + "2. Gmail App Password\n"
                            + "3. Email address\n\n"
                            + "Retry?",
                            "Failed",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE);

                    if (retry == JOptionPane.YES_OPTION)
                        sendHistoryEmail(finalH);
                }
            });
        }).start();
    }

    // ─── GET PATIENT EMAIL FROM DB ───
    private String getPatientEmail(String patientName) {
        String sql =
            "SELECT email FROM users "
            + "WHERE name = ? "
            + "AND role = 'patient' "
            + "LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patientName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String e = rs.getString("email");
                if (e != null && !e.isEmpty())
                    return e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
}
