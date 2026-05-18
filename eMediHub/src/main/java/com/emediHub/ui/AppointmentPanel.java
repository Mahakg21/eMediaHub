package com.emediHub.ui;

import com.emediHub.hospital
    .AppointmentDao;
import com.emediHub.notification
    .AppointmentEmailService;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AppointmentPanel extends JFrame {

    private AppointmentDao dao = new AppointmentDao();
    private String userName;
    private String role;
    private DefaultTableModel allModel;
    private DefaultTableModel pendingModel;
    private JTable allTable;
    private JTable pendingTable;
    private JLabel pendingLbl;
    private JLabel approvedLbl;
    private JLabel rejectedLbl;

    public AppointmentPanel(String userName, String role) {
        this.userName = userName;
        this.role     = role;

        setTitle("eMediHub — Appointments");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(),BorderLayout.NORTH);
        add(buildBody(),BorderLayout.CENTER);
        add(buildFooter(),BorderLayout.SOUTH);

        loadAll();
        loadPending();
        loadStats();
    }

    // ─────────── HEADER ───────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("  Appointment Management");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        statsPanel.setOpaque(false);

        pendingLbl  = makeChip("Pending: 0",new Color(243, 156, 18));
        approvedLbl = makeChip("Approved: 0",new Color(39, 174, 96));
        rejectedLbl = makeChip("Rejected: 0",new Color(231, 76, 60));

        JButton refreshBtn =new JButton("⟳ Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.addActionListener(e -> {
            loadAll();
            loadPending();
            loadStats();
        });

        statsPanel.add(pendingLbl);
        statsPanel.add(approvedLbl);
        statsPanel.add(rejectedLbl);
        statsPanel.add(refreshBtn);

        header.add(title,BorderLayout.WEST);
        header.add(statsPanel,BorderLayout.EAST);
        return header;
    }

    private JLabel makeChip(String text, Color color) {
        JLabel lbl = new JLabel("  " + text + "  ");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(color);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return lbl;
    }

    // ─────────── BODY ───────────
    private JTabbedPane buildBody() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabs.addTab("Pending Approval",buildPendingTab());
        tabs.addTab("All Appointments",buildAllTab());

        return tabs;
    }

    // ── Pending Tab ──
    private JPanel buildPendingTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel info = new JLabel(
            "  Pending appointments — "
            + "Approve or Reject below",
            SwingConstants.LEFT);
        info.setFont(new Font("Arial", Font.ITALIC, 12));
        info.setForeground(new Color(243, 156, 18));

        String[] cols = {"ID", "Patient","Doctor", "Department","Date", "Status", "Remarks"};
        pendingModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

        pendingTable = new JTable(pendingModel);
        pendingTable.setRowHeight(30);
        pendingTable.setFont(new Font("Arial", Font.PLAIN, 13));
        pendingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        pendingTable.getTableHeader().setBackground(new Color(243, 156, 18));
        pendingTable.getTableHeader().setForeground(Color.WHITE);
        pendingTable.setSelectionBackground(new Color(52, 152, 219));

        styleStatusColumn(pendingTable, 5);

        // Column widths
        int[] widths = {40,130,130,120,100,90,150};
        for (int i = 0; i < widths.length; i++) {
            pendingTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

        JButton approveBtn = new JButton("✓ Approve");
        JButton rejectBtn = new JButton("✗ Reject");
        JButton refreshBtn = new JButton("Refresh");

        approveBtn.setBackground(new Color(39, 174, 96));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        approveBtn.setPreferredSize(new Dimension(130, 38));
        approveBtn.setFocusPainted(false);

        rejectBtn.setBackground(new Color(231, 76, 60));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFont(new Font("Arial", Font.BOLD, 14));
        rejectBtn.setPreferredSize(new Dimension(120, 38));
        rejectBtn.setFocusPainted(false);

        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(refreshBtn);

        // Actions
        approveBtn.addActionListener(
            e -> approveAppointment());
        rejectBtn.addActionListener(
            e -> rejectAppointment());
        refreshBtn.addActionListener(e -> {
            loadPending();
            loadStats();
        });

        panel.add(info, BorderLayout.NORTH);
        panel.add(new JScrollPane(pendingTable),BorderLayout.CENTER);
        panel.add(btnPanel,BorderLayout.SOUTH);
        return panel;
    }

    // ── All Appointments Tab ──
    private JPanel buildAllTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Pending","Approved", "Rejected"});
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton filterBtn = new JButton("Filter");
        JButton showAllBtn = new JButton("Show All");
        filterBtn.setBackground(new Color(52, 152, 219));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.setFocusPainted(false);
        showAllBtn.setBackground(new Color(149, 165, 166));
        showAllBtn.setForeground(Color.WHITE);
        showAllBtn.setFocusPainted(false);

        filterBar.add(new JLabel("Status:"));
        filterBar.add(statusFilter);
        filterBar.add(filterBtn);
        filterBar.add(showAllBtn);

        String[] cols = {"ID", "Patient","Doctor", "Department","Date", "Status", "Remarks"};
        allModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

        allTable = new JTable(allModel);
        allTable.setRowHeight(30);
        allTable.setFont(new Font("Arial", Font.PLAIN, 13));
        allTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        allTable.getTableHeader().setBackground(new Color(44, 62, 80));
        allTable.getTableHeader().setForeground(Color.WHITE);
        allTable.setSelectionBackground(new Color(52, 152, 219));

        styleStatusColumn(allTable, 5);

        int[] widths = {40,130,130,120,100,100,150};
        for (int i = 0; i < widths.length; i++) {
            allTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        filterBtn.addActionListener(e -> {
            String s = statusFilter.getSelectedItem().toString();
            if (s.equals("All"))
                loadAll();
            else
                filterByStatus(s);
        });

        showAllBtn.addActionListener(
            e -> loadAll());

        panel.add(filterBar,BorderLayout.NORTH);
        panel.add(new JScrollPane(allTable),BorderLayout.CENTER);
        return panel;
    }

    // ─────────── FOOTER ───────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(44, 62, 80));
        JLabel lbl = new JLabel(
            "  eMediHub Appointments  "
        	+ "| Developed by Mahak Gupta "
            + "|  Orange=Pending  "
            + "Green=Approved  "
            + "Red=Rejected");
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.add(lbl);
        return footer;
    }

    // ─────────── APPROVE ───────────
    private void approveAppointment() {
        int row = pendingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an "
                + "appointment to approve!",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(pendingModel.getValueAt(row, 0).toString());
        String patient = pendingModel.getValueAt(row, 1).toString();
        String doctor  = pendingModel.getValueAt(row, 2).toString();
        String dept    = pendingModel.getValueAt(row, 3).toString();
        String date    = pendingModel.getValueAt(row, 4).toString();

        // Remarks optional
        String remarks = JOptionPane.showInputDialog(this,
                "Approve appointment for:\n"
                + "Patient: " + patient + "\n"
                + "Doctor:  Dr." + doctor
                + "\n"
                + "Date:    " + date + "\n\n"
                + "Add remarks (optional):",
                "Approve Appointment",
                JOptionPane.QUESTION_MESSAGE);

        if (remarks == null) return;

        if (dao.updateStatus(id, "Approved", remarks)) {

            JOptionPane.showMessageDialog(
                this,
                "Appointment Approved!\n"
                + "Patient: " + patient,
                "Approved",
                JOptionPane.INFORMATION_MESSAGE);

            // ✅ Email bhejo
            sendApprovalEmail(id, patient, doctor,dept, date,"Approved", remarks);

            loadPending();
            loadAll();
            loadStats();
        }
    }

    // ─────────── REJECT ───────────
    private void rejectAppointment() {
        int row = pendingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an "
                + "appointment to reject!",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(pendingModel.getValueAt(row, 0).toString());
        String patient = pendingModel.getValueAt(row, 1).toString();
        String doctor  = pendingModel.getValueAt(row, 2).toString();
        String dept    = pendingModel.getValueAt(row, 3).toString();
        String date    = pendingModel.getValueAt(row, 4).toString();

        // Rejection reason required
        String remarks = JOptionPane.showInputDialog(this,
                "Reject appointment for:\n"
                + "Patient: " + patient + "\n"
                + "Doctor:  Dr." + doctor
                + "\n"
                + "Date:    " + date + "\n\n"
                + "Enter reason for "
                + "rejection: *",
                "Reject Appointment",
                JOptionPane.WARNING_MESSAGE);

        if (remarks == null || remarks.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Rejection reason "
                + "is required!",
                "Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dao.updateStatus(id, "Rejected",remarks.trim())) {

            JOptionPane.showMessageDialog(this,
                "Appointment Rejected.\n"
                + "Patient: " + patient,
                "Rejected",
                JOptionPane.WARNING_MESSAGE);

            // ✅ Email bhejo
            sendApprovalEmail(id, patient, doctor,dept, date,"Rejected", remarks);

            loadPending();
            loadAll();
            loadStats();
        }
    }

    // ─────────── SEND EMAIL ───────────
    private void sendApprovalEmail(int apptId, String patient, String doctor, String dept, String date, String status, String remarks) {

        String email = dao.getPatientEmail(apptId);

        if (email.isEmpty()) {
            email = JOptionPane.showInputDialog(this,
                    "Patient email not found.\n"
                    + "Enter email to notify:",
                    "Patient Email",
                    JOptionPane.QUESTION_MESSAGE);
        }

        if (email == null || email.trim().isEmpty())
            return;

        final String finalEmail = email.trim();
        final String finalPatient  = patient;
        final String finalDoctor   = doctor;
        final String finalDept     = dept;
        final String finalDate     = date;
        final String finalStatus   = status;
        final String finalRemarks  = remarks;

        new Thread(() -> {
            boolean sent = AppointmentEmailService.sendApproval(
                        finalEmail,
                        finalPatient,
                        finalDoctor,
                        finalDept,
                        finalDate,
                        finalStatus,
                        finalRemarks);

            SwingUtilities.invokeLater(() -> {
                if (sent)
                    JOptionPane.showMessageDialog(this,
                            "Email sent to "
                            + "patient!\n"
                            + finalEmail,
                            "Email Sent",
                            JOptionPane
                            .INFORMATION_MESSAGE
                        );
                else
                    JOptionPane.showMessageDialog( this,
                            "Email failed!\n"
                            + "Check EmailConfig"
                            + ".java",
                            "Email Failed",
                            JOptionPane
                            .ERROR_MESSAGE);
            });
        }).start();
    }

    // ─────────── LOAD DATA ───────────
    private void loadAll() {
        allModel.setRowCount(0);
        List<String[]> list = role.equals("doctor") ? dao.getByDoctor(userName) : dao.getAll();
        for (String[] row : list)
            allModel.addRow(row);
    }

    private void loadPending() {
        pendingModel.setRowCount(0);
        List<String[]> list = role.equals("doctor") ? dao.getByDoctor(userName).stream().filter(r ->
                    r[5].equals("Pending"))
                .collect(java.util.stream.Collectors.toList()) : dao.getPending();
        for (String[] row : list)
            pendingModel.addRow(row);
    }

    private void filterByStatus(String status) {
        allModel.setRowCount(0);
        dao.getAll().stream().filter(r ->
                r[5].equals(status)).forEach(r ->
                allModel.addRow(r));
    }

    private void loadStats() {
        pendingLbl.setText("  Pending: " + dao.countByStatus("Pending") + "  ");
        approvedLbl.setText("  Approved: " + dao.countByStatus("Approved") + "  ");
        rejectedLbl.setText("  Rejected: " + dao.countByStatus("Rejected") + "  ");
    }

    // Status column color renderer
    private void styleStatusColumn(JTable table, int col) {
        table.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer(){
                public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,boolean foc,int r, int c) {
                    JLabel lbl = new JLabel();
                    lbl.setOpaque(true);
                    lbl.setHorizontalAlignment(CENTER);
                    lbl.setFont(new Font("Arial", Font.BOLD, 12));
                    String s = v != null ? v.toString() : "";
                    switch (s) {
                        case "Pending":
                            lbl.setText("● Pending");
                            lbl.setForeground(new Color(243,156,18));
                            lbl.setBackground(new Color(254,240,212));
                            break;
                        case "Approved":
                            lbl.setText("✓ Approved");
                            lbl.setForeground(new Color(39,174,96));
                            lbl.setBackground(new Color(212,247,229));
                            break;
                        case "Rejected":
                            lbl.setText("✗ Rejected");
                            lbl.setForeground(new Color(231,76,60));
                            lbl.setBackground(new Color(252,220,216));
                            break;
                        default:
                            lbl.setText(s);
                    }
                    if (sel) {
                        lbl.setBackground(new Color(52,152,219));
                        lbl.setForeground(Color.WHITE);
                    }
                    return lbl;
                }
            });
    }
}
