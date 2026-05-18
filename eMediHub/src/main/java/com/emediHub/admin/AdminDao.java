package com.emediHub.admin;

import com.emediHub.db.DBConnection;
import java.sql.*;
import java.util.*;

public class AdminDao {

    // Total counts
    public int getTotalPatients() {
        return getCount("SELECT COUNT(*) FROM patients");
    }

    public int getTotalDoctors() {
        return getCount("SELECT COUNT(*) FROM doctors");
    }

    public int getTotalDonors() {
        return getCount("SELECT COUNT(*) FROM blood_donors");
    }

    public int getAvailableDonors() {
        return getCount(
            "SELECT COUNT(*) FROM blood_donors WHERE available = TRUE");
    }

    public int getTotalAppointments() {
        return getCount("SELECT COUNT(*) FROM appointments");
    }

    public int getTotalChats() {
        return getCount("SELECT COUNT(*) FROM chat_logs");
    }

    // Blood group wise donor count
    public Map<String, Integer> getBloodGroupStats() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT blood_group, COUNT(*) as total "
                   + "FROM blood_donors GROUP BY blood_group "
                   + "ORDER BY blood_group";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("blood_group"),
                        rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    // Recent patients (last 5)
    public List<String[]> getRecentPatients() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT name, disease, age " + "FROM patients ORDER BY id DESC LIMIT 5";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("name"),
                    rs.getString("disease"),
                    rs.getString("age")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Recent donors (last 5)
    public List<String[]> getRecentDonors() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT name, blood_group, city " + "FROM blood_donors ORDER BY id DESC LIMIT 5";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("name"),
                    rs.getString("blood_group"),
                    rs.getString("city")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Recent chat logs (last 5)
    public List<String[]> getRecentChats() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT user_message, bot_reply, timestamp " + "FROM chat_logs ORDER BY id DESC LIMIT 5";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
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

    // Gender wise patient count
    public Map<String, Integer> getGenderStats() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT gender, COUNT(*) as total " + "FROM patients GROUP BY gender";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("gender"),
                        rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    // Helper method
    private int getCount(String sql) {
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
