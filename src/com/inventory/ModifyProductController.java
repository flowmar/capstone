package com.inventory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * @author Omar Imam
 * @version %I% %G%
 */

public class ModifyProductController implements Initializable {

/*
 * Fields
 */

/**
 * saves the {@link com.inventory.Product} to the {@link com.inventory.Inventory}
 */
@FXML
private Button modifyProductSaveButton;

/**
 * Cancels the form and closes the window
 */
@FXML
private Button modifyProductCancelButton;

/**
 * the {@link javafx.scene.control.Label} that is used to display error messages
 */
@FXML
private Label modifyProductErrorLabel;

/**
 * the {@link javafx.scene.control.Label} that is used to display error messages related to saving
 */
@FXML
private Label modifyProductSaveErrorLabel;

/**
 * a {@link javafx.scene.control.TableView} that displays each {@link com.inventory.Part} in the {@link
 * com.inventory.Inventory}
 */
@FXML
private TableView<Part> modifyProductPartsTableView;

/**
 * a {@link javafx.scene.control.TableView} that displays each {@link com.inventory.Part} associated with the current
 * {@link com.inventory.Product}
 */
@FXML
private TableView<Part> modifyProductAssociatedPartsTableView;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product id
 */
@FXML
private TextField modifyProductIdTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product name
 */
@FXML
private TextField modifyProductNameTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product stock
 */
@FXML
private TextField modifyProductStockTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product price
 */
@FXML
private TextField modifyProductPriceTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product max
 */
@FXML
private TextField modifyProductMaxTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product min
 */
@FXML
private TextField modifyProductMinTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the filtering the {@link #modifyProductPartsTableView}
 */
@FXML
private TextField modifyProductSearchField;

/**
 * a list that holds each {@link com.inventory.Part} associated with the product
 */
@FXML
ObservableList<Part> associatedPartsList = FXCollections.observableArrayList( );

/**
 * Associates the selected part in the top TableView with the Product on the left.&nbsp;The Part will be copied
 * into the bottom TableView to show the association
 *
 * @param actionEvent fires when the Add button is clicked
 */
public void addAssociatedPartButtonListener( ActionEvent actionEvent ) {
  modifyProductSaveErrorLabel.setText("");
  // Get the selected part from the top table view
  Part selectedPart = modifyProductPartsTableView.getSelectionModel( ).getSelectedItem( );
  
  // Add the selected Part to the associated parts list
  associatedPartsList.add( selectedPart );
  
  // Set the data from the parts list into the TableView
  modifyProductAssociatedPartsTableView.setItems( associatedPartsList );
}

/**
 * Removes the selected part in the bottom TableView's association with the product on the left hand side.&nbsp;The
 * part will be removed from the bottom TableView
 *
 * @param actionEvent fired when the Remove button is clicked
 */
public void removeAssociatedPartButtonListener( ActionEvent actionEvent ) {
  // Get the selected part from the bottom table view
  Part selectedPart = ( Part ) modifyProductAssociatedPartsTableView.getSelectionModel( ).getSelectedItem( );
  
  if ( selectedPart == null ) {
    modifyProductErrorLabel.setText( "Error: No part selected!" );
  }
  else {
    // Create a dialog box that confirms the deletion
    Alert alert = new Alert( Alert.AlertType.CONFIRMATION );
    
    alert.setContentText( "Are you sure you want to remove this part association?" );
    
    alert.showAndWait( ).ifPresent( response ->
    {
      if ( response == ButtonType.OK ) {
        // Remove the selected part from the associated parts list
        associatedPartsList.remove( selectedPart );
        modifyProductSaveErrorLabel.setText( "Part association removed!" );
        modifyProductSaveErrorLabel.setStyle( "-fx-text-fill: #00ff00;" );
      }
      else if ( response == ButtonType.CANCEL ) {
        // If no, close out the window
        System.out.println( "Association removal cancelled" );
      }
    } );
  }
}

/**
 * Creates a new Product using the information from the TextFields.&nbsp;It also includes input
 * validation for the Stock, Min and Max fields.
 *
 * @param actionEvent fired when the Save button is clicked
 */
public void modifyProductSaveButtonListener( ActionEvent actionEvent ) {
  // Get the information from the text fields and place them into variables
  String modifyProductName  = modifyProductNameTextField.getText( );
  int    modifyProductStock = Integer.parseInt( modifyProductStockTextField.getText( ) );
  double modifyProductPrice = Double.parseDouble( modifyProductPriceTextField.getText( ) );
  int    modifyProductMax   = Integer.parseInt( modifyProductMaxTextField.getText( ) );
  int    modifyProductMin   = Integer.parseInt( modifyProductMinTextField.getText( ) );
  int    randomId           = Integer.parseInt( modifyProductIdTextField.getText( ) );
  
  boolean passCheck = true;
  // Input Validation Logic
  if ( modifyProductMin > modifyProductMax ) {
    modifyProductSaveErrorLabel.setStyle( "-fx-text-fill: #ff0000;" );
    modifyProductSaveErrorLabel.setText( "Error: Minimum cannot be more than maximum!" );
    passCheck = false;
  }
  else if ( modifyProductStock > modifyProductMax ) {
    modifyProductSaveErrorLabel.setStyle( "-fx-text-fill: #ff0000;" );
    modifyProductSaveErrorLabel.setText( "Error: Current Stock cannot be more than the maximum!" );
    passCheck = false;
  }
  else if ( modifyProductStock < modifyProductMin ) {
    modifyProductSaveErrorLabel.setStyle( "-fx-text-fill: #ff0000;" );
    modifyProductSaveErrorLabel.setText( "Error: Current Stock cannot be less than the minimum!" );
    passCheck = false;
  }
  else {
    modifyProductSaveErrorLabel.setText( "" );
  }
  
  if ( passCheck ) {
    try {
      Connection conn = DatabaseConnection.getConnection();
      
      // First update the product
      String productSql = "UPDATE products SET name = ?, price = ?, stock = ?, min = ?, max = ? WHERE id = ?";
      PreparedStatement productStmt = conn.prepareStatement(productSql);
      productStmt.setString(1, modifyProductName);
      productStmt.setDouble(2, modifyProductPrice);
      productStmt.setInt(3, modifyProductStock);
      productStmt.setInt(4, modifyProductMin);
      productStmt.setInt(5, modifyProductMax);
      productStmt.setInt(6, randomId);
      
      int productRowsAffected = productStmt.executeUpdate();
      
      if (productRowsAffected > 0) {
        // If product was updated successfully, update the associated parts
        
        // First, delete all existing associations
        String deleteAssocSql = "DELETE FROM product_parts WHERE product_id = ?";
        PreparedStatement deleteAssocStmt = conn.prepareStatement(deleteAssocSql);
        deleteAssocStmt.setInt(1, randomId);
        deleteAssocStmt.executeUpdate();
        
        // Then add all current associations
        String associationSql = "INSERT INTO product_parts (product_id, part_id) VALUES (?, ?)";
        PreparedStatement associationStmt = conn.prepareStatement(associationSql);
        
        for (Part part : associatedPartsList) {
          associationStmt.setInt(1, randomId);
          associationStmt.setInt(2, part.getId());
          associationStmt.executeUpdate();
        }
        
        // If everything was successful, update the UI
        Product modifiedProduct = new Product(randomId, modifyProductName, modifyProductPrice,
            modifyProductStock, modifyProductMin, modifyProductMax, associatedPartsList);
        Inventory.allProducts.set(Inventory.selectedProductIndex, modifiedProduct);
        
        // Close the window
        Stage stage = (Stage) modifyProductSaveButton.getScene().getWindow();
        stage.close();
      } else {
        modifyProductSaveErrorLabel.setText("Error: Product not found in database!");
        modifyProductSaveErrorLabel.setStyle("-fx-text-fill: #ff0000");
      }
    } catch (SQLException e) {
      System.out.println("Error updating product in database:");
      e.printStackTrace();
      modifyProductSaveErrorLabel.setText("Error: " + e.getMessage());
      modifyProductSaveErrorLabel.setStyle("-fx-text-fill: #ff0000");
    }
  } else {
    System.out.println("Didn't Pass");
  }
}

/**
 * Cancels the form and closes out the window
 *
 * @param actionEvent fires when the cancel button is clicked
 */
public void modifyProductCancelButtonListener( ActionEvent actionEvent ) {
  Stage stage = ( Stage ) modifyProductCancelButton.getScene( ).getWindow( );
  stage.close( );
}

/**
 * Initializes the new scene by placing the selected product data into the UI, creating TableColumns for
 *     the TableViews, associating the data with the columns, and adding a filtering function to the TextField in the
 *     upper right corner.&nbsp;It also sets TextFormatters on the TextFields for input validation.
 */
@Override
public void initialize( URL url, ResourceBundle resourceBundle ) {
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

  // Fill the TextFields with the information from the selected product
  Product selectedProduct = Inventory.selectedProduct;
  modifyProductIdTextField.setText(Integer.toString(selectedProduct.getId()));
  modifyProductNameTextField.setText(selectedProduct.getName());
  modifyProductStockTextField.setText(Integer.toString(selectedProduct.getStock()));
  modifyProductPriceTextField.setText(Double.toString(selectedProduct.getPrice()));
  modifyProductMaxTextField.setText(Integer.toString(selectedProduct.getMax()));
  modifyProductMinTextField.setText(Integer.toString(selectedProduct.getMin()));
  
  // Set the associated parts
  associatedPartsList.setAll(selectedProduct.getAllAssociatedParts());
  modifyProductAssociatedPartsTableView.setItems(associatedPartsList);
  
  // Create the table columns
  TableColumn<Part, Integer> partsIdColumn    = new TableColumn<>( "id" );
  TableColumn<Part, String>  partsNameColumn  = new TableColumn<>( "Name" );
  TableColumn<Part, Double>  partsPriceColumn = new TableColumn<>( "Price" );
  TableColumn<Part, Integer> partsStockColumn = new TableColumn<>( "Stock" );
  modifyProductPartsTableView.getColumns( ).addAll( partsIdColumn, partsNameColumn, partsPriceColumn,
      partsStockColumn );
  
  TableColumn<Part, Integer> associatedPartsIdColumn    = new TableColumn<>( "id" );
  TableColumn<Part, String>  associatedPartsNameColumn  = new TableColumn<>( "Name" );
  TableColumn<Part, Double>  associatedPartsPriceColumn = new TableColumn<>( "Price" );
  TableColumn<Part, Integer> associatedPartsStockColumn = new TableColumn<>( "Stock" );
  modifyProductAssociatedPartsTableView.getColumns( ).addAll( associatedPartsIdColumn, associatedPartsNameColumn,
      associatedPartsPriceColumn,
      associatedPartsStockColumn );
  
  // Associate the data with the columns
  partsIdColumn.setCellValueFactory( new PropertyValueFactory<>( ( "id" ) ) );
  partsNameColumn.setCellValueFactory( new PropertyValueFactory<Part, String>( ( "Name" ) ) );
  partsStockColumn.setCellValueFactory( new PropertyValueFactory<Part, Integer>( ( "Stock" ) ) );
  partsPriceColumn.setCellValueFactory( new PropertyValueFactory<Part, Double>( ( "Price" ) ) );
  
  associatedPartsIdColumn.setCellValueFactory( new PropertyValueFactory<>( ( "id" ) ) );
  associatedPartsNameColumn.setCellValueFactory( new PropertyValueFactory<Part, String>( ( "Name" ) ) );
  associatedPartsStockColumn.setCellValueFactory( new PropertyValueFactory<Part, Integer>( ( "Stock" ) ) );
  associatedPartsPriceColumn.setCellValueFactory( new PropertyValueFactory<Part, Double>( ( "Price" ) ) );
  
  // Add data to the table view
  modifyProductPartsTableView.setItems( Inventory.allParts );
  
  // Wrap the Observable list in a FilteredList
  FilteredList<Part> filteredProductData = new FilteredList<Part>( Inventory.allParts, t -> true );
  
  // Set the filter to change whenever the filter changes
  modifyProductSearchField.textProperty( ).addListener( ( observable, oldValue, newValue ) ->
  {
    
    filteredProductData.setPredicate( product -> {
      
      if ( newValue == null || newValue.isEmpty( ) ) {
        modifyProductErrorLabel.setText( "" );
        return true;
      }
      
      // Compare each part with the filter text
      String lowerCaseFilter = newValue.toLowerCase( );
      
      if ( product.getName( ).toLowerCase( ).contains( lowerCaseFilter ) ) {
        modifyProductErrorLabel.setText( "" );
        return true;
      }
      else if ( Integer.toString( ( product.getId( ) ) ).contains( lowerCaseFilter ) ) {
        modifyProductErrorLabel.setText( "" );
        return true;
      }
  //      else if ( String.valueOf( product.getName( ) ).indexOf( lowerCaseFilter ) != -1 ) {
  //        productsErrorLabel.setText("3");
  //        return true;
  //      }
      else {
  //                modifyProductErrorLabel.setText( "4" );
        return false;
      }
    } );
  } );
  
  SortedList<Part> sortedProductData = new SortedList<>( filteredProductData );
  
  sortedProductData.comparatorProperty( ).bind( modifyProductPartsTableView.comparatorProperty( ) );
  
  modifyProductPartsTableView.setItems( sortedProductData );

// Set TextFormatters on each TextField to apply input validation
// Display an error message if incorrect characters are typed
  modifyProductStockTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      modifyProductSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      modifyProductSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      modifyProductSaveErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  modifyProductMaxTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      modifyProductSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      modifyProductSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      modifyProductSaveErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  modifyProductMinTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      modifyProductSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      modifyProductSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      modifyProductSaveErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  modifyProductNameTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "[a-zA-Z]+" ) ) {
      modifyProductSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      modifyProductSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      modifyProductSaveErrorLabel.setText( "Letters only!" );
      return change;
    }
  } ) );
  
  modifyProductPriceTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) || change.getText( ).matches( "\\." ) ) {
      modifyProductSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      modifyProductSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      modifyProductSaveErrorLabel.setText( "Prices don't contain letters!!" );
      return change;
    }
  } ) );
}

/**
 * Called when "Enter" is pressed when searching for a part in the parts table.&nbsp;It selects the matching
 *    part
 *
 * @param actionEvent fired when the user presses "Enter"
 */
public void modifyProductsSearchFieldListener( ActionEvent actionEvent ) {
  modifyProductPartsTableView.getSelectionModel( ).clearSelection( );
  modifyProductPartsTableView.getSelectionModel( ).selectFirst( );
  Part selected = modifyProductPartsTableView.getSelectionModel( ).getSelectedItem( );
  if ( selected == null ) {
    modifyProductErrorLabel.setText( "Error: Part not found" );
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
  if (button == modifyProductSaveButton) {
    return "#27B611";
  } else if (button == modifyProductCancelButton) {
    return "#ff0000";
  }
  return "#000000";
}
}
