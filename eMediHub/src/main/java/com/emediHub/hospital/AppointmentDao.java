package com.emediHub.hospital;

import com.emediHub.db.DBConnection;
import java.sql.*;
import java.util.*;

public class AppointmentDao {

    // Get all appointments
    public List<String[]> getAll() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT a.id, "
            + "p.name as patient_name, "
            + "d.name as doctor_name, "
            + "d.department, "
            + "a.date, a.status, "
            + "a.remarks "
            + "FROM appointments a "
            + "LEFT JOIN patients p "
            + "ON a.patient_id = p.id "
            + "LEFT JOIN doctors d "
            + "ON a.doctor_id = d.id "
            + "ORDER BY a.id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getString("department"),
                    rs.getString("date"),
                    rs.getString("status"),
                    rs.getString("remarks")
                        != null
                        ? rs.getString("remarks")
                        : ""
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get by doctor
    public List<String[]> getByDoctor(String doctorName) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT a.id, "
            + "p.name as patient_name, "
            + "d.name as doctor_name, "
            + "d.department, "
            + "a.date, a.status, "
            + "a.remarks "
            + "FROM appointments a "
            + "LEFT JOIN patients p "
            + "ON a.patient_id = p.id "
            + "LEFT JOIN doctors d "
            + "ON a.doctor_id = d.id "
            + "WHERE d.name = ? "
            + "ORDER BY a.date ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, doctorName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getString("department"),
                    rs.getString("date"),
                    rs.getString("status"),
                    rs.getString("remarks")
                        != null
                        ? rs.getString("remarks")
                        : ""
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get by patient id
    public List<String[]> getByPatient(int patientId) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT a.id, "
            + "p.name as patient_name, "
            + "d.name as doctor_name, "
            + "d.department, "
            + "a.date, a.status, "
            + "a.remarks "
            + "FROM appointments a "
            + "LEFT JOIN patients p "
            + "ON a.patient_id = p.id "
            + "LEFT JOIN doctors d "
            + "ON a.doctor_id = d.id "
            + "WHERE a.patient_id = ? "
            + "ORDER BY a.date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getString("department"),
                    rs.getString("date"),
                    rs.getString("status"),
                    rs.getString("remarks")
                        != null
                        ? rs.getString("remarks")
                        : ""
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get pending appointments
    public List<String[]> getPending() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT a.id, "
            + "p.name as patient_name, "
            + "d.name as doctor_name, "
            + "d.department, "
            + "a.date, a.status, "
            + "a.remarks "
            + "FROM appointments a "
            + "LEFT JOIN patients p "
            + "ON a.patient_id = p.id "
            + "LEFT JOIN doctors d "
            + "ON a.doctor_id = d.id "
            + "WHERE a.status = 'Pending' "
            + "ORDER BY a.date ASC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getString("department"),
                    rs.getString("date"),
                    rs.getString("status"),
                    rs.getString("remarks")
                        != null
                        ? rs.getString("remarks")
                        : ""
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Update status — Approved/Rejected
    public boolean updateStatus(int id, String status, String remarks) {
        String sql =
            "UPDATE appointments "
            + "SET status = ?, "
            + "remarks = ? "
            + "WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, remarks);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Count by status
    public int countByStatus(String status) {
        String sql =
            "SELECT COUNT(*) "
            + "FROM appointments "
            + "WHERE status = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get patient email by appointment id
    public String getPatientEmail(int id) {
        String sql =
            "SELECT u.email "
            + "FROM appointments a "
            + "JOIN patients p "
            + "ON a.patient_id = p.id "
            + "JOIN users u "
            + "ON u.name = p.name "
            + "AND u.role = 'patient' "
            + "WHERE a.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString("email");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
