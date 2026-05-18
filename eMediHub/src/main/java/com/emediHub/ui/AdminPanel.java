package com.emediHub.ui;

import com.emediHub.admin.AdminDao;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AdminPanel extends JFrame {

    private AdminDao adminDAO = new AdminDao();

    // Stat cards
    private JLabel totalPatientsLbl;
    private JLabel totalDoctorsLbl;
    private JLabel totalDonorsLbl;
    private JLabel availDonorsLbl;
    private JLabel totalChatsLbl;

    // Tables
    private DefaultTableModel recentPatientsModel;
    private DefaultTableModel recentDonorsModel;
    private DefaultTableModel recentChatsModel;
    private DefaultTableModel bloodStatsModel;

    public AdminPanel() {
        setTitle("eMediHub — Admin Dashboard");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        loadAllData();
    }

    // ─────────── HEADER ───────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("📊  eMediHub Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton refreshBtn = new JButton("⟳  Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 13));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadAllData());
        header.add(refreshBtn, BorderLayout.EAST);

        return header;
    }

    // ─────────── BODY ───────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        body.add(buildStatsRow(),   BorderLayout.NORTH);
        body.add(buildTablesArea(), BorderLayout.CENTER);

        return body;
    }

    // ─────────── STATS ROW ───────────
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 5, 12, 0));
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        totalPatientsLbl = new JLabel("0", SwingConstants.CENTER);
        totalDoctorsLbl  = new JLabel("0", SwingConstants.CENTER);
        totalDonorsLbl   = new JLabel("0", SwingConstants.CENTER);
        availDonorsLbl   = new JLabel("0", SwingConstants.CENTER);
        totalChatsLbl    = new JLabel("0", SwingConstants.CENTER);

        row.add(buildStatCard("Total Patients", totalPatientsLbl, new Color(52,  152, 219), "🧑‍⚕️"));
        row.add(buildStatCard("Total Doctors", totalDoctorsLbl,  new Color(46,  204, 113), "👨‍⚕️"));
        row.add(buildStatCard("Total Donors", totalDonorsLbl,   new Color(231, 76,  60),  "🩸"));
        row.add(buildStatCard("Available Donors", availDonorsLbl,   new Color(243, 156, 18),  "✅"));
        row.add(buildStatCard("Total Chats", totalChatsLbl,    new Color(155, 89,  182), "💬"));

        return row;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new GridLayout(3, 1));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Arial", Font.PLAIN, 26));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 13));
        titleLbl.setForeground(new Color(255, 255, 255, 220));

        card.add(iconLbl);
        card.add(valueLabel);
        card.add(titleLbl);

        return card;
    }

    // ─────────── TABLES AREA ───────────
    private JTabbedPane buildTablesArea() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        tabs.addTab("🧑 Recent Patients",   buildRecentPatientsTab());
        tabs.addTab("🩸 Recent Donors",     buildRecentDonorsTab());
        tabs.addTab("💬 Recent Chats",      buildRecentChatsTab());
        tabs.addTab("📊 Blood Group Stats", buildBloodStatsTab());
        tabs.addTab("👥 Gender Stats",      buildGenderStatsPanel());

        return tabs;
    }

    // Recent Patients Tab
    private JPanel buildRecentPatientsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Name", "Disease", "Age"};
        recentPatientsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildStyledTable(recentPatientsModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel note = new JLabel( "  Showing last 5 registered patients");
        note.setFont(new Font("Arial", Font.ITALIC, 12));
        panel.add(note, BorderLayout.SOUTH);
        return panel;
    }

    // Recent Donors Tab
    private JPanel buildRecentDonorsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Name", "Blood Group", "City"};
        recentDonorsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildStyledTable(recentDonorsModel);

        // Color blood group column
        table.getColumnModel().getColumn(1)
            .setCellRenderer(new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent( JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                    super.getTableCellRendererComponent( t, v, sel, foc, r, c);
                    setForeground(new Color(231, 76, 60));
                    setFont(new Font("Arial", Font.BOLD, 13));
                    setHorizontalAlignment(CENTER);
                    return this;
                }
            });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel note = new JLabel("  Showing last 5 registered donors");
        note.setFont(new Font("Arial", Font.ITALIC, 12));
        panel.add(note, BorderLayout.SOUTH);
        return panel;
    }

    // Recent Chats Tab
    private JPanel buildRecentChatsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"User Message", "Bot Reply", "Time"};
        recentChatsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildStyledTable(recentChatsModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel note = new JLabel("  Showing last 5 chat interactions");
        note.setFont(new Font("Arial", Font.ITALIC, 12));
        panel.add(note, BorderLayout.SOUTH);
        return panel;
    }

    // Blood Group Stats Tab
    private JPanel buildBloodStatsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Blood Group", "Total Donors"};
        bloodStatsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildStyledTable(bloodStatsModel);

        // Center align both columns
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // Gender Stats Panel (visual bars)
    private JPanel buildGenderStatsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel inner = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGenderBars(g);
            }
        };
        inner.setPreferredSize(new Dimension(600, 300));
        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    private void drawGenderBars(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Map<String, Integer> stats = adminDAO.getGenderStats();
        if (stats.isEmpty()) {
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString("No patient data available.", 100, 150);
            return;
        }

        int total = stats.values().stream()
            .mapToInt(Integer::intValue).sum();
        if (total == 0) return;

        Color[] colors = {
            new Color(52,  152, 219),
            new Color(231, 76,  60),
            new Color(46,  204, 113)
        };

        int x = 60, barWidth = 100, gap = 60, maxH = 220;
        int i = 0;

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            int count  = entry.getValue();
            int barH   = (int)((double) count / total * maxH);
            int y      = 260 - barH;
            Color col  = colors[i % colors.length];

            g2.setColor(col);
            g2.fillRoundRect(x, y, barWidth, barH, 10, 10);

            g2.setColor(col.darker());
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(String.valueOf(count), x + barWidth / 2 - 10, y - 8);

            g2.setColor(new Color(200, 200, 200));
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.drawString(entry.getKey(), x + barWidth / 2 - 20, 280);

            x += barWidth + gap;
            i++;
        }

        g2.setColor(new Color(180, 180, 180));
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        g2.drawString("Patient Gender Distribution", 120, 30);
    }

    // ─────────── FOOTER ───────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(44, 62, 80));
        footer.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        JLabel lbl = new JLabel( "eMediHub Admin Panel  |  Developed by Mahak Gupta");
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.add(lbl);

        return footer;
    }

    // ─────────── LOAD ALL DATA ───────────
    private void loadAllData() {
        // Update stat cards
        totalPatientsLbl.setText( String.valueOf(adminDAO.getTotalPatients()));
        totalDoctorsLbl.setText( String.valueOf(adminDAO.getTotalDoctors()));
        totalDonorsLbl.setText( String.valueOf(adminDAO.getTotalDonors()));
        availDonorsLbl.setText( String.valueOf(adminDAO.getAvailableDonors()));
        totalChatsLbl.setText( String.valueOf(adminDAO.getTotalChats()));

        // Recent patients
        recentPatientsModel.setRowCount(0);
        for (String[] row : adminDAO.getRecentPatients()) {
            recentPatientsModel.addRow(row);
        }

        // Recent donors
        recentDonorsModel.setRowCount(0);
        for (String[] row : adminDAO.getRecentDonors()) {
            recentDonorsModel.addRow(row);
        }

        // Recent chats
        recentChatsModel.setRowCount(0);
        for (String[] row : adminDAO.getRecentChats()) {
            recentChatsModel.addRow(row);
        }

        // Blood group stats
        bloodStatsModel.setRowCount(0);
        adminDAO.getBloodGroupStats().forEach((bg, count) ->
            bloodStatsModel.addRow(new Object[]{bg, count})
        );
    }

    // ─────────── HELPER ───────────
    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont( new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground( new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(70, 70, 70));
        return table;
    }
}