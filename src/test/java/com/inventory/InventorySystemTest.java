package com.inventory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Test class for the Inventory Management System
 *
 * @author Omar Imam
 */
@TestMethodOrder( MethodOrderer.OrderAnnotation.class )
class InventorySystemTest {
	// MySQL Connection Configuration
	private static final String DB_URL = "jdbc:mysql://inventobot.c1k6qe4uye0z.us-east-2.rds.amazonaws.com:3306/inventory?useSSL=true&requireSSL=false&verifyServerCertificate=false";
	private static final String DB_USER = "admin";
	private static final String DB_PASSWORD = "Passing123?!";
	// private static final String DB_URL = "jdbc:mysql://sql.freedb.tech:3306/freedb_inventory_app";
	// private static final String DB_USER = "freedb_flowmar";
	// private static final String DB_PASSWORD = "vG&7GQ3Q5u%Mv9h";
	// private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory";
	// private static final String DB_USER = "flowmar";
	// private static final String DB_PASSWORD = "passing321";
	private static Connection dbConnection;
	private static Inventory inventory;
	private static int testProductId;
	
	@BeforeAll
	static void setUp() {
		try {
			// Initialize database connection
			dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			inventory = new Inventory();
			
			// Clean up any existing test data
			cleanupTestData();
			
			System.out.println("Test setup completed successfully");
		}
		catch ( SQLException e ) {
			System.err.println("Error during test setup:");
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test environment", e);
		}
	}
	
	private static void cleanupTestData() throws SQLException {
		// Delete test products and parts
		String[] cleanupQueries = {
				"DELETE FROM product_parts WHERE product_id IN (SELECT id FROM products WHERE name LIKE 'Test%')",
				"DELETE FROM products WHERE name LIKE 'Test%'",
				"DELETE FROM parts WHERE name LIKE 'Test%'"
		};
		
		for ( String query : cleanupQueries ) {
			try ( PreparedStatement stmt = dbConnection.prepareStatement(query) ) {
				stmt.executeUpdate();
			}
		}
	}
	
	@AfterAll
	static void tearDown() {
		try {
			// Clean up test data
			cleanupTestData();
			
			// Close database connection
			if ( dbConnection != null && !dbConnection.isClosed() ) {
				dbConnection.close();
			}
			System.out.println("Test teardown completed successfully");
		}
		catch ( SQLException e ) {
			System.err.println("Error during test teardown:");
			e.printStackTrace();
		}
	}
	
	@Test
	@Order( 1 )
	@DisplayName( "Test adding a part to inventory" )
	void testAddPart() {
		// Create a test part
		Part testPart = new InHouse(1, "Test Part", 10.0, 5, 1, 10, 123);
		
		// Add the part to inventory
		inventory.addPart(testPart);
		
		// Verify the part was added
		ObservableList<Part> allParts = Inventory.getAllParts();
		Assertions.assertTrue(allParts.stream()
		                              .anyMatch(p -> p.getName().equals("Test Part")), "Part should be in inventory");
	}
	
	@Test
	@Order( 2 )
	@DisplayName( "Test adding a product" )
	void testAddProduct() {
		// Create and add test parts first
		Part part1 = new InHouse(0, "Test Part 1", 5.0, 10, 1, 20, 124);
		Part part2 = new Outsourced(0, "Test Part 2", 7.0, 8, 1, 15, "Test Company");
		int part1Id = inventory.addPart(part1);
		int part2Id = inventory.addPart(part2);
		
		// Verify parts were added successfully
		Assertions.assertTrue(part1Id > 0, "Part 1 should have a valid ID");
		Assertions.assertTrue(part2Id > 0, "Part 2 should have a valid ID");
		
		// Create a test product with empty parts list
		ObservableList<Part> parts = FXCollections.observableArrayList();
		Product testProduct = new Product(0, "Test Product", 20.0, 3, 1, 5, parts);
		
		// Add the associated parts that are now in the database
		testProduct.addAssociatedPart(part1);
		testProduct.addAssociatedPart(part2);
		
		// Add the product to inventory
		inventory.addProduct(testProduct);
		
		// Verify the product was added
		ObservableList<Product> allProducts = Inventory.getAllProducts();
		Assertions.assertTrue(allProducts.stream()
		                                 .anyMatch(p -> p.getName().equals("Test Product")),
		                      "Product should be in inventory");
	}
	
	@Test
	@Order( 3 )
	@DisplayName( "Test loading parts from database" )
	void testLoadPartsFromDatabase() {
		// Clear existing parts
		Inventory.setAllParts(FXCollections.observableArrayList());
		
		// Load parts from database
		inventory.loadPartsFromDatabase();
		
		// Verify parts were loaded
		ObservableList<Part> allParts = Inventory.getAllParts();
		Assertions.assertFalse(allParts.isEmpty(), "Parts should be loaded from database");
	}
	
	@Test
	@Order( 4 )
	@DisplayName( "Test loading products from database" )
	void testLoadProductsFromDatabase() {
		// Clear existing products
		Inventory.setAllProducts(FXCollections.observableArrayList());
		
		// Load products from database
		inventory.loadProductsFromDatabase();
		
		// Verify products were loaded
		ObservableList<Product> allProducts = Inventory.getAllProducts();
		Assertions.assertFalse(allProducts.isEmpty(), "Products should be loaded from database");
	}
	
	@Test
	@Order( 5 )
	@DisplayName( "Test updating a product" )
	void testUpdateProduct() {
		// Create and add a test product with empty parts list
		ObservableList<Part> parts = FXCollections.observableArrayList();
		Product originalProduct = new Product(4, "Test Product Original", 25.0, 4, 1, 8, parts);
		inventory.addProduct(originalProduct);
		
		// Create modified product with empty parts list
		ObservableList<Part> modifiedParts = FXCollections.observableArrayList();
		Product modifiedProduct = new Product(4, "Test Product Modified", 30.0, 5, 1, 10, modifiedParts);
		
		// Update the product
		Inventory.updateProduct(Inventory.getAllProducts().size() - 1, modifiedProduct);
		
		// Verify the product was updated
		ObservableList<Product> allProducts = Inventory.getAllProducts();
		Assertions.assertTrue(allProducts.stream()
		                                 .anyMatch(p -> p.getName()
		                                                 .equals("Test Product Modified")),
		                      "Product should be updated in inventory");
	}
}
