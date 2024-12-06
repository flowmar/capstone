package com.inventory;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Inventory extends Application {

    public static ObservableList<Part> allParts = FXCollections.observableArrayList();
    public static ObservableList<Product> allProducts = FXCollections.observableArrayList();
    public static Part selectedPart;
    public static int selectedPartIndex;
    public static int selectedPartId;
    public static Product selectedProduct;
    public static int selectedProductIndex;
    public static int selectedProductId;

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(
		            "/com/inventory/inventoryScene.fxml")));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Inventory Management System");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Error starting application:");
            e.printStackTrace();
        }
    }

    public void loadPartsFromDatabase() {
        String sql = "SELECT * FROM parts";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Part part;
                if (rs.getString("type").equals("InHouse")) {
                    part = new InHouse(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("min"),
                        rs.getInt("max"),
                        rs.getInt("machine_id")
                    );
                } else {
                    part = new Outsourced(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("min"),
                        rs.getInt("max"),
                        rs.getString("company_name")
                    );
                }
                allParts.add(part);
                System.out.println("Loaded part: " + part.getName());
            }
        } catch (SQLException e) {
            System.out.println("Error loading parts from database:");
            e.printStackTrace();
        }
    }

    public void loadProductsFromDatabase() {
        String sql = "SELECT * FROM products";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ObservableList<Part> associatedParts = FXCollections.observableArrayList();
                Product product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getInt("min"),
                    rs.getInt("max"),
                    associatedParts
                );
                
                // Load associated parts
                String partsSql = "SELECT p.* FROM parts p " +
                                "JOIN product_parts pp ON p.id = pp.part_id " +
                                "WHERE pp.product_id = ?";
                try (PreparedStatement partsStmt = DatabaseConnection.getConnection().prepareStatement(partsSql)) {
                    partsStmt.setInt(1, product.getId());
                    ResultSet partsRs = partsStmt.executeQuery();
                    while (partsRs.next()) {
                        for (Part part : allParts) {
                            if (part.getId() == partsRs.getInt("id")) {
                                product.addAssociatedPart(part);
                                break;
                            }
                        }
                    }
                }
                allProducts.add(product);
                System.out.println("Loaded product: " + product.getName());
            }
        } catch (SQLException e) {
            System.out.println("Error loading products from database:");
            e.printStackTrace();
        }
    }

    public void addPart(Part newPart) {
        allParts.add(newPart);
        // Add to database
        String sql = "INSERT INTO parts (name, price, stock, min, max, type, machine_id, company_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, newPart.getName());
            stmt.setDouble(2, newPart.getPrice());
            stmt.setInt(3, newPart.getStock());
            stmt.setInt(4, newPart.getMin());
            stmt.setInt(5, newPart.getMax());
            
            if (newPart instanceof InHouse) {
                stmt.setString(6, "InHouse");
                stmt.setInt(7, ((InHouse) newPart).getMachineId());
                stmt.setNull(8, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(6, "Outsourced");
                stmt.setNull(7, java.sql.Types.INTEGER);
                stmt.setString(8, ((Outsourced) newPart).getCompanyName());
            }
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding part to database:");
            e.printStackTrace();
        }
    }

    public void addProduct(Product newProduct) {
        allProducts.add(newProduct);
        // Add to database
        String sql = "INSERT INTO products (name, price, stock, min, max) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, newProduct.getName());
            stmt.setDouble(2, newProduct.getPrice());
            stmt.setInt(3, newProduct.getStock());
            stmt.setInt(4, newProduct.getMin());
            stmt.setInt(5, newProduct.getMax());
            
            stmt.executeUpdate();
            
            // Get the generated product ID
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int productId = rs.getInt(1);
                
                // Add associated parts
                String partsSql = "INSERT INTO product_parts (product_id, part_id) VALUES (?, ?)";
                try (PreparedStatement partsStmt = DatabaseConnection.getConnection().prepareStatement(partsSql)) {
                    for (Part part : newProduct.getAllAssociatedParts()) {
                        partsStmt.setInt(1, productId);
                        partsStmt.setInt(2, part.getId());
                        partsStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding product to database:");
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        DatabaseConnection.closeAllConnections();
    }

    public static void main(String[] args) {
        launch(args);
    }
}