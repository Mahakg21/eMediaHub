package com.emediHub.ui;

import com.emediHub.prescription.*;
import com.emediHub.notification.PrescriptionEmailService;
import com.emediHub.db.DBConnection;
import com.emediHub.hospital.PatientDao;
import com.emediHub.hospital.Patient;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionPanel extends JFrame {

    private PrescriptionDao dao    =  new PrescriptionDao();
    private PatientDao patientDAO  =  new PatientDao();
    private String doctorName;
    private String role;

    // Form fields
    private JTextField  patientField;
    private JTextField  diagnosisField;
    private JTextArea   notesArea;

    // Medicine table inside form
    private DefaultTableModel medicineModel;
    private JTable medicineTable;

    // Medicine input fields
    private JTextField medNameField;
    private JTextField dosageField;
    private JComboBox<String> frequencyBox;
    private JTextField durationField;
    private JTextField instrField;

    // Prescription list table
    private DefaultTableModel prescModel;
    private JTable prescTable;

    public PrescriptionPanel(String doctorName, String role) {
        this.doctorName = doctorName;
        this.role = role;

        setTitle("eMediHub — Prescription Module");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
        loadPrescriptions();
    }

    // ─────────── HEADER ───────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("  Prescription Module");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel docLabel = new JLabel("Dr. " + doctorName + "  |  Role: "+ role + "  ");
        docLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        docLabel.setForeground(new Color(180, 220, 255));

        header.add(title,    BorderLayout.WEST);
        header.add(docLabel, BorderLayout.EAST);
        return header;
    }

    // ─────────── BODY ───────────
    private JSplitPane buildBody() {
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            buildFormPanel(),
            buildListPanel());
        split.setDividerLocation(520);
        split.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return split;
    }

    // ─────────── FORM PANEL ───────────
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Top — patient + diagnosis
        JPanel topForm = new JPanel(new GridLayout(3, 2, 8, 8));
        topForm.setBorder(BorderFactory.createTitledBorder("New Prescription"));

        patientField   = new JTextField();
        diagnosisField = new JTextField();

        // Patient autocomplete from DB
        JComboBox<String> patientSuggest =new JComboBox<>();
        patientSuggest.setEditable(true);
        for (Patient p :patientDAO.getAllPatients()) {
            patientSuggest.addItem(p.getName());
        }
        patientSuggest.addActionListener(e -> {
            Object sel =patientSuggest.getSelectedItem();
            if (sel != null)
                patientField.setText(sel.toString());
        });

        topForm.add(new JLabel("Patient Name:"));
        topForm.add(patientSuggest);
        topForm.add(new JLabel("Diagnosis:"));
        topForm.add(diagnosisField);

        notesArea = new JTextArea(2, 20);
        notesArea.setLineWrap(true);
        notesArea.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        topForm.add(new JLabel("Notes:"));
        topForm.add(new JScrollPane(notesArea));

        // Middle — medicine table
        JPanel medPanel = new JPanel(new BorderLayout(5, 5));
        medPanel.setBorder(BorderFactory.createTitledBorder("Medicines"));

        String[] medCols = {"Medicine", "Dosage","Frequency", "Duration", "Instructions"};
        medicineModel =new DefaultTableModel(medCols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
        medicineTable = new JTable(medicineModel);
        medicineTable.setRowHeight(26);
        medicineTable.setFont(new Font("Arial", Font.PLAIN, 12));
        medicineTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        medicineTable.getTableHeader().setBackground(new Color(44, 62, 80));
        medicineTable.getTableHeader().setForeground(Color.WHITE);

        // Medicine input row
        JPanel medInput = new JPanel(new GridLayout(2, 4, 6, 6));
        medInput.setBorder(BorderFactory.createTitledBorder("Add Medicine"));

        medNameField = new JTextField();
        dosageField  = new JTextField("500mg");
        frequencyBox = new JComboBox<>(new String[]{
            "Once a day",
            "Twice a day",
            "Three times a day",
            "Four times a day",
            "Every 6 hours",
            "Every 8 hours",
            "Before food",
            "After food",
            "At bedtime",
            "As needed"
        });
        durationField = new JTextField("5 days");
        instrField    = new JTextField("Take with water");

        medInput.add(new JLabel("Medicine name:"));
        medInput.add(medNameField);
        medInput.add(new JLabel("Dosage:"));
        medInput.add(dosageField);
        medInput.add(new JLabel("Frequency:"));
        medInput.add(frequencyBox);
        medInput.add(new JLabel("Duration:"));
        medInput.add(durationField);

        JPanel instrRow = new JPanel(new BorderLayout(6, 0));
        instrRow.add(new JLabel("Instructions:"),BorderLayout.WEST);
        instrRow.add(instrField,BorderLayout.CENTER);

        JButton addMedBtn = new JButton("+ Add Medicine");
        addMedBtn.setBackground(new Color(52, 152, 219));
        addMedBtn.setForeground(Color.WHITE);
        addMedBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addMedBtn.setFocusPainted(false);
        addMedBtn.addActionListener(
            e -> addMedicineRow());

        JButton removeMedBtn = new JButton("Remove Selected");
        removeMedBtn.setBackground(new Color(231, 76, 60));
        removeMedBtn.setForeground(Color.WHITE);
        removeMedBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        removeMedBtn.setFocusPainted(false);
        removeMedBtn.addActionListener(e -> {
            int row =medicineTable.getSelectedRow();
            if (row != -1)
                medicineModel.removeRow(row);
        });

        JPanel medBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        medBtnRow.add(instrRow);
        medBtnRow.add(addMedBtn);
        medBtnRow.add(removeMedBtn);

        medPanel.add(medInput,BorderLayout.NORTH);
        medPanel.add(new JScrollPane(medicineTable),BorderLayout.CENTER);
        medPanel.add(medBtnRow,BorderLayout.SOUTH);

        // Bottom — save + clear buttons
        JPanel bottomBtns = new JPanel(new FlowLayout(FlowLayout.CENTER,12, 6));

        JButton saveBtn = new JButton("Save Prescription");
        saveBtn.setBackground(new Color(39, 174, 96));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(200, 40));
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(
            e -> savePrescription());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        clearBtn.setPreferredSize(new Dimension(100, 40));
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(
            e -> clearForm());

        bottomBtns.add(saveBtn);
        bottomBtns.add(clearBtn);

        panel.add(topForm,     BorderLayout.NORTH);
        panel.add(medPanel,    BorderLayout.CENTER);
        panel.add(bottomBtns,  BorderLayout.SOUTH);

        return panel;
    }

    // ─────────── PRESCRIPTION LIST PANEL ───────────
    private JPanel buildListPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setBorder(BorderFactory.createTitledBorder("Prescription History"));

        JTextField searchField =new JTextField(16);
        JButton searchBtn  =new JButton("Search");
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

        // Prescription list table
        String[] cols = {"ID", "Patient","Doctor", "Diagnosis", "Date"};
        prescModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
        prescTable = new JTable(prescModel);
        prescTable.setRowHeight(28);
        prescTable.setFont(new Font("Arial", Font.PLAIN, 12));
        prescTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        prescTable.getTableHeader()
            .setBackground(new Color(44, 62, 80));
        prescTable.getTableHeader()
            .setForeground(Color.WHITE);
        prescTable.setSelectionBackground(
            new Color(52, 152, 219));
        prescTable.setSelectionForeground(Color.WHITE);

        // Column widths
        prescTable.getColumnModel()
            .getColumn(0).setPreferredWidth(40);
        prescTable.getColumnModel()
            .getColumn(1).setPreferredWidth(120);
        prescTable.getColumnModel()
            .getColumn(2).setPreferredWidth(120);
        prescTable.getColumnModel()
            .getColumn(3).setPreferredWidth(150);
        prescTable.getColumnModel()
            .getColumn(4).setPreferredWidth(130);

        // Bottom action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JButton viewBtn    = new JButton("View Details");
        JButton printBtn   = new JButton("Print Prescription");
        JButton deleteBtn  = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        viewBtn.setBackground(new Color(52, 152, 219));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setFocusPainted(false);

        printBtn.setBackground(new Color(155, 89, 182));
        printBtn.setForeground(Color.WHITE);
        printBtn.setFocusPainted(false);

        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);

        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);

        btnPanel.add(viewBtn);
        btnPanel.add(printBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        // Actions
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            if (!kw.isEmpty())
                populateTable(dao.getByPatient(kw));
        });

        showAllBtn.addActionListener(
            e -> loadPrescriptions());

        searchField.addActionListener(
            e -> searchBtn.doClick());

        viewBtn.addActionListener(
            e -> viewDetails());

        printBtn.addActionListener(
            e -> printPrescription());

        deleteBtn.addActionListener(e -> {
            int row =prescTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,"Please select a prescription!");
                return;
            }
            int id = (int)prescModel.getValueAt(row, 0);
            int confirm =JOptionPane.showConfirmDialog(
                    this,
                    "Delete this prescription?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.deletePrescription(id)) {
                    JOptionPane.showMessageDialog( this, "Prescription deleted!");
                    loadPrescriptions();
                }
            }
        });

        refreshBtn.addActionListener(
            e -> loadPrescriptions());

        panel.add(searchBar,BorderLayout.NORTH);
        panel.add(new JScrollPane(prescTable),BorderLayout.CENTER);
        panel.add(btnPanel,BorderLayout.SOUTH);

        return panel;
    }

    // ─────────── FOOTER ───────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(44, 62, 80));
        JLabel lbl = new JLabel(
            "  eMediHub Prescription Module | developed by Mahak Gupta "
            + "|  Total: "
            + dao.countPrescriptions()
            + " prescriptions");
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.add(lbl);
        return footer;
    }

    // ─────────── ADD MEDICINE ROW ───────────
    private void addMedicineRow() {
        String med = medNameField.getText().trim();
        if (med.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please enter medicine name!");
            return;
        }
        medicineModel.addRow(new Object[]{
            med,
            dosageField.getText().trim(),
            frequencyBox.getSelectedItem()
                .toString(),
            durationField.getText().trim(),
            instrField.getText().trim()
        });
        medNameField.setText("");
        medNameField.requestFocus();
    }

    // ─────────── SAVE PRESCRIPTION ───────────
    private void savePrescription() {
        String patient =
            patientField.getText().trim();
        String diagnosis =
            diagnosisField.getText().trim();

        if (patient.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a patient!");
            return;
        }
        if (diagnosis.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter diagnosis!");
            return;
        }
        if (medicineModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Please add at least "
                + "one medicine!");
            return;
        }

        // Build prescription
        Prescription p = new Prescription();
        p.setPatientName(patient);
        p.setDoctorName(doctorName);
        p.setDiagnosis(diagnosis);
        p.setNotes(notesArea.getText().trim());

        // Build medicine items
        List<PrescriptionItem> items =
            new ArrayList<>();
        for (int i = 0;
                i < medicineModel.getRowCount();
                i++) {
            PrescriptionItem item =
                new PrescriptionItem();
            item.setMedicineName(
                medicineModel.getValueAt(i, 0)
                    .toString());
            item.setDosage(
                medicineModel.getValueAt(i, 1)
                    .toString());
            item.setFrequency(
                medicineModel.getValueAt(i, 2)
                    .toString());
            item.setDuration(
                medicineModel.getValueAt(i, 3)
                    .toString());
            item.setInstructions(
                medicineModel.getValueAt(i, 4)
                    .toString());
            items.add(item);
        }
        p.setItems(items);

        // Save to DB
        int savedId = dao.savePrescription(p);

        if (savedId > 0) {
            p.setId(savedId);

            // ✅ Email popup
            int choice = JOptionPane
                .showOptionDialog(
                    this,
                    "Prescription #" + savedId
                    + " saved!\n\n"
                    + "Send to patient on email?",
                    "Prescription Saved",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{
                        "Yes, Send Email",
                        "No, Skip"
                    },
                    "Yes, Send Email");

            if (choice == 0) {
                // Send email
                sendPrescriptionEmail(p, items);
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Prescription saved!\n"
                    + "ID: #" + savedId,
                    "Saved",
                    JOptionPane
                        .INFORMATION_MESSAGE);
            }

            clearForm();
            loadPrescriptions();

        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to save prescription!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────── EMAIL SEND METHOD ───────────
    private void sendPrescriptionEmail(
            Prescription p,
            List<PrescriptionItem> items) {

        // Patient email DB se fetch karo
        String email = getPatientEmail(
            p.getPatientName());

        // Email nahi mili toh manually pucho
        if (email.isEmpty()) {
            email = JOptionPane.showInputDialog(
                this,
                "Patient email not found in DB.\n"
                + "Enter patient email manually:",
                "Enter Email",
                JOptionPane.QUESTION_MESSAGE);
        }

        // Email cancel kiya
        if (email == null
                || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Email not provided. Skipped.",
                "Skipped",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!email.contains("@")
                || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                "Invalid email address!\n"
                + "Please enter a valid email.",
                "Invalid Email",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        final String finalEmail =
            email.trim();
        final Prescription finalP = p;
        final List<PrescriptionItem> finalItems =
            items;

        // Loading dialog dikhao
        JDialog loadingDialog = new JDialog(
            this, "Sending Email...", false);
        JPanel lPanel = new JPanel(
            new BorderLayout(10, 10));
        lPanel.setBorder(
            BorderFactory.createEmptyBorder(
                20, 30, 20, 30));
        JLabel lLabel = new JLabel(
            "Sending prescription email "
            + "to " + finalEmail + "...",
            SwingConstants.CENTER);
        lLabel.setFont(
            new Font("Arial", Font.PLAIN, 13));
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        lPanel.add(lLabel, BorderLayout.CENTER);
        lPanel.add(bar, BorderLayout.SOUTH);
        loadingDialog.add(lPanel);
        loadingDialog.setSize(380, 110);
        loadingDialog.setLocationRelativeTo(this);

        // Background thread mein email bhejo
        new Thread(() -> {

            // Show loading
            SwingUtilities.invokeLater(()
                -> loadingDialog.setVisible(true));

            // Email send karo
            boolean sent =
                PrescriptionEmailService.send(
                    finalEmail,
                    finalP,
                    finalItems);

            // Hide loading
            SwingUtilities.invokeLater(() -> {
                loadingDialog.dispose();

                if (sent) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Prescription email sent!\n\n"
                        + "Sent to: "
                        + finalEmail + "\n"
                        + "Prescription ID: #"
                        + finalP.getId(),
                        "Email Sent Successfully",
                        JOptionPane
                            .INFORMATION_MESSAGE);
                } else {
                    // Email fail — reason batao
                    int retry = JOptionPane
                        .showConfirmDialog(
                            this,
                            "Email sending failed!\n\n"
                            + "Possible reasons:\n"
                            + "1. Wrong Gmail password\n"
                            + "2. App Password not set\n"
                            + "3. No internet\n"
                            + "4. Wrong email address\n\n"
                            + "Check EmailConfig.java "
                            + "and try again.\n\n"
                            + "Retry?",
                            "Email Failed",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE);

                    if (retry ==
                            JOptionPane.YES_OPTION) {
                        sendPrescriptionEmail(
                            finalP, finalItems);
                    }
                }
            });

        }).start();
    }

    // ─────────── GET PATIENT EMAIL ───────────
    private String getPatientEmail(
            String patientName) {
        // First users table mein dhundo
        String sql1 =
            "SELECT email FROM users "
            + "WHERE name = ? "
            + "AND role = 'patient' "
            + "LIMIT 1";
        try (Connection con =
                 DBConnection.getConnection();
             PreparedStatement ps =
                 con.prepareStatement(sql1)) {
            ps.setString(1, patientName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String email =
                    rs.getString("email");
                if (email != null
                        && !email.isEmpty())
                    return email;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Phir patients table mein dhundo
        String sql2 =
            "SELECT email FROM patients "
            + "WHERE name = ? LIMIT 1";
        try (Connection con =
                 DBConnection.getConnection();
             PreparedStatement ps =
                 con.prepareStatement(sql2)) {
            ps.setString(1, patientName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String email =
                    rs.getString("email");
                if (email != null
                        && !email.isEmpty())
                    return email;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
    // ─────────── VIEW DETAILS ───────────
    private void viewDetails() {
        int row = prescTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,"Please select a prescription!");
            return;
        }

        int id         = (int) prescModel.getValueAt(row, 0);
        String patient = prescModel.getValueAt(row, 1).toString();
        String doctor  = prescModel.getValueAt(row, 2).toString();
        String diag    = prescModel.getValueAt(row, 3).toString();
        String date    = prescModel.getValueAt(row, 4).toString();

        // Load medicine items
        List<PrescriptionItem> items =dao.getItems(id);

        StringBuilder sb = new StringBuilder();
        sb.append("Prescription #").append(id)
          .append("\n")
          .append("═".repeat(40)).append("\n")
          .append("Patient : ").append(patient)
          .append("\n")
          .append("Doctor  : Dr. ").append(doctor)
          .append("\n")
          .append("Date    : ").append(date)
          .append("\n")
          .append("Diagnosis: ").append(diag)
          .append("\n")
          .append("═".repeat(40)).append("\n")
          .append("MEDICINES:\n")
          .append("─".repeat(40)).append("\n");

        for (int i = 0; i < items.size(); i++) {
            PrescriptionItem item = items.get(i);
            sb.append(i + 1).append(". ")
              .append(item.getMedicineName())
              .append("\n")
              .append("   Dosage    : ")
              .append(item.getDosage()).append("\n")
              .append("   Frequency : ")
              .append(item.getFrequency())
              .append("\n")
              .append("   Duration  : ")
              .append(item.getDuration())
              .append("\n")
              .append("   Note      : ")
              .append(item.getInstructions())
              .append("\n")
              .append("─".repeat(40)).append("\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(new Color(245, 245, 245));

        JOptionPane.showMessageDialog(this,
            new JScrollPane(area),
            "Prescription Details",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────── PRINT PRESCRIPTION ───────────
    private void printPrescription() {
        int row = prescTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a prescription " + "to print!");
            return;
        }

        int    id      = (int) prescModel.getValueAt(row, 0);
        String patient = prescModel.getValueAt(row, 1).toString();
        String doctor  = prescModel.getValueAt(row, 2).toString();
        String diag    = prescModel.getValueAt(row, 3).toString();
        String date    = prescModel.getValueAt(row, 4).toString();

        List<PrescriptionItem> items =dao.getItems(id);

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════╗\n")
          .append("║         eMediHub Hospital         ║\n")
          .append("║      Medical Prescription         ║\n")
          .append("╚══════════════════════════════════╝\n\n")
          .append("Rx #").append(id)
          .append("                  Date: ")
          .append(date).append("\n")
          .append("─".repeat(40)).append("\n")
          .append("Patient  : ").append(patient)
          .append("\n")
          .append("Doctor   : Dr. ").append(doctor)
          .append("\n")
          .append("Diagnosis: ").append(diag)
          .append("\n")
          .append("─".repeat(40)).append("\n\n")
          .append("Rx  (Medicines Prescribed)\n")
          .append("─".repeat(40)).append("\n\n");

        for (int i = 0; i < items.size(); i++) {
            PrescriptionItem item = items.get(i);
            sb.append(i + 1).append(". ")
              .append(item.getMedicineName())
              .append("  ")
              .append(item.getDosage()).append("\n")
              .append("   Sig: ")
              .append(item.getFrequency())
              .append(" x ")
              .append(item.getDuration())
              .append("\n")
              .append("   (")
              .append(item.getInstructions())
              .append(")\n\n");
        }

        sb.append("─".repeat(40)).append("\n")
          .append("Doctor's Signature: Dr.")
          .append(doctor).append("\n\n")
          .append("eMediHub Hospital\n")
          .append("Helpline: 1800-XXX-XXXX\n")
          .append("═".repeat(40));

        JTextArea printArea = new JTextArea(sb.toString());
        printArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        printArea.setEditable(false);

        // Print dialog
        try {
            printArea.print();
        } catch (Exception ex) {
            // If printing fails show on screen
            JOptionPane.showMessageDialog(this,
                new JScrollPane(printArea),
                "Prescription — Rx #" + id,
                JOptionPane.PLAIN_MESSAGE);
        }
    }

    // ─────────── LOAD + POPULATE ───────────
    private void loadPrescriptions() {
        // If doctor role — show only their own
        if (role.equals("doctor")) {
            populateTable(dao.getByDoctor(doctorName));
        } else {
            // Admin sees all
            populateTable(dao.getAllPrescriptions());
        }
    }

    private void populateTable(
            List<Prescription> list) {
        prescModel.setRowCount(0);
        for (Prescription p : list) {
            prescModel.addRow(new Object[]{
                p.getId(),
                p.getPatientName(),
                p.getDoctorName(),
                p.getDiagnosis(),
                p.getCreatedAt()
            });
        }
    }

    private void clearForm() {
        patientField.setText("");
        diagnosisField.setText("");
        notesArea.setText("");
        medicineModel.setRowCount(0);
        medNameField.setText("");
        dosageField.setText("500mg");
        durationField.setText("5 days");
        instrField.setText("Take with water");
        frequencyBox.setSelectedIndex(0);
    }
}
