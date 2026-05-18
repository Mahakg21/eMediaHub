package com.emediHub.ui;

import javax.swing.*;
import java.awt.*;

public class DashboardScreen extends JFrame {

    private String adminName;

    public DashboardScreen(String adminName) {
        this.adminName = adminName;

        setTitle("eMediHub — Admin Dashboard | " + adminName);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel( "  eMediHub Dashboard  |  Welcome, " + adminName, JLabel.LEFT);
        header.setFont( new Font("Arial", Font.BOLD, 22));
        header.setForeground( new Color(100, 180, 255));
        header.setPreferredSize( new Dimension(1000, 60));
        add(header, BorderLayout.NORTH);

        // GridLayout(3, 3) karo — 9 slots
        JPanel center = new JPanel(new GridLayout(3, 3, 20, 20));
        center.setBorder( BorderFactory.createEmptyBorder( 30, 40, 30, 40));

        // Row 1
        center.add(makeModuleButton("🏥  Hospital Management",new Color(52, 120, 200),"hospital"));
        center.add(makeModuleButton("🩸  Blood Donation", new Color(200, 60, 60), "blood"));
        center.add(makeModuleButton("💬  Chatbot",new Color(60, 160, 100), "chatbot"));
        // Row 2
        center.add(makeModuleButton( "📊  Admin Panel", new Color(140, 80, 200), "admin"));
        center.add(makeModuleButton("💳  Payment Center",new Color(142, 68, 173),"payment"));
        center.add(makeModuleButton("📋  Prescription",new Color(22, 160, 133),"prescription"));
        add(center, BorderLayout.CENTER);
       
        center.add(makeModuleButton("📋  Medical History",new Color(41, 128, 185),"history"));
        center.add(makeModuleButton("Lab Reports", new Color(41, 128, 185),"lab"));
        center.add(makeModuleButton("Appointments",new Color(22, 160, 133),"appointments"));
        
        // Footer
        JLabel footer = new JLabel( "  eMediHub v1.0  |  " + "Developed by Mahak Gupta",  JLabel.LEFT);
        footer.setFont( new Font("Arial", Font.PLAIN, 12));
        footer.setPreferredSize( new Dimension(1000, 30));
        add(footer, BorderLayout.SOUTH);
    }

    // ✅ String type use kiya — null problem fix
    private JButton makeModuleButton(String text,Color color,String type) {

        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e ->
            openModule(type));

        return btn;
    }

    // ✅ Switch se har module handle hoga
    private void openModule(String type) {
        try {
            switch (type) {
                case "hospital":
                    new HospitalPanel().setVisible(true);
                    break;

                case "blood":
                    new BloodDonorPanel().setVisible(true);
                    break;

                case "chatbot":
                    new ChatBotPanel().setVisible(true);
                    break;

                case "admin":
                    new AdminPanel().setVisible(true);
                    break;

                case "payment":
                    new PaymentPanel(adminName).setVisible(true);
                    break;

                case "prescription":
                    // ✅ Ye tha actual fix
                    new PrescriptionPanel(adminName, "admin").setVisible(true);
                    break;
                   
                case "history":
                    new MedicalHistoryPanel(adminName, "admin").setVisible(true);
                    break;
                case "lab":
                    new LabReportPanel(adminName, "admin").setVisible(true);
                    break;
                case "appointments":
                    new AppointmentPanel(adminName, "admin").setVisible(true);
                    break;
                    
                default:
                    JOptionPane.showMessageDialog(this,"Module not found!","Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error opening module:\n"
                + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}