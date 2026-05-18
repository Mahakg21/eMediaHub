package com.emediHub.ui;

import com.emediHub.blood.*;
import com.emediHub.db.DBConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public class BloodDonorPanel extends JFrame {

    private BloodDonorDao donorDAO = new BloodDonorDao();

    private JTable donorTable;
    private DefaultTableModel tableModel;

    // Add form fields
    private JTextField dName, dPhone, dCity;
    private JComboBox<String> dBloodGroup, dAvailable;

    // Search fields
    private JComboBox<String> searchBloodGroup;
    private JTextField searchCity;

    private static final String[] BLOOD_GROUPS = { "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-" };

    public BloodDonorPanel() {
        setTitle("eMediHub — Blood Donor Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        loadDonors();
    }

    // ───────── TOP PANEL (Add + Search) ─────────
    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new GridLayout(1, 2, 15, 0));
        top.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        top.add(buildAddForm());
        top.add(buildSearchPanel());
        return top;
    }

    private JPanel buildAddForm() {
        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Register New Donor"));

        dName       = new JTextField();
        dPhone      = new JTextField();
        dCity       = new JTextField();
        dBloodGroup = new JComboBox<>(BLOOD_GROUPS);
        dAvailable  = new JComboBox<>(new String[]{"Yes", "No"});

        form.add(new JLabel("Name:"));        form.add(dName);
        form.add(new JLabel("Blood Group:")); form.add(dBloodGroup);
        form.add(new JLabel("Phone:"));       form.add(dPhone);
        form.add(new JLabel("City:"));        form.add(dCity);
        form.add(new JLabel("Available:"));   form.add(dAvailable);

        JButton addBtn = new JButton("Register Donor");
        addBtn.setBackground(new Color(192, 57, 43));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 13));
        addBtn.addActionListener(e -> addDonor());
        form.add(addBtn);

        return form;
    }

    private JPanel buildSearchPanel() {
        JPanel search = new JPanel(new GridLayout(3, 2, 8, 8));
        search.setBorder(BorderFactory.createTitledBorder("Search Donors"));

        searchBloodGroup = new JComboBox<>(BLOOD_GROUPS);
        searchCity       = new JTextField();

        search.add(new JLabel("Search by Blood Group:"));
        search.add(searchBloodGroup);

        JButton searchBGBtn = new JButton("Find by Blood Group");
        searchBGBtn.setBackground(new Color(142, 68, 173));
        searchBGBtn.setForeground(Color.WHITE);
        searchBGBtn.addActionListener(e -> searchByBloodGroup());
        search.add(searchBGBtn);

        search.add(new JLabel("Search by City:"));
        search.add(searchCity);

        JButton searchCityBtn = new JButton("Find by City");
        searchCityBtn.setBackground(new Color(41, 128, 185));
        searchCityBtn.setForeground(Color.WHITE);
        searchCityBtn.addActionListener(e -> searchByCity());
        search.add(searchCityBtn);

        return search;
    }

    // ───────── TABLE PANEL ─────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        String[] cols = {"ID", "Name", "Blood Group", "Phone", "City", "Available"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        donorTable = new JTable(tableModel);
        donorTable.setRowHeight(28);
        donorTable.setFont(new Font("Arial", Font.PLAIN, 13));
        donorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Color available column
        donorTable.getColumnModel().getColumn(5)
            .setCellRenderer(new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,boolean foc, int r, int c) {
                    super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                    String val = v != null ? v.toString() : "";
                    setForeground(val.equals("Yes") ? new Color(39, 174, 96) : new Color(231, 76, 60));
                    setFont(new Font("Arial", Font.BOLD, 13));
                    setText(val.equals("Yes") ? "✔ Available" : "✘ Not Available");
                    return this;
                }
            });

        panel.add(new JScrollPane(donorTable), BorderLayout.CENTER);
        return panel;
    }

    // ───────── BOTTOM PANEL (Buttons + Stats) ─────────
    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        // Action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton deleteBtn  = new JButton("Delete Selected");
        JButton toggleBtn  = new JButton("Toggle Availability");
        JButton refreshBtn = new JButton("Show All");
        JButton statsBtn   = new JButton("Blood Group Stats");

        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        toggleBtn.setBackground(new Color(243, 156, 18));
        toggleBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);
        statsBtn.setBackground(new Color(26, 188, 156));
        statsBtn.setForeground(Color.WHITE);

        btnPanel.add(deleteBtn);
        btnPanel.add(toggleBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(statsBtn);

        deleteBtn.addActionListener(e  -> deleteDonor());
        toggleBtn.addActionListener(e  -> toggleAvailability());
        refreshBtn.addActionListener(e -> loadDonors());
        statsBtn.addActionListener(e   -> showStats());

        bottom.add(btnPanel, BorderLayout.CENTER);
        return bottom;
    }

    // ───────── ACTIONS ─────────
    private void addDonor() {
        if (dName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!");
            return;
        }
        if (dPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone is required!");
            return;
        }

        BloodDonor donor = new BloodDonor();
        donor.setName(dName.getText().trim());
        donor.setBloodGroup(dBloodGroup.getSelectedItem().toString());
        donor.setPhone(dPhone.getText().trim());
        donor.setCity(dCity.getText().trim());
        donor.setAvailable(dAvailable.getSelectedItem().toString().equals("Yes"));

        if (donorDAO.addDonor(donor)) {
            JOptionPane.showMessageDialog(this,"Donor registered successfully!");
            clearFields();
            loadDonors();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register donor.");
        }
    }

    private void deleteDonor() {
        int row = donorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,"Please select a donor to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this, "Delete this donor?", "Confirm",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (donorDAO.deleteDonor(id)) {
                JOptionPane.showMessageDialog(this, "Donor deleted!");
                loadDonors();
            }
        }
    }

    private void toggleAvailability() {
        int row = donorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a donor.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        // ✅ DB se actual status fetch karo
        // table value pe depend mat karo
        boolean currentStatus =getActualStatus(id);
        boolean newStatus = !currentStatus;

        if (donorDAO.updateAvailability(id, newStatus)) {
            JOptionPane.showMessageDialog(
                this,
                "Status updated to: "
                + (newStatus
                    ? "Available"
                    : "Not Available"));
            loadDonors();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to update status!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ✅ DB se actual boolean value fetch karo
    private boolean getActualStatus(int id) {
        String sql =
            "SELECT available "
            + "FROM blood_donors "
            + "WHERE id = ?";
        try (Connection con =DBConnection.getConnection();
             PreparedStatement ps =con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getBoolean("available");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void searchByBloodGroup() {
        String bg = searchBloodGroup.getSelectedItem().toString();
        List<BloodDonor> list = donorDAO.searchByBloodGroup(bg);
        populateTable(list);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available donors found for blood group: " + bg);
        }
    }

    private void searchByCity() {
        String city = searchCity.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a city name.");
            return;
        }
        List<BloodDonor> list = donorDAO.searchByCity(city);
        populateTable(list);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this,"No donors found in city: " + city);
        }
    }

    private void showStats() {
        Map<String, Integer> stats = donorDAO.countByBloodGroup();
        if (stats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No donor data found.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== Blood Group Statistics ===\n\n");
        stats.forEach((bg, count) ->
            sb.append(String.format("  %-6s : %d donors\n", bg, count))
        );
        sb.append("\nTotal donors: ").append(donorDAO.countDonors());
        JOptionPane.showMessageDialog(this, sb.toString(), "Blood Group Stats", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadDonors() {
        populateTable(donorDAO.getAllDonors());
    }

    private void populateTable(List<BloodDonor> list) {
        tableModel.setRowCount(0);
        for (BloodDonor d : list) {
            tableModel.addRow(new Object[]{
                d.getId(),
                d.getName(),
                d.getBloodGroup(),
                d.getPhone(),
                d.getCity(),
                d.isAvailable() ? "Yes" : "No"
            });
        }
    }

    private void clearFields() {
        dName.setText("");
        dPhone.setText("");
        dCity.setText("");
        dBloodGroup.setSelectedIndex(0);
        dAvailable.setSelectedIndex(0);
    }
}