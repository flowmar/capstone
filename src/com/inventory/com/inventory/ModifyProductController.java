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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

@SuppressWarnings("unchecked")
public class ModifyProductController implements Initializable {
	
	@FXML
	private TextField modifyProductIdTextField;
	
	@FXML
	private TextField modifyProductNameTextField;
	
	@FXML
	private TextField modifyProductStockTextField;
	
	@FXML
	private TextField modifyProductPriceTextField;
	
	@FXML
	private TextField modifyProductMaxTextField;
	
	@FXML
	private TextField modifyProductMinTextField;
	
	@FXML
	private TableView<com.inventory.Part> modifyProductPartsTableView;
	
	@FXML
	private TableView<com.inventory.Part> modifyProductAssociatedPartsTableView;
	
	@FXML
	private TextField modifyProductSearchField;
	
	@FXML
	private Label modifyProductErrorLabel;
	
	@FXML
	private Label modifyProductSaveErrorLabel;
	
	@FXML
	private Button modifyProductSaveButton;
	
	@FXML
	private Button modifyProductCancelButton;
	
	@FXML
	private ObservableList<com.inventory.Part> associatedPartsList = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// Get the selected product
		com.inventory.Product selectedProduct = com.inventory.Inventory.getSelectedProduct();
		
		// Set up the product details
		modifyProductIdTextField.setText(String.valueOf(selectedProduct.getId()));
		modifyProductNameTextField.setText(selectedProduct.getName());
		modifyProductStockTextField.setText(String.valueOf(selectedProduct.getStock()));
		modifyProductPriceTextField.setText(String.valueOf(selectedProduct.getPrice()));
		modifyProductMaxTextField.setText(String.valueOf(selectedProduct.getMax()));
		modifyProductMinTextField.setText(String.valueOf(selectedProduct.getMin()));
		
		// Set up the available parts table
		setupAvailablePartsTable();
		
		// Set up the associated parts table
		setupAssociatedPartsTable();
		
		// Load associated parts from database
		loadAssociatedParts(selectedProduct.getId());
		
		// Set up the search functionality
		setupSearch();
		
		// Add button hover and click animation with consistent styling
		modifyProductSaveButton.setStyle(modifyProductSaveButton.getStyle() +
		                                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
		modifyProductCancelButton.setStyle(modifyProductCancelButton.getStyle() +
		                                   "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
		
		// Add button hover and click animation
		modifyProductSaveButton.setOnMouseEntered(event -> onButtonHover(event));
		modifyProductSaveButton.setOnMouseExited(event -> onButtonExit(event));
		modifyProductSaveButton.setOnMousePressed(event -> onButtonPress(event));
		modifyProductSaveButton.setOnMouseReleased(event -> onButtonRelease(event));
		
		modifyProductCancelButton.setOnMouseEntered(event -> onButtonHover(event));
		modifyProductCancelButton.setOnMouseExited(event -> onButtonExit(event));
		modifyProductCancelButton.setOnMousePressed(event -> onButtonPress(event));
		modifyProductCancelButton.setOnMouseReleased(event -> onButtonRelease(event));
	}
	
	private void setupAvailablePartsTable() {
		// Set up columns for available parts table
		TableColumn<com.inventory.Part, Integer> partIdCol = new TableColumn<>("Part ID");
		partIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		
		TableColumn<com.inventory.Part, String> partNameCol = new TableColumn<>("Part Name");
		partNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		
		TableColumn<com.inventory.Part, Integer> partStockCol = new TableColumn<>("Stock");
		partStockCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
		
		TableColumn<com.inventory.Part, Double> partPriceCol = new TableColumn<>("Price");
		partPriceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
		
		modifyProductPartsTableView.getColumns().addAll(partIdCol, partNameCol, partStockCol, partPriceCol);
		
		// Load all available parts
		modifyProductPartsTableView.setItems(com.inventory.Inventory.getAllParts());
	}
	
	private void setupAssociatedPartsTable() {
		// Set up columns for associated parts table
		TableColumn<com.inventory.Part, Integer> assocPartIdCol = new TableColumn<>("Part ID");
		assocPartIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		
		TableColumn<com.inventory.Part, String> assocPartNameCol = new TableColumn<>("Part Name");
		assocPartNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		
		TableColumn<com.inventory.Part, Integer> assocPartStockCol = new TableColumn<>("Stock");
		assocPartStockCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
		
		TableColumn<com.inventory.Part, Double> assocPartPriceCol = new TableColumn<>("Price");
		assocPartPriceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
		
		modifyProductAssociatedPartsTableView.getColumns().addAll(assocPartIdCol, assocPartNameCol, assocPartStockCol, assocPartPriceCol);
	}
	
	private void loadAssociatedParts(int productId) {
		String sql = "SELECT p.* FROM parts p JOIN product_parts pp ON p.id = pp.part_id WHERE pp.product_id = ?";
		try (PreparedStatement stmt = com.inventory.DatabaseConnection.getConnection().prepareStatement(sql)) {
			stmt.setInt(1, productId);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
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
				associatedPartsList.add(part);
			}
			modifyProductAssociatedPartsTableView.setItems(associatedPartsList);
		} catch (SQLException e) {
			System.err.println("Error loading associated parts: " + e.getMessage());
			e.printStackTrace();
			modifyProductErrorLabel.setText("Error loading associated parts: " + e.getMessage());
		}
	}
	
