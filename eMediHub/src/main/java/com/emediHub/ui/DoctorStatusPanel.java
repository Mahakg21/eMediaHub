package com.emediHub.ui;

import com.emediHub.hospital.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class DoctorStatusPanel extends JFrame {

    private DoctorDao doctorDAO = new DoctorDao();

    private DefaultTableModel tableModel;
    private JTable doctorTable;

    // Stat labels
    private JLabel availableLbl;
    private JLabel busyLbl;
    private JLabel onLeaveLbl;
    private JLabel totalLbl;

    public DoctorStatusPanel() {
        setTitle("eMediHub — Doctor Status Board");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        loadDoctors();
        loadStats();

        // Auto refresh every 30 seconds
        Timer autoRefresh = new Timer(30000, e -> {
            loadDoctors();
            loadStats();
        });
        autoRefresh.start();
    }

    // ─────────── HEADER ───────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder( BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel(  "  Doctor Status Board");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // Stats panel in header
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        statsPanel.setOpaque(false);

        availableLbl = makeStatChip( "Available: 0", new Color(39, 174, 96));
        busyLbl      = makeStatChip( "Busy: 0", new Color(231, 76, 60));
        onLeaveLbl   = makeStatChip( "On Leave: 0", new Color(243, 156, 18));
        totalLbl     = makeStatChip( "Total: 0", new Color(52, 152, 219));

        statsPanel.add(availableLbl);
        statsPanel.add(busyLbl);
        statsPanel.add(onLeaveLbl);
        statsPanel.add(totalLbl);

        JButton refreshBtn = new JButton("⟳ Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 13));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.addActionListener(e -> {
            loadDoctors();
            loadStats();
        });
        statsPanel.add(refreshBtn);

        header.add(statsPanel, BorderLayout.EAST);
        return header;
    }

    private JLabel makeStatChip(String text, Color color) {
        JLabel lbl = new JLabel("  " + text + "  ");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(color);
        lbl.setBorder(BorderFactory.createEmptyBorder( 4, 8, 4, 8));
        return lbl;
    }

    // ─────────── BODY ───────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setBorder( BorderFactory.createEmptyBorder(10, 15, 10, 15));

        body.add(buildFilterBar(),  BorderLayout.NORTH);
        body.add(buildTablePanel(), BorderLayout.CENTER);
        body.add(buildActionPanel(),BorderLayout.SOUTH);

        return body;
    }

    // Filter bar
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        bar.setBorder(BorderFactory.createTitledBorder( "Filter by Status"));

        JButton allBtn       = makeFilterBtn( "All Doctors",  new Color(52,  73,  94));
        JButton availBtn     = makeFilterBtn( "Available",    new Color(39,  174, 96));
        JButton busyBtn      = makeFilterBtn( "Busy",         new Color(231, 76,  60));
        JButton leaveBtn     = makeFilterBtn( "On Leave",     new Color(243, 156, 18));
        JButton offDutyBtn   = makeFilterBtn( "Off Duty",     new Color(127, 140, 141));

        allBtn.addActionListener(e     -> loadDoctors());
        availBtn.addActionListener(e   ->
            populateTable(
                doctorDAO.getDoctorsByStatus("Available")));
        busyBtn.addActionListener(e    ->
            populateTable(
                doctorDAO.getDoctorsByStatus("Busy")));
        leaveBtn.addActionListener(e   ->
            populateTable(
                doctorDAO.getDoctorsByStatus("On Leave")));
        offDutyBtn.addActionListener(e ->
            populateTable(
                doctorDAO.getDoctorsByStatus("Off Duty")));

        bar.add(allBtn);
        bar.add(availBtn);
        bar.add(busyBtn);
        bar.add(leaveBtn);
        bar.add(offDutyBtn);

        return bar;
    }

    private JButton makeFilterBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor( Cursor.HAND_CURSOR));
        return btn;
    }

    // Table panel
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"ID", "Doctor Name", "Specialization", "Phone", "Email", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        doctorTable = new JTable(tableModel);
        doctorTable.setRowHeight(36);
        doctorTable.setFont(new Font("Arial", Font.PLAIN, 13));
        doctorTable.getTableHeader().setFont( new Font("Arial", Font.BOLD, 13));
        doctorTable.getTableHeader().setBackground( new Color(44, 62, 80));
        doctorTable.getTableHeader().setForeground( Color.WHITE);
        doctorTable.setSelectionBackground( new Color(52, 152, 219));
        doctorTable.setSelectionForeground(Color.WHITE);
        doctorTable.setGridColor(new Color(220, 220, 220));

        // ✅ Status column with colored badge renderer
        doctorTable.getColumnModel().getColumn(5)
            .setCellRenderer(new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent( JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                    JLabel lbl = new JLabel();
                    lbl.setOpaque(true);
                    lbl.setHorizontalAlignment(CENTER);
                    lbl.setFont(new Font("Arial", Font.BOLD, 12));

                    String status = v != null ? v.toString() : "";

                    switch (status) {
                        case "Available":
                            lbl.setText("● Available");
                            lbl.setForeground(new Color(39, 174, 96));
                            lbl.setBackground(new Color(212, 247, 229));
                            break;
                        case "Busy":
                            lbl.setText("● Busy");
                            lbl.setForeground(new Color(231, 76, 60));
                            lbl.setBackground(new Color(252, 220, 216));
                            break;
                        case "On Leave":
                            lbl.setText("● On Leave");
                            lbl.setForeground(new Color(243, 156, 18));
                            lbl.setBackground(new Color(254, 240, 212));
                            break;
                        case "Off Duty":
                            lbl.setText("● Off Duty");
                            lbl.setForeground(new Color(127, 140, 141));
                            lbl.setBackground(new Color(235, 237, 239));
                            break;
                        default:
                            lbl.setText(status);
                            lbl.setForeground(Color.BLACK);
                            lbl.setBackground(Color.WHITE);
                    }

                    if (sel) {
                        lbl.setBackground(new Color(52, 152, 219));
                        lbl.setForeground(Color.WHITE);
                    }
                    return lbl;
                }
            });

        // Column widths
        doctorTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        doctorTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        doctorTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        doctorTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        doctorTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        doctorTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        panel.add(new JScrollPane(doctorTable));
        return panel;
    }

    // Action panel — change status buttons
    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JLabel hint = new JLabel("  Select a doctor then click a status button:");
        hint.setFont(new Font("Arial", Font.ITALIC, 12));
        hint.setForeground(new Color(100, 100, 100));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JButton availBtn   = makeStatusChangeBtn( "● Set Available", new Color(39,  174, 96), "Available");
        JButton busyBtn    = makeStatusChangeBtn( "● Set Busy",      new Color(231, 76,  60), "Busy");
        JButton leaveBtn   = makeStatusChangeBtn( "● Set On Leave",  new Color(243, 156, 18), "On Leave");
        JButton offBtn     = makeStatusChangeBtn( "● Set Off Duty",  new Color(127, 140, 141), "Off Duty");

        btns.add(availBtn);
        btns.add(busyBtn);
        btns.add(leaveBtn);
        btns.add(offBtn);

        panel.add(hint, BorderLayout.WEST);
        panel.add(btns, BorderLayout.CENTER);

        return panel;
    }

    private JButton makeStatusChangeBtn(
            String text, Color color, String status) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(150, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e ->
            changeStatus(status));
        return btn;
    }

    // ─────────── FOOTER ───────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(44, 62, 80));
        JLabel lbl = new JLabel( "  Auto-refreshes every 30 seconds  " + "|  eMediHub Doctor Status Board " + " | Developed by Mahak Gupta");
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.add(lbl);
        return footer;
    }

    // ─────────── ACTIONS ───────────
    private void changeStatus(String newStatus) {
        int row = doctorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor first!", "No Selection",JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    id   = (int) tableModel.getValueAt(row, 0);
        String name = tableModel.getValueAt(row, 1).toString();
        String curr = tableModel.getValueAt(row, 5).toString();

        if (curr.equals(newStatus)) {
            JOptionPane.showMessageDialog(this, "Dr. " + name + " is already " + newStatus + "!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Change Dr. " + name
            + " status to '" + newStatus + "'?",
            "Confirm Status Change",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorDAO.updateStatus(id, newStatus)) {
                JOptionPane.showMessageDialog(this,
                    "Dr. " + name
                    + " status updated to: "
                    + newStatus + "!",
                    "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE);
                loadDoctors();
                loadStats();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update status!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ─────────── LOAD DATA ───────────
    private void loadDoctors() {
        populateTable(doctorDAO.getAllDoctors());
    }

    private void populateTable(List<Doctor> list) {
        tableModel.setRowCount(0);
        for (Doctor d : list) {
            tableModel.addRow(new Object[]{
                d.getId(),
                d.getName(),
                d.getSpecialization(),
                d.getPhone(),
                d.getEmail(),
                d.getStatus()
            });
        }
    }

    private void loadStats() {
        int avail = doctorDAO.countByStatus("Available");
        int busy  = doctorDAO.countByStatus("Busy");
        int leave = doctorDAO.countByStatus("On Leave");
        int total = doctorDAO.countDoctors();

        availableLbl.setText("  Available: " + avail + "  ");
        busyLbl.setText     ("  Busy: "      + busy  + "  ");
        onLeaveLbl.setText  ("  On Leave: "  + leave + "  ");
        totalLbl.setText    ("  Total: "     + total + "  ");
    }
}