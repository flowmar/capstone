package com.inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String URL = "jdbc:mysql://sql.freedb.tech:3306/freedb_inventory_app";
	private static final String USER = "freedb_flowmar";
	private static final String PASSWORD = "vG&7GQ3Q5u%Mv9h";
	
	
	static {
		try {
			// Load the MySQL JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("MySQL JDBC Driver registered successfully");
		}
		catch ( ClassNotFoundException e ) {
			System.err.println("MySQL JDBC Driver not found");
			e.printStackTrace();
			throw new RuntimeException("MySQL JDBC Driver not found", e);
		}
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
	
	public static void releaseConnection(Connection conn) {
		if ( conn != null ) {
			try {
				conn.close();
			}
			catch ( SQLException e ) {
				e.printStackTrace();
			}
		}
	}
	
	public static void closeAllConnections() {
		// No-op method for compatibility
	}
}
