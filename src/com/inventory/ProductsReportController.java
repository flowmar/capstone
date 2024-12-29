package com.inventory;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductsReportController {
    @FXML private TableView<ProductRow> productsTableView;
    @FXML private TableColumn<ProductRow, Integer> idColumn;
    @FXML private TableColumn<ProductRow, String> nameColumn;
    @FXML private TableColumn<ProductRow, String> priceColumn;
    @FXML private TableColumn<ProductRow, Integer> stockColumn;
    @FXML private TableColumn<ProductRow, Integer> minColumn;
    @FXML private TableColumn<ProductRow, Integer> maxColumn;
    @FXML private Label timestampLabel;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @FXML
    public void initialize() {
        setupTableColumns();
        loadProductsData();
        setupTimestamp();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        minColumn.setCellValueFactory(new PropertyValueFactory<>("min"));
        maxColumn.setCellValueFactory(new PropertyValueFactory<>("max"));
    }

    private void loadProductsData() {
        ObservableList<ProductRow> productRows = FXCollections.observableArrayList();
        ObservableList<Product> products = Inventory.getAllProducts();

        for (Product product : products) {
            productRows.add(new ProductRow(
                product.getId(),
                product.getName(),
                currencyFormat.format(product.getPrice()),
                product.getStock(),
                product.getMin(),
                product.getMax()
            ));
        }

        productsTableView.setItems(productRows);
    }

    private void setupTimestamp() {
        timestampLabel.setText(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a")));
    }

    @FXML
    private void handleExportReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Products Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.setInitialFileName("ProductsReport_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");

        File file = fileChooser.showSaveDialog(productsTableView.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write header
                writer.println("Products Summary Report");
                writer.println("Generated: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a")));
                writer.println("=".repeat(80));
                writer.println();

                // Write table header
                writer.printf("%-5s | %-30s | %-10s | %-6s | %-4s | %-4s%n",
                    "ID", "Name", "Price", "Stock", "Min", "Max");
                writer.println("-".repeat(80));

                // Write table data
                for (ProductRow row : productsTableView.getItems()) {
                    writer.printf("%-5d | %-30s | %-10s | %-6d | %-4d | %-4d%n",
                        row.getId(),
                        row.getName(),
                        row.getPrice(),
                        row.getStock(),
                        row.getMin(),
                        row.getMax()
                    );
                }

                System.out.println("Report exported successfully to: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error exporting report: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) productsTableView.getScene().getWindow()).close();
    }

    // Helper class for TableView rows
    public static class ProductRow {
        private final int id;
        private final String name;
        private final String price;
        private final int stock;
        private final int min;
        private final int max;

        public ProductRow(int id, String name, String price, int stock, int min, int max) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.min = min;
            this.max = max;
        }

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getPrice() { return price; }
        public int getStock() { return stock; }
        public int getMin() { return min; }
        public int getMax() { return max; }
    }
}
