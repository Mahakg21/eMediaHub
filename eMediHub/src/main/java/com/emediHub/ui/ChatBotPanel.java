package com.emediHub.ui;

import com.emediHub.chatbot.ChatBot;
import com.emediHub.chatbot.ChatLogDao;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatBotPanel extends JFrame {

    private ChatLogDao chatLogDAO = new ChatLogDao();

    private JPanel   chatArea;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton    sendBtn;
    private JButton    clearBtn;
    private JLabel     statusLabel;

    private static final Color BOT_COLOR  = new Color(52,  152, 219);
    private static final Color USER_COLOR = new Color(39,  174, 96);
    private static final Color BG_COLOR   = new Color(30,  30,  30);
    private static final Color CHAT_BG    = new Color(45,  45,  45);

    public ChatBotPanel() {
        setTitle("eMediHub — Chatbot Assistant");
        setSize(700, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildChat(),    BorderLayout.CENTER);
        add(buildInput(),   BorderLayout.SOUTH);

        // Welcome message
        addBotMessage("Hello! I am eMediHub Assistant.\n"
                    + "I can help you with:\n"
                    + "• Appointments & Doctors\n"
                    + "• Blood Donation\n"
                    + "• Emergency Info\n"
                    + "• Hospital Timings\n\n"
                    + "Type your question below!");
    }

    // ─────────── HEADER ───────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BOT_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        // Bot avatar circle
        JLabel avatar = new JLabel("🤖");
        avatar.setFont(new Font("Arial", Font.PLAIN, 28));
        left.add(avatar);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel name = new JLabel("eMediHub Assistant");
        name.setFont(new Font("Arial", Font.BOLD, 16));
        name.setForeground(Color.WHITE);
        JLabel online = new JLabel("● Online");
        online.setFont(new Font("Arial", Font.PLAIN, 12));
        online.setForeground(new Color(200, 255, 200));
        info.add(name);
        info.add(online);
        left.add(info);

        header.add(left, BorderLayout.WEST);

        // Clear button
        clearBtn = new JButton("Clear Chat");
        clearBtn.setBackground(new Color(231, 76, 60));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.addActionListener(e -> clearChat());
        header.add(clearBtn, BorderLayout.EAST);

        return header;
    }

    // ─────────── CHAT AREA ───────────
    private JScrollPane buildChat() {
        chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setBackground(CHAT_BG);
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    // ─────────── INPUT AREA ───────────
    private JPanel buildInput() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(BG_COLOR);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Status bar
        statusLabel = new JLabel("Type a message and press Enter or Send");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        statusLabel.setForeground(new Color(150, 150, 150));
        bottom.add(statusLabel, BorderLayout.NORTH);

        // Input row
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BOT_COLOR, 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        sendBtn = new JButton("Send ▶");
        sendBtn.setBackground(BOT_COLOR);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Arial", Font.BOLD, 14));
        sendBtn.setFocusPainted(false);
        sendBtn.setBorderPainted(false);
        sendBtn.setPreferredSize(new Dimension(100, 40));
        sendBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        inputRow.add(inputField, BorderLayout.CENTER);
        inputRow.add(sendBtn,    BorderLayout.EAST);
        bottom.add(inputRow, BorderLayout.CENTER);

        // Quick suggestion buttons
        JPanel suggestions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        suggestions.setOpaque(false);
        String[] quickReplies = {
            "Book Appointment", "Find Doctor",
            "Blood Donor", "Emergency", "Timings"
        };
        for (String qr : quickReplies) {
            JButton btn = new JButton(qr);
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.setBackground(new Color(60, 60, 60));
            btn.setForeground(new Color(200, 200, 200));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> sendMessage(qr));
            suggestions.add(btn);
        }
        bottom.add(suggestions, BorderLayout.SOUTH);

        // Action listeners
        sendBtn.addActionListener(e -> sendMessage(inputField.getText()));
        inputField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    sendMessage(inputField.getText());
            }
        });

        return bottom;
    }

    // ─────────── SEND MESSAGE ───────────
    private void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;

        String userMsg = text.trim();
        inputField.setText("");

        // Show user message
        addUserMessage(userMsg);

        // Show typing indicator
        statusLabel.setText("eMediHub Assistant is typing...");

        // Simulate typing delay then show bot reply
        Timer timer = new Timer(600, e -> {
            String botReply = ChatBot.getReply(userMsg);
            addBotMessage(botReply);
            chatLogDAO.saveChat(userMsg, botReply);
            statusLabel.setText("Type a message and press Enter or Send");
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ─────────── ADD USER MESSAGE BUBBLE ───────────
    private void addUserMessage(String text) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));

        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(4, 60, 4, 0));

        JPanel msgBox = new JPanel(new BorderLayout());
        msgBox.setBackground(USER_COLOR);
        msgBox.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel msgLabel = new JLabel(
            "<html><body style='width:280px'>" + text + "</body></html>");
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        msgLabel.setForeground(Color.WHITE);

        JLabel timeLabel = new JLabel(time + "  You");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(200, 255, 200));
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        msgBox.add(msgLabel,  BorderLayout.CENTER);
        msgBox.add(timeLabel, BorderLayout.SOUTH);
        msgBox.setBorder(new RoundBorder(14, USER_COLOR));

        bubble.add(msgBox, BorderLayout.EAST);
        chatArea.add(bubble);
        chatArea.add(Box.createVerticalStrut(6));
        chatArea.revalidate();
        scrollToBottom();
    }

    // ─────────── ADD BOT MESSAGE BUBBLE ───────────
    private void addBotMessage(String text) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));

        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 60));

        JPanel msgBox = new JPanel(new BorderLayout());
        msgBox.setBackground(new Color(60, 60, 70));
        msgBox.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Convert \n to <br> for HTML display
        String htmlText = text.replace("\n", "<br>");
        JLabel msgLabel = new JLabel( "<html><body style='width:300px'>" + htmlText + "</body></html>");
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        msgLabel.setForeground(new Color(230, 230, 230));

        JLabel timeLabel = new JLabel("🤖 Assistant  " + time);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(150, 150, 150));

        msgBox.add(msgLabel,  BorderLayout.CENTER);
        msgBox.add(timeLabel, BorderLayout.SOUTH);
        msgBox.setBorder(new RoundBorder(14, new Color(60, 60, 70)));

        bubble.add(msgBox, BorderLayout.WEST);
        chatArea.add(bubble);
        chatArea.add(Box.createVerticalStrut(6));
        chatArea.revalidate();
        scrollToBottom();
    }

    // ─────────── CLEAR CHAT ───────────
    private void clearChat() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Clear all chat messages?",
            "Confirm",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            chatArea.removeAll();
            chatArea.revalidate();
            chatArea.repaint();
            addBotMessage("Chat cleared! How can I help you?");
        }
    }
 
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    // ─────────── ROUND BORDER ───────────
    static class RoundBorder extends AbstractBorder {
        private int radius;
        private Color color;

        RoundBorder(int radius, Color color) {
            this.radius = radius;
            this.color  = color;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, w - 1, h - 1, radius, radius);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
    }
}
