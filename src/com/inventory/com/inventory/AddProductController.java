package com.inventory;

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
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AddProductController implements Initializable {
    @FXML
    private TableView<com.inventory.Part> addProductTableView;
    
    @FXML
    private TableView<com.inventory.Part> associatedPartTableView;
    
    @FXML
    private TextField addProductFilterField;
    
    @FXML
    private TextField addProductNameTextField;
    
    @FXML
    private TextField addProductStockTextField;
    
    @FXML
    private TextField addProductPriceTextField;
    
    @FXML
    private TextField addProductMinTextField;
    
    @FXML
    private TextField addProductMaxTextField;
    
    @FXML
    private Label addProductSaveErrorLabel;
    
    @FXML
    private Label addProductErrorLabel;
    
    private ObservableList<com.inventory.Part> associatedParts;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupAvailablePartsTable();
        setupAssociatedPartsTable();
        setupSearch();
        
        // Initialize the associated parts list
        associatedParts = FXCollections.observableArrayList();
        
        // Set up the tables
        addProductTableView.setItems(com.inventory.Inventory.getAllParts());
        associatedPartTableView.setItems(associatedParts);
    }
    
    private void setupAvailablePartsTable() {
        // Available Parts Table
        TableColumn<com.inventory.Part, Integer> partIdCol = new TableColumn<>("Part ID");
        partIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        
        TableColumn<com.inventory.Part, String> partNameCol = new TableColumn<>("Part Name");
        partNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        
        TableColumn<com.inventory.Part, Integer> partInventoryCol = new TableColumn<>("Inventory Level");
        partInventoryCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
        
        TableColumn<com.inventory.Part, Double> partPriceCol = new TableColumn<>("Price/Cost per Unit");
        partPriceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        
        addProductTableView.getColumns().setAll(partIdCol, partNameCol, partInventoryCol, partPriceCol);
    }
    
    private void setupAssociatedPartsTable() {
        // Associated Parts Table
        TableColumn<com.inventory.Part, Integer> assocPartIdCol = new TableColumn<>("Part ID");
        assocPartIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        
        TableColumn<com.inventory.Part, String> assocPartNameCol = new TableColumn<>("Part Name");
        assocPartNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        
        TableColumn<com.inventory.Part, Integer> assocPartInventoryCol = new TableColumn<>("Inventory Level");
        assocPartInventoryCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
        
        TableColumn<com.inventory.Part, Double> assocPartPriceCol = new TableColumn<>("Price/Cost per Unit");
        assocPartPriceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        
        associatedPartTableView.getColumns().setAll(assocPartIdCol, assocPartNameCol, assocPartInventoryCol, assocPartPriceCol);
    }
    
    private void setupSearch() {
        FilteredList<com.inventory.Part> filteredData = new FilteredList<>(com.inventory.Inventory.getAllParts(), p -> true);
        
        addProductFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(part -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                return part.getName().toLowerCase().contains(lowerCaseFilter) ||
                       String.valueOf(part.getId()).contains(lowerCaseFilter);
            });
            
            SortedList<com.inventory.Part> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(addProductTableView.comparatorProperty());
            addProductTableView.setItems(sortedData);
        });
    }
    
    @FXML
    private void addProductAddButtonListener(ActionEvent event) {
        com.inventory.Part selectedPart = addProductTableView.getSelectionModel().getSelectedItem();
        if (selectedPart != null) {
            if (!associatedParts.contains(selectedPart)) {
                associatedParts.add(selectedPart);
            } else {
                addProductErrorLabel.setText("Part is already associated with this product");
            }
        } else {
            addProductErrorLabel.setText("Please select a part to add");
        }
    }
    
    @FXML
    private void removeAssociatedPartButtonListener(ActionEvent event) {
        com.inventory.Part selectedPart = associatedPartTableView.getSelectionModel().getSelectedItem();
        if (selectedPart != null) {
            associatedParts.remove(selectedPart);
        } else {
            addProductErrorLabel.setText("Please select a part to remove");
        }
    }
    
    @FXML
    private void addProductSaveButtonListener(ActionEvent event) {
        addProductSaveErrorLabel.setText("");
        addProductSaveErrorLabel.setStyle("-fx-text-fill: #ff0000");
        
        try {
            String name = addProductNameTextField.getText().trim();
            if (name.isEmpty()) {
                addProductSaveErrorLabel.setText("Error: Name cannot be empty!");
                return;
            }
            
            int stock = Integer.parseInt(addProductStockTextField.getText().trim());
            double price = Double.parseDouble(addProductPriceTextField.getText().trim());
            int min = Integer.parseInt(addProductMinTextField.getText().trim());
            int max = Integer.parseInt(addProductMaxTextField.getText().trim());
            
            boolean passCheck = true;
            // Input Validation Logic
            if (min > max) {
                addProductSaveErrorLabel.setText("Error: Minimum cannot be more than maximum!");
                passCheck = false;
            } else if (stock > max) {
                addProductSaveErrorLabel.setText("Error: Current Stock cannot be more than the maximum!");
                passCheck = false;
            } else if (stock < min) {
                addProductSaveErrorLabel.setText("Error: Current Stock cannot be less than the minimum!");
                passCheck = false;
            }
            
            if (passCheck) {
                Connection conn = null;
                try {
                    conn = com.inventory.DatabaseConnection.getConnection();
                    conn.setAutoCommit(false);  // Start transaction
                    
                    // Save product to database
                    String sql = "INSERT INTO products (name, stock, price, min, max) VALUES (?, ?, ?, ?, ?)";
                    int productId;
                    
                    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, name);
                        stmt.setInt(2, stock);
                        stmt.setDouble(3, price);
                        stmt.setInt(4, min);
                        stmt.setInt(5, max);
                        
                        int rowsAffected = stmt.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            // Get the generated product ID
                            ResultSet rs = stmt.getGeneratedKeys();
                            if (rs.next()) {
                                productId = rs.getInt(1);
                                
                                // Create new product with the generated ID
                                com.inventory.Product newProduct = new com.inventory.Product(productId, name, price, stock, min, max, associatedParts);
                                
                                // Save associated parts
                                String assocSql = "INSERT INTO product_parts (product_id, part_id) VALUES (?, ?)";
                                try (PreparedStatement assocStmt = conn.prepareStatement(assocSql)) {
                                    for (com.inventory.Part part : associatedParts) {
                                        assocStmt.setInt(1, productId);
                                        assocStmt.setInt(2, part.getId());
                                        assocStmt.executeUpdate();
                                    }
                                }
                                
                                // If we got here, commit the transaction
                                conn.commit();
                                
                                // Add to UI list and close window
                                com.inventory.Inventory.getAllProducts().add(newProduct);
                                Stage stage = (Stage) addProductNameTextField.getScene().getWindow();
                                stage.close();
                            }
                        } else {
                            conn.rollback();
                            addProductSaveErrorLabel.setText("Error: Failed to save product to database!");
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
                    System.err.println("Error saving product to database: " + e.getMessage());
                    e.printStackTrace();
                    addProductSaveErrorLabel.setText("Database error: " + e.getMessage());
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
            addProductSaveErrorLabel.setText("Error: Please enter valid numbers for Stock, Price, Min, and Max!");
        } catch (Exception e) {
            System.err.println("Error saving product: " + e.getMessage());
            e.printStackTrace();
            addProductSaveErrorLabel.setText("Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void addProductCancelButtonListener(ActionEvent event) {
        Stage stage = (Stage) addProductNameTextField.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void addProductsSearchFieldListener(ActionEvent event) {
        String searchText = addProductFilterField.getText().toLowerCase();
        FilteredList<com.inventory.Part> filteredData = new FilteredList<>(com.inventory.Inventory.getAllParts(), p -> true);
        
        filteredData.setPredicate(part -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            
            String lowerCaseFilter = searchText.toLowerCase();
            return part.getName().toLowerCase().contains(lowerCaseFilter) ||
                   String.valueOf(part.getId()).contains(lowerCaseFilter);
        });
        
        SortedList<com.inventory.Part> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(addProductTableView.comparatorProperty());
        addProductTableView.setItems(sortedData);
    }
    
    @FXML
    private void handlePartSearch() {
        String searchText = addProductFilterField.getText().toLowerCase();
        FilteredList<com.inventory.Part> filteredData = new FilteredList<>(com.inventory.Inventory.getAllParts(), p -> true);
        
        filteredData.setPredicate(part -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            
            String lowerCaseFilter = searchText.toLowerCase();
            return part.getName().toLowerCase().contains(lowerCaseFilter) ||
                   String.valueOf(part.getId()).contains(lowerCaseFilter);
        });
        
        SortedList<com.inventory.Part> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(addProductTableView.comparatorProperty());
        addProductTableView.setItems(sortedData);
    }
}