	private void setupSearch() {
		// Wrap the Observable list in a FilteredList
		FilteredList<com.inventory.Part> filteredProductData = new FilteredList<com.inventory.Part>(com.inventory.Inventory.getAllParts(),
		                                                                                            t -> true);
		
		// Set the filter to change whenever the filter changes
		modifyProductSearchField.textProperty().addListener((observable, oldValue, newValue) ->
		                                                    {
			                                                    filteredProductData.setPredicate(product -> {
				                                                    if ( newValue == null || newValue.isEmpty() ) {
					                                                    modifyProductErrorLabel.setText("");
					                                                    return true;
				                                                    }
				                                                    // Compare each part with the filter text
				                                                    String lowerCaseFilter = newValue.toLowerCase();
				                                                    if ( product.getName()
				                                                                .toLowerCase()
				                                                                .contains(lowerCaseFilter) ) {
					                                                    modifyProductErrorLabel.setText("");
					                                                    return true;
				                                                    } else if ( Integer.toString((product.getId()))
				                                                                       .contains(lowerCaseFilter) ) {
					                                                    modifyProductErrorLabel.setText("");
					                                                    return true;
				                                                    }
				                                                    else {
					                                                    return false;
				                                                    }
			                                                    });
		                                                    });
		
		SortedList<com.inventory.Part> sortedProductData = new SortedList<>(filteredProductData);
		
		sortedProductData.comparatorProperty().bind(modifyProductPartsTableView.comparatorProperty());
		
		modifyProductPartsTableView.setItems(sortedProductData);
	}
	
