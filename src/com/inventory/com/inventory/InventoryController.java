package com.inventory;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

@SuppressWarnings("unchecked")
public class InventoryController implements Initializable {
	
	@FXML
	public TableView<com.inventory.Part> partsTableView = new TableView<>();
	
	@FXML
	public TableView<com.inventory.Product> productsTableView = new TableView<>();
	
	@FXML
	public Stage stage = new Stage();
	@FXML
	public Button mainFormExitButton;
	@FXML
	private TextField filterFieldParts;
	@FXML
	private TextField filterFieldProducts;
	@FXML
	private Label partsErrorLabel;
	
	@FXML
	private Label productsErrorLabel;
	
	@FXML
	private Button partsAddButton;
	
	@FXML
	private Button partsModifyButton;
	
	@FXML
	private Button partsDeleteButton;
	
	@FXML
	private Button productsAddButton;
	
	@FXML
	private Button productsModifyButton;
	
	@FXML
	private Button productsDeleteButton;
	
	@FXML
	private TextField partSearchField;
	
	@FXML
	private TextField productSearchField;
	
	@FXML
	private Label partSearchErrorLabel;
	
	@FXML
	private Label productSearchErrorLabel;
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// Initialize the columns with proper cell value factories
		setupPartsTable();
		setupProductsTable();
		
		// Load data from database
		loadDataFromDatabase();
		
		// Setup table selection listeners
		setupTableSelectionListeners();
		
		// Setup button listeners
		setupButtonListeners();
		
