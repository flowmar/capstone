package com.inventory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the Inventory Management System
 */
@TestMethodOrder( MethodOrderer.OrderAnnotation.class )
public class InventorySystemTest {
	// MySQL Connection Configuration
	// private static final String DB_URL = "jdbc:mysql://sql.freedb.tech:3306/freedb_inventory_app";
	// private static final String DB_USER = "freedb_flowmar";
	// private static final String DB_PASSWORD = "vG&7GQ3Q5u%Mv9h";
	private static final String DB_URL = "jdbc:mysql://inventobot-1.c1k6qe4uye0z.us-east-2.rds.amazonaws" +
	                                     ".com:3306/inventory";
	private static final String DB_USER = "admin";
	private static final String DB_PASSWORD = "passing123";
	private static Connection dbConnection;
	private static com.inventory.Inventory inventory;
	private static int testProductId;
	private static int testPartId;
	
	@BeforeAll
	static void setUp() throws SQLException, IOException {
		inventory = new com.inventory.Inventory();
		initializeDatabase();
	}
	
	private static void initializeDatabase() throws SQLException, IOException {
		// Establish database connection
		dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		
		// Read SQL script from resources
		InputStream is = InventorySystemTest.class.getResourceAsStream("/init-db.sql");
		if ( is == null ) {
			throw new RuntimeException("Could not find init-db.sql");
		}
		
		String sql = new BufferedReader(new InputStreamReader(is))
				.lines()
				.collect(Collectors.joining("\n"));
		
		// Split SQL script into individual statements
		String[] statements = sql.split(";");
		
		// Execute each statement
		try ( Statement stmt = dbConnection.createStatement() ) {
			for ( String statement : statements ) {
				if ( !statement.trim().isEmpty() ) {
					stmt.execute(statement);
				}
			}
		}
	}
	
	@AfterAll
	static void tearDown() {
		try {
			if ( dbConnection != null && !dbConnection.isClosed() ) {
				try ( Statement stmt = dbConnection.createStatement() ) {
					stmt.execute("DROP TABLE IF EXISTS product_parts");
					stmt.execute("DROP TABLE IF EXISTS products");
					stmt.execute("DROP TABLE IF EXISTS parts");
				}
				dbConnection.close();
			}
		}
		catch ( SQLException e ) {
			System.err.println("Error cleaning up test data: " + e.getMessage());
		}
	}
	
