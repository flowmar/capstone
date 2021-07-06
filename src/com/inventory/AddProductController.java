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
import java.util.Random;
import java.util.ResourceBundle;

/**
 * @author Omar Imam
 * @version %I% %G%
 */

public class AddProductController implements Initializable {

/**
 * Fields
 */

@FXML
private Button addProductCancelButton;

@FXML
private Button addProductSaveButton;

@FXML
private Label addProductErrorLabel;

@FXML
private Label addProductSaveErrorLabel;

@FXML
private TextField addProductIdTextField;

@FXML
private TextField addProductNameTextField;

@FXML
private TextField addProductPriceTextField;

@FXML
private TextField addProductStockTextField;

@FXML
private TextField addProductMaxTextField;

@FXML
private TextField addProductMinTextField;

@FXML
private TextField addProductFilterField;

@FXML
private TableView<Part> addProductTableView;

@FXML
private TableView<Part> associatedPartTableView;

@FXML
// Create an ObservableList Object to hold the Associated Parts
ObservableList<Part> associatedPartsList = FXCollections.observableArrayList( );

/**
 * @return the random ID number
 *
 * @function Creates a random ID number for the new product
 */
public int getRandomNumber( ) {
  Random randomNumbers = new Random( );
  return Math.abs( randomNumbers.nextInt( 1000 ) );
}

/**
 * @function This function associates a part with a product
 * @param actionEvent fired when the add button is clicked
 */
public void addProductAddButtonListener( ActionEvent actionEvent ) {
  // Get the selected Part in the top TableView
  Part selectedPart = ( Part ) addProductTableView.getSelectionModel( ).getSelectedItem( );
  
  // Add the selected Part to the associated parts list
  associatedPartsList.add( selectedPart );
  
  // Set the data from the parts list into the TableView
  associatedPartTableView.setItems( associatedPartsList );
}

/**
 *@function This function removes a part-product association
 * @param actionEvent fired when the remove associated part button is clicked
 */
public void removeAssociatedPartButtonListener( ActionEvent actionEvent ) {
  // Get the selected Part in the bottom TableView
  Part selectedPart = ( Part ) associatedPartTableView.getSelectionModel( ).getSelectedItem( );
  
  // Remove the selected part from the associated parts list
  associatedPartsList.remove( selectedPart );
  addProductSaveErrorLabel.setText( "Part association removed!" );
  addProductSaveErrorLabel.setStyle( "-fx-text-fill: #00ff00;" );
  
}

/**
 *@function This function saves a product to the inventory
 * @param actionEvent fired when the save button is clicked
 */
public void addProductSaveListener( ActionEvent actionEvent ) {
  // Get the information from the text fields and place them into variables
  String newProductName  = addProductNameTextField.getText( );
  int    newProductStock = Integer.parseInt( addProductStockTextField.getText( ) );
  double newProductPrice = Double.parseDouble( addProductPriceTextField.getText( ) );
  int    newProductMax   = Integer.parseInt( addProductMaxTextField.getText( ) );
  int    newProductMin   = Integer.parseInt( addProductMinTextField.getText( ) );
  int    randomId        = Integer.parseInt( addProductIdTextField.getText( ) );
  
  boolean passCheck = true;
  // Input Validation Logic
  if ( newProductMin > newProductMax ) {
    addProductSaveErrorLabel.setText( "Error: Minimum cannot be more than maximum!" );
    addProductSaveErrorLabel.setStyle( "-fx-text-fill: #ff00000" );
    passCheck = false;
  }
  else if ( newProductStock > newProductMax ) {
    addProductSaveErrorLabel.setText( "Error: Current Stock cannot be more than the maximum!" );
    addProductSaveErrorLabel.setStyle( "-fx-text-fill: #ff00000" );
    passCheck = false;
  }
  else if ( newProductStock < newProductMin ) {
    addProductSaveErrorLabel.setText( "Error: Current Stock cannot be less than the minimum!" );
    addProductSaveErrorLabel.setStyle( "-fx-text-fill: #ff00000" );
    passCheck = false;
  }
  else {
    passCheck = true;
  }
  
  if ( passCheck == true ) {
    Inventory.allProducts.add( new Product( randomId, newProductName, newProductPrice, newProductStock,
        newProductMin,
        newProductMax, associatedPartsList ) );
    
    Stage stage = ( Stage ) addProductSaveButton.getScene( ).getWindow( );
    stage.close( );
  }
  else {
    System.out.print( "Didn't Pass." );
  }
}

/**
 * @function Cancels the add product form and closes out the window
 * @param actionEvent fires when the cancel button is clicked
 */
public void addProductCancelButtonListener( ActionEvent actionEvent ) {
  Stage stage = ( Stage ) addProductCancelButton.getScene( ).getWindow( );
  stage.close( );
}

/**
 * @function Create the TableViews, Filter TextFields, and set a TextFormatter on each TextField for input
 * validation
 * @RUNTIME-ERROR I had trouble making the data show up on the TableViews. I incorrectly named the PropertyValues and
 * the Object Types for the TableColumns. I had to ensure that the PropertyValues matched up with the TableColumn names
 * and that the TableColumn Object types matched the Parts and Products
 */
@Override
public void initialize( URL url, ResourceBundle resourceBundle ) {
  // Generate a random ID number
  int randomId = getRandomNumber( );
  addProductIdTextField.setText( Integer.toString( randomId ) );
  
  // Create the table columns
  TableColumn<Part, Integer> partsIdColumn    = new TableColumn<>( "id" );
  TableColumn<Part, String>  partsNameColumn  = new TableColumn<>( "Name" );
  TableColumn<Part, Double>  partsPriceColumn = new TableColumn<>( "Price" );
  TableColumn<Part, Integer> partsStockColumn = new TableColumn<>( "Stock" );
  addProductTableView.getColumns( ).addAll( partsIdColumn, partsNameColumn, partsPriceColumn, partsStockColumn );
  
  TableColumn<Part, Integer> associatedPartsIdColumn    = new TableColumn<>( "id" );
  TableColumn<Part, String>  associatedPartsNameColumn  = new TableColumn<>( "Name" );
  TableColumn<Part, Double>  associatedPartsPriceColumn = new TableColumn<>( "Price" );
  TableColumn<Part, Integer> associatedPartsStockColumn = new TableColumn<>( "Stock" );
  associatedPartTableView.getColumns( ).addAll( associatedPartsIdColumn, associatedPartsNameColumn,
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
  
  // Add data to the Table View
  addProductTableView.setItems( Inventory.allParts );
  
  // Wrap the Observable list in a FilteredList
  FilteredList<Part> filteredData = new FilteredList<>( Inventory.allParts, t -> true );
  
  // Set the filter to change whenever the filter changes
  addProductFilterField.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
    
    filteredData.setPredicate( part -> {
      
      if ( newValue == null || newValue.isEmpty( ) ) {
        addProductErrorLabel.setText( "" );
        return true;
      }
      
      // Compare each part with the filter text
      String lowerCaseFilter = newValue.toLowerCase( );
      
      if ( part.getName( ).toLowerCase( ).contains( lowerCaseFilter ) ) {
        addProductErrorLabel.setText( "" );
        return true;
      }
      else if ( Integer.toString( ( part.getId( ) ) ).contains( lowerCaseFilter ) ) {
        addProductErrorLabel.setText( "" );
        return true;
      }
      else {
//                addProductErrorLabel.setText( "Error: No matching product found!" );
        return false;
      }
    } );
  } );
  
