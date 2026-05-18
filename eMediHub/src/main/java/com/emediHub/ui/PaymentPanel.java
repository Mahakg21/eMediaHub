package com.emediHub.ui;

import com.emediHub.db.DBConnection;
import com.emediHub.notification.PaymentEmailService;
import com.emediHub.payment.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

public class PaymentPanel extends JFrame {

    private PaymentDao paymentDAO = new PaymentDao();

    private JTextField patientField;
    private JComboBox<String> typeBox;
    private JTextField amountField;
    private JComboBox<String> methodBox;
    private JTextField remarksField;

    private DefaultTableModel tableModel;
    private JTable paymentTable;

    private JLabel totalRevLbl;
    private JLabel paidCountLbl;
    private JLabel pendingCountLbl;

    private String loggedInUser;

    public PaymentPanel(String loggedInUser) {
        this.loggedInUser = loggedInUser;
        setTitle("eMediHub — Payment Center");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        loadPayments();
        loadStats();
    }

    // ─────────── HEADER ───────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel( "  Payment Center — eMediHub");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // Stats row
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        stats.setOpaque(false);

        totalRevLbl   = makeStatLabel("Revenue: Rs. 0");
        paidCountLbl  = makeStatLabel("Paid: 0");
        pendingCountLbl = makeStatLabel("Pending: 0");

        totalRevLbl.setForeground(new Color(255, 220, 80));
        paidCountLbl.setForeground(new Color(100, 255, 150));
        pendingCountLbl.setForeground(new Color(255, 150, 100));

        stats.add(totalRevLbl);
        stats.add(paidCountLbl);
        stats.add(pendingCountLbl);
        header.add(stats, BorderLayout.EAST);

