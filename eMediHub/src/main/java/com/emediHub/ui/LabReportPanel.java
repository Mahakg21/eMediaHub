package com.emediHub.ui;

import com.emediHub.lab.*;
import com.emediHub.hospital.PatientDao;
import com.emediHub.hospital.Patient;
import com.emediHub.hospital.DoctorDao;
import com.emediHub.hospital.Doctor;
import com.emediHub.notification.LabReportEmailService;
import com.emediHub.db.DBConnection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class LabReportPanel extends JFrame {

    private LabReportDao dao = new LabReportDao();
    private PatientDao patientDAO = new PatientDao();
    private DoctorDao doctorDAO = new DoctorDao();
    private String doctorName;
    private String role;

    // Form fields
    private JComboBox<String> patientBox;
    private JTextField testDateField;
    private JComboBox<String> testNameBox;
    private JTextField resultField;
    private JTextField normalRangeField;
    private JTextField unitField;
    private JComboBox<String> statusBox;
    private JTextField labTechField;
    private JTextArea  remarksArea;

    // Table
    private DefaultTableModel tableModel;
    private JTable reportTable;

    // Stats
    private JLabel totalLbl;
    private JLabel abnormalLbl;

    private static final String[]
        TEST_NAMES = {
            "Complete Blood Count (CBC)",
            "Blood Sugar Fasting",
            "Blood Sugar PP",
            "HbA1c",
            "Lipid Profile",
            "Liver Function Test (LFT)",
            "Kidney Function Test (KFT)",
            "Thyroid Profile (TSH)",
            "Urine Routine",
            "Urine Culture",
            "Urine Pregnancy Test",
            "Dengue NS1 Antigen",
            "Malaria Test",
            "Typhoid (Widal Test)",
            "COVID-19 RT-PCR",
            "HIV Test",
            "Hepatitis B (HBsAg)",
            "Hepatitis C (Anti-HCV)",
            "Chest X-Ray",
            "ECG",
            "Ultrasound Abdomen",
            "Hemoglobin",
            "Platelet Count",
            "Serum Creatinine",
            "Blood Urea",
            "Uric Acid",
            "Vitamin D",
            "Vitamin B12",
            "Iron Studies",
            "Other"
        };

    private static final String[]
        STATUSES = {"Normal","Abnormal","Borderline","Critical"};

    public LabReportPanel(String doctorName, String role) {
        this.doctorName = doctorName;
        this.role       = role;

        setTitle("eMediHub — Lab Reports");
        setSize(1150, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout( 10, 10));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        loadReports();
        loadStats();
    }

    // ─────────── HEADER ───────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("  Lab Reports");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        statsPanel.setOpaque(false);

        totalLbl = makeChip("Total: 0", new Color(52, 152, 219));
        abnormalLbl = makeChip("Abnormal: 0", new Color(231, 76, 60));

        JButton refreshBtn = new JButton("⟳ Refresh");
        refreshBtn.setBackground( new Color(52, 152, 219));
        refreshBtn.setForeground( Color.WHITE);
        refreshBtn.setFont(new Font( "Arial", Font.BOLD, 12));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.addActionListener(e -> {
            loadReports();
            loadStats();
        });

        statsPanel.add(totalLbl);
        statsPanel.add(abnormalLbl);
        statsPanel.add(refreshBtn);

        header.add(title, BorderLayout.WEST);
        header.add(statsPanel, BorderLayout.EAST);
        return header;
    }

    private JLabel makeChip(String text, Color color) {
        JLabel lbl = new JLabel("  " + text + "  ");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(color);
        lbl.setBorder( BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return lbl;
    }

    // ─────────── BODY ───────────
    private JSplitPane buildBody() {
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            buildFormPanel(),
            buildTablePanel());
        split.setDividerLocation(420);
        split.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return split;
    }

    // ─────────── FORM ───────────
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel( new BorderLayout(8, 8));

        JPanel form = new JPanel( new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder( "Add Lab Report"));

        // Patient dropdown
        patientBox = new JComboBox<>();
        patientBox.addItem("-- Select Patient --");
        for (Patient p : patientDAO.getAllPatients())
            patientBox.addItem(p.getName());
        patientBox.setFont(new Font("Arial", Font.PLAIN, 13));

        // Test date
        testDateField = new JTextField(java.time.LocalDate.now().toString());

        // Test name dropdown
        testNameBox = new JComboBox<>(TEST_NAMES);
        testNameBox.setFont(new Font("Arial", Font.PLAIN, 12));

        // Result
        resultField = new JTextField();

        // Unit
        unitField = new JTextField();

        // Normal range
        normalRangeField = new JTextField();

        // Status
        statusBox = new JComboBox<>(STATUSES);
        statusBox.setFont(new Font("Arial", Font.PLAIN, 13));

        // Lab technician
        labTechField = new JTextField();

        form.add(new JLabel("Patient:*"));
        form.add(patientBox);
        form.add(new JLabel("Test Date:*"));
        form.add(testDateField);
        form.add(new JLabel("Test Name:*"));
        form.add(testNameBox);
        form.add(new JLabel("Result:*"));
        form.add(resultField);
        form.add(new JLabel("Unit:"));
        form.add(unitField);
        form.add(new JLabel("Normal Range:"));
        form.add(normalRangeField);
        form.add(new JLabel("Status:*"));
        form.add(statusBox);
        form.add(new JLabel("Lab Technician:"));
        form.add(labTechField);

        // Remarks
        JPanel remPanel = new JPanel(new BorderLayout(5, 5));
        remPanel.setBorder(BorderFactory.createTitledBorder("Doctor Remarks"));
        remarksArea = new JTextArea(3, 15);
        remarksArea.setLineWrap(true);
        remarksArea.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        remPanel.add(new JScrollPane(remarksArea));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        JButton saveBtn = new JButton("Generate Report");
        saveBtn.setBackground( new Color(39, 174, 96));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(180, 38));
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(
            e -> saveReport());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        clearBtn.setPreferredSize(new Dimension(90, 38));
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(
            e -> clearForm());

        btnPanel.add(saveBtn);
        btnPanel.add(clearBtn);

        JPanel topPart = new JPanel(new BorderLayout(6, 6));
        topPart.add(form,BorderLayout.NORTH);
        topPart.add(remPanel,BorderLayout.CENTER);

        panel.add(new JScrollPane(topPart),BorderLayout.CENTER);
        panel.add(btnPanel,BorderLayout.SOUTH);
        return panel;
    }

    // ─────────── TABLE PANEL ───────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JTextField searchField = new JTextField(14);
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        JButton abnormalBtn = new JButton("Abnormal Only");

        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        showAllBtn.setBackground(new Color(149, 165, 166));
        showAllBtn.setForeground(Color.WHITE);
        showAllBtn.setFocusPainted(false);
        abnormalBtn.setBackground(new Color(231, 76, 60));
        abnormalBtn.setForeground(Color.WHITE);
        abnormalBtn.setFocusPainted(false);

        filterBar.add(new JLabel("Patient:"));
        filterBar.add(searchField);
        filterBar.add(searchBtn);
        filterBar.add(showAllBtn);
        filterBar.add(abnormalBtn);

        // Table
        String[] cols = {
            "ID", "Patient", "Doctor",
            "Test Name", "Result",
            "Unit", "Status", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(30);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 12));
        reportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        reportTable.getTableHeader().setBackground(new Color(44, 62, 80));
        reportTable.getTableHeader().setForeground(Color.WHITE);
        reportTable.setSelectionBackground( new Color(52, 152, 219));
        reportTable.setSelectionForeground(Color.WHITE);

        // Status column color
        reportTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer(){
                public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                    JLabel lbl = new JLabel();
                    lbl.setOpaque(true);
                    lbl.setHorizontalAlignment(CENTER);
                    lbl.setFont(new Font("Arial", Font.BOLD, 11));
                    String s = v != null ? v.toString() : "";
                    switch (s) {
                        case "Normal":
                            lbl.setText("● Normal");
                            lbl.setForeground(new Color(39,174,96));
                            lbl.setBackground(new Color(212,247,229));
                            break;
                        case "Abnormal":
                            lbl.setText("● Abnormal");
                            lbl.setForeground(new Color(231,76,60));
                            lbl.setBackground(new Color(252,220,216));
                            break;
                        case "Critical":
                            lbl.setText("⚠ Critical");
                            lbl.setForeground(new Color(192,57,43));
                            lbl.setBackground(new Color(250,200,200));
                            break;
                        case "Borderline":
                            lbl.setText("● Borderline");
                            lbl.setForeground(new Color(243,156,18));
                            lbl.setBackground(new Color(254,240,212));
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

        // Column widths
        int[] widths = {40,120,100,170,80,60,90,100};
        for (int i = 0; i < widths.length; i++) {
            reportTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Bottom buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JButton viewBtn = new JButton("View Details");
        JButton emailBtn = new JButton("Send Email");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        viewBtn.setBackground(new Color(52, 152, 219));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setFocusPainted(false);
        emailBtn.setBackground(new Color(142, 68, 173));
        emailBtn.setForeground(Color.WHITE);
        emailBtn.setFocusPainted(false);
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);

        btnPanel.add(viewBtn);
        btnPanel.add(emailBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        // Actions
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            if (!kw.isEmpty())
                populateTable(dao.getByPatient(kw));
        });

        searchField.addActionListener(
            e -> searchBtn.doClick());

        showAllBtn.addActionListener(
            e -> loadReports());

        abnormalBtn.addActionListener(
            e -> populateTable(dao.getByStatus("Abnormal")));

        viewBtn.addActionListener(
            e -> viewDetails());

        emailBtn.addActionListener(
            e -> sendEmailForSelected());

        deleteBtn.addActionListener(e -> {
            int row = reportTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a report first!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete this report?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Report deleted!");
                    loadReports();
                    loadStats();
                }
            }
        });

        refreshBtn.addActionListener(e -> {
            loadReports();
            loadStats();
        });

        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ─────────── FOOTER ───────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(44, 62, 80));
        JLabel lbl = new JLabel(
            "  eMediHub Lab Reports |Developed by Mahak Gupta | "
            + "|  Color: Green=Normal  "
            + "Orange=Borderline  "
            + "Red=Abnormal  "
            + "Dark Red=Critical");
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.add(lbl);
        return footer;
    }

    // ─────────── SAVE REPORT ───────────
    private void saveReport() {
        String patient = patientBox.getSelectedItem().toString();

        if (patient.equals("-- Select Patient --")) {
            JOptionPane.showMessageDialog(this,"Please select a patient!");
            return;
        }
        if (resultField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,"Result is required!");
            return;
        }

        LabReport r = new LabReport();
        r.setPatientName(patient);
        r.setDoctorName(doctorName);
        r.setTestDate(testDateField.getText().trim());
        r.setTestName(testNameBox.getSelectedItem().toString());
        r.setResult(resultField.getText().trim());
        r.setUnit(unitField.getText().trim());
        r.setNormalRange(normalRangeField.getText().trim());
        r.setStatus(statusBox.getSelectedItem().toString());
        r.setLabTechnician(labTechField.getText().trim());
        r.setRemarks(remarksArea.getText().trim());

        if (dao.addReport(r)) {

            // ✅ Email bhejne ka option
            int choice = JOptionPane.showOptionDialog(this,
                    "Lab Report #"
                    + r.getId()
                    + " generated!\n\n"
                    + "Send to patient "
                    + "on email?",
                    "Report Generated",
                    JOptionPane
                        .YES_NO_OPTION,
                    JOptionPane
                        .QUESTION_MESSAGE,
                    null,
                    new String[]{
                        "Yes, Send Email",
                        "No, Skip"
                    },
                    "Yes, Send Email");

            if (choice == 0)
                sendEmail(r);
            else
                JOptionPane.showMessageDialog(
                        this,
                        "Report saved!\n"
                        + "ID: #" + r.getId(),
                        "Saved",
                        JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadReports();
            loadStats();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to save report!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────── SEND EMAIL ───────────
    private void sendEmail(LabReport r) {
        String email = getPatientEmail(r.getPatientName());

        if (email.isEmpty()) {
            email = JOptionPane.showInputDialog(this,
                    "Patient email not found.\n"
                    + "Enter email address:",
                    "Patient Email",
                    JOptionPane.QUESTION_MESSAGE);
        }

        if (email == null || email.trim().isEmpty())
            return;

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                "Invalid email address!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        final String finalEmail = email.trim();
        final LabReport finalR = r;

        // Loading dialog
        JDialog dlg = new JDialog(this, "Sending Report...", false);
        JPanel dlgPanel = new JPanel(new BorderLayout(10, 10));
        dlgPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        JLabel lbl = new JLabel("Sending lab report to " + finalEmail + "...", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        dlgPanel.add(lbl,BorderLayout.CENTER);
        dlgPanel.add(bar,BorderLayout.SOUTH);
        dlg.add(dlgPanel);
        dlg.setSize(400, 110);
        dlg.setLocationRelativeTo(this);

        new Thread(() -> {
            SwingUtilities.invokeLater(
                () -> dlg.setVisible(true));

            boolean sent = LabReportEmailService.send(finalEmail, finalR);

            SwingUtilities.invokeLater(() -> {
                dlg.dispose();
                if (sent) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Lab report sent!\n\n"
                            + "Sent to: "
                            + finalEmail + "\n"
                            + "Test: "
                            + finalR
                                .getTestName()
                            + "\n"
                            + "Report #: "
                            + finalR.getId(),
                            "Email Sent",
                            JOptionPane
                            .INFORMATION_MESSAGE
                        );
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Email failed!\n"
                            + "Check EmailConfig"
                            + ".java settings.",
                            "Failed",
                            JOptionPane
                            .ERROR_MESSAGE);
                }
            });
        }).start();
    }

    // Send email for selected row
    private void sendEmailForSelected() {
        int row = reportTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,"Please select a report!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String patient = tableModel.getValueAt(row, 1).toString();
        String doctor = tableModel.getValueAt(row, 2).toString();
        String testName = tableModel.getValueAt(row, 3).toString();
        String result = tableModel.getValueAt(row, 4).toString();
        String unit = tableModel.getValueAt(row, 5).toString();
        String status = tableModel.getValueAt(row, 6).toString();
        String date = tableModel.getValueAt(row, 7).toString();

        LabReport r = new LabReport();
        r.setId(id);
        r.setPatientName(patient);
        r.setDoctorName(doctor);
        r.setTestName(testName);
        r.setResult(result);
        r.setUnit(unit);
        r.setStatus(status);
        r.setTestDate(date);
        r.setCreatedAt(date);

        sendEmail(r);
    }

    // ─────────── VIEW DETAILS ───────────
    private void viewDetails() {
        int row = reportTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,"Select a report first!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        String details =
            "═".repeat(40) + "\n"
            + "    LAB REPORT DETAILS\n"
            + "═".repeat(40) + "\n"
            + "Report ID   : #" + id + "\n"
            + "Patient     : "
            + tableModel
                .getValueAt(row, 1) + "\n"
            + "Doctor      : Dr."
            + tableModel
                .getValueAt(row, 2) + "\n"
            + "Test Name   : "
            + tableModel
                .getValueAt(row, 3) + "\n"
            + "Result      : "
            + tableModel
                .getValueAt(row, 4) + " "
            + tableModel
                .getValueAt(row, 5) + "\n"
            + "Status      : "
            + tableModel
                .getValueAt(row, 6) + "\n"
            + "Test Date   : "
            + tableModel
                .getValueAt(row, 7) + "\n"
            + "═".repeat(40);

        JTextArea area = new JTextArea(details);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);

        JOptionPane.showMessageDialog(this,
            new JScrollPane(area),
            "Report #" + id,
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────── HELPERS ───────────
    private void loadReports() {
        if (role.equals("doctor"))
            populateTable(dao.getByDoctor(doctorName));
        else
            populateTable(dao.getAll());
    }

    private void populateTable(List<LabReport> list) {
        tableModel.setRowCount(0);
        for (LabReport r : list) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getPatientName(),
                r.getDoctorName(),
                r.getTestName(),
                r.getResult(),
                r.getUnit().isEmpty() ? "-" : r.getUnit(),
                r.getStatus(),
                r.getTestDate()
            });
        }
    }

    private void loadStats() {
        totalLbl.setText("  Total: " + dao.countTotal() + "  ");
        abnormalLbl.setText("  Abnormal: " + dao.countAbnormal() + "  ");
    }

    private void clearForm() {
        patientBox.setSelectedIndex(0);
        testDateField.setText(java.time.LocalDate.now().toString());
        testNameBox.setSelectedIndex(0);
        resultField.setText("");
        unitField.setText("");
        normalRangeField.setText("");
        statusBox.setSelectedIndex(0);
        labTechField.setText("");
        remarksArea.setText("");
    }

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
