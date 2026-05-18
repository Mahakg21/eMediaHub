package com.emediHub.ui;

import com.emediHub.db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("eMediHub — Login");
        setSize(480, 490);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 15, 10, 15);

        // Title
        JLabel title = new JLabel("eMediHub");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(new Color(52, 152, 219));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        add(title, g);

      /*  JLabel sub = new JLabel("Hospital & Blood Donation System");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        g.gridy = 1;
        add(sub, g);   */

        // Role
        g.gridy = 2; g.gridwidth = 1; g.gridx = 0;
        add(new JLabel("Role:"), g);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"patient", "doctor", "admin"});
        roleBox.setFont(new Font("Arial", Font.PLAIN, 14));
        g.gridx = 1;
        add(roleBox, g);

        // Username
        g.gridy = 3; g.gridx = 0;
        add(new JLabel("Username:"), g);
        JTextField userField = new JTextField(16);
        g.gridx = 1;
        add(userField, g);

        // Password
        g.gridy = 4; g.gridx = 0;
        add(new JLabel("Password:"), g);
        JPasswordField passField = new JPasswordField(16);
        g.gridx = 1;
        add(passField, g);

        // Login Button
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(52, 152, 219));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 15));
        loginBtn.setPreferredSize(new Dimension(200, 40));
        g.gridy = 5; g.gridx = 0; g.gridwidth = 2;
        add(loginBtn, g);

        // Divider label
        JLabel orLabel = new JLabel( "────────  New user?  ────────");
        orLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        orLabel.setForeground(new Color(150, 150, 150));
        orLabel.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 6;
        add(orLabel, g);

        // Register Button
        JButton registerBtn = new JButton( "Create New Account");
        registerBtn.setBackground(new Color(39, 174, 96));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 15));
        registerBtn.setPreferredSize(new Dimension(200, 40));
        g.gridy = 7;
        add(registerBtn, g);

        // Message label
        JLabel msg = new JLabel(" ");
        msg.setForeground(Color.RED);
        msg.setFont(new Font("Arial", Font.PLAIN, 12));
        g.gridy = 8;
        add(msg, g);
                
        // Actions
        loginBtn.addActionListener(e -> {
            String role = roleBox.getSelectedItem().toString();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                msg.setText("Please enter username and password!");
                return;
            }
            loginUser(role, user, pass, msg);
        });

        passField.addActionListener(e -> loginBtn.doClick());

        registerBtn.addActionListener(e -> {
            dispose();
           
            new RegisterScreen().setVisible(true);
        });
    }

    private void loginUser(String role, String username,String password, JLabel msg) {
        String sql = "SELECT * FROM users " + "WHERE username=? AND password=? AND role=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int    id   = rs.getInt("id");
                dispose();
                openDashboard(role, name, id);
            } else {
                msg.setText("Invalid credentials or wrong role selected!");
            }
        } catch (SQLException ex) {
            msg.setText("DB Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void openDashboard(String role, String name, int userId) {
        switch (role) {
            case "admin":
                new DashboardScreen(name).setVisible(true);
                break;
            case "doctor":
                new DoctorDashboard(name, userId).setVisible(true);
                break;
            case "patient":
                new PatientDashboard(name, userId).setVisible(true);
                break;
        }
    }
}