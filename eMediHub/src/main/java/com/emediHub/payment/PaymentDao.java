package com.emediHub.payment;

import com.emediHub.db.DBConnection;
import java.sql.*;
import java.util.*;

public class PaymentDao {

    // Save new payment
    public boolean savePayment(Payment p) {
        String sql =
            "INSERT INTO payments(patient_name, payment_type, "
            + "amount, payment_method, upi_id, status, "
            + "transaction_id, remarks) "
            + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getPatientName());
            ps.setString(2, p.getPaymentType());
            ps.setDouble(3, p.getAmount());
            ps.setString(4, p.getPaymentMethod());
            ps.setString(5, p.getUpiId());
            ps.setString(6, p.getStatus());
            ps.setString(7, p.getTransactionId());
            ps.setString(8, p.getRemarks());
            ps.executeUpdate();

            // ✅ Generated ID fetch karo
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                p.setId(newId);

                // ✅ created_at DB se fetch karo
                String dateSql =
                    "SELECT created_at "
                    + "FROM payments "
                    + "WHERE id = ?";
                PreparedStatement ds =con.prepareStatement(dateSql);
                ds.setInt(1, newId);
                ResultSet dateRs =ds.executeQuery();
                if (dateRs.next()) {
                    // ✅ Payment object mein set
                    p.setCreatedAt(dateRs.getString("created_at"));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all payments
    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get payments by patient name
    public List<Payment> getPaymentsByPatient(String name) {
        List<Payment> list = new ArrayList<>();
        String sql =
            "SELECT * FROM payments "
            + "WHERE patient_name LIKE ? ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get payments by type
    public List<Payment> getPaymentsByType(String type) {
        List<Payment> list = new ArrayList<>();
        String sql =
            "SELECT * FROM payments "
            + "WHERE payment_type = ? ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update payment status
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE payments SET status=? WHERE id=?";
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

    // Total revenue
    public double getTotalRevenue() {
        String sql =
            "SELECT SUM(amount) FROM payments "
            + "WHERE status='Paid'";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Count payments by status
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM payments WHERE status=?";
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

    // Helper map row
    private Payment mapRow(ResultSet rs) throws SQLException {
        return new Payment(
            rs.getInt   ("id"),
            rs.getString("patient_name"),
            rs.getString("payment_type"),
            rs.getDouble("amount"),
            rs.getString("payment_method"),
            rs.getString("upi_id"),
            rs.getString("status"),
            rs.getString("transaction_id"),
            rs.getString("remarks"),
            rs.getString("created_at")
        );
    }
}
