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

public class PartsReportController {
    @FXML private TableView<PartRow> partsTableView;
    @FXML private TableColumn<PartRow, Integer> idColumn;
    @FXML private TableColumn<PartRow, String> nameColumn;
    @FXML private TableColumn<PartRow, String> priceColumn;
    @FXML private TableColumn<PartRow, Integer> stockColumn;
    @FXML private TableColumn<PartRow, Integer> minColumn;
    @FXML private TableColumn<PartRow, Integer> maxColumn;
    @FXML private TableColumn<PartRow, String> typeColumn;
    @FXML private TableColumn<PartRow, String> additionalInfoColumn;
    @FXML private Label timestampLabel;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @FXML
    public void initialize() {
        setupTableColumns();
        loadPartsData();
        setupTimestamp();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        minColumn.setCellValueFactory(new PropertyValueFactory<>("min"));
        maxColumn.setCellValueFactory(new PropertyValueFactory<>("max"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        additionalInfoColumn.setCellValueFactory(new PropertyValueFactory<>("additionalInfo"));
    }

    private void loadPartsData() {
        ObservableList<PartRow> partRows = FXCollections.observableArrayList();
        ObservableList<Part> parts = Inventory.getAllParts();

        for (Part part : parts) {
            String type = part instanceof InHouse ? "In-House" : "Outsourced";
            String additionalInfo = part instanceof InHouse ? 
                "Machine ID: " + ((InHouse) part).getMachineId() :
                "Company: " + ((Outsourced) part).getCompanyName();

            partRows.add(new PartRow(
                part.getId(),
                part.getName(),
                currencyFormat.format(part.getPrice()),
                part.getStock(),
                part.getMin(),
                part.getMax(),
                type,
                additionalInfo
            ));
        }

        partsTableView.setItems(partRows);
    }

    private void setupTimestamp() {
        timestampLabel.setText(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a")));
    }

    @FXML
    private void handleExportReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Parts Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.setInitialFileName("PartsReport_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");

        File file = fileChooser.showSaveDialog(partsTableView.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write header
                writer.println("Parts Summary Report");
                writer.println("Generated: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a")));
                writer.println("=".repeat(100));
                writer.println();

                // Write table header
                writer.printf("%-5s | %-20s | %-10s | %-6s | %-4s | %-4s | %-10s | %-30s%n",
                    "ID", "Name", "Price", "Stock", "Min", "Max", "Type", "Additional Info");
                writer.println("-".repeat(100));

                // Write table data
                for (PartRow row : partsTableView.getItems()) {
                    writer.printf("%-5d | %-20s | %-10s | %-6d | %-4d | %-4d | %-10s | %-30s%n",
                        row.getId(),
                        row.getName(),
                        row.getPrice(),
                        row.getStock(),
                        row.getMin(),
                        row.getMax(),
                        row.getType(),
                        row.getAdditionalInfo()
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
        ((Stage) partsTableView.getScene().getWindow()).close();
    }

    // Helper class for TableView rows
    public static class PartRow {
        private final int id;
        private final String name;
        private final String price;
        private final int stock;
        private final int min;
        private final int max;
        private final String type;
        private final String additionalInfo;

        public PartRow(int id, String name, String price, int stock, int min, int max, 
                      String type, String additionalInfo) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.min = min;
            this.max = max;
            this.type = type;
            this.additionalInfo = additionalInfo;
        }

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getPrice() { return price; }
        public int getStock() { return stock; }
        public int getMin() { return min; }
        public int getMax() { return max; }
        public String getType() { return type; }
        public String getAdditionalInfo() { return additionalInfo; }
    }
}
