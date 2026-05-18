package com.emediHub.ui;

import com.emediHub.db.DBConnection;
import com.emediHub.history.MedicalHistory;
import com.emediHub.history.MedicalHistoryDao;
import com.emediHub.lab.LabReport;
import com.emediHub.lab.LabReportDao;
import com.emediHub.prescription.Prescription;
import com.emediHub.prescription.PrescriptionDao;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class PatientDashboard extends JFrame {

    private String patientName;
    private int userId;

    public PatientDashboard(String patientName, int userId) {
        this.patientName = patientName;
        this.userId = userId;

        setTitle("eMediHub — Patient Panel | " + patientName);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(142, 68, 173));
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("  Welcome, " + patientName);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 13));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });
        header.add(logoutBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));
        tabs.addTab("My Records",       buildMyRecordsTab());
        tabs.addTab("Book Appointment", buildAppointmentTab());
        tabs.addTab("Blood Donors",     buildBloodTab());
        tabs.addTab("Chatbot",          buildChatTab());
        tabs.addTab("💳 Payments",      buildPaymentTab());
        tabs.addTab("My Prescriptions", buildMyPrescTab());
        tabs.addTab("My Medical History",buildMyHistoryTab());
        tabs.addTab("My Lab Reports", buildMyLabTab());
        
        JPanel body = new JPanel(new BorderLayout());
        body.add(tabs);
        return body;
    }

    // Show patient's own record
    private JPanel buildMyRecordsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 12));
        infoPanel.setBorder(BorderFactory.createTitledBorder("My Medical Record"));

        JLabel nameLbl    = new JLabel("-");
        JLabel ageLbl     = new JLabel("-");
        JLabel genderLbl  = new JLabel("-");
        JLabel phoneLbl   = new JLabel("-");
        JLabel diseaseLbl = new JLabel("-");

        Font vFont = new Font("Arial", Font.BOLD, 14);
        nameLbl.setFont(vFont);
        ageLbl.setFont(vFont);
        genderLbl.setFont(vFont);
        phoneLbl.setFont(vFont);
        diseaseLbl.setFont(vFont);

        infoPanel.add(new JLabel("Name:"));    infoPanel.add(nameLbl);
        infoPanel.add(new JLabel("Age:"));     infoPanel.add(ageLbl);
        infoPanel.add(new JLabel("Gender:"));  infoPanel.add(genderLbl);
        infoPanel.add(new JLabel("Phone:"));   infoPanel.add(phoneLbl);
        infoPanel.add(new JLabel("Disease:")); infoPanel.add(diseaseLbl);

        // Load patient record by matching name
        String sql = "SELECT * FROM patients WHERE name = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patientName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameLbl.setText(rs.getString("name"));
                ageLbl.setText(String.valueOf(rs.getInt("age")));
                genderLbl.setText(rs.getString("gender"));
                phoneLbl.setText(rs.getString("phone"));
                diseaseLbl.setText(rs.getString("disease"));
            } else {
                nameLbl.setText(patientName + " (not registered yet)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panel.add(infoPanel, BorderLayout.NORTH);
        return panel;
    }

    // Book appointment tab
    private JPanel buildAppointmentTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 12));
        form.setBorder(BorderFactory.createTitledBorder("Book New Appointment"));

        JComboBox<String> doctorBox = new JComboBox<>();
        JTextField dateField = new JTextField(java.time.LocalDate.now().toString());

        // Load doctors into combo box
        String sql = "SELECT id, name, specialization FROM doctors";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                doctorBox.addItem(
                    rs.getInt("id") + " - "
                    + rs.getString("name")
                    + " (" + rs.getString("specialization") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        form.add(new JLabel("Select Doctor:")); form.add(doctorBox);
        form.add(new JLabel("Date (YYYY-MM-DD):")); form.add(dateField);

        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.setBackground(new Color(52, 152, 219));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Arial", Font.BOLD, 13));
        form.add(new JLabel("")); form.add(bookBtn);

        // Appointments table
        String[] cols = {"ID", "Doctor", "Date", "Status"};
        DefaultTableModel apptModel =new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
        JTable apptTable = new JTable(apptModel);
        apptTable.setRowHeight(28);

        // Load existing appointments
        loadAppointments(apptModel);

        bookBtn.addActionListener(e -> {
            String selected = (String) doctorBox.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "No doctors available!");
                return;
            }
            int doctorId = Integer.parseInt(selected.split(" - ")[0]);
            String date = dateField.getText().trim();
            String bookSql =
                "INSERT INTO appointments"
                + "(patient_id, doctor_id, date, status) "
                + "VALUES (?, ?, ?, 'Pending')";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps =con.prepareStatement(bookSql)) {
                ps.setInt   (1, userId);
                ps.setInt   (2, doctorId);
                ps.setString(3, date);
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this,"Appointment booked successfully!");
                    loadAppointments(apptModel);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(apptTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadAppointments(DefaultTableModel model) {
        model.setRowCount(0);
        String sql =
            "SELECT a.id, d.name, a.date, a.status "
            + "FROM appointments a "
            + "JOIN doctors d ON a.doctor_id = d.id "
            + "WHERE a.patient_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt   ("id"),
                    rs.getString("name"),
                    rs.getString("date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Blood donor search
    private JPanel buildBloodTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        String[] groups = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};
        JComboBox<String> bgBox = new JComboBox<>(groups);
        JButton searchBtn = new JButton("Find Donors");
        searchBtn.setBackground(new Color(192, 57, 43));
        searchBtn.setForeground(Color.WHITE);
        searchPanel.add(new JLabel("Blood Group:"));
        searchPanel.add(bgBox);
        searchPanel.add(searchBtn);

        String[] cols = {"Name", "Blood Group", "Phone", "City"};
        DefaultTableModel model =new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        table.setRowHeight(28);

        searchBtn.addActionListener(e -> {
            String bg = bgBox.getSelectedItem().toString();
            model.setRowCount(0);
            String sql =
                "SELECT name, blood_group, phone, city "
                + "FROM blood_donors "
                + "WHERE blood_group=? AND available=TRUE";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps =con.prepareStatement(sql)) {
                ps.setString(1, bg);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getString("phone"),
                        rs.getString("city")
                    });
                }
                if (model.getRowCount() == 0)
                    JOptionPane.showMessageDialog(this,"No donors found for " + bg);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        panel.add(searchPanel,  BorderLayout.NORTH);
        panel.add(new JScrollPane(table),   BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildChatTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton openChat = new JButton("Open Chatbot");
        openChat.setFont(new Font("Arial", Font.BOLD, 15));
        openChat.setBackground(new Color(52, 152, 219));
        openChat.setForeground(Color.WHITE);
        openChat.addActionListener(e ->
            new ChatBotPanel().setVisible(true));
        panel.add(openChat, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(44, 62, 80));
        JLabel lbl = new JLabel("  eMediHub — Patient Portal");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.add(lbl);
        return footer;
    }
    
    private JPanel buildPaymentTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton openBtn = new JButton("Open Payment Center");
        openBtn.setFont(new Font("Arial", Font.BOLD, 15));
        openBtn.setBackground(new Color(142, 68, 173));
        openBtn.setForeground(Color.WHITE);
        openBtn.addActionListener(e ->
            new PaymentPanel(patientName).setVisible(true));
        panel.add(openBtn, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel buildMyPrescTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"ID", "Doctor","Diagnosis", "Date"};
        DefaultTableModel model =new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        // Load patient's own prescriptions
        PrescriptionDao dao = new PrescriptionDao();
        for (Prescription p :dao.getByPatient(patientName)) {
            model.addRow(new Object[]{
                p.getId(),
                p.getDoctorName(),
                p.getDiagnosis(),
                p.getCreatedAt()
            });
        }

        panel.add(new JScrollPane(table),BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel buildMyHistoryTab() {
        JPanel panel = new JPanel(
            new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {
            "ID", "Doctor",
            "Visit Date", "Diagnosis",
            "Treatment", "Follow Up"};
        DefaultTableModel model =
            new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(44,62,80));
        table.getTableHeader().setForeground(Color.WHITE);

        // Load patient's history
        MedicalHistoryDao histDao =new MedicalHistoryDao();
        for (MedicalHistory h :histDao.getByPatient(patientName)) {
            model.addRow(new Object[]{
                h.getId(),
                "Dr. " + h.getDoctorName(),
                h.getVisitDate(),
                h.getDiagnosis(),
                h.getTreatment(),
                (h.getFollowUpDate() != null
                    && !h.getFollowUpDate()
                        .isEmpty())
                    ? h.getFollowUpDate()
                    : "Not scheduled"
            });
        }

        JLabel infoLbl = new JLabel("  Your complete visit history");
        infoLbl.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLbl.setForeground(new Color(100, 100, 100));

        panel.add(new JScrollPane(table),BorderLayout.CENTER);
        panel.add(infoLbl, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel buildMyLabTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"ID", "Test Name","Result", "Unit","Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);

        // Status column color
        table.getColumnModel().getColumn(4).setCellRenderer( new DefaultTableCellRenderer(){
                public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                    super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                    setHorizontalAlignment(CENTER);
                    setFont(new Font("Arial", Font.BOLD, 12));
                    String s = v != null ? v.toString() : "";
                    switch (s) {
                        case "Normal":
                            setForeground( new Color(39,174,96));
                            break;
                        case "Abnormal":
                        case "Critical":
                            setForeground(new Color(231,76,60));
                            break;
                        case "Borderline":
                            setForeground(new Color(243,156,18));
                            break;
                    }
                    return this;
                }
            });

        // Load patient's own lab reports
        LabReportDao labDao = new LabReportDao();
        for (LabReport r : labDao.getByPatient(patientName)) {
            model.addRow(new Object[]{
                r.getId(),
                r.getTestName(),
                r.getResult(),
                r.getUnit().isEmpty()
                    ? "-" : r.getUnit(),
                r.getStatus(),
                r.getTestDate()
            });
        }

        JLabel infoLbl = new JLabel("  Your lab reports — read only");
        infoLbl.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLbl.setForeground(new Color(100, 100, 100));

        panel.add(new JScrollPane(table),BorderLayout.CENTER);
        panel.add(infoLbl,BorderLayout.SOUTH);
        return panel;
    }
    
}
