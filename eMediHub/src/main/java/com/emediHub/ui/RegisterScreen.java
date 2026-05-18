package com.emediHub.ui;

import com.emediHub.db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterScreen extends JFrame {

    public RegisterScreen() {
        setTitle("eMediHub — Create New Account");
        setSize(520, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(10, 15, 10, 15);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        // Title
        JLabel title = new JLabel("Create New Account");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(39, 174, 96));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        add(title, g);

        JLabel sub = new JLabel("Register yourself to access eMediHub");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 1;
        add(sub, g);

        // Separator
        JSeparator sep = new JSeparator();
        g.gridy = 2;
        add(sep, g);

        // Full Name
        g.gridwidth = 1; g.gridy = 3; g.gridx = 0;
        add(makeLabel("Full Name: *"), g);
        JTextField nameField = new JTextField(18);
        g.gridx = 1;
        add(nameField, g);

        // Username
        g.gridy = 4; g.gridx = 0;
        add(makeLabel("Username: *"), g);
        JTextField userField = new JTextField(18);
        g.gridx = 1;
        add(userField, g);

        // Password
        g.gridy = 5; g.gridx = 0;
        add(makeLabel("Password: *"), g);
        JPasswordField passField = new JPasswordField(18);
        g.gridx = 1;
        add(passField, g);

        // Confirm Password
        g.gridy = 6; g.gridx = 0;
        add(makeLabel("Confirm Password: *"), g);
        JPasswordField confirmField = new JPasswordField(18);
        g.gridx = 1;
        add(confirmField, g);

        // Role
        g.gridy = 7; g.gridx = 0;
        add(makeLabel("Register as: *"), g);
        JComboBox<String> roleBox = new JComboBox<>(
            new String[]{"patient", "doctor"});
        roleBox.setFont(new Font("Arial", Font.PLAIN, 14));
        g.gridx = 1;
        add(roleBox, g);

        // Phone
        g.gridy = 8; g.gridx = 0;
        add(makeLabel("Phone Number:"), g);
        JTextField phoneField = new JTextField(18);
        g.gridx = 1;
        add(phoneField, g);

        // Email
        g.gridy = 9; g.gridx = 0;
        add(makeLabel("Email Address:"), g);
        JTextField emailField = new JTextField(18);
        g.gridx = 1;
        add(emailField, g);

        // Age (for patients)
        g.gridy = 10; g.gridx = 0;
        add(makeLabel("Age (patients only):"), g);
        JTextField ageField = new JTextField(18);
        g.gridx = 1;
        add(ageField, g);

        // Gender (for patients)
        g.gridy = 11; g.gridx = 0;
        add(makeLabel("Gender (patients only):"), g);
        JComboBox<String> genderBox = new JComboBox<>(
            new String[]{"Male", "Female", "Other"});
        genderBox.setFont(new Font("Arial", Font.PLAIN, 13));
        g.gridx = 1;
        add(genderBox, g);

        // Password strength label
        JLabel strengthLabel = new JLabel(" ");
        strengthLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        g.gridy = 12; g.gridx = 0; g.gridwidth = 2;
        add(strengthLabel, g);

        // Register Button
        JButton registerBtn = new JButton("Register Now");
        registerBtn.setBackground(new Color(39, 174, 96));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 15));
        registerBtn.setPreferredSize(new Dimension(220, 42));
        g.gridy = 13;
        add(registerBtn, g);

        // Back to login
        JButton backBtn = new JButton("Already have an account? Login");
        backBtn.setBackground(new Color(52, 152, 219));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        g.gridy = 14;
        add(backBtn, g);

        // Message
        JLabel msg = new JLabel(" ");
        msg.setFont(new Font("Arial", Font.PLAIN, 12));
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 15;
        add(msg, g);

        // Password strength checker
        passField.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    checkStrength();
                }
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    checkStrength();
                }
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    checkStrength();
                }
                private void checkStrength() {
                    String p = new String(passField.getPassword());
                    if (p.length() == 0) {
                        strengthLabel.setText(" ");
                    } else if (p.length() < 6) {
                        strengthLabel.setText("Password strength: Weak");
                        strengthLabel.setForeground(new Color(231, 76, 60));
                    } else if (p.length() < 10) {
                        strengthLabel.setText("Password strength: Medium");
                        strengthLabel.setForeground(new Color(243, 156, 18));
                    } else {
                        strengthLabel.setText("Password strength: Strong");
                        strengthLabel.setForeground(new Color(39, 174, 96));
                    }
                }
            });

        // Register action
        registerBtn.addActionListener(e -> {
            String name    = nameField.getText().trim();
            String user    = userField.getText().trim();
            String pass    = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());
            String role    = roleBox.getSelectedItem().toString();
            String phone   = phoneField.getText().trim();
            String email   = emailField.getText().trim();
            String age     = ageField.getText().trim();
            String gender  = genderBox.getSelectedItem().toString();

            // Validation
            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                showError(msg,"Name, username and password are required!");
                return;
            }
            if (user.length() < 4) {
                showError(msg,"Username must be at least 4 characters!");
                return;
            }
            if (pass.length() < 6) {
                showError(msg,"Password must be at least 6 characters!");
                return;
            }
            if (!pass.equals(confirm)) {
                showError(msg, "Passwords do not match!");
                return;
            }
            if (!email.isEmpty() && !email.contains("@")) {
                showError(msg,"Please enter a valid email address!");
                return;
            }

            // Register user
            registerUser(name, user, pass, role,phone, email, age, gender, msg);
        });

        backBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });
    }

    private void registerUser(String name, String username,
            String password, String role, String phone,
            String email, String age, String gender,
            JLabel msg) {

        // Check if username already exists
        String checkSql ="SELECT id FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement check =con.prepareStatement(checkSql)) {

            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                showError(msg,
                    "Username already taken!"
                    + " Please choose another.");
                return;
            }

            // Insert into users table
            String insertSql =
                "INSERT INTO users(name, username, "
                + "password, role, phone, email) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps =con.prepareStatement(insertSql,Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.setString(5, phone);
            ps.setString(6, email);
            ps.executeUpdate();

            // If patient — also insert into patients table
            if (role.equals("patient") && !age.isEmpty()) {
                ResultSet keys = ps.getGeneratedKeys();
                String patientSql =
                    "INSERT INTO patients"
                    + "(name, age, gender, phone, disease) "
                    + "VALUES (?, ?, ?, ?, 'Not specified')";
                PreparedStatement pp =con.prepareStatement(patientSql);
                pp.setString(1, name);
                pp.setInt   (2, Integer.parseInt(age));
                pp.setString(3, gender);
                pp.setString(4, phone);
                pp.executeUpdate();
            }

            // If doctor — also insert into doctors table
            if (role.equals("doctor")) {
                String doctorSql =
                    "INSERT INTO doctors"
                    + "(name, specialization, phone, email) "
                    + "VALUES (?, 'General', ?, ?)";
                PreparedStatement dp =con.prepareStatement(doctorSql);
                dp.setString(1, name);
                dp.setString(2, phone);
                dp.setString(3, email);
                dp.executeUpdate();
            }

            // Success
            JOptionPane.showMessageDialog(this,
                "Account created successfully!\n"
                + "You can now login with your username: "
                + username,
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginScreen().setVisible(true);

        } catch (SQLException ex) {
            showError(msg, "DB Error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            showError(msg, "Age must be a valid number!");
        }
    }

    private void showError(JLabel msg, String text) {
        msg.setText(text);
        msg.setForeground(new Color(231, 76, 60));
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        return lbl;
    }
}
