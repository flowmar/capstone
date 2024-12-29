package com.inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
	// private static final String URL = "jdbc:mysql://sql.freedb.tech:3306/freedb_inventory_app";
	// private static final String USER = "freedb_flowmar";
	// private static final String PASSWORD = "vG&7GQ3Q5u%Mv9h";
	private static final String URL = "jdbc:mysql://inventobot.c1k6qe4uye0z.us-east-2.rds.amazonaws" +
	                                  ".com:3306/inventory";
	private static final String USER = "admin";
	private static final String PASSWORD = "Passing123?!";
	// private static final String URL = "jdbc:mysql://localhost:3306/inventory";
	// private static final String USER = "flowmar";
	// private static final String PASSWORD = "passing321";
	
	private static final List<Connection> activeConnections = new ArrayList<>();
	
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
		Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
		activeConnections.add(conn);
		return conn;
	}
	
	public static void closeAllConnections() {
		List<Connection> connectionsToClose = new ArrayList<>(activeConnections);
		for ( Connection conn : connectionsToClose ) {
			releaseConnection(conn);
		}
		activeConnections.clear();
		System.out.println("All database connections closed.");
	}
	
	public static void releaseConnection(Connection conn) {
		if ( conn != null ) {
			try {
				if ( !conn.isClosed() ) {
					conn.close();
				}
				activeConnections.remove(conn);
			}
			catch ( SQLException e ) {
				System.err.println("Error closing connection:");
				e.printStackTrace();
			}
		}
	}
}
