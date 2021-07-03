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

public class ModifyProductController implements Initializable {


@FXML
private Button modifyProductSaveButton;

@FXML
private Button modifyProductCancelButton;

@FXML
private Button addAssociatedPartButton;

@FXML
private Button removeAssociatedPartButton;

@FXML
private Label modifyProductErrorLabel;

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


public void addAssociatedPartButtonListener( ActionEvent actionEvent ) {
    // Get the selected part from the top table view
    Part selectedPart = modifyProductPartsTableView.getSelectionModel( ).getSelectedItem( );
    
    // Add the selected Part to the associated parts list
    associatedPartsList.add( selectedPart );
    
    // Set the data from the parts list into the TableView
    modifyProductAssociatedPartsTableView.setItems( associatedPartsList );
}

public void removeAssociatedPartButtonListener( ActionEvent actionEvent ) {
    // Get the selected part from the bottom table view
    Part selectedPart = ( Part ) modifyProductAssociatedPartsTableView.getSelectionModel( ).getSelectedItem( );
    
    // Remove the selected part from the associated parts list
    associatedPartsList.remove( selectedPart );
}

public void modifyProductSaveButtonListener( ActionEvent actionEvent ) {
    // Get the information from the text fields and place them into variables
    String modifyPartName  = modifyProductNameTextField.getText( );
    int    modifyPartStock = Integer.parseInt( modifyProductStockTextField.getText( ) );
    double modifyPartPrice = Double.parseDouble( modifyProductPriceTextField.getText( ) );
    int    modifyPartMax   = Integer.parseInt( modifyProductMaxTextField.getText( ) );
    int    modifyPartMin   = Integer.parseInt( modifyProductMinTextField.getText( ) );
    int    randomId        = Integer.parseInt( modifyProductIdTextField.getText( ) );
    // Save the modified product
    Inventory.allProducts.set( Inventory.selectedProductIndex, new Product( randomId, modifyPartName, modifyPartPrice,
        modifyPartStock, modifyPartMin, modifyPartMax, associatedPartsList ) );
    
    // Close the window
    Stage stage = ( Stage ) modifyProductSaveButton.getScene( ).getWindow( );
    stage.close( );
}

/**
 * This function cancels the form and closes out the window
 *
 * @param actionEvent fires when the cancel button is clicked
 */
public void modifyProductCancelButtonListener( ActionEvent actionEvent ) {
    Stage stage = ( Stage ) modifyProductCancelButton.getScene( ).getWindow( );
    stage.close( );
    
}

/**
 * This function initializes the new scene by placing the selected product data into the UI
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
    modifyProductSearchField.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
        
        filteredProductData.setPredicate( product -> {
            
            if ( newValue == null || newValue.isEmpty( ) ) {
                modifyProductErrorLabel.setText( "" );
                return true;
            }
            
            // Compare first name and last name of every product with filter text.
            String lowerCaseFilter = newValue.toLowerCase( );
            
            if ( product.getName( ).toLowerCase( ).contains( lowerCaseFilter ) ) {
                modifyProductErrorLabel.setText( "1" );
                return true;
            }
            else if ( Integer.toString( ( product.getId( ) ) ).contains( lowerCaseFilter ) ) {
                modifyProductErrorLabel.setText( "2" );
                return true;
            }
//      else if ( String.valueOf( product.getName( ) ).indexOf( lowerCaseFilter ) != -1 ) {
//        productsErrorLabel.setText("3");
//        return true;
//      }
            else {
                modifyProductErrorLabel.setText( "4" );
                return false;
            }
        } );
    } );
    
    SortedList<Part> sortedProductData = new SortedList<>( filteredProductData );
    
    sortedProductData.comparatorProperty( ).bind( modifyProductPartsTableView.comparatorProperty( ) );
    
    modifyProductPartsTableView.setItems( sortedProductData );
}
}