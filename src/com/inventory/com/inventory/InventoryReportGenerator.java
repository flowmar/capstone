package com.inventory;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
	private final com.inventory.DatabaseConnection dbConnection;
	
	public InventoryReportGenerator() {
		this.dbConnection = new com.inventory.DatabaseConnection();
	}
	
	public void generateInventorySummaryReport() {
		try ( Connection conn = DatabaseConnection.getConnection() ) {
			// Prepare SQL queries for inventory summary
			String productQuery = "SELECT " +
			                      "COUNT(*) as total_products, " +
			                      "SUM(stock) as total_stock, " +
			                      "ROUND(AVG(price), 2) as avg_price, " +
			                      "ROUND(MIN(price), 2) as min_price, " +
			                      "ROUND(MAX(price), 2) as max_price, " +
			                      "ROUND(SUM(stock * price), 2) as total_inventory_value, " +
			                      "SUM(CASE WHEN stock <= (min + 3) THEN 1 ELSE 0 END) as low_stock_products " +
			                      "FROM products";
			
			String partsQuery = "SELECT " +
			                    "COUNT(*) as total_parts, " +
			                    "SUM(stock) as total_parts_stock, " +
			                    "ROUND(AVG(price), 2) as avg_part_price, " +
			                    "ROUND(MIN(price), 2) as min_part_price, " +
			                    "ROUND(MAX(price), 2) as max_part_price, " +
			                    "SUM(CASE WHEN stock <= (min + 3) THEN 1 ELSE 0 END) as low_stock_parts, " +
			                    "COUNT(CASE WHEN type = 'InHouse' THEN 1 END) as inhouse_parts, " +
			                    "COUNT(CASE WHEN type = 'Outsourced' THEN 1 END) as outsourced_parts " +
			                    "FROM parts";
			
			String productPartsQuery = "SELECT " +
			                           "COUNT(DISTINCT product_id) as products_with_parts, " +
			                           "COUNT(DISTINCT part_id) as parts_in_products, " +
			                           "COUNT(*) as total_associations " +
			                           "FROM product_parts";
			
			PreparedStatement productStmt = conn.prepareStatement(productQuery);
			PreparedStatement partsStmt = conn.prepareStatement(partsQuery);
			PreparedStatement productPartsStmt = conn.prepareStatement(productPartsQuery);
			
			ResultSet productRs = productStmt.executeQuery();
			ResultSet partsRs = partsStmt.executeQuery();
			ResultSet productPartsRs = productPartsStmt.executeQuery();
			
			if ( productRs.next() && partsRs.next() ) {
				// Create a new stage for the report
				Stage reportStage = new Stage();
				reportStage.initModality(Modality.APPLICATION_MODAL);
				reportStage.setTitle("Inventory Summary Report");
				
				// Create header layout
				GridPane headerPane = new GridPane();
				headerPane.setPadding(new Insets(10));
				headerPane.setHgap(10);
				headerPane.setVgap(10);
				
				// Title label
				Label titleLabel = new Label("INVENTORY SUMMARY REPORT");
				titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
				GridPane.setHalignment(titleLabel, HPos.LEFT);
				
				// Timestamp label
				Label timestampLabel = new Label(LocalDateTime.now()
				                                              .format(DateTimeFormatter.ofPattern(
						                                              "EEEE, MMMM d, yyyy 'at' h:mm a")));
				timestampLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
				GridPane.setHalignment(timestampLabel, HPos.RIGHT);
				
				headerPane.add(titleLabel, 0, 0);
				headerPane.add(timestampLabel, 1, 0);
				GridPane.setHgrow(timestampLabel, Priority.ALWAYS);
				
				// Create TableView for product statistics
				TableView<ReportRow> productTable = new TableView<>();
				productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
				
				TableColumn<ReportRow, String> metricCol = new TableColumn<>("Metric");
				TableColumn<ReportRow, String> valueCol = new TableColumn<>("Value");
				
				metricCol.setCellValueFactory(new PropertyValueFactory<>("metric"));
				valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
				
				productTable.getColumns().addAll(metricCol, valueCol);
				
				// Format for currency and numbers
				NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
				NumberFormat numberFormat = NumberFormat.getNumberInstance();
				
				// Add data to table
				ObservableList<ReportRow> productData = FXCollections.observableArrayList(
						new ReportRow("Total Products", numberFormat.format(productRs.getInt("total_products"))),
						new ReportRow("Total Stock", numberFormat.format(productRs.getInt("total_stock"))),
						new ReportRow("Average Product Price", currencyFormat.format(productRs.getDouble("avg_price"))),
						new ReportRow("Lowest Product Price", currencyFormat.format(productRs.getDouble("min_price"))),
						new ReportRow("Highest Product Price", currencyFormat.format(productRs.getDouble("max_price"))),
						new ReportRow("Total Inventory Value",
						              currencyFormat.format(productRs.getDouble("total_inventory_value"))),
						new ReportRow("Low Stock Products",
						              numberFormat.format(productRs.getInt("low_stock_products")))
				);
				
				productTable.setItems(productData);
				
				// Create TableView for parts statistics
				TableView<ReportRow> partsTable = new TableView<>();
				partsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
				
				TableColumn<ReportRow, String> partsMetricCol = new TableColumn<>("Metric");
				TableColumn<ReportRow, String> partsValueCol = new TableColumn<>("Value");
				
				partsMetricCol.setCellValueFactory(new PropertyValueFactory<>("metric"));
				partsValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
				
				partsTable.getColumns().addAll(partsMetricCol, partsValueCol);
				
				ObservableList<ReportRow> partsData = FXCollections.observableArrayList(
						new ReportRow("Total Parts", numberFormat.format(partsRs.getInt("total_parts"))),
						new ReportRow("Total Parts Stock", numberFormat.format(partsRs.getInt("total_parts_stock"))),
						new ReportRow("Average Part Price", currencyFormat.format(partsRs.getDouble("avg_part_price"))),
						new ReportRow("Lowest Part Price", currencyFormat.format(partsRs.getDouble("min_part_price"))),
						new ReportRow("Highest Part Price", currencyFormat.format(partsRs.getDouble("max_part_price"))),
						new ReportRow("Low Stock Parts", numberFormat.format(partsRs.getInt("low_stock_parts"))),
						new ReportRow("In-House Parts", numberFormat.format(partsRs.getInt("inhouse_parts"))),
						new ReportRow("Outsourced Parts", numberFormat.format(partsRs.getInt("outsourced_parts")))
				);
				
				partsTable.setItems(partsData);
				
				// Create TableView for product-parts relationships
				TableView<ReportRow> relationshipTable = new TableView<>();
				relationshipTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
				
				TableColumn<ReportRow, String> relMetricCol = new TableColumn<>("Metric");
				TableColumn<ReportRow, String> relValueCol = new TableColumn<>("Value");
				
				relMetricCol.setCellValueFactory(new PropertyValueFactory<>("metric"));
				relValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
				
				relationshipTable.getColumns().addAll(relMetricCol, relValueCol);
				
				if ( productPartsRs.next() ) {
					ObservableList<ReportRow> relationshipData = FXCollections.observableArrayList(
							new ReportRow("Products with Parts",
							              numberFormat.format(productPartsRs.getInt("products_with_parts"))),
							new ReportRow("Parts Used in Products",
							              numberFormat.format(productPartsRs.getInt("parts_in_products"))),
							new ReportRow("Total Part Associations",
							              numberFormat.format(productPartsRs.getInt("total_associations")))
					);
					relationshipTable.setItems(relationshipData);
				}
				
				// Export button
				Button exportButton = new Button("Export Report");
				exportButton.setStyle(
						"-fx-background-color: #FFA500; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
				exportButton.setOnAction(e -> exportReport(generateReportContent(productData,
				                                                                 partsData,
				                                                                 relationshipTable.getItems())));
				
				// Layout
				VBox layout = new VBox(10);
				layout.setPadding(new Insets(15));
				layout.setStyle("-fx-background-color: white;");
				
				// Add section headers
				Label productHeader = new Label("PRODUCT STATISTICS");
				productHeader.setFont(Font.font("System", FontWeight.BOLD, 16));
				Label partsHeader = new Label("PARTS STATISTICS");
				partsHeader.setFont(Font.font("System", FontWeight.BOLD, 16));
				Label relationshipHeader = new Label("PRODUCT-PART RELATIONSHIPS");
				relationshipHeader.setFont(Font.font("System", FontWeight.BOLD, 16));
				
				layout.getChildren().addAll(headerPane,
				                            productHeader, productTable,
				                            partsHeader, partsTable,
				                            relationshipHeader, relationshipTable,
				                            exportButton);
				
				// Scene
				Scene scene = new Scene(layout, 600, 600);
				reportStage.setScene(scene);
				reportStage.show();
			}
		}
		catch ( SQLException e ) {
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
		if ( selectedFile != null ) {
			try ( FileWriter writer = new FileWriter(selectedFile) ) {
				writer.write(reportContent);
				showInfoAlert("Export Successful", "Report exported to " + selectedFile.getAbsolutePath());
			}
			catch ( IOException e ) {
				showErrorAlert("Export Error", "Could not export report", e.getMessage());
			}
		}
	}
	
	private String generateReportContent(ObservableList<ReportRow> productData,
	                                     ObservableList<ReportRow> partsData,
	                                     ObservableList<ReportRow> relationshipData) {
		StringBuilder content = new StringBuilder();
		content.append("INVENTORY SUMMARY REPORT\n")
		       .append("=".repeat(24)).append("\n")
		       .append("Generated on: ")
		       .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a")))
		       .append("\n\n");
		
		content.append("PRODUCT STATISTICS\n").append("-".repeat(20)).append("\n");
		for ( ReportRow row : productData ) {
			content.append(row.getMetric()).append(": ").append(row.getValue()).append("\n");
		}
		content.append("\n");
		
		content.append("PARTS STATISTICS\n").append("-".repeat(19)).append("\n");
		for ( ReportRow row : partsData ) {
			content.append(row.getMetric()).append(": ").append(row.getValue()).append("\n");
		}
		content.append("\n");
		
		content.append("PRODUCT-PART RELATIONSHIPS\n").append("-".repeat(25)).append("\n");
		for ( ReportRow row : relationshipData ) {
			content.append(row.getMetric()).append(": ").append(row.getValue()).append("\n");
		}
		
		return content.toString();
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
	
	// Helper class for TableView rows
	public static class ReportRow {
		private final StringProperty metric;
		private final StringProperty value;
		
		public ReportRow(String metric, String value) {
			this.metric = new SimpleStringProperty(metric);
			this.value = new SimpleStringProperty(value);
		}
		
		public String getMetric() {
			return metric.get();
		}
		
		public String getValue() {
			return value.get();
		}
		
		public StringProperty metricProperty() {
			return metric;
		}
		
		public StringProperty valueProperty() {
			return value;
		}
	}
}