        return header;
    }

    private JLabel makeStatLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    // ─────────── BODY ───────────
    private JSplitPane buildBody() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildFormPanel(),buildTablePanel());
        split.setDividerLocation(340);
        split.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return split;
    }

    // ─────────── FORM PANEL ───────────
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Appointment fee section
        JPanel apptPanel = new JPanel( new GridLayout(4, 2, 8, 10));
        apptPanel.setBorder(BorderFactory.createTitledBorder("New Payment"));

        patientField = new JTextField();
        patientField.setText(loggedInUser);

        typeBox = new JComboBox<>(new String[]{
            "Appointment Fee",
            "Hospital Bill",
            "Consultation Fee",
            "Lab Test Fee",
            "Medicine Bill",
            "Surgery Fee"
        });
        typeBox.setFont(new Font("Arial", Font.PLAIN, 13));

        amountField = new JTextField("500");

        methodBox = new JComboBox<>(new String[]{"UPI", "Cash", "Card"});
        methodBox.setFont(new Font("Arial", Font.PLAIN, 13));

        remarksField = new JTextField();

        apptPanel.add(new JLabel("Patient Name:"));
        apptPanel.add(patientField);
        apptPanel.add(new JLabel("Payment Type:"));
        apptPanel.add(typeBox);
        apptPanel.add(new JLabel("Amount (Rs.):"));
        apptPanel.add(amountField);
        apptPanel.add(new JLabel("Payment Method:"));
        apptPanel.add(methodBox);

        // Auto fill amount based on type
        typeBox.addActionListener(e -> {
            String type = typeBox.getSelectedItem().toString();
            switch (type) {
                case "Appointment Fee":amountField.setText("500");
                break;
                case "Consultation Fee":amountField.setText("300");
                break;
                case "Lab Test Fee":amountField.setText("800");
                break;
                case "Surgery Fee":amountField.setText("15000");
                break;
                default:amountField.setText("");
            }
        });

        // UPI Pay button
        JButton upiBtn = new JButton("Pay via UPI / QR");
        upiBtn.setBackground(new Color(52, 152, 219));
        upiBtn.setForeground(Color.WHITE);
        upiBtn.setFont(new Font("Arial", Font.BOLD, 14));
        upiBtn.setPreferredSize(new Dimension(280, 44));
        upiBtn.setFocusPainted(false);
        upiBtn.addActionListener(e -> initiateUPIPayment());

        // Cash/Card Pay button
        JButton cashBtn = new JButton("Record Cash / Card Payment");
        cashBtn.setBackground(new Color(39, 174, 96));
        cashBtn.setForeground(Color.WHITE);
        cashBtn.setFont(new Font("Arial", Font.BOLD, 13));
        cashBtn.setPreferredSize(new Dimension(280, 40));
        cashBtn.setFocusPainted(false);
        cashBtn.addActionListener(e ->
            recordDirectPayment());

        // Remarks
        JPanel remarksPanel = new JPanel(new BorderLayout(5,5));
        remarksPanel.add(new JLabel("Remarks:"),BorderLayout.WEST);
        remarksPanel.add(remarksField, BorderLayout.CENTER);

        // Quick amount buttons
        JPanel quickAmts = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        quickAmts.setBorder(BorderFactory.createTitledBorder("Quick amounts"));
        for (String amt : new String[]{"500","1000","2000","5000","10000"}) {
            JButton qBtn = new JButton("Rs." + amt);
            qBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            qBtn.setBackground(new Color(52, 73, 94));
            qBtn.setForeground(Color.WHITE);
            qBtn.setFocusPainted(false);
            qBtn.addActionListener(e ->
                amountField.setText(amt));
            quickAmts.add(qBtn);
        }

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnPanel.add(upiBtn);
        btnPanel.add(cashBtn);

        panel.add(apptPanel,   BorderLayout.NORTH);
        panel.add(quickAmts,   BorderLayout.CENTER);
        panel.add(btnPanel,    BorderLayout.SOUTH);

        return panel;
    }

    // ─────────── TABLE PANEL ───────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JTextField searchField = new JTextField(16);
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        JComboBox<String> filterType = new JComboBox<>(
            new String[]{
                "All Types",
                "Appointment Fee",
                "Hospital Bill",
                "Consultation Fee",
                "Lab Test Fee",
                "Medicine Bill",
                "Surgery Fee"
            });

        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        showAllBtn.setBackground(new Color(149, 165, 166));
        showAllBtn.setForeground(Color.WHITE);

        searchBar.add(new JLabel("Patient:"));
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchBar.add(new JLabel("Type:"));
        searchBar.add(filterType);
        searchBar.add(showAllBtn);

        // Table
        String[] cols = {
            "ID", "Patient", "Type",
            "Amount(Rs.)", "Method", "Status",
            "Txn ID", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(28);
        paymentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        paymentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        paymentTable.getTableHeader().setBackground(new Color(44, 62, 80));
        paymentTable.getTableHeader().setForeground(Color.WHITE);

        // Color status column
        paymentTable.getColumnModel().getColumn(5)
            .setCellRenderer(new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,boolean foc, int r, int c) {
                    super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                    String val = v != null ? v.toString() : "";
                    if (val.equals("Paid")) {
                        setForeground(new Color(39, 174, 96));
                        setFont(new Font( "Arial", Font.BOLD, 12));
                    } else if (val.equals("Pending")) {
                        setForeground(new Color(243, 156, 18));
                        setFont(new Font("Arial", Font.BOLD, 12));
                    } else {
                        setForeground(new Color(231, 76, 60));
                    }
                    return this;
                }
            });

        // Bottom buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton markPaidBtn  = new JButton("Mark as Paid");
        JButton receiptBtn   = new JButton("Print Receipt");
        JButton refreshBtn   = new JButton("Refresh");

        markPaidBtn.setBackground(new Color(39, 174, 96));
        markPaidBtn.setForeground(Color.WHITE);
        receiptBtn.setBackground(new Color(155, 89, 182));
        receiptBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);

        btnPanel.add(markPaidBtn);
        btnPanel.add(receiptBtn);
        btnPanel.add(refreshBtn);

        // Actions
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            populateTable(paymentDAO.getPaymentsByPatient(kw));
        });

        filterType.addActionListener(e -> {
            String t = filterType.getSelectedItem().toString();
            if (t.equals("All Types")) 
            	loadPayments();
            else 
            	populateTable(paymentDAO.getPaymentsByType(t));
        });

        showAllBtn.addActionListener(e -> loadPayments());

        markPaidBtn.addActionListener(e -> {
            int row = paymentTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,"Please select a payment to mark paid.");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (paymentDAO.updateStatus(id, "Paid")) {
                JOptionPane.showMessageDialog(this,"Payment marked as Paid!");
                loadPayments();
                loadStats();
            }
        });

        receiptBtn.addActionListener(e -> {
            int row = paymentTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,"Please select a payment first.");
                return;
            }
            showReceipt(row);
        });

        refreshBtn.addActionListener(e -> {
            loadPayments();
            loadStats();
        });

        panel.add(searchBar,  BorderLayout.NORTH);
        panel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
        panel.add(btnPanel,  BorderLayout.SOUTH);
        return panel;
    }

    // ─────────── FOOTER ───────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(44, 62, 80));
        JLabel lbl = new JLabel("  eMediHub Payment Center  | Developed by Mahak Gupta | " + "UPI ID: emediHub@upi");
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.add(lbl);
        return footer;
    }

    // ─────────── UPI PAYMENT ───────────
    private void initiateUPIPayment() {
        String patient = patientField.getText().trim();
        String type    = typeBox.getSelectedItem().toString();
        String amtStr  = amountField.getText().trim();

        if (patient.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter patient name!");
            return;
        }
        if (amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please enter amount!");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,"Please enter a valid amount!");
            return;
        }

        // Show UPI QR dialog
        UPIQRPanel qrDialog = new UPIQRPanel(this, amount, patient, type);
        qrDialog.setVisible(true);

        // After dialog closes get txn ID
        String txnId = qrDialog.getName();
        if (txnId != null && !txnId.isEmpty() && !txnId.equals("dialog0")) {
            // Save payment
            Payment p = new Payment();
            p.setPatientName(patient);
            p.setPaymentType(type);
            p.setAmount(amount);
            p.setPaymentMethod("UPI");
            p.setUpiId("emediHub@upi");
            p.setStatus("Paid");
            p.setTransactionId(txnId);
            p.setRemarks("UPI payment via QR");

            if (paymentDAO.savePayment(p)) {
                JOptionPane.showMessageDialog(this,
                    "Payment successful!\n"
                    + "Transaction ID: " + txnId
                    + "\nAmount: Rs. "
                    + String.format("%.2f", amount),
                    "Payment Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
             // ✅ Receipt email bhejo
                int choice = JOptionPane.showConfirmDialog(this,
                        "Send receipt to patient "
                        + "on email?",
                        "Send Receipt",
                        JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION)
                    sendReceipt(p);
                
                clearForm();
                loadPayments();
                loadStats();
            }
        }
    }

    // ─────────── CASH/CARD PAYMENT ───────────
    private void recordDirectPayment() {
        String patient = patientField.getText().trim();
        String type    = typeBox.getSelectedItem().toString();
        String amtStr  = amountField.getText().trim();
        String method  = methodBox.getSelectedItem().toString();

        if (patient.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please fill patient name and amount!");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,"Invalid amount!");
            return;
        }

        // Generate transaction ID for cash/card
        String txnId = method.toUpperCase() + "-" + System.currentTimeMillis();

        Payment p = new Payment();
        p.setPatientName(patient);
        p.setPaymentType(type);
        p.setAmount(amount);
        p.setPaymentMethod(method);
        p.setUpiId("-");
        p.setStatus("Paid");
        p.setTransactionId(txnId);
        p.setRemarks(method + " payment recorded");

        if (paymentDAO.savePayment(p)) {
            JOptionPane.showMessageDialog(this,
                method + " payment recorded!\n"
                + "Txn ID: " + txnId
                + "\nAmount: Rs. "
                + String.format("%.2f", amount),
                "Payment Recorded",
                JOptionPane.INFORMATION_MESSAGE);
            
         // ✅ Receipt email bhejo
            int choice = JOptionPane.showConfirmDialog(this,
                    "Send receipt to patient "
                    + "on email?",
                    "Send Receipt",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION)
                sendReceipt(p);
            
            clearForm();
            loadPayments();
            loadStats();
        }
    }

    // ─────────── RECEIPT ───────────
    private void showReceipt(int row) {
        String id      = tableModel.getValueAt(row, 0).toString();
        String patient = tableModel.getValueAt(row, 1).toString();
        String type    = tableModel.getValueAt(row, 2).toString();
        String amount  = tableModel.getValueAt(row, 3).toString();
        String method  = tableModel.getValueAt(row, 4).toString();
        String status  = tableModel.getValueAt(row, 5).toString();
        String txnId   = tableModel.getValueAt(row, 6).toString();
        String date    = tableModel.getValueAt(row, 7).toString();

        String receipt =
            "══════════════════════════════\n"
            + "         eMediHub Receipt\n"
            + "══════════════════════════════\n"
            + "Payment ID   : #" + id + "\n"
            + "Patient Name : " + patient + "\n"
            + "Payment Type : " + type + "\n"
            + "Amount       : Rs. " + amount + "\n"
            + "Method       : " + method + "\n"
            + "Status       : " + status + "\n"
            + "Txn ID       : " + txnId + "\n"
            + "Date & Time  : " + date + "\n"
            + "══════════════════════════════\n"
            + "  Thank you for choosing eMediHub!\n"
            + "  UPI: emediHub@upi\n"
            + "  Helpline: 1800-XXX-XXXX\n"
            + "══════════════════════════════";

        JTextArea receiptArea = new JTextArea(receipt);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        receiptArea.setEditable(false);
        receiptArea.setBackground(new Color(245, 245, 245));

        JOptionPane.showMessageDialog(this,
            new JScrollPane(receiptArea),
            "Payment Receipt — eMediHub",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────── HELPERS ───────────
    private void loadPayments() {
        populateTable(paymentDAO.getAllPayments());
    }

    private void loadStats() {
        double rev = paymentDAO.getTotalRevenue();
        int paid   = paymentDAO.countByStatus("Paid");
        int pend   = paymentDAO.countByStatus("Pending");
        totalRevLbl.setText("Revenue: Rs. " + String.format("%.2f", rev));
        paidCountLbl.setText("Paid: " + paid);
        pendingCountLbl.setText("Pending: " + pend);
    }

    private void populateTable(List<Payment> list) {
        tableModel.setRowCount(0);
        for (Payment p : list) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getPatientName(),
                p.getPaymentType(),
                String.format("%.2f", p.getAmount()),
                p.getPaymentMethod(),
                p.getStatus(),
                p.getTransactionId(),
                p.getCreatedAt()
            });
        }
    }

    private void clearForm() {
        patientField.setText(loggedInUser);
        amountField.setText("500");
        typeBox.setSelectedIndex(0);
        methodBox.setSelectedIndex(0);
        remarksField.setText("");
    }
    
 // ─────────── SEND RECEIPT ───────────
    private void sendReceipt(Payment p) {

        // DB se email fetch karo
        String email = getPatientEmail(
            p.getPatientName());

        // Nahi mili toh manually lo
        if (email.isEmpty()) {
            email = JOptionPane.showInputDialog(
                this,
                "Patient email not found.\n"
                + "Enter email to send receipt:",
                "Send Receipt",
                JOptionPane.QUESTION_MESSAGE);
        }

        // Skip kiya
        if (email == null || email.trim().isEmpty()) {
            return;
        }

        // Email validate karo
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid email address!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        final String finalEmail =email.trim();
        final Payment finalP = p;

        // Loading dialog
        JDialog dlg = new JDialog(this,"Sending Receipt...", false);
        JPanel dlgPanel = new JPanel(new BorderLayout(10, 10));
        dlgPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        JLabel lbl = new JLabel("Sending receipt to " + finalEmail + "...",SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        dlgPanel.add(lbl, BorderLayout.CENTER);
        dlgPanel.add(bar, BorderLayout.SOUTH);
        dlg.add(dlgPanel);
        dlg.setSize(380, 110);
        dlg.setLocationRelativeTo(this);

        // Background mein send karo
        new Thread(() -> {

            SwingUtilities.invokeLater(
                () -> dlg.setVisible(true));

            boolean sent = PaymentEmailService.sendReceipt(finalP, finalEmail);

            SwingUtilities.invokeLater(() -> {
                dlg.dispose();

                if (sent) {
                    JOptionPane
                        .showMessageDialog(
                            this,
                            "Payment receipt sent!\n\n"
                            + "Sent to: "
                            + finalEmail
                            + "\nReceipt #: "
                            + finalP.getId()
                            + "\nAmount: Rs. "
                            + String.format("%.2f",finalP.getAmount()),
                            "Receipt Sent",
                            JOptionPane
                            .INFORMATION_MESSAGE);
                } else {
                    int retry = JOptionPane.showConfirmDialog(
                            this,
                            "Email failed!\n\n"
                            + "Possible reasons:\n"
                            + "1. Wrong App Password\n"
                            + "2. No internet\n"
                            + "3. Wrong email\n\n"
                            + "Retry?",
                            "Failed",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE);

                    if (retry ==JOptionPane.YES_OPTION)
                        sendReceipt(finalP);
                }
            });
        }).start();
    }

    // ─────────── GET EMAIL FROM DB ───────────
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
