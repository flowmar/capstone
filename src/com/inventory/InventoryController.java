package com.inventory;

import javafx.application.Platform;
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
import javafx.scene.control.cell.PropertyValueFactory;
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
    public TableView<Part> partsTableView = new TableView<>();

    @FXML
    public TableView<Product> productsTableView = new TableView<>();

    @FXML
    public Stage stage = new Stage();

    @FXML
    private TextField filterFieldParts;

    @FXML
    private TextField filterFieldProducts;

    @FXML
    public Button mainFormExitButton;

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
        TableColumn<Part, Integer> partsIdColumn = new TableColumn<>("id");
        TableColumn<Part, String> partsNameColumn = new TableColumn<>("Name");
        TableColumn<Part, Double> partsPriceColumn = new TableColumn<>("Price");
        TableColumn<Part, Integer> partsStockColumn = new TableColumn<>("Stock");
        partsTableView.getColumns().addAll(partsIdColumn, partsNameColumn, partsPriceColumn, partsStockColumn);

        TableColumn<Product, Integer> productsIdColumn = new TableColumn<>("id");
        TableColumn<Product, String> productsNameColumn = new TableColumn<>("Name");
        TableColumn<Product, Double> productsPriceColumn = new TableColumn<>("Price");
        TableColumn<Product, Integer> productsStockColumn = new TableColumn<>("Stock");
        productsTableView.getColumns().addAll(productsIdColumn, productsNameColumn, productsPriceColumn, productsStockColumn);

        // Associate the data with the columns
        partsIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partsNameColumn.setCellValueFactory(new PropertyValueFactory<Part, String>("Name"));
        partsStockColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("Stock"));
        partsPriceColumn.setCellValueFactory(new PropertyValueFactory<Part, Double>("Price"));

        productsIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productsNameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("Name"));
        productsStockColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("Stock"));
        productsPriceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("Price"));

        // Add the data to the table
        partsTableView.setItems(Inventory.allParts);
        productsTableView.setItems(Inventory.allProducts);

        partsIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        partsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        // Set up filtering for parts
        FilteredList<Part> filteredData = new FilteredList<>(Inventory.allParts, t -> true);
        setupPartsFilter(filteredData);
        SortedList<Part> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(partsTableView.comparatorProperty());
        partsTableView.setItems(sortedData);

        // Set up filtering for products
        FilteredList<Product> filteredProductData = new FilteredList<>(Inventory.allProducts, t -> true);
        setupProductsFilter(filteredProductData);
        SortedList<Product> sortedProductData = new SortedList<>(filteredProductData);
        sortedProductData.comparatorProperty().bind(productsTableView.comparatorProperty());
        productsTableView.setItems(sortedProductData);

        // Add button hover and click animation
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

        mainFormExitButton.setOnMouseEntered(event -> onButtonHover(event));
        mainFormExitButton.setOnMouseExited(event -> onButtonExit(event));
        mainFormExitButton.setOnMousePressed(event -> onButtonPress(event));
        mainFormExitButton.setOnMouseReleased(event -> onButtonRelease(event));
    }

    private void loadDataFromDatabase() {
        // This method is now deprecated since data loading is handled in LoginController
        System.out.println("Data loading is now handled during login");
    }

    private void loadPartsFromDatabase() {
        // This method is now deprecated since data loading is handled in LoginController
        System.out.println("Parts loading is now handled during login");
    }

    private void loadProductsFromDatabase() {
        // This method is now deprecated since data loading is handled in LoginController
        System.out.println("Products loading is now handled during login");
    }

    private void setupPartsFilter(FilteredList<Part> filteredData) {
        filterFieldParts.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(part -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (part.getName().toLowerCase().contains(lowerCaseFilter)) {
                    partsErrorLabel.setText("");
                    return true;
                } else if (Integer.toString(part.getId()).contains(lowerCaseFilter)) {
                    partsErrorLabel.setText("");
                    return true;
                } else {
                    partsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
                    partsErrorLabel.setText("Error: No Matching Part Found!");
                }
                return false;
            });
        });
    }

    private void setupProductsFilter(FilteredList<Product> filteredProductData) {
        filterFieldProducts.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProductData.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (product.getName().toLowerCase().contains(lowerCaseFilter)) {
                    productsErrorLabel.setText("");
                    return true;
                } else if (Integer.toString(product.getId()).contains(lowerCaseFilter)) {
                    productsErrorLabel.setText("");
                    return true;
                } else {
                    productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
                    productsErrorLabel.setText("Error: No Matching Product Found!");
                }
                return false;
            });
        });
    }

    public int getRandomNumber() {
        Random randomNumbers = new Random();
        return Math.abs(randomNumbers.nextInt(1000));
    }

    public void partsAddButtonListener(ActionEvent actionEvent) throws Exception {
        Parent parent;
        partsErrorLabel.setText("");
        parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("addPart.fxml")));
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Add Part");
        stage.setScene(scene);
        stage.show();
    }

    public void partsModifyButtonListener(ActionEvent actionEvent) throws Exception {
        partsErrorLabel.setText("");
        Part selectedPart = partsTableView.getSelectionModel().getSelectedItem();
        if (selectedPart == null) {
            partsErrorLabel.setText("Error: Please selected a part to modify!");
        } else {
            int selectedPartId = selectedPart.getId();
            Inventory.selectedPart = selectedPart;
            Inventory.selectedPartId = selectedPartId;
            Inventory.selectedPartIndex = partsTableView.getItems().indexOf(selectedPart);
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("modifyPart.fxml")));
            Stage stage = new Stage();
            Scene scene = new Scene(parent);
            stage.setTitle("Modify Part");
            stage.setScene(scene);
            stage.show();
        }
    }

    public void partsDeleteButtonListener(ActionEvent actionEvent) {
        partsErrorLabel.setText("");
        if (Inventory.allParts.size() == 0) {
            partsErrorLabel.setText("Error: Cannot Delete, the parts list is empty!");
        } else {
            Part selectedPart = partsTableView.getSelectionModel().getSelectedItem();
            if (selectedPart == null) {
                partsErrorLabel.setText("Error: Please select a part to delete!");
                return;
            }

            // Check if part is associated with any products
            try {
                String checkSql = "SELECT COUNT(*) FROM product_parts WHERE part_id = ?";
	            ResultSet rs;
	            try ( PreparedStatement checkStmt = DatabaseConnection.getConnection().prepareStatement(checkSql) ) {
		            checkStmt.setInt(1, selectedPart.getId());
		            rs = checkStmt.executeQuery();
	            }
	            rs.next();
                int count = rs.getInt(1);
                
                if (count > 0) {
                    partsErrorLabel.setText("Error: Cannot delete a part that is associated with products!");
                    partsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
                    return;
                }
            } catch (SQLException e) {
                System.out.println("Error checking product associations:");
                e.printStackTrace();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure you want to delete this part?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Delete from database
                        String sql = "DELETE FROM parts WHERE id = ?";
	                    int rowsAffected;
	                    try ( PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql) ) {
		                    stmt.setInt(1, selectedPart.getId());
		                    rowsAffected = stmt.executeUpdate();
	                    }
	                    
	                    if (rowsAffected > 0) {
                            // If database delete successful, remove from UI list
                            Inventory.allParts.remove(selectedPart);
                            partsErrorLabel.setText("Part Deleted!");
                            partsErrorLabel.setStyle("-fx-text-fill: #00ff00;");
                        } else {
                            partsErrorLabel.setText("Error: Part not found in database!");
                            partsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error deleting part from database:");
                        e.printStackTrace();
                        partsErrorLabel.setText("Error deleting from database: " + e.getMessage());
                        partsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
                    }
                } else if (response == ButtonType.CANCEL) {
                    System.out.println("Deletion cancelled");
                }
            });
        }
    }

    public void productsAddButtonListener(ActionEvent actionEvent) throws Exception {
        productsErrorLabel.setText("");
        productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("addProduct.fxml")));
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Add Product");
        stage.setScene(scene);
        stage.show();
    }

    public void productsModifyButtonListener(ActionEvent actionEvent) throws Exception {
        productsErrorLabel.setText("");
        productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
        Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            productsErrorLabel.setText("Error: Please select a product to modify! ");
        } else {
            int selectedProductId = selectedProduct.getId();
            Inventory.selectedProduct = selectedProduct;
            Inventory.selectedProductId = selectedProductId;
            Inventory.selectedProductIndex = productsTableView.getItems().indexOf(selectedProduct);
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("modifyProduct.fxml")));
            Stage stage = new Stage();
            Scene scene = new Scene(parent);
            stage.setTitle("Modify Product");
            stage.setScene(scene);
            stage.show();
        }
    }

    public void productsDeleteButtonListener(ActionEvent actionEvent) {
        productsErrorLabel.setText("");
        Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
            productsErrorLabel.setText("Error: Please select a product to delete!");
            return;
        }

        try {
            // Check if product has associated parts
            String checkSql = "SELECT COUNT(*) FROM product_parts WHERE product_id = ?";
	        ResultSet rs;
	        try ( PreparedStatement checkStmt = DatabaseConnection.getConnection().prepareStatement(checkSql) ) {
		        checkStmt.setInt(1, selectedProduct.getId());
		        rs = checkStmt.executeQuery();
	        }
	        rs.next();
            int count = rs.getInt(1);
            
            if (count > 0) {
                productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
                productsErrorLabel.setText("Error: Cannot delete a product that has associated parts.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure you want to delete this product?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Delete the product from database
                        String sql = "DELETE FROM products WHERE id = ?";
	                    int rowsAffected;
	                    try ( PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql) ) {
		                    stmt.setInt(1, selectedProduct.getId());
		                    rowsAffected = stmt.executeUpdate();
	                    }
	                    
	                    if (rowsAffected > 0) {
                            // If database delete successful, remove from UI
                            Inventory.allProducts.remove(selectedProduct);
                            productsErrorLabel.setText("Product Deleted!");
                            productsErrorLabel.setStyle("-fx-text-fill: #00ff00;");
                        } else {
                            productsErrorLabel.setText("Error: Product not found in database!");
                            productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error deleting product from database:");
                        e.printStackTrace();
                        productsErrorLabel.setText("Error deleting from database: " + e.getMessage());
                        productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
                    }
                } else if (response == ButtonType.CANCEL) {
                    System.out.println("Deletion cancelled");
                }
            });
        } catch (SQLException e) {
            System.out.println("Error checking product associations:");
            e.printStackTrace();
            productsErrorLabel.setText("Error checking database: " + e.getMessage());
            productsErrorLabel.setStyle("-fx-text-fill: #ff0000;");
        }
    }

    public void exitButtonListener(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void partsSearchFieldListener(ActionEvent actionEvent) {
        partsTableView.getSelectionModel().clearSelection();
        partsTableView.getSelectionModel().selectFirst();
        Part selected = partsTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            partsErrorLabel.setText("Error: Part not found");
        }
    }

    public void productsSearchFieldListener(ActionEvent actionEvent) {
        productsTableView.getSelectionModel().clearSelection();
        productsTableView.getSelectionModel().selectFirst();
        Product selected = productsTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            productsErrorLabel.setText("Error: Product not found!");
        }
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
            .replaceAll("-fx-effect: dropshadow\\(three-pass-box, rgba\\(0,0,0,0.2\\), 3, 0, 0, 1\\);", 
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);"));
    }

    private String getButtonColor(Button button) {
        if (button == partsAddButton || button == productsAddButton) {
            return "#27B611";
        } else if (button == partsModifyButton || button == productsModifyButton) {
            return "#0d2c54";
        } else if (button == partsDeleteButton || button == productsDeleteButton || button == mainFormExitButton) {
            return "#ff0000";
        }
        return "#000000";
    }
}