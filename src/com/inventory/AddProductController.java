package com.inventory;

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

public class AddProductController implements Initializable {

@FXML
private Button addProductCancelButton;

@FXML
private Button addProductAddButton;

@FXML
private Button removeAssociatedPartButton;

@FXML
private Button addProductSaveButton;

@FXML
private Label addProductErrorLabel;

@FXML
private TextField addProductIdTextField;

@FXML
private TextField addProductNameTextField;

@FXML
private TextField addProductStockTextField;

@FXML
private TextField addProductMaxTextField;

@FXML
private TextField addProductMinTextField;

@FXML
private TextField addProductFilterField;

@FXML
private TableView addProductTableView;

@FXML
private TableView associatedPartTableView;


/**
 * Creates a random ID number
 *
 * @return the random ID number
 */
public int getRandomNumber( ) {
    Random randomNumbers = new Random( );
    return Math.abs( randomNumbers.nextInt( 1000 ) );
}

public void addProductAddButtonListener( ) {}

public void removeAssociatedPartButtonListener( ) {}

public void addProductSaveListener( ActionEvent actionEvent ) {

}

/**
 * Cancels the add product form and closes out the window
 *
 * @param actionEvent fires when the cancel button is clicked
 */
public void addProductCancelButtonListener( ActionEvent actionEvent ) {
    Stage stage = ( Stage ) addProductCancelButton.getScene( ).getWindow( );
    stage.close( );
    
}

/**
 * This function initializes the tables and the filtering text field
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
            
            // Compare first name and last name of every part with filter text
            String lowerCaseFilter = newValue.toLowerCase( );
            
            if ( part.getName( ).toLowerCase( ).contains( lowerCaseFilter ) || Integer.toString( ( part.getId( ) ) ).contains( lowerCaseFilter ) ) {
                addProductErrorLabel.setText( "Error: No matching part found.1" );
                return true;
            }
//      else if ( part.getName( ).toLowerCase( ).contains( lowerCaseFilter ) || Integer.toString( ( part.getId( ) ) )
//      .contains( lowerCaseFilter ) ) {
//        partsErrorLabel.setText("Error: No matching part found.2");
//        return true;
//      }
//      else if ( String.valueOf( part.getName( ) ).indexOf( lowerCaseFilter ) != -1 ) {
//        partsErrorLabel.setText("Error: No matching part found.3");
//        return true;
//      }
            else {
                addProductErrorLabel.setText( "4" );
                return false;
            }
        } );
    } );
    
    SortedList<Part> sortedData = new SortedList<>( filteredData );
    
    sortedData.comparatorProperty( ).bind( addProductTableView.comparatorProperty( ) );
    
    addProductTableView.setItems( sortedData );
    
}
}
