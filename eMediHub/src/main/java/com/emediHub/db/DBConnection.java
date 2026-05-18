package com.emediHub.db;

import java.sql.*;

public class DBConnection {

	private static final String URL  = "jdbc:mysql://localhost:3306/emediHub?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Admin_2101M";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
