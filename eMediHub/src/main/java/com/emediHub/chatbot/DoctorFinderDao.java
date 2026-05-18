package com.emediHub.chatbot;

import com.emediHub.db.DBConnection;
import java.sql.*;
import java.util.*;

public class DoctorFinderDao {

    // Find available doctors
    // by department
    public List<String[]> findByDepartment(String department) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT name, specialization, "
            + "phone, room_number, status "
            + "FROM doctors "
            + "WHERE department = ? "
            + "ORDER BY "
            + "CASE status "
            + "WHEN 'Available' THEN 1 "
            + "ELSE 2 END";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, department);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("name"),
                    rs.getString("specialization"),
                    rs.getString("phone"),
                    rs.getString("room_number"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Find by specialization
    // if department not found
    public List<String[]> findBySpecialization(String keyword) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT name, specialization, "
            + "phone, room_number, status "
            + "FROM doctors "
            + "WHERE specialization "
            + "LIKE ? "
            + "ORDER BY status";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("name"),
                    rs.getString("specialization"),
                    rs.getString("phone"),
                    rs.getString("room_number"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}