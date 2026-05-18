package com.emediHub.ui;

import com.emediHub.hospital.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class HospitalPanel extends JFrame {

    private PatientDao patientDAO = new PatientDao();
    private DoctorDao  doctorDAO  = new DoctorDao();
    // Patient table
    private JTable patientTable;
    private DefaultTableModel patientModel;
    // Doctor table
    private JTable doctorTable;
    private DefaultTableModel doctorModel;

    // Patient fields
    private JTextField pName, pAge, pPhone, pDisease;
    private JComboBox<String> pGender;

    // Doctor fields
    private JTextField dName, dSpec, dPhone, dEmail;

    public HospitalPanel() {
        setTitle("eMediHub — Hospital Management");
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 14));
        tabs.addTab("🧑‍⚕ Patients", buildPatientPanel());
        tabs.addTab("👨‍⚕ Doctors",  buildDoctorPanel());

        add(tabs);
        loadPatients();
        loadDoctors();
    }

    // ───────────────── PATIENT PANEL ─────────────────
    private JPanel buildPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form
        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Add New Patient"));

        pName    = new JTextField();
        pAge     = new JTextField();
        pGender  = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        pPhone   = new JTextField();
        pDisease = new JTextField();

        form.add(new JLabel("Name:"));       form.add(pName);
        form.add(new JLabel("Age:"));        form.add(pAge);
        form.add(new JLabel("Gender:"));     form.add(pGender);
        form.add(new JLabel("Phone:"));      form.add(pPhone);
        form.add(new JLabel("Disease:"));    form.add(pDisease);

        JButton addBtn = new JButton("Add Patient");
        addBtn.setBackground(new Color(52, 152, 219));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 13));
        form.add(addBtn);

        JTextField searchField = new JTextField();
        form.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(39, 174, 96));
        searchBtn.setForeground(Color.WHITE);
        form.add(searchBtn);

        // Table
        String[] cols = {"ID", "Name", "Age", "Gender", "Phone", "Disease"};
        patientModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        patientTable = new JTable(patientModel);
        patientTable.setRowHeight(28);
        patientTable.setFont(new Font("Arial", Font.PLAIN, 13));
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        JScrollPane scroll = new JScrollPane(patientTable);

        // Bottom buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        panel.add(form,     BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // Actions
        addBtn.addActionListener(e -> addPatient());
        deleteBtn.addActionListener(e -> deletePatient());
        refreshBtn.addActionListener(e -> loadPatients());
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            if (kw.isEmpty()) { 
            	loadPatients(); 
            	return; 
            	}
            patientModel.setRowCount(0);
            for (Patient p : patientDAO.searchPatient(kw)) {
                patientModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getAge(),
                    p.getGender(), p.getPhone(), p.getDisease()
                });
            }
        });

        return panel;
    }

    // ───────────────── DOCTOR PANEL ─────────────────
    private JPanel buildDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Add New Doctor"));

        dName  = new JTextField();
        dSpec  = new JTextField();
        dPhone = new JTextField();
        dEmail = new JTextField();

        form.add(new JLabel("Name:"));           form.add(dName);
        form.add(new JLabel("Specialization:")); form.add(dSpec);
        form.add(new JLabel("Phone:"));          form.add(dPhone);
        form.add(new JLabel("Email:"));          form.add(dEmail);

        JButton addBtn = new JButton("Add Doctor");
        addBtn.setBackground(new Color(52, 152, 219));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 13));
        form.add(addBtn);

        String[] cols = {"ID", "Name", "Specialization", "Phone", "Email"};
        doctorModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        doctorTable = new JTable(doctorModel);
        doctorTable.setRowHeight(28);
        doctorTable.setFont(new Font("Arial", Font.PLAIN, 13));
        doctorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        JScrollPane scroll = new JScrollPane(doctorTable);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton deleteBtn  = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        panel.add(form,     BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addDoctor());
        deleteBtn.addActionListener(e -> deleteDoctor());
        refreshBtn.addActionListener(e -> loadDoctors());

        return panel;
    }

    // ───────────────── ACTIONS ─────────────────
    private void addPatient() {
        try {
            if (pName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required!");
                return;
            }
            Patient p = new Patient();
            p.setName(pName.getText().trim());
            p.setAge(Integer.parseInt(pAge.getText().trim()));
            p.setGender(pGender.getSelectedItem().toString());
            p.setPhone(pPhone.getText().trim());
            p.setDisease(pDisease.getText().trim());

            if (patientDAO.addPatient(p)) {
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
                clearPatientFields();
                loadPatients();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add patient.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number!");
        }
    }

    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.");
            return;
        }
        int id = (int) patientModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this, "Delete this patient?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (patientDAO.deletePatient(id)) {
                JOptionPane.showMessageDialog(this, "Patient deleted!");
                loadPatients();
            }
        }
    }

    private void addDoctor() {
        if (dName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!");
            return;
        }
        Doctor d = new Doctor();
        d.setName(dName.getText().trim());
        d.setSpecialization(dSpec.getText().trim());
        d.setPhone(dPhone.getText().trim());
        d.setEmail(dEmail.getText().trim());

        if (doctorDAO.addDoctor(d)) {
            JOptionPane.showMessageDialog(this, "Doctor added successfully!");
            clearDoctorFields();
            loadDoctors();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add doctor.");
        }
    }

    private void deleteDoctor() {
        int row = doctorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.");
            return;
        }
        int id = (int) doctorModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this, "Delete this doctor?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorDAO.deleteDoctor(id)) {
                JOptionPane.showMessageDialog(this, "Doctor deleted!");
                loadDoctors();
            }
        }
    }

    private void loadPatients() {
        patientModel.setRowCount(0);
        for (Patient p : patientDAO.getAllPatients()) {
            patientModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getAge(),
                p.getGender(), p.getPhone(), p.getDisease()
            });
        }
    }

    private void loadDoctors() {
        doctorModel.setRowCount(0);
        for (Doctor d : doctorDAO.getAllDoctors()) {
            doctorModel.addRow(new Object[]{
                d.getId(), d.getName(),
                d.getSpecialization(), d.getPhone(), d.getEmail()
            });
        }
    }

    private void clearPatientFields() {
        pName.setText(""); pAge.setText("");
        pPhone.setText(""); pDisease.setText("");
        pGender.setSelectedIndex(0);
    }

    private void clearDoctorFields() {
        dName.setText(""); dSpec.setText("");
        dPhone.setText(""); dEmail.setText("");
    }
}