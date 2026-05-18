package com.emediHub.lab;

import com.emediHub.db.DBConnection;
import java.sql.*;
import java.util.*;

public class LabReportDao {

    // Add new report
    public boolean addReport(LabReport r) {
        String sql =
            "INSERT INTO lab_reports("
            + "patient_name, doctor_name,"
            + "test_name, test_date,"
            + "result, normal_range,"
            + "unit, status, remarks,"
            + "lab_technician) "
            + "VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS )) {
            ps.setString(1, r.getPatientName());
            ps.setString(2, r.getDoctorName());
            ps.setString(3, r.getTestName());
            ps.setString(4, r.getTestDate());
            ps.setString(5, r.getResult());
            ps.setString(6, r.getNormalRange());
            ps.setString(7, r.getUnit());
            ps.setString(8, r.getStatus());
            ps.setString(9, r.getRemarks());
            ps.setString(10, r.getLabTechnician());
            ps.executeUpdate();

            // Get generated ID + date
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                r.setId(newId);

                // Fetch created_at
                String dateSql =
                    "SELECT created_at "
                    + "FROM lab_reports "
                    + "WHERE id = ?";
                PreparedStatement ds = con.prepareStatement(dateSql);
                ds.setInt(1, newId);
                ResultSet dateRs = ds.executeQuery();
                if (dateRs.next())
                    r.setCreatedAt(dateRs.getString("created_at"));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all reports
    public List<LabReport> getAll() {
        List<LabReport> list = new ArrayList<>();
        String sql = "SELECT * FROM lab_reports " + "ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get by patient
    public List<LabReport> getByPatient(String name) {
        List<LabReport> list = new ArrayList<>();
        String sql =
            "SELECT * FROM lab_reports "
            + "WHERE patient_name LIKE ? "
            + "ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1,"%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get by doctor
    public List<LabReport> getByDoctor(String doctorName) {
        List<LabReport> list = new ArrayList<>();
        String sql =
            "SELECT * FROM lab_reports "
            + "WHERE doctor_name = ? "
            + "ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, doctorName);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get by status
    public List<LabReport> getByStatus(String status) {
        List<LabReport> list = new ArrayList<>();
        String sql =
            "SELECT * FROM lab_reports "
            + "WHERE status = ? "
            + "ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Delete report
    public boolean delete(int id) {
        String sql = "DELETE FROM lab_reports " + "WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Count total
    public int countTotal() {
        return getCount("SELECT COUNT(*) " + "FROM lab_reports");
    }

    // Count abnormal
    public int countAbnormal() {
        return getCount(
            "SELECT COUNT(*) "
            + "FROM lab_reports "
            + "WHERE status = 'Abnormal'");
    }

    private int getCount(String sql) {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private LabReport mapRow(ResultSet rs) throws SQLException {
        return new LabReport(
            rs.getInt   ("id"),
            rs.getString("patient_name"),
            rs.getString("doctor_name"),
            rs.getString("test_name"),
            rs.getString("test_date"),
            rs.getString("result"),
            rs.getString("normal_range"),
            rs.getString("unit"),
            rs.getString("status"),
            rs.getString("remarks"),
            rs.getString("lab_technician"),
            rs.getString("created_at")
        );
    }
}
