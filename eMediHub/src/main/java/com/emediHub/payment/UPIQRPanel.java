package com.emediHub.payment;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class UPIQRPanel extends JDialog {

    private static final String UPI_ID = "emediHub@upi";
    private double amount;
    private String patientName;
    private String paymentType;

    public UPIQRPanel(JFrame parent, double amount, String patientName, String paymentType) {
        super(parent, "UPI Payment — eMediHub", true);
        this.amount      = amount;
        this.patientName = patientName;
        this.paymentType = paymentType;

        setSize(420, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildQRArea(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 4, 4));
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel( "Pay via UPI", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel amt = new JLabel(
            "Amount: Rs. " + String.format("%.2f", amount),
            SwingConstants.CENTER);
        amt.setFont(new Font("Arial", Font.BOLD, 18));
        amt.setForeground(new Color(255, 230, 100));

        JLabel patient = new JLabel( "Patient: " + patientName, SwingConstants.CENTER);
        patient.setFont(new Font("Arial", Font.PLAIN, 13));
        patient.setForeground(Color.WHITE);

        JLabel type = new JLabel( "Type: " + paymentType, SwingConstants.CENTER);
        type.setFont(new Font("Arial", Font.PLAIN, 13));
        type.setForeground(new Color(200, 240, 255));

        panel.add(title);
        panel.add(amt);
        panel.add(patient);
        panel.add(type);
        return panel;
    }

    private JPanel buildQRArea() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder( BorderFactory.createEmptyBorder(15, 20, 10, 20));

        // QR Code drawn with Java2D
        JPanel qrPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawQRCode(g);
            }
        };
        qrPanel.setPreferredSize(new Dimension(260, 260));
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        // UPI ID label
        JLabel upiLabel = new JLabel("UPI ID: " + UPI_ID, SwingConstants.CENTER);
        upiLabel.setFont(new Font("Arial", Font.BOLD, 15));
        upiLabel.setForeground(new Color(52, 152, 219));

        JLabel scanLabel = new JLabel("Scan with any UPI app",SwingConstants.CENTER);
        scanLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel appsLabel = new JLabel( "GPay  |  PhonePe  |  Paytm  |  BHIM", SwingConstants.CENTER);
        appsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        appsLabel.setForeground(new Color(100, 100, 100));

        JPanel center = new JPanel(new BorderLayout());
        center.add(qrPanel, BorderLayout.CENTER);

        JPanel labels = new JPanel(new GridLayout(3, 1, 4, 4));
        labels.add(upiLabel);
        labels.add(scanLabel);
        labels.add(appsLabel);

        panel.add(center, BorderLayout.CENTER);
        panel.add(labels, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 6, 6));
        panel.setBorder( BorderFactory.createEmptyBorder(5, 20, 15, 20));

        JLabel instrLabel = new JLabel(
            "After payment enter Transaction ID below:",
            SwingConstants.CENTER);
        instrLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JTextField txnField = new JTextField();
        txnField.setFont(new Font("Arial", Font.PLAIN, 14));
        txnField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        txnField.setToolTipText( "Enter UPI Transaction ID / UTR number");

        JButton confirmBtn = new JButton("Confirm Payment");
        confirmBtn.setBackground(new Color(39, 174, 96));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 14));
        confirmBtn.setFocusPainted(false);

        confirmBtn.addActionListener(e -> {
            String txnId = txnField.getText().trim();
            if (txnId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter Transaction ID "
                    + "after completing UPI payment!");
                return;
            }
            // Return transaction ID to caller
            setName(txnId);
            dispose();
        });

        panel.add(instrLabel);
        panel.add(txnField);
        panel.add(confirmBtn);
        return panel;
    }

    // Draw QR code pattern using Java2D
    private void drawQRCode(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        int size   = 240;
        int startX = 10;
        int startY = 10;
        int cell   = size / 21;

        // White background
        g2.setColor(Color.WHITE);
        g2.fillRect(startX, startY, size, size);

        // QR pattern (simulated visual)
        int[][] pattern = generateQRPattern();

        for (int row = 0; row < 21; row++) {
            for (int col = 0; col < 21; col++) {
                if (pattern[row][col] == 1) {
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRect(
                    startX + col * cell,
                    startY + row * cell,
                    cell, cell);
            }
        }

        // Center logo area
        int logoSize = cell * 4;
        int logoX = startX + (size - logoSize) / 2;
        int logoY = startY + (size - logoSize) / 2;
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(logoX - 2, logoY - 2, logoSize + 4, logoSize + 4, 6, 6);
        g2.setColor(new Color(52, 152, 219));
        g2.fillRoundRect(logoX, logoY, logoSize, logoSize, 6, 6);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("UPI", logoX + logoSize / 2 - 10, logoY + logoSize / 2 + 4);

        // Amount overlay at bottom
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(startX + 10, startY + size - 30, size - 20, 22, 6, 6);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString( "Rs. " + String.format("%.2f", amount) + "  |  " + UPI_ID, startX + 18, startY + size - 14);
    }

    private int[][] generateQRPattern() {
        int[][] p = new int[21][21];

        // Top-left finder pattern
        fillSquare(p, 0, 0, 7);
        clearSquare(p, 1, 1, 5);
        fillSquare(p, 2, 2, 3);

        // Top-right finder pattern
        fillSquare(p, 0, 14, 7);
        clearSquare(p, 1, 15, 5);
        fillSquare(p, 2, 16, 3);

        // Bottom-left finder pattern
        fillSquare(p, 14, 0, 7);
        clearSquare(p, 15, 1, 5);
        fillSquare(p, 16, 2, 3);

        // Timing patterns
        for (int i = 8; i < 13; i++) {
            p[6][i] = (i % 2 == 0) ? 1 : 0;
            p[i][6] = (i % 2 == 0) ? 1 : 0;
        }

        // Data modules (pseudo-random based on amount)
        long seed = (long)(amount * 100);
        java.util.Random rnd = new java.util.Random(seed);
        for (int r = 8; r < 21; r++) {
            for (int c = 8; c < 21; c++) {
                if (r >= 14 && c <= 7) continue;
                if (p[r][c] == 0)
                    p[r][c] = rnd.nextBoolean() ? 1 : 0;
            }
        }
        return p;
    }

    private void fillSquare(int[][] p, int r, int c, int size) {
        for (int i = r; i < r + size && i < 21; i++)
            for (int j = c; j < c + size && j < 21; j++)
                p[i][j] = 1;
    }

    private void clearSquare(int[][] p, int r, int c, int size) {
        for (int i = r; i < r + size && i < 21; i++)
            for (int j = c; j < c + size && j < 21; j++)
                p[i][j] = 0;
    }
}
