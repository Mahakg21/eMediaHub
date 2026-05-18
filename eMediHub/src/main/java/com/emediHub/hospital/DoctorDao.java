package com.emediHub.hospital;

import com.emediHub.db.DBConnection;
import java.sql.*;
import java.util.*;

public class DoctorDao {

    // Add doctor
    public boolean addDoctor(Doctor d) {
        String sql =
            "INSERT INTO doctors"
            + "(name, specialization, phone, email, status)"
            + " VALUES (?, ?, ?, ?, 'Available')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =con.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getSpecialization());
            ps.setString(3, d.getPhone());
            ps.setString(4, d.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all doctors with status
    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY "
                   + "CASE status "
                   + "WHEN 'Available' THEN 1 "
                   + "WHEN 'Busy' THEN 2 "
                   + "WHEN 'On Leave' THEN 3 "
                   + "ELSE 4 END, name";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Doctor(
                    rs.getInt   ("id"),
                    rs.getString("name"),
                    rs.getString("specialization"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Update doctor status
    public boolean updateStatus(int id, String status) {
        String sql ="UPDATE doctors SET status=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt   (2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get doctors by status
    public List<Doctor> getDoctorsByStatus(String status) {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE status=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Doctor(
                    rs.getInt   ("id"),
                    rs.getString("name"),
                    rs.getString("specialization"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Count by status
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM doctors " + "WHERE status=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Delete doctor
    public boolean deleteDoctor(int id) {
        String sql = "DELETE FROM doctors WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Count total doctors
    public int countDoctors() {
        String sql = "SELECT COUNT(*) FROM doctors";
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