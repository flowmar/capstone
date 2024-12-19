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

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

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
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// Initialize the columns
		TableColumn<com.inventory.Part, Integer> partsIdColumn = new TableColumn<>("id");
		TableColumn<com.inventory.Part, String> partsNameColumn = new TableColumn<>("Name");
		TableColumn<com.inventory.Part, Double> partsPriceColumn = new TableColumn<>("Price");
		TableColumn<com.inventory.Part, Integer> partsStockColumn = new TableColumn<>("Stock");
		partsTableView.getColumns().addAll(partsIdColumn, partsNameColumn, partsPriceColumn, partsStockColumn);
		
		TableColumn<com.inventory.Product, Integer> productsIdColumn = new TableColumn<>("id");
		TableColumn<com.inventory.Product, String> productsNameColumn = new TableColumn<>("Name");
		TableColumn<com.inventory.Product, Double> productsPriceColumn = new TableColumn<>("Price");
		TableColumn<com.inventory.Product, Integer> productsStockColumn = new TableColumn<>("Stock");
		productsTableView.getColumns()
		                 .addAll(productsIdColumn, productsNameColumn, productsPriceColumn, productsStockColumn);
		
		// Load data from database
		loadDataFromDatabase();
		
		// Setup table selection listeners
		setupTableSelectionListeners();
		
		// Setup button listeners
		setupButtonListeners();
		
		// Setup search/filter
		setupSearchAndFilter();
	}
	
	private void loadDataFromDatabase() {
		Connection conn = null;
		try {
			conn = com.inventory.DatabaseConnection.getConnection();
			
			// Parts loading
			try ( PreparedStatement stmt = conn.prepareStatement("SELECT * FROM parts") ) {
				ResultSet rs = stmt.executeQuery();
				ObservableList<com.inventory.Part> partsList = FXCollections.observableArrayList();
				while ( rs.next() ) {
					// Create an InHouse part with default machineId 0
					com.inventory.Part part = new com.inventory.InHouse(
							rs.getInt("id"),
							rs.getString("name"),
							rs.getDouble("price"),
							rs.getInt("stock"),
							rs.getInt("min"),
							rs.getInt("max"),
							0  // Default machine ID
					);
					partsList.add(part);
				}
				partsTableView.setItems(partsList);
			}
			
			// Products loading
			try ( PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products") ) {
				ResultSet rs = stmt.executeQuery();
				ObservableList<com.inventory.Product> productsList = FXCollections.observableArrayList();
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
					productsList.add(product);
				}
				productsTableView.setItems(productsList);
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
		mainFormExitButton.setOnAction(event -> exitButtonListener(event));
		mainFormExitButton.setOnMouseEntered(this::onButtonHover);
		mainFormExitButton.setOnMouseExited(this::onButtonExit);
		mainFormExitButton.setOnMousePressed(this::onButtonPress);
		mainFormExitButton.setOnMouseReleased(this::onButtonRelease);
		
		partsAddButton.setOnMouseEntered(event -> onButtonHover(event));
		partsAddButton.setOnMouseExited(event -> onButtonExit(event));
		partsAddButton.setOnMousePressed(event -> onButtonPress(event));
		partsAddButton.setOnMouseReleased(event -> onButtonRelease(event));
		
		partsModifyButton.setOnMouseEntered(event -> onButtonHover(event));
		partsModifyButton.setOnMouseExited(event -> onButtonExit(event));
		partsModifyButton.setOnMousePressed(event -> onButtonPress(event));
		partsModifyButton.setOnMouseReleased(event -> onButtonRelease(event));
		
		partsDeleteButton.setOnMouseEntered(event -> onButtonHover(event));
		partsDeleteButton.setOnMouseExited(event -> onButtonExit(event));
		partsDeleteButton.setOnMousePressed(event -> onButtonPress(event));
		partsDeleteButton.setOnMouseReleased(event -> onButtonRelease(event));
		
		productsAddButton.setOnMouseEntered(event -> onButtonHover(event));
		productsAddButton.setOnMouseExited(event -> onButtonExit(event));
		productsAddButton.setOnMousePressed(event -> onButtonPress(event));
		productsAddButton.setOnMouseReleased(event -> onButtonRelease(event));
		
		productsModifyButton.setOnMouseEntered(event -> onButtonHover(event));
		productsModifyButton.setOnMouseExited(event -> onButtonExit(event));
		productsModifyButton.setOnMousePressed(event -> onButtonPress(event));
		productsModifyButton.setOnMouseReleased(event -> onButtonRelease(event));
		
		productsDeleteButton.setOnMouseEntered(event -> onButtonHover(event));
		productsDeleteButton.setOnMouseExited(event -> onButtonExit(event));
		productsDeleteButton.setOnMousePressed(event -> onButtonPress(event));
		productsDeleteButton.setOnMouseReleased(event -> onButtonRelease(event));
	}
	
	private void setupSearchAndFilter() {
		// Associate the data with the columns
		TableColumn<com.inventory.Part, Integer> partsIdColumn = (TableColumn<com.inventory.Part, Integer>) partsTableView.getColumns()
		                                                                                                                  .get(0);
		TableColumn<com.inventory.Part, String> partsNameColumn = (TableColumn<com.inventory.Part, String>) partsTableView.getColumns()
		                                                                                                                  .get(1);
		TableColumn<com.inventory.Part, Double> partsPriceColumn = (TableColumn<com.inventory.Part, Double>) partsTableView.getColumns()
		                                                                                                                   .get(2);
		TableColumn<com.inventory.Part, Integer> partsStockColumn = (TableColumn<com.inventory.Part, Integer>) partsTableView.getColumns()
		                                                                                                                     .get(3);
		
		partsIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue()
		                                                                                .getId()).asObject());
		partsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		partsPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue()
		                                                                                  .getPrice()).asObject());
		partsStockColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue()
		                                                                                   .getStock()).asObject());
		
		TableColumn<com.inventory.Product, Integer> productsIdColumn = (TableColumn<com.inventory.Product, Integer>) productsTableView.getColumns()
		                                                                                                                              .get(0);
		TableColumn<com.inventory.Product, String> productsNameColumn = (TableColumn<com.inventory.Product, String>) productsTableView.getColumns()
		                                                                                                                              .get(1);
		TableColumn<com.inventory.Product, Double> productsPriceColumn = (TableColumn<com.inventory.Product, Double>) productsTableView.getColumns()
		                                                                                                                               .get(2);
		TableColumn<com.inventory.Product, Integer> productsStockColumn = (TableColumn<com.inventory.Product, Integer>) productsTableView.getColumns()
		                                                                                                                                 .get(3);
		
		productsIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue()
		                                                                                   .getId()).asObject());
		productsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		productsPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue()
		                                                                                     .getPrice()).asObject());
		productsStockColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue()
		                                                                                      .getStock()).asObject());
		
		// Set up filtering for parts
		FilteredList<com.inventory.Part> filteredData = new FilteredList<>(partsTableView.getItems(), t -> true);
		filterFieldParts.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(part -> {
				if ( newValue == null || newValue.isEmpty() ) {
					return true;
				}
				
				String lowerCaseFilter = newValue.toLowerCase();
				
				if ( part.getName().toLowerCase().contains(lowerCaseFilter) ) {
					return true;
				} else return String.valueOf(part.getId()).contains(lowerCaseFilter);
			});
		});
		SortedList<com.inventory.Part> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(partsTableView.comparatorProperty());
		partsTableView.setItems(sortedData);
		
		// Set up filtering for products
		FilteredList<com.inventory.Product> filteredProductData = new FilteredList<>(productsTableView.getItems(),
		                                                                             t -> true);
		filterFieldProducts.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredProductData.setPredicate(product -> {
				if ( newValue == null || newValue.isEmpty() ) {
					return true;
				}
				
				String lowerCaseFilter = newValue.toLowerCase();
				
				if ( product.getName().toLowerCase().contains(lowerCaseFilter) ) {
					return true;
				} else return String.valueOf(product.getId()).contains(lowerCaseFilter);
			});
		});
		SortedList<com.inventory.Product> sortedProductData = new SortedList<>(filteredProductData);
		sortedProductData.comparatorProperty().bind(productsTableView.comparatorProperty());
		productsTableView.setItems(sortedProductData);
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
		parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("com/inventory/addPart.fxml")));
		Stage stage = new Stage();
		Scene scene = new Scene(parent);
		stage.setTitle("Add Part");
		stage.setScene(scene);
		stage.show();
	}
	
	public void partsModifyButtonListener(ActionEvent actionEvent) throws Exception {
		partsErrorLabel.setText("");
		com.inventory.Part selectedPart = partsTableView.getSelectionModel().getSelectedItem();
		if ( selectedPart == null ) {
			partsErrorLabel.setText("Error: Please selected a part to modify!");
		} else {
			int selectedPartId = selectedPart.getId();
			com.inventory.Inventory.selectedPart = selectedPart;
			com.inventory.Inventory.selectedPartId = selectedPartId;
			com.inventory.Inventory.selectedPartIndex = partsTableView.getItems().indexOf(selectedPart);
			Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(
					"com/inventory/modifyPart.fxml")));
			Stage stage = new Stage();
			Scene scene = new Scene(parent);
			stage.setTitle("Modify Part");
			stage.setScene(scene);
			stage.show();
		}
	}
	
	public void partsDeleteButtonListener(ActionEvent actionEvent) {
		partsErrorLabel.setText("");
		if ( com.inventory.Inventory.allParts.size() == 0 ) {
			partsErrorLabel.setText("Error: Cannot Delete, the parts list is empty!");
		} else {
			com.inventory.Part selectedPart = partsTableView.getSelectionModel().getSelectedItem();
			if ( selectedPart == null ) {
				partsErrorLabel.setText("Error: Please select a part to delete!");
				return;
			}
			
			// Check if part is associated with any products
			try {
				String checkSql = "SELECT COUNT(*) FROM product_parts WHERE part_id = ?";
				ResultSet rs;
				try ( PreparedStatement checkStmt = com.inventory.DatabaseConnection.getConnection()
				                                                                    .prepareStatement(checkSql) ) {
					checkStmt.setInt(1, selectedPart.getId());
					rs = checkStmt.executeQuery();
				}
				rs.next();
				int count = rs.getInt(1);
				
				if ( count > 0 ) {
					partsErrorLabel.setText("Error: Cannot delete a part that is associated with products!");
					partsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
					return;
				}
			}
			catch ( SQLException e ) {
				System.out.println("Error checking product associations:");
				e.printStackTrace();
				return;
			}
			
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("Are you sure you want to delete this part?");
			alert.showAndWait().ifPresent(response -> {
				if ( response == ButtonType.OK ) {
					try {
						// Delete from database
						String sql = "DELETE FROM parts WHERE id = ?";
						int rowsAffected;
						try ( PreparedStatement stmt = com.inventory.DatabaseConnection.getConnection()
						                                                               .prepareStatement(sql) ) {
							stmt.setInt(1, selectedPart.getId());
							rowsAffected = stmt.executeUpdate();
						}
						
						if ( rowsAffected > 0 ) {
							// If database delete successful, remove from UI list
							com.inventory.Inventory.allParts.remove(selectedPart);
							partsErrorLabel.setText("Part Deleted!");
							partsErrorLabel.setStyle("-fx-text-fill: #00ff00;");
						} else {
							partsErrorLabel.setText("Error: Part not found in database!");
							partsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
						}
					}
					catch ( SQLException e ) {
						System.out.println("Error deleting part from database:");
						e.printStackTrace();
						partsErrorLabel.setText("Error deleting from database: " + e.getMessage());
						partsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
					}
				} else if ( response == ButtonType.CANCEL ) {
					System.out.println("Deletion cancelled");
				}
			});
		}
	}
	
	public void productsAddButtonListener(ActionEvent actionEvent) throws Exception {
		productsErrorLabel.setText("");
		productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
		Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("com/inventory/addProduct.fxml")));
		Stage stage = new Stage();
		Scene scene = new Scene(parent);
		stage.setTitle("Add Product");
		stage.setScene(scene);
		stage.show();
	}
	
	public void productsModifyButtonListener(ActionEvent actionEvent) throws Exception {
		productsErrorLabel.setText("");
		productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
		com.inventory.Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
		if ( selectedProduct == null ) {
			productsErrorLabel.setText("Error: Please select a product to modify! ");
		} else {
			int selectedProductId = selectedProduct.getId();
			com.inventory.Inventory.selectedProduct = selectedProduct;
			com.inventory.Inventory.selectedProductId = selectedProductId;
			com.inventory.Inventory.selectedProductIndex = productsTableView.getItems().indexOf(selectedProduct);
			Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(
					"com/inventory/modifyProduct.fxml")));
			Stage stage = new Stage();
			Scene scene = new Scene(parent);
			stage.setTitle("Modify Product");
			stage.setScene(scene);
			stage.show();
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
			ResultSet rs;
			try ( PreparedStatement checkStmt = com.inventory.DatabaseConnection.getConnection()
			                                                                    .prepareStatement(checkSql) ) {
				checkStmt.setInt(1, selectedProduct.getId());
				rs = checkStmt.executeQuery();
			}
			rs.next();
			int count = rs.getInt(1);
			
			if ( count > 0 ) {
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
							com.inventory.Inventory.allProducts.remove(selectedProduct);
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
}