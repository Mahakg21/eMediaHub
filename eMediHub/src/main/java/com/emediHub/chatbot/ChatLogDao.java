package com.emediHub.chatbot;

import com.emediHub.db.DBConnection;
import java.sql.*;
import java.util.*;

public class ChatLogDao {

    // Save chat to database
    public void saveChat(String userMessage, String botReply) {
        String sql = "INSERT INTO chat_logs(user_message, bot_reply, timestamp) "
                   + "VALUES (?, ?, NOW())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userMessage);
            ps.setString(2, botReply);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get recent chat history
    public List<String[]> getRecentChats(int limit) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT user_message, bot_reply, timestamp "
                   + "FROM chat_logs ORDER BY id DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("user_message"),
                    rs.getString("bot_reply"),
                    rs.getString("timestamp")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Clear all chat logs
    public boolean clearAllLogs() {
        String sql = "DELETE FROM chat_logs";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Count total chats
    public int countChats() {
        String sql = "SELECT COUNT(*) FROM chat_logs";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
