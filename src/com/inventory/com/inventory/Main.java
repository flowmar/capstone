package com.inventory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize database connection pool
		initializeDatabaseConnection();
		
		// Load login scene
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/inventoryScene.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("Inventory Management System - Login");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void initializeDatabaseConnection() {
		try {
			// This will trigger the static initialization of the connection pool
			Connection conn = com.inventory.DatabaseConnection.getConnection();
			// Immediately release the connection back to the pool
			com.inventory.DatabaseConnection.releaseConnection(conn);
			System.out.println("Database connection initialized successfully");
		}
		catch ( SQLException e ) {
			System.err.println("Failed to initialize database connection");
			e.printStackTrace();
			// You might want to show an error dialog or exit the application
			System.exit(1);
		}
	}
	
	@Override
	public void stop() throws Exception {
		// Close all database connections when application exits
		com.inventory.DatabaseConnection.closeAllConnections();
		super.stop();
	}
}