  SortedList<Part> sortedData = new SortedList<>( filteredData );
  
  sortedData.comparatorProperty( ).bind( addProductTableView.comparatorProperty( ) );
  
  addProductTableView.setItems( sortedData );
  
  // Set TextFormatters on each TextField to apply input validation
  // Display an error message if incorrect characters are typed
  addProductStockTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      addProductSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      addProductSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      addProductSaveErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  addProductMaxTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      addProductSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      addProductSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      addProductSaveErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  addProductMinTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      addProductSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      addProductSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      addProductSaveErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  addProductNameTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "[a-zA-Z]+" ) ) {
      addProductSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      addProductSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      addProductSaveErrorLabel.setText( "Letters only!" );
      return change;
    }
  } ) );
  
  addProductPriceTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) || change.getText( ).matches( "\\." ) ) {
      addProductSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      addProductSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      addProductSaveErrorLabel.setText( "Prices don't contain letters!!" );
      return change;
    }
  } ) );
  
}

/**
 * @param actionEvent fired when the user presses "Enter"
 * @function is called when "Enter" is pressed when searching for a part in the parts table. It selects the matching
 *     part
 */
public void addProductsSearchFieldListener( ActionEvent actionEvent ) {
  addProductTableView.getSelectionModel( ).clearSelection( );
  addProductTableView.getSelectionModel( ).selectFirst( );
  Part selected = addProductTableView.getSelectionModel( ).getSelectedItem( );
  if ( selected == null ) {
    addProductErrorLabel.setText( "Error: Part not found" );
  }
}


}