	@Test
	@Order( 1 )
	@DisplayName( "Test Part Creation and Database Insertion" )
	void testPartCreation() throws SQLException {
		// Create a test part with constructor parameters
		com.inventory.InHouse testPart = new com.inventory.InHouse(0, "Test Part", 19.99, 10, 5, 20, 1001);
		
		// Insert the part
		String sql = "INSERT INTO parts (name, price, stock, min, max, type, machine_id) " +
		             "VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS) ) {
			stmt.setString(1, testPart.getName());
			stmt.setDouble(2, testPart.getPrice());
			stmt.setInt(3, testPart.getStock());
			stmt.setInt(4, testPart.getMin());
			stmt.setInt(5, testPart.getMax());
			stmt.setString(6, "InHouse");
			stmt.setInt(7, testPart.getMachineId());
			
			int affectedRows = stmt.executeUpdate();
			
			assertTrue(affectedRows > 0, "Part should be inserted successfully");
			
			// Get the generated id
			try ( ResultSet generatedKeys = stmt.getGeneratedKeys() ) {
				if ( generatedKeys.next() ) {
					testPartId = generatedKeys.getInt(1);
				}
			}
			
			// Verify the part exists
			try ( PreparedStatement selectStmt = dbConnection.prepareStatement("SELECT * FROM parts WHERE id = ?") ) {
				selectStmt.setInt(1, testPartId);
				try ( ResultSet rs = selectStmt.executeQuery() ) {
					assertTrue(rs.next(), "Part should exist in database");
					assertEquals("Test Part", rs.getString("name"), "Part name should match");
					assertEquals(19.99, rs.getDouble("price"), 0.001, "Part price should match");
				}
			}
		}
	}
	
	@Test
	@Order( 2 )
	@DisplayName( "Test Product Creation with Associated Part" )
	void testProductCreation() throws SQLException {
		// Create empty parts list for the product
		ObservableList<com.inventory.Part> parts = FXCollections.observableArrayList();
		
		// Create a test product with constructor parameters
		com.inventory.Product testProduct = new com.inventory.Product(0, "Test Product", 99.99, 5, 1, 10, parts);
		
		// Insert the product
		String sql = "INSERT INTO products (name, price, stock, min, max) VALUES (?, ?, ?, ?, ?)";
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS) ) {
			stmt.setString(1, testProduct.getName());
			stmt.setDouble(2, testProduct.getPrice());
			stmt.setInt(3, testProduct.getStock());
			stmt.setInt(4, testProduct.getMin());
			stmt.setInt(5, testProduct.getMax());
			
			int affectedRows = stmt.executeUpdate();
			assertTrue(affectedRows > 0, "Product should be inserted successfully");
			
			// Get the generated id
			try ( ResultSet generatedKeys = stmt.getGeneratedKeys() ) {
				if ( generatedKeys.next() ) {
					testProductId = generatedKeys.getInt(1);
				}
			}
			
			// Associate the part with the product
			sql = "INSERT INTO product_parts (product_id, part_id) VALUES (?, ?)";
			try ( PreparedStatement assocStmt = dbConnection.prepareStatement(sql) ) {
				assocStmt.setInt(1, testProductId);
				assocStmt.setInt(2, testPartId);
				
				affectedRows = assocStmt.executeUpdate();
				assertTrue(affectedRows > 0, "Product-Part association should be created successfully");
			}
			
			// Verify the product exists
			try ( PreparedStatement selectStmt = dbConnection.prepareStatement("SELECT * FROM products WHERE id = ?") ) {
				selectStmt.setInt(1, testProductId);
				try ( ResultSet rs = selectStmt.executeQuery() ) {
					assertTrue(rs.next(), "Product should exist in database");
					assertEquals("Test Product", rs.getString("name"), "Product name should match");
					assertEquals(99.99, rs.getDouble("price"), 0.001, "Product price should match");
				}
			}
		}
	}
	
	@Test
	@Order( 3 )
	@DisplayName( "Test Inventory Report Generation" )
	void testInventoryReport() throws SQLException {
		// Verify product statistics
		String sql = "SELECT COUNT(*) as count, SUM(stock) as total_stock FROM products";
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql) ) {
			try ( ResultSet rs = stmt.executeQuery() ) {
				assertTrue(rs.next(), "Should get product statistics");
				assertTrue(rs.getInt("count") > 0, "Should have at least one product");
				assertTrue(rs.getInt("total_stock") > 0, "Should have positive total stock");
			}
		}
		
		// Verify part statistics
		sql = "SELECT COUNT(*) as count, SUM(stock) as total_stock FROM parts";
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql) ) {
			try ( ResultSet rs = stmt.executeQuery() ) {
				assertTrue(rs.next(), "Should get part statistics");
				assertTrue(rs.getInt("count") > 0, "Should have at least one part");
				assertTrue(rs.getInt("total_stock") > 0, "Should have positive total stock");
			}
		}
		
		// Verify product-part relationships
		sql = "SELECT COUNT(*) as count FROM product_parts";
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql) ) {
			try ( ResultSet rs = stmt.executeQuery() ) {
				assertTrue(rs.next(), "Should get relationship statistics");
				assertTrue(rs.getInt("count") > 0, "Should have at least one product-part relationship");
			}
		}
	}
	
	@Test
	@Order( 4 )
	@DisplayName( "Test Stock Level Validation" )
	void testStockValidation() throws SQLException {
		// Update test product stock
		String sql = "UPDATE products SET stock = ? WHERE id = ?";
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql) ) {
			stmt.setInt(1, 0); // Set stock to 0
			stmt.setInt(2, testProductId);
			stmt.executeUpdate();
		}
		
		// Verify low stock detection
		sql = "SELECT COUNT(*) as count FROM products WHERE stock < min AND id = ?";
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql) ) {
			stmt.setInt(1, testProductId);
			try ( ResultSet rs = stmt.executeQuery() ) {
				assertTrue(rs.next(), "Should get low stock count");
				assertEquals(1, rs.getInt("count"), "Product should be marked as low stock");
			}
		}
		
		// Test stock update
		sql = "UPDATE products SET stock = ? WHERE id = ?";
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql) ) {
			stmt.setInt(1, 5); // Set stock back to normal
			stmt.setInt(2, testProductId);
			int affectedRows = stmt.executeUpdate();
			
			assertEquals(1, affectedRows, "Stock update should be successful");
		}
	}
	
	@Test
	@Order( 5 )
	@DisplayName( "Test Product-Part Association" )
	void testProductPartAssociation() throws SQLException {
		// Verify the association exists
		String sql = "SELECT * FROM product_parts WHERE product_id = ? AND part_id = ?";
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql) ) {
			stmt.setInt(1, testProductId);
			stmt.setInt(2, testPartId);
			try ( ResultSet rs = stmt.executeQuery() ) {
				assertTrue(rs.next(), "Product-Part association should exist");
			}
		}
		
		// Test removing the association
		sql = "DELETE FROM product_parts WHERE product_id = ? AND part_id = ?";
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql) ) {
			stmt.setInt(1, testProductId);
			stmt.setInt(2, testPartId);
			int affectedRows = stmt.executeUpdate();
			
			assertEquals(1, affectedRows, "Association should be removed successfully");
		}
		
		// Verify the association is removed
		sql = "SELECT COUNT(*) as count FROM product_parts WHERE product_id = ? AND part_id = ?";
		try ( PreparedStatement stmt = dbConnection.prepareStatement(sql) ) {
			stmt.setInt(1, testProductId);
			stmt.setInt(2, testPartId);
			try ( ResultSet rs = stmt.executeQuery() ) {
				assertTrue(rs.next(), "Should get association count");
				assertEquals(0, rs.getInt("count"), "Association should not exist");
			}
		}
	}
}
