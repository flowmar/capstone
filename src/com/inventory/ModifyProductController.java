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
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Omar Imam
 * @version %I% %G%
 */

public class ModifyProductController implements Initializable {

/**
 * Fields
 */
@FXML
private Button modifyProductSaveButton;

@FXML
private Button modifyProductCancelButton;

@FXML
private Label modifyProductErrorLabel;

@FXML
private Label modifyProductSaveErrorLabel;

@FXML
private TableView<Part> modifyProductPartsTableView;

@FXML
private TableView<Part> modifyProductAssociatedPartsTableView;

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
private TextField modifyProductSearchField;

@FXML
ObservableList<Part> associatedPartsList = FXCollections.observableArrayList( );

/**
 * @param actionEvent fires when the Add button is clicked
 * @function Associates the selected part in the top TableView with the Product on the left. The Part will be copied
 *     into the bottom TableView to show the association
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
 * @param actionEvent fired when the Remove button is clicked
 * @function Removes the selected part in the bottom TableView's association with the product on the left hand side.
 *     The part will be removed from the bottom TableView
 */
public void removeAssociatedPartButtonListener( ActionEvent actionEvent ) {
  // Get the selected part from the bottom table view
  Part selectedPart = ( Part ) modifyProductAssociatedPartsTableView.getSelectionModel( ).getSelectedItem( );
  
  if ( selectedPart == null ) {
    modifyProductErrorLabel.setText( "Error: No part selected!" );
  }
  else {
    // Remove the selected part from the associated parts list
    associatedPartsList.remove( selectedPart );
    modifyProductSaveErrorLabel.setText( "Part association removed!" );
    modifyProductSaveErrorLabel.setStyle( "-fx-text-fill: #00ff00;" );
  }
}

/**
 * @function Creates a new Product using the information from the TextFields. It also includes input
 * validation for the Stock, Min and Max fields.
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
    passCheck = true;
  }
  
  if ( passCheck == true ) {
    // Save the modified product
    Inventory.allProducts.set( Inventory.selectedProductIndex, new Product( randomId, modifyProductName,
        modifyProductPrice,
        modifyProductStock, modifyProductMin, modifyProductMax, associatedPartsList ) );
    
    // Close the window
    Stage stage = ( Stage ) modifyProductSaveButton.getScene( ).getWindow( );
    stage.close( );
  }
  else {
    System.out.println( "Didn't Pass" );
  }
}

/**
 * @param actionEvent fires when the cancel button is clicked
 * @function Cancels the form and closes out the window
 */
public void modifyProductCancelButtonListener( ActionEvent actionEvent ) {
  Stage stage = ( Stage ) modifyProductCancelButton.getScene( ).getWindow( );
  stage.close( );
}

/**
 * @function Initializes the new scene by placing the selected product data into the UI, creating TableColumns for
 *     the TableViews, associating the data with the columns, and adding a filtering function to the TextField in the
 *     upper right corner. It also sets TextFormatters on the TextFields for input validation.
 */
@Override
public void initialize( URL url, ResourceBundle resourceBundle ) {
  // Fill the TextFields with the information from the selected product
  modifyProductIdTextField.setText( Integer.toString( Inventory.selectedProduct.getId( ) ) );
  modifyProductNameTextField.setText( Inventory.selectedProduct.getName( ) );
  modifyProductStockTextField.setText( Integer.toString( Inventory.selectedProduct.getStock( ) ) );
  modifyProductPriceTextField.setText( Double.toString( Inventory.selectedProduct.getPrice( ) ) );
  modifyProductMaxTextField.setText( Integer.toString( Inventory.selectedProduct.getMax( ) ) );
  modifyProductMinTextField.setText( Integer.toString( Inventory.selectedProduct.getMin( ) ) );
  associatedPartsList.setAll( Inventory.selectedProduct.getAllAssociatedParts( ) );
  modifyProductAssociatedPartsTableView.setItems( associatedPartsList );
  
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
 * @param actionEvent fired when the user presses "Enter"
 * @function is called when "Enter" is pressed when searching for a part in the parts table. It selects the matching
 *     part
 */
public void modifyProductsSearchFieldListener( ActionEvent actionEvent ) {
  modifyProductPartsTableView.getSelectionModel( ).clearSelection( );
  modifyProductPartsTableView.getSelectionModel( ).selectFirst( );
  Part selected = modifyProductPartsTableView.getSelectionModel( ).getSelectedItem( );
  if ( selected == null ) {
    modifyProductErrorLabel.setText( "Error: Part not found" );
  }
}


}
