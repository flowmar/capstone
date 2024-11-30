package com.inventory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InventoryReportGenerator {
    private DatabaseConnection dbConnection;

    public InventoryReportGenerator() {
        this.dbConnection = new DatabaseConnection();
    }

    public void generateInventorySummaryReport() {
        try (Connection conn = dbConnection.getConnection()) {
            // Prepare SQL queries for inventory summary
            String productQuery = "SELECT " +
                "COUNT(*) as total_products, " +
                "SUM(stock) as total_stock, " +
                "ROUND(AVG(price), 2) as avg_price, " +
                "ROUND(SUM(stock * price), 2) as total_inventory_value, " +
                "SUM(CASE WHEN stock < min THEN 1 ELSE 0 END) as low_stock_products " +
                "FROM products";

            String partsQuery = "SELECT " +
                "COUNT(*) as total_parts, " +
                "SUM(stock) as total_parts_stock " +
                "FROM parts";

            PreparedStatement productStmt = conn.prepareStatement(productQuery);
            PreparedStatement partsStmt = conn.prepareStatement(partsQuery);

            ResultSet productRs = productStmt.executeQuery();
            ResultSet partsRs = partsStmt.executeQuery();

            if (productRs.next() && partsRs.next()) {
                // Create a new stage for the report
                Stage reportStage = new Stage();
                reportStage.initModality(Modality.APPLICATION_MODAL);
                reportStage.setTitle("Inventory Summary Report");

                // Format for currency and numbers
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
                NumberFormat numberFormat = NumberFormat.getNumberInstance();

                // Create report text area
                TextArea reportTextArea = new TextArea();
                reportTextArea.setEditable(false);
                reportTextArea.setWrapText(true);
                reportTextArea.setStyle("-fx-control-inner-background: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold;");

                // Generate report content
                StringBuilder reportContent = new StringBuilder();
                reportContent.append("INVENTORY SUMMARY REPORT\n").append("=".repeat(24)).append("\n");
                reportContent.append("Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a"))).append("\n\n");
                
                reportContent.append("PRODUCT STATISTICS\n").append("-".repeat(20)).append("\n");
                reportContent.append("Total Products: ").append(numberFormat.format(productRs.getInt("total_products"))).append("\n");
                reportContent.append("Total Stock: ").append(numberFormat.format(productRs.getInt("total_stock"))).append("\n");
                reportContent.append("Average Product Price: ").append(currencyFormat.format(productRs.getDouble("avg_price"))).append("\n");
                reportContent.append("Total Inventory Value: ").append(currencyFormat.format(productRs.getDouble("total_inventory_value"))).append("\n");
                reportContent.append("Low Stock Products: ").append(numberFormat.format(productRs.getInt("low_stock_products"))).append("\n\n");

                reportContent.append("PARTS STATISTICS\n").append("-".repeat(19)).append("\n");
                reportContent.append("Total Parts: ").append(numberFormat.format(partsRs.getInt("total_parts"))).append("\n");
                reportContent.append("Total Parts Stock: ").append(numberFormat.format(partsRs.getInt("total_parts_stock"))).append("\n");

                reportTextArea.setText(reportContent.toString());

                // Export button
                Button exportButton = new Button("Export Report");
                exportButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
                exportButton.setOnAction(e -> exportReport(reportContent.toString()));

                // Layout
                VBox layout = new VBox(10);
                layout.setPadding(new Insets(15));
                layout.setStyle("-fx-background-color: #0d2c54;");
                layout.getChildren().addAll(reportTextArea, exportButton);

                // Scene
                Scene scene = new Scene(layout, 500, 500);
                scene.setFill(javafx.scene.paint.Color.valueOf("#0d2c54"));
                reportStage.setScene(scene);
                reportStage.show();
            }
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Could not generate inventory report", e.getMessage());
        }
    }

    private void exportReport(String reportContent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Inventory Report");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            try (FileWriter writer = new FileWriter(selectedFile)) {
                writer.write(reportContent);
                showInfoAlert("Export Successful", "Report exported to " + selectedFile.getAbsolutePath());
            } catch (IOException e) {
                showErrorAlert("Export Error", "Could not export report", e.getMessage());
            }
        }
    }

    private void showErrorAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private void showInfoAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
