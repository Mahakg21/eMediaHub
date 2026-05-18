package com.emediHub.blood;

import com.emediHub.db.DBConnection;
import java.sql.*;
import java.util.*;

public class BloodDonorDao {

    // Add new donor
    public boolean addDonor(BloodDonor d) {
        String sql = "INSERT INTO blood_donors(name, blood_group, phone, city, available) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString (1, d.getName());
            ps.setString (2, d.getBloodGroup());
            ps.setString (3, d.getPhone());
            ps.setString (4, d.getCity());
            ps.setBoolean(5, d.isAvailable());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all donors
    public List<BloodDonor> getAllDonors() {
        List<BloodDonor> list = new ArrayList<>();
        String sql = "SELECT * FROM blood_donors ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new BloodDonor(
                    rs.getInt    ("id"),
                    rs.getString ("name"),
                    rs.getString ("blood_group"),
                    rs.getString ("phone"),
                    rs.getString ("city"),
                    rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Search by blood group
    public List<BloodDonor> searchByBloodGroup(String bloodGroup) {
        List<BloodDonor> list = new ArrayList<>();
        String sql = "SELECT * FROM blood_donors "
                   + "WHERE blood_group = ? AND available = TRUE";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bloodGroup);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new BloodDonor(
                    rs.getInt    ("id"),
                    rs.getString ("name"),
                    rs.getString ("blood_group"),
                    rs.getString ("phone"),
                    rs.getString ("city"),
                    rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Search by city
    public List<BloodDonor> searchByCity(String city) {
        List<BloodDonor> list = new ArrayList<>();
        String sql = "SELECT * FROM blood_donors WHERE city LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + city + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new BloodDonor(
                    rs.getInt    ("id"),
                    rs.getString ("name"),
                    rs.getString ("blood_group"),
                    rs.getString ("phone"),
                    rs.getString ("city"),
                    rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update availability
    public boolean updateAvailability(int id, boolean available) {
        String sql = "UPDATE blood_donors SET available = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt    (2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete donor
    public boolean deleteDonor(int id) {
        String sql = "DELETE FROM blood_donors WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Count by blood group
    public Map<String, Integer> countByBloodGroup() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT blood_group, COUNT(*) as total "
                   + "FROM blood_donors GROUP BY blood_group";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("blood_group"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    // Total donor count
    public int countDonors() {
        String sql = "SELECT COUNT(*) FROM blood_donors";
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