		// Set up search functionality
		filterFieldParts.setOnKeyReleased(event -> handlePartSearch());
		filterFieldProducts.setOnKeyReleased(event -> handleProductSearch());
	}
	
	private void setupPartsTable() {
		TableColumn<com.inventory.Part, Integer> partsIdColumn = new TableColumn<>("id");
		partsIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		
		TableColumn<com.inventory.Part, String> partsNameColumn = new TableColumn<>("Name");
		partsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		
		TableColumn<com.inventory.Part, Double> partsPriceColumn = new TableColumn<>("Price");
		partsPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
		
		TableColumn<com.inventory.Part, Integer> partsStockColumn = new TableColumn<>("Stock");
		partsStockColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
		
		partsTableView.getColumns().addAll(partsIdColumn, partsNameColumn, partsPriceColumn, partsStockColumn);
	}
	
	private void setupProductsTable() {
		TableColumn<com.inventory.Product, Integer> productsIdColumn = new TableColumn<>("id");
		productsIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		
		TableColumn<com.inventory.Product, String> productsNameColumn = new TableColumn<>("Name");
		productsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		
		TableColumn<com.inventory.Product, Double> productsPriceColumn = new TableColumn<>("Price");
		productsPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
		
		TableColumn<com.inventory.Product, Integer> productsStockColumn = new TableColumn<>("Stock");
		productsStockColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
		
		productsTableView.getColumns().addAll(productsIdColumn, productsNameColumn, productsPriceColumn, productsStockColumn);
	}
	
	private void loadDataFromDatabase() {
		Connection conn = null;
		try {
			conn = com.inventory.DatabaseConnection.getConnection();
			
			// Parts loading
			try ( PreparedStatement stmt = conn.prepareStatement("SELECT * FROM parts") ) {
				ResultSet rs = stmt.executeQuery();
				// Clear existing parts
				com.inventory.Inventory.getAllParts().clear();
				while ( rs.next() ) {
					com.inventory.Part part;
					if (rs.getString("type").equals("InHouse")) {
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
					com.inventory.Inventory.getAllParts().add(part);
				}
				// Use the same list for the TableView
				partsTableView.setItems(com.inventory.Inventory.getAllParts());
			}
			
			// Products loading
			try ( PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products") ) {
				ResultSet rs = stmt.executeQuery();
				// Clear existing products
				com.inventory.Inventory.getAllProducts().clear();
				while ( rs.next() ) {
					com.inventory.Product product = new com.inventory.Product(
							rs.getInt("id"),
							rs.getString("name"),
							rs.getDouble("price"),
							rs.getInt("stock"),
							rs.getInt("min"),
							rs.getInt("max"),
							FXCollections.observableArrayList()  // Empty list of associated parts
					);
					com.inventory.Inventory.getAllProducts().add(product);
				}
				// Use the same list for the TableView
				productsTableView.setItems(com.inventory.Inventory.getAllProducts());
			}
		}
		catch ( SQLException e ) {
			e.printStackTrace();
			// Show error to user
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Database Error");
			alert.setHeaderText("Error Loading Data");
			alert.setContentText("Could not load data from the database. Please check your connection.");
			alert.showAndWait();
		}
		finally {
			// Always release the connection back to the pool
			if ( conn != null ) {
				com.inventory.DatabaseConnection.releaseConnection(conn);
			}
		}
	}
	
	private void setupTableSelectionListeners() {
		partsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if ( newValue != null ) {
				partsErrorLabel.setText("");
			}
		});
		
		productsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if ( newValue != null ) {
				productsErrorLabel.setText("");
			}
		});
	}
	
	private void setupButtonListeners() {
		mainFormExitButton.setOnAction(this::exitButtonListener);
		mainFormExitButton.setOnMouseEntered(this::onButtonHover);
		mainFormExitButton.setOnMouseExited(this::onButtonExit);
		mainFormExitButton.setOnMousePressed(this::onButtonPress);
		mainFormExitButton.setOnMouseReleased(this::onButtonRelease);
		
		partsAddButton.setOnAction(event -> {
			try {
				partsAddButtonListener(event);
			} catch (Exception e) {
				partsErrorLabel.setText("Error opening add part window: " + e.getMessage());
				e.printStackTrace();
			}
		});
		partsAddButton.setOnMouseEntered(this::onButtonHover);
		partsAddButton.setOnMouseExited(this::onButtonExit);
		partsAddButton.setOnMousePressed(this::onButtonPress);
		partsAddButton.setOnMouseReleased(this::onButtonRelease);
		
		partsModifyButton.setOnAction(this::partsModifyButtonListener);
		partsModifyButton.setOnMouseEntered(this::onButtonHover);
		partsModifyButton.setOnMouseExited(this::onButtonExit);
		partsModifyButton.setOnMousePressed(this::onButtonPress);
		partsModifyButton.setOnMouseReleased(this::onButtonRelease);
		
		partsDeleteButton.setOnAction(this::partsDeleteButtonListener);
		partsDeleteButton.setOnMouseEntered(this::onButtonHover);
		partsDeleteButton.setOnMouseExited(this::onButtonExit);
		partsDeleteButton.setOnMousePressed(this::onButtonPress);
		partsDeleteButton.setOnMouseReleased(this::onButtonRelease);
		
		productsAddButton.setOnAction(event -> {
			try {
				productsAddButtonListener(event);
			} catch (Exception e) {
				productsErrorLabel.setText("Error opening add product window: " + e.getMessage());
				e.printStackTrace();
			}
		});
		productsAddButton.setOnMouseEntered(this::onButtonHover);
		productsAddButton.setOnMouseExited(this::onButtonExit);
		productsAddButton.setOnMousePressed(this::onButtonPress);
		productsAddButton.setOnMouseReleased(this::onButtonRelease);
		
		productsModifyButton.setOnAction(this::productsModifyButtonListener);
		productsModifyButton.setOnMouseEntered(this::onButtonHover);
		productsModifyButton.setOnMouseExited(this::onButtonExit);
		productsModifyButton.setOnMousePressed(this::onButtonPress);
		productsModifyButton.setOnMouseReleased(this::onButtonRelease);
		
		productsDeleteButton.setOnAction(this::productsDeleteButtonListener);
		productsDeleteButton.setOnMouseEntered(this::onButtonHover);
		productsDeleteButton.setOnMouseExited(this::onButtonExit);
		productsDeleteButton.setOnMousePressed(this::onButtonPress);
		productsDeleteButton.setOnMouseReleased(this::onButtonRelease);
	}
	
	private void setupSearchAndFilter() {
		// Create filtered lists
		FilteredList<com.inventory.Part> filteredParts = new FilteredList<>(com.inventory.Inventory.getAllParts(), p -> true);
		FilteredList<com.inventory.Product> filteredProducts = new FilteredList<>(com.inventory.Inventory.getAllProducts(), p -> true);
		
		// Add listeners to the search fields
		filterFieldParts.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredParts.setPredicate(part -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				String lowerCaseFilter = newValue.toLowerCase();
				return part.getName().toLowerCase().contains(lowerCaseFilter);
			});
		});
		
		filterFieldProducts.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredProducts.setPredicate(product -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				String lowerCaseFilter = newValue.toLowerCase();
				return product.getName().toLowerCase().contains(lowerCaseFilter);
			});
		});
		
		// Wrap filtered lists in sorted lists
		SortedList<com.inventory.Part> sortedParts = new SortedList<>(filteredParts);
		SortedList<com.inventory.Product> sortedProducts = new SortedList<>(filteredProducts);
		
		// Bind sorted lists to table views
		sortedParts.comparatorProperty().bind(partsTableView.comparatorProperty());
		sortedProducts.comparatorProperty().bind(productsTableView.comparatorProperty());
		
		// Set items to table views
		partsTableView.setItems(sortedParts);
		productsTableView.setItems(sortedProducts);
	}
	
	public void exitButtonListener(ActionEvent actionEvent) {
		Platform.exit();
	}
	
	@FXML
	private void onButtonHover(MouseEvent event) {
		Button sourceButton = (Button) event.getSource();
		sourceButton.setStyle(sourceButton.getStyle() +
		                      "-fx-background-color: derive(" + getButtonColor(sourceButton) + ", 10%); " +
		                      "-fx-cursor: hand;");
	}
	
	@FXML
	private void onButtonExit(MouseEvent event) {
		Button sourceButton = (Button) event.getSource();
		sourceButton.setStyle(sourceButton.getStyle().replaceAll("-fx-background-color: derive\\([^;]+\\);", ""));
	}
	
	@FXML
	private void onButtonPress(MouseEvent event) {
		Button sourceButton = (Button) event.getSource();
		sourceButton.setStyle(sourceButton.getStyle() +
		                      "-fx-translate-y: 2px; " +
		                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1);");
	}
	
	@FXML
	private void onButtonRelease(MouseEvent event) {
		Button sourceButton = (Button) event.getSource();
		sourceButton.setStyle(sourceButton.getStyle()
		                                  .replaceAll("-fx-translate-y: 2px;", "")
		                                  .replaceAll(
				                                  "-fx-effect: dropshadow\\(three-pass-box, rgba\\(0,0,0,0.2\\), 3, 0, 0, 1\\);",
				                                  "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);"));
	}
	
	private String getButtonColor(Button button) {
		if ( button == partsAddButton || button == productsAddButton ) {
			return "#27B611";
		} else if ( button == partsModifyButton || button == productsModifyButton ) {
			return "#0d2c54";
		} else if ( button == partsDeleteButton || button == productsDeleteButton || button == mainFormExitButton ) {
			return "#ff0000";
		}
		return "#000000";
	}
	
	public int getRandomNumber() {
		Random randomNumbers = new Random();
		return Math.abs(randomNumbers.nextInt(1000));
	}
	
	public void partsAddButtonListener(ActionEvent actionEvent) throws Exception {
		Parent parent;
		partsErrorLabel.setText("");
		parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/inventory/addPart.fxml")));
		Stage stage = new Stage();
		Scene scene = new Scene(parent);
		stage.setTitle("Add Part");
		stage.setScene(scene);
		stage.setOnHidden(e -> refreshTables());  // Add refresh on window close
		stage.show();
	}
	
	@FXML
	private void partsModifyButtonListener(ActionEvent actionEvent) {
		try {
			partsErrorLabel.setText("");
			partsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
			com.inventory.Part selectedPart = partsTableView.getSelectionModel().getSelectedItem();
			if (selectedPart == null) {
				partsErrorLabel.setText("Error: Please select a part to modify!");
			} else {
				int selectedPartId = selectedPart.getId();
				com.inventory.Inventory.setSelectedPart(selectedPart);
				com.inventory.Inventory.setSelectedPartId(selectedPartId);
				com.inventory.Inventory.setSelectedPartIndex(partsTableView.getItems().indexOf(selectedPart));
				
				Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/inventory/modifyPart.fxml")));
				Stage stage = new Stage();
				Scene scene = new Scene(parent);
				stage.setTitle("Modify Part");
				stage.setScene(scene);
				stage.setOnHidden(e -> refreshTables());
				stage.show();
			}
		} catch (IOException e) {
			e.printStackTrace();
			partsErrorLabel.setText("Error loading modify part window: " + e.getMessage());
		}
	}

	@FXML
	private void productsModifyButtonListener(ActionEvent actionEvent) {
		try {
			productsErrorLabel.setText("");
			productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
			com.inventory.Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
			if (selectedProduct == null) {
				productsErrorLabel.setText("Error: Please select a product to modify!");
			} else {
				int selectedProductId = selectedProduct.getId();
				com.inventory.Inventory.setSelectedProduct(selectedProduct);
				com.inventory.Inventory.setSelectedProductId(selectedProductId);
				com.inventory.Inventory.setSelectedProductIndex(productsTableView.getItems().indexOf(selectedProduct));
				
				Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/inventory/modifyProduct.fxml")));
				Stage stage = new Stage();
				Scene scene = new Scene(parent);
				stage.setTitle("Modify Product");
				stage.setScene(scene);
				stage.setOnHidden(e -> refreshTables());
				stage.show();
			}
		} catch (IOException e) {
			e.printStackTrace();
			productsErrorLabel.setText("Error loading modify product window: " + e.getMessage());
		}
	}
	
	public void productsAddButtonListener(ActionEvent actionEvent) throws Exception {
		productsErrorLabel.setText("");
		productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
		Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/inventory/addProduct.fxml")));
		Stage stage = new Stage();
		Scene scene = new Scene(parent);
		stage.setTitle("Add Product");
		stage.setScene(scene);
		stage.setOnHidden(e -> refreshTables());  // Add refresh on window close
		stage.show();
	}
	
	@FXML
	public void partsDeleteButtonListener(ActionEvent actionEvent) {
		partsErrorLabel.setText("");
		partsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
		
		// Check if there are any parts to delete
		if (partsTableView.getItems().isEmpty()) {
			partsErrorLabel.setText("Error: No parts available to delete!");
			return;
		}
		
		// Get selected part
		com.inventory.Part selectedPart = partsTableView.getSelectionModel().getSelectedItem();
		if (selectedPart == null) {
			partsErrorLabel.setText("Error: Please select a part to delete!");
			return;
		}
		
		Connection conn = null;
		try {
			conn = com.inventory.DatabaseConnection.getConnection();
			conn.setAutoCommit(false);  // Start transaction
			
			// Check if part is associated with any products
			String checkSql = "SELECT COUNT(*) FROM product_parts WHERE part_id = ?";
			try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
				checkStmt.setInt(1, selectedPart.getId());
				try (ResultSet rs = checkStmt.executeQuery()) {
					rs.next();
					if (rs.getInt(1) > 0) {
						partsErrorLabel.setText("Error: Cannot delete a part that is associated with products!");
						return;
					}
				}
			}
			
			// Show confirmation dialog
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Delete Part");
			alert.setHeaderText("Delete Part Confirmation");
			alert.setContentText("Are you sure you want to delete this part?\nName: " + selectedPart.getName() + "\nID: " + selectedPart.getId());
			
			if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
				// Delete from database
				String sql = "DELETE FROM parts WHERE id = ?";
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					stmt.setInt(1, selectedPart.getId());
					int rowsAffected = stmt.executeUpdate();
					
					if (rowsAffected > 0) {
						// Commit transaction
						conn.commit();
						
						// Remove from UI
						com.inventory.Inventory.getAllParts().remove(selectedPart);
						partsTableView.refresh();
						
						// Show success message
						partsErrorLabel.setStyle("-fx-text-fill: #00ff00;");
						partsErrorLabel.setText("Part successfully deleted!");
					} else {
						conn.rollback();
						partsErrorLabel.setText("Error: Part not found in database!");
					}
				}
			}
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					System.err.println("Error rolling back transaction: " + ex.getMessage());
				}
			}
			System.err.println("Error deleting part: " + e.getMessage());
			e.printStackTrace();
			partsErrorLabel.setText("Database error: " + e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					com.inventory.DatabaseConnection.releaseConnection(conn);
				} catch (SQLException e) {
					System.err.println("Error resetting auto-commit: " + e.getMessage());
				}
			}
		}
	}
	
	public void productsDeleteButtonListener(ActionEvent actionEvent) {
		productsErrorLabel.setText("");
		com.inventory.Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
		if ( selectedProduct == null ) {
			productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
			productsErrorLabel.setText("Error: Please select a product to delete!");
			return;
		}
		
		try {
			// Check if product has associated parts
			String checkSql = "SELECT COUNT(*) FROM product_parts WHERE product_id = ?";
			int count;
			try (PreparedStatement checkStmt = com.inventory.DatabaseConnection.getConnection()
					.prepareStatement(checkSql)) {
				checkStmt.setInt(1, selectedProduct.getId());
				try (ResultSet rs = checkStmt.executeQuery()) {
					rs.next();
					count = rs.getInt(1);
				}
			}
			
			if (count > 0) {
				productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
				productsErrorLabel.setText("Error: Cannot delete a product that has associated parts.");
				return;
			}
			
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("Are you sure you want to delete this product?");
			alert.showAndWait().ifPresent(response -> {
				if ( response == ButtonType.OK ) {
					try {
						// Delete the product from database
						String sql = "DELETE FROM products WHERE id = ?";
						int rowsAffected;
						try ( PreparedStatement stmt = com.inventory.DatabaseConnection.getConnection()
								.prepareStatement(sql) ) {
							stmt.setInt(1, selectedProduct.getId());
							rowsAffected = stmt.executeUpdate();
						}
						
						if ( rowsAffected > 0 ) {
							// If database delete successful, remove from UI
							com.inventory.Inventory.getAllProducts().remove(selectedProduct);
							productsErrorLabel.setText("Product Deleted!");
							productsErrorLabel.setStyle("-fx-text-fill: #00ff00;");
						} else {
							productsErrorLabel.setText("Error: Product not found in database!");
							productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
						}
					}
					catch ( SQLException e ) {
						System.out.println("Error deleting product from database:");
						e.printStackTrace();
						productsErrorLabel.setText("Error deleting from database: " + e.getMessage());
						productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
					}
				} else if ( response == ButtonType.CANCEL ) {
					System.out.println("Deletion cancelled");
				}
			});
		}
		catch ( SQLException e ) {
			System.out.println("Error checking product associations:");
			e.printStackTrace();
			productsErrorLabel.setText("Error checking database: " + e.getMessage());
			productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
		}
	}
	
	public void partsSearchFieldListener(ActionEvent actionEvent) {
		partsTableView.getSelectionModel().clearSelection();
		partsTableView.getSelectionModel().selectFirst();
		com.inventory.Part selected = partsTableView.getSelectionModel().getSelectedItem();
		if ( selected == null ) {
			partsErrorLabel.setText("Error: Part not found");
		}
	}
	
	public void productsSearchFieldListener(ActionEvent actionEvent) {
		productsTableView.getSelectionModel().clearSelection();
		productsTableView.getSelectionModel().selectFirst();
		com.inventory.Product selected = productsTableView.getSelectionModel().getSelectedItem();
		if ( selected == null ) {
			productsErrorLabel.setText("Error: Product not found!");
		}
	}
	
	@FXML
	private void generateInventoryReport() {
		com.inventory.InventoryReportGenerator reportGenerator = new com.inventory.InventoryReportGenerator();
		reportGenerator.generateInventorySummaryReport();
	}
	
	private void refreshTables() {
		Platform.runLater(() -> {
			// Create new observable lists
			ObservableList<com.inventory.Part> newParts = FXCollections.observableArrayList();
			ObservableList<com.inventory.Product> newProducts = FXCollections.observableArrayList();
			
			com.inventory.Inventory.setAllParts(newParts);
			com.inventory.Inventory.setAllProducts(newProducts);
			
			// Set the new lists to the table views
			partsTableView.setItems(com.inventory.Inventory.getAllParts());
			productsTableView.setItems(com.inventory.Inventory.getAllProducts());
			
			// Reload data from database
			loadDataFromDatabase();
		});
	}
	
	@FXML
	private void handlePartSearch() {
		String searchText = filterFieldParts.getText().toLowerCase();
		ObservableList<com.inventory.Part> filteredParts = FXCollections.observableArrayList();
		
		if (searchText == null || searchText.isEmpty()) {
			partsTableView.setItems(com.inventory.Inventory.getAllParts());
			partsErrorLabel.setText("");
			return;
		}
		
		for (com.inventory.Part part : com.inventory.Inventory.getAllParts()) {
			if (part.getName().toLowerCase().contains(searchText) || 
				String.valueOf(part.getId()).contains(searchText)) {
				filteredParts.add(part);
			}
		}
		
		if (filteredParts.isEmpty()) {
			partsErrorLabel.setText("No matching parts found");
		} else {
			partsTableView.setItems(filteredParts);
			partsErrorLabel.setText("");
		}
	}
	
	@FXML
	private void handleProductSearch() {
		String searchText = filterFieldProducts.getText().toLowerCase();
		ObservableList<com.inventory.Product> filteredProducts = FXCollections.observableArrayList();
		
		if (searchText == null || searchText.isEmpty()) {
			productsTableView.setItems(com.inventory.Inventory.getAllProducts());
			productsErrorLabel.setText("");
			return;
		}
		
		for (com.inventory.Product product : com.inventory.Inventory.getAllProducts()) {
			if (product.getName().toLowerCase().contains(searchText) || 
				String.valueOf(product.getId()).contains(searchText)) {
				filteredProducts.add(product);
			}
		}
		
		if (filteredProducts.isEmpty()) {
			productsErrorLabel.setText("No matching products found");
		} else {
			productsTableView.setItems(filteredProducts);
			productsErrorLabel.setText("");
		}
	}
}