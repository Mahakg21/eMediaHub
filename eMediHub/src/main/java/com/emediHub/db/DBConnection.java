package com.emediHub.db;

import java.sql.*;

public class DBConnection {

	private static final String URL  = "dbpath";
    private static final String USER = "root";
    private static final String PASS = "yourdbpassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
