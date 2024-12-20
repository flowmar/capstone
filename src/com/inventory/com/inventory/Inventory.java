package com.inventory;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Inventory extends Application {
	
	private static ObservableList<com.inventory.Part> allParts = FXCollections.observableArrayList();
	private static ObservableList<com.inventory.Product> allProducts = FXCollections.observableArrayList();
	private static com.inventory.Part selectedPart;
	private static int selectedPartIndex;
	private static int selectedPartId;
	private static com.inventory.Product selectedProduct;
	private static int selectedProductIndex;
	private static int selectedProductId;
	
	public static ObservableList<com.inventory.Part> getAllParts() {
		return allParts;
	}
	
	public static void setAllParts(ObservableList<com.inventory.Part> parts) {
		allParts = parts;
	}
	
	public static ObservableList<com.inventory.Product> getAllProducts() {
		return allProducts;
	}
	
	public static void setAllProducts(ObservableList<com.inventory.Product> products) {
		allProducts = products;
	}
	
	public static com.inventory.Part getSelectedPart() {
		return selectedPart;
	}
	
	public static void setSelectedPart(com.inventory.Part part) {
		selectedPart = part;
	}
	
	public static int getSelectedPartIndex() {
		return selectedPartIndex;
	}
	
	public static void setSelectedPartIndex(int index) {
		selectedPartIndex = index;
	}
	
	public static int getSelectedPartId() {
		return selectedPartId;
	}
	
	public static void setSelectedPartId(int id) {
		selectedPartId = id;
	}
	
	public static com.inventory.Product getSelectedProduct() {
		return selectedProduct;
	}
	
	public static void setSelectedProduct(com.inventory.Product product) {
		selectedProduct = product;
	}
	
	public static int getSelectedProductIndex() {
		return selectedProductIndex;
	}
	
	public static void setSelectedProductIndex(int index) {
		selectedProductIndex = index;
	}
	
	public static int getSelectedProductId() {
		return selectedProductId;
	}
	
	public static void setSelectedProductId(int id) {
		selectedProductId = id;
	}
	
	public static void updateProduct(int index, com.inventory.Product product) {
		if (index >= 0 && index < getAllProducts().size()) {
			getAllProducts().set(index, product);
		}
	}
	
	protected void updateLoadingMessage(String message) {
		System.out.println(message);
		// This method will be overridden in the anonymous subclass
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		try {
			Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(
					"/com/inventory/inventoryScene.fxml")));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Inventory Management System");
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch ( Exception e ) {
			System.out.println("Error starting application:");
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() {
		com.inventory.DatabaseConnection.closeAllConnections();
	}
	
	public void loadPartsFromDatabase() {
		String sql = "SELECT * FROM parts";
		try ( PreparedStatement stmt = com.inventory.DatabaseConnection.getConnection().prepareStatement(sql) ) {
			ResultSet rs = stmt.executeQuery();
			while ( rs.next() ) {
				com.inventory.Part part;
				if ( rs.getString("type").equals("InHouse") ) {
					part = new com.inventory.InHouse(
							rs.getInt("id"),
							rs.getString("name"),
							rs.getDouble("price"),
							rs.getInt("stock"),
							rs.getInt("min"),
							rs.getInt("max"),
							rs.getInt("machine_id")
					);
				} else {
					part = new com.inventory.Outsourced(
							rs.getInt("id"),
							rs.getString("name"),
							rs.getDouble("price"),
							rs.getInt("stock"),
							rs.getInt("min"),
							rs.getInt("max"),
							rs.getString("company_name")
					);
				}
				getAllParts().add(part);
				updateLoadingMessage("Loaded part: " + part.getName());
			}
		}
		catch ( SQLException e ) {
			String errorMsg = "Error loading parts from database: " + e.getMessage();
			System.out.println(errorMsg);
			e.printStackTrace();
			updateLoadingMessage(errorMsg);
		}
	}
	
	public void loadProductsFromDatabase() {
		String sql = "SELECT * FROM products";
		try ( PreparedStatement stmt = com.inventory.DatabaseConnection.getConnection().prepareStatement(sql) ) {
			ResultSet rs = stmt.executeQuery();
			while ( rs.next() ) {
				ObservableList<com.inventory.Part> associatedParts = FXCollections.observableArrayList();
				com.inventory.Product product = new com.inventory.Product(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getDouble("price"),
						rs.getInt("stock"),
						rs.getInt("min"),
						rs.getInt("max"),
						associatedParts
				);
				
				// Load associated parts for this product
				String assocSql = "SELECT p.* FROM parts p JOIN product_parts pp ON p.id = pp.part_id WHERE pp.product_id = ?";
				try ( PreparedStatement assocStmt = com.inventory.DatabaseConnection.getConnection()
				                                                                    .prepareStatement(assocSql) ) {
					assocStmt.setInt(1, product.getId());
					ResultSet assocRs = assocStmt.executeQuery();
					while ( assocRs.next() ) {
						if ( assocRs.getString("type").equals("InHouse") ) {
							associatedParts.add(new com.inventory.InHouse(
									assocRs.getInt("id"),
									assocRs.getString("name"),
									assocRs.getDouble("price"),
									assocRs.getInt("stock"),
									assocRs.getInt("min"),
									assocRs.getInt("max"),
									assocRs.getInt("machine_id")
							));
						} else {
							associatedParts.add(new com.inventory.Outsourced(
									assocRs.getInt("id"),
									assocRs.getString("name"),
									assocRs.getDouble("price"),
									assocRs.getInt("stock"),
									assocRs.getInt("min"),
									assocRs.getInt("max"),
									assocRs.getString("company_name")
							));
						}
					}
				}
				
				getAllProducts().add(product);
				updateLoadingMessage("Loaded product: " + product.getName());
			}
			updateLoadingMessage("Loading complete!");
		}
		catch ( SQLException e ) {
			String errorMsg = "Error loading products from database: " + e.getMessage();
			System.out.println(errorMsg);
			e.printStackTrace();
			updateLoadingMessage(errorMsg);
		}
	}
	
	public void addPart(com.inventory.Part newPart) {
		getAllParts().add(newPart);
		// Add to database
		String sql = "INSERT INTO parts (name, price, stock, min, max, type, machine_id, company_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try ( PreparedStatement stmt = com.inventory.DatabaseConnection.getConnection().prepareStatement(sql) ) {
			stmt.setString(1, newPart.getName());
			stmt.setDouble(2, newPart.getPrice());
			stmt.setInt(3, newPart.getStock());
			stmt.setInt(4, newPart.getMin());
			stmt.setInt(5, newPart.getMax());
			
			if ( newPart instanceof com.inventory.InHouse ) {
				stmt.setString(6, "InHouse");
				stmt.setInt(7, ((com.inventory.InHouse) newPart).getMachineId());
				stmt.setNull(8, java.sql.Types.VARCHAR);
			} else {
				stmt.setString(6, "Outsourced");
				stmt.setNull(7, java.sql.Types.INTEGER);
				stmt.setString(8, ((com.inventory.Outsourced) newPart).getCompanyName());
			}
			
			stmt.executeUpdate();
		}
		catch ( SQLException e ) {
			System.out.println("Error adding part to database:");
			e.printStackTrace();
		}
	}
	
	public void addProduct(com.inventory.Product newProduct) {
		getAllProducts().add(newProduct);
		// Add to database
		String sql = "INSERT INTO products (name, price, stock, min, max) VALUES (?, ?, ?, ?, ?)";
		try ( PreparedStatement stmt = com.inventory.DatabaseConnection.getConnection()
		                                                               .prepareStatement(sql,
		                                                                                 PreparedStatement.RETURN_GENERATED_KEYS) ) {
			stmt.setString(1, newProduct.getName());
			stmt.setDouble(2, newProduct.getPrice());
			stmt.setInt(3, newProduct.getStock());
			stmt.setInt(4, newProduct.getMin());
			stmt.setInt(5, newProduct.getMax());
			
			stmt.executeUpdate();
			
			// Get the generated product ID
			ResultSet rs = stmt.getGeneratedKeys();
			if ( rs.next() ) {
				int productId = rs.getInt(1);
				
				// Add associated parts
				String partsSql = "INSERT INTO product_parts (product_id, part_id) VALUES (?, ?)";
				try ( PreparedStatement partsStmt = com.inventory.DatabaseConnection.getConnection()
				                                                                    .prepareStatement(partsSql) ) {
					for ( com.inventory.Part part : newProduct.getAllAssociatedParts() ) {
						partsStmt.setInt(1, productId);
						partsStmt.setInt(2, part.getId());
						partsStmt.executeUpdate();
					}
				}
			}
		}
		catch ( SQLException e ) {
			System.out.println("Error adding product to database:");
			e.printStackTrace();
		}
	}
}