package com.emediHub.prescription;

import com.emediHub.db.DBConnection;
import java.sql.*;
import java.util.*;

public class PrescriptionDao {

    // Save full prescription with all medicine items
    public int savePrescription(Prescription p) {
        String sql =
            "INSERT INTO prescriptions"
            + "(patient_name, doctor_name, "
            + "diagnosis, notes) "
            + "VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getPatientName());
            ps.setString(2, p.getDoctorName());
            ps.setString(3, p.getDiagnosis());
            ps.setString(4, p.getNotes());
            ps.executeUpdate();

            // Get generated prescription ID
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int prescriptionId = keys.getInt(1);

                // Save all medicine items
                if (p.getItems() != null) {
                    for (PrescriptionItem item: p.getItems()) {
                        saveItem(con,prescriptionId, item);
                    }
                }
                return prescriptionId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Save single medicine item
    private void saveItem(Connection con, int prescriptionId, PrescriptionItem item) throws SQLException {
        String sql =
            "INSERT INTO prescription_items"
            + "(prescription_id, medicine_name, "
            + "dosage, frequency, duration, "
            + "instructions) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps =con.prepareStatement(sql);
        ps.setInt   (1, prescriptionId);
        ps.setString(2, item.getMedicineName());
        ps.setString(3, item.getDosage());
        ps.setString(4, item.getFrequency());
        ps.setString(5, item.getDuration());
        ps.setString(6, item.getInstructions());
        ps.executeUpdate();
    }

    // Get all prescriptions
    public List<Prescription> getAllPrescriptions() {
        List<Prescription> list = new ArrayList<>();
        String sql =
            "SELECT * FROM prescriptions "
            + "ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get prescriptions by patient name
    public List<Prescription> getByPatient( String name) {
        List<Prescription> list = new ArrayList<>();
        String sql =
            "SELECT * FROM prescriptions "
            + "WHERE patient_name LIKE ? "
            + "ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =con.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get prescriptions by doctor name
    public List<Prescription> getByDoctor( String doctorName) {
        List<Prescription> list = new ArrayList<>();
        String sql =
            "SELECT * FROM prescriptions "
            + "WHERE doctor_name = ? "
            + "ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, doctorName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get medicine items for a prescription
    public List<PrescriptionItem> getItems( int prescriptionId) {
        List<PrescriptionItem> list = new ArrayList<>();
        String sql =
            "SELECT * FROM prescription_items "
            + "WHERE prescription_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, prescriptionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PrescriptionItem(
                    rs.getInt   ("id"),
                    rs.getInt   ("prescription_id"),
                    rs.getString("medicine_name"),
                    rs.getString("dosage"),
                    rs.getString("frequency"),
                    rs.getString("duration"),
                    rs.getString("instructions")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Delete prescription
    public boolean deletePrescription(int id) {
        String sql ="DELETE FROM prescriptions WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Total prescription count
    public int countPrescriptions() {
        String sql = "SELECT COUNT(*) FROM prescriptions";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Prescription mapRow(ResultSet rs)throws SQLException {
        return new Prescription(
            rs.getInt   ("id"),
            rs.getString("patient_name"),
            rs.getString("doctor_name"),
            rs.getString("diagnosis"),
            rs.getString("notes"),
            rs.getString("created_at")
        );
    }
}
