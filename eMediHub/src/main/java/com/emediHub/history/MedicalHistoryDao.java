package com.emediHub.history;

import com.emediHub.db.DBConnection;
import java.sql.*;
import java.util.*;

public class MedicalHistoryDao {

    // Add new visit record
    public boolean addHistory(MedicalHistory h) {
        String sql =
            "INSERT INTO medical_history("
            + "patient_name, doctor_name, "
            + "visit_date, diagnosis, "
            + "treatment, symptoms, "
            + "blood_pressure, temperature, "
            + "weight, notes, follow_up_date) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con =DBConnection.getConnection();
             PreparedStatement ps =con.prepareStatement(sql)) {
            ps.setString(1,h.getPatientName());
            ps.setString(2,h.getDoctorName());
            ps.setString(3,h.getVisitDate());
            ps.setString(4,h.getDiagnosis());
            ps.setString(5,h.getTreatment());
            ps.setString(6,h.getSymptoms());
            ps.setString(7,h.getBloodPressure());
            ps.setString(8,h.getTemperature());
            ps.setString(9,h.getWeight());
            ps.setString(10,h.getNotes());
            ps.setString(11,h.getFollowUpDate().isEmpty() ? null: h.getFollowUpDate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all history
    public List<MedicalHistory> getAll() {
        List<MedicalHistory> list =new ArrayList<>();
        String sql =
            "SELECT * FROM medical_history "
            + "ORDER BY id DESC";
        try (Connection con =DBConnection.getConnection();
             Statement st =con.createStatement();
             ResultSet rs =st.executeQuery(sql)) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get history by patient name
    public List<MedicalHistory> getByPatient(String name) {
        List<MedicalHistory> list =new ArrayList<>();
        String sql =
            "SELECT * FROM medical_history "
            + "WHERE patient_name LIKE ? "
            + "ORDER BY visit_date DESC";
        try (Connection con =DBConnection.getConnection();
             PreparedStatement ps =con.prepareStatement(sql)) {
            ps.setString(1,"%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get history by doctor name
    public List<MedicalHistory> getByDoctor(String doctorName) {
        List<MedicalHistory> list =new ArrayList<>();
        String sql =
            "SELECT * FROM medical_history "
            + "WHERE doctor_name = ? "
            + "ORDER BY visit_date DESC";
        try (Connection con =DBConnection.getConnection();
             PreparedStatement ps =con.prepareStatement(sql)) {
            ps.setString(1, doctorName);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get single record by id
    public MedicalHistory getById(int id) {
        String sql =
            "SELECT * FROM medical_history "
            + "WHERE id = ?";
        try (Connection con =DBConnection.getConnection();
             PreparedStatement ps =con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get upcoming follow ups
    public List<MedicalHistory>getUpcomingFollowUps() {
        List<MedicalHistory> list =new ArrayList<>();
        String sql =
            "SELECT * FROM medical_history "
            + "WHERE follow_up_date >= CURDATE() "
            + "AND follow_up_date IS NOT NULL "
            + "ORDER BY follow_up_date ASC "
            + "LIMIT 10";
        try (Connection con =DBConnection.getConnection();
             Statement st =con.createStatement();
             ResultSet rs =st.executeQuery(sql)) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Delete record
    public boolean delete(int id) {
        String sql =
            "DELETE FROM medical_history "
            + "WHERE id = ?";
        try (Connection con =DBConnection.getConnection();
             PreparedStatement ps =con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Total visits count
    public int countAll() {
        String sql =
            "SELECT COUNT(*) " + "FROM medical_history";
        try (Connection con =DBConnection.getConnection();
             Statement st =con.createStatement();
             ResultSet rs =st.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Count follow ups today
    public int countFollowUpsToday() {
        String sql =
            "SELECT COUNT(*) "
            + "FROM medical_history "
            + "WHERE follow_up_date = CURDATE()";
        try (Connection con =DBConnection.getConnection();
             Statement st =con.createStatement();
             ResultSet rs =st.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private MedicalHistory mapRow(ResultSet rs)throws SQLException {
        return new MedicalHistory(
            rs.getInt   ("id"),
            rs.getString("patient_name"),
            rs.getString("doctor_name"),
            rs.getString("visit_date"),
            rs.getString("diagnosis"),
            rs.getString("treatment"),
            rs.getString("symptoms"),
            rs.getString("blood_pressure"),
            rs.getString("temperature"),
            rs.getString("weight"),
            rs.getString("notes"),
            rs.getString("follow_up_date") != null ? rs.getString("follow_up_date") : "",
            rs.getString("created_at")
        );
    }
}