	@FXML
	private void modifyProductSaveButtonListener(ActionEvent actionEvent) {
		modifyProductSaveErrorLabel.setText("");
		modifyProductSaveErrorLabel.setStyle("-fx-text-fill: #ff0000");
		
		try {
			int id = Integer.parseInt(modifyProductIdTextField.getText().trim());
			String name = modifyProductNameTextField.getText().trim();
			if (name.isEmpty()) {
				modifyProductSaveErrorLabel.setText("Error: Name cannot be empty!");
				return;
			}
			
			int stock = Integer.parseInt(modifyProductStockTextField.getText().trim());
			double price = Double.parseDouble(modifyProductPriceTextField.getText().trim());
			int min = Integer.parseInt(modifyProductMinTextField.getText().trim());
			int max = Integer.parseInt(modifyProductMaxTextField.getText().trim());
			
			boolean passCheck = true;
			// Input Validation Logic
			if (min > max) {
				modifyProductSaveErrorLabel.setText("Error: Minimum cannot be more than maximum!");
				passCheck = false;
			} else if (stock > max) {
				modifyProductSaveErrorLabel.setText("Error: Current Stock cannot be more than the maximum!");
				passCheck = false;
			} else if (stock < min) {
				modifyProductSaveErrorLabel.setText("Error: Current Stock cannot be less than the minimum!");
				passCheck = false;
			}
			
			if (passCheck) {
				Connection conn = null;
				try {
					conn = com.inventory.DatabaseConnection.getConnection();
					conn.setAutoCommit(false);  // Start transaction
					
					// Update product in database
					String sql = "UPDATE products SET name = ?, stock = ?, price = ?, min = ?, max = ? WHERE id = ?";
					try (PreparedStatement stmt = conn.prepareStatement(sql)) {
						stmt.setString(1, name);
						stmt.setInt(2, stock);
						stmt.setDouble(3, price);
						stmt.setInt(4, min);
						stmt.setInt(5, max);
						stmt.setInt(6, id);
						
						int rowsAffected = stmt.executeUpdate();
						
						if (rowsAffected > 0) {
							// Delete existing associated parts
							String deleteSql = "DELETE FROM product_parts WHERE product_id = ?";
							try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
								deleteStmt.setInt(1, id);
								deleteStmt.executeUpdate();
							}
							
							// Save new associated parts
							String assocSql = "INSERT INTO product_parts (product_id, part_id) VALUES (?, ?)";
							try (PreparedStatement assocStmt = conn.prepareStatement(assocSql)) {
								for (com.inventory.Part part : associatedPartsList) {
									assocStmt.setInt(1, id);
									assocStmt.setInt(2, part.getId());
									assocStmt.executeUpdate();
								}
							}
							
							// If we got here, commit the transaction
							conn.commit();
							
							// Update UI list
							com.inventory.Product updatedProduct = new com.inventory.Product(id, name, price, stock, min, max, associatedPartsList);
							com.inventory.Inventory.updateProduct(com.inventory.Inventory.getSelectedProductIndex(), updatedProduct);
							
							// Close window
							Stage stage = (Stage) modifyProductNameTextField.getScene().getWindow();
							stage.close();
						} else {
							conn.rollback();
							modifyProductSaveErrorLabel.setText("Error: Failed to update product in database!");
						}
					}
				} catch (SQLException e) {
					if (conn != null) {
						try {
							conn.rollback();
						} catch (SQLException ex) {
							ex.printStackTrace();
						}
					}
					System.err.println("Error updating product in database: " + e.getMessage());
					e.printStackTrace();
					modifyProductSaveErrorLabel.setText("Database error: " + e.getMessage());
				} finally {
					if (conn != null) {
						try {
							conn.setAutoCommit(true);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			modifyProductSaveErrorLabel.setText("Error: Please enter valid numbers for Stock, Price, Min, and Max!");
		} catch (Exception e) {
			System.err.println("Error updating product: " + e.getMessage());
			e.printStackTrace();
			modifyProductSaveErrorLabel.setText("Error: " + e.getMessage());
		}
	}
	
	@FXML
	private void addAssociatedPartButtonListener(ActionEvent actionEvent) {
		com.inventory.Part selectedPart = modifyProductPartsTableView.getSelectionModel().getSelectedItem();
		if (selectedPart != null) {
			if (!associatedPartsList.contains(selectedPart)) {
				associatedPartsList.add(selectedPart);
				modifyProductAssociatedPartsTableView.setItems(associatedPartsList);
			} else {
				modifyProductErrorLabel.setText("Part is already associated with this product");
			}
		} else {
			modifyProductErrorLabel.setText("Please select a part to add");
		}
	}
	
	@FXML
	private void removeAssociatedPartButtonListener(ActionEvent actionEvent) {
		com.inventory.Part selectedPart = modifyProductAssociatedPartsTableView.getSelectionModel().getSelectedItem();
		if (selectedPart != null) {
			associatedPartsList.remove(selectedPart);
			modifyProductAssociatedPartsTableView.setItems(associatedPartsList);
		} else {
			modifyProductErrorLabel.setText("Please select a part to remove");
		}
	}
	
	@FXML
	private void modifyProductCancelButtonListener(ActionEvent actionEvent) {
		Stage stage = (Stage) modifyProductCancelButton.getScene().getWindow();
		stage.close();
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
		if ( button == modifyProductSaveButton ) {
			return "#27B611";
		} else if ( button == modifyProductCancelButton ) {
			return "#ff0000";
		}
		return "#000000";
	}
	
	@FXML
	private void modifyProductsSearchFieldListener(ActionEvent actionEvent) {
		modifyProductPartsTableView.getSelectionModel().clearSelection();
		modifyProductPartsTableView.getSelectionModel().selectFirst();
		com.inventory.Part selected = modifyProductPartsTableView.getSelectionModel().getSelectedItem();
		if ( selected == null ) {
			modifyProductErrorLabel.setText("Error: Part not found");
		}
	}
}
