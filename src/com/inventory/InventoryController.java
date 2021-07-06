package com.inventory;


import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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


public class InventoryController implements Initializable {

/*
 * Fields
 */

/**
 * A {@link javafx.scene.control.TableView} containing each {@link com.inventory.Part} in the {@link
 * com.inventory.Inventory}
 */
@FXML
public TableView<Part> partsTableView = new TableView<>( );

/**
 * A {@link javafx.scene.control.TableView} containing each {@link com.inventory.Product} in the {@link
 * com.inventory.Inventory}
 */
@FXML
public TableView<Product> productsTableView = new TableView<>( );

/**
 * The {@link javafx.stage.Stage} that the {@link javafx.scene.Scene} is placed in.
 */
@FXML
public Stage stage = new Stage( );

/**
 * a {@link javafx.scene.control.TextField} used to filter the contents of the {@link #partsTableView}
 */
@FXML
private TextField filterFieldParts;

/**
 * a {@link javafx.scene.control.TextField} used to filter the contents of the {@link #productsTableView}
 */
@FXML
private TextField filterFieldProducts;

/**
 * a {@link javafx.scene.control.Button} used to exit the application
 */
@FXML
public Button mainFormExitButton;

/**
 * a {@link javafx.scene.control.Label} that is used to display error messages for the {@link #partsTableView}
 */
@FXML
private Label partsErrorLabel;

/**
 * a {@link javafx.scene.control.Label} that is used to display error messages for the {@link #productsTableView}
 */
@FXML
private Label productsErrorLabel;

/**
 * Initializes the scene including:
 * <ul>
 * <li>{@link javafx.scene.control.TableColumn},</li>
 * <li>The <code>allParts</code> <code>ObservableList</code>,</li>
 * <li>The <code>allProducts</code> <code>ObservableList</code>,</li>
 * <li>{@link #filterFieldParts},</li>
 * <li>{@link #filterFieldProducts}</li>
 * </ul>
 * <p>
 * in addition to associating the data with the columns
 */
@Override
public void initialize( URL url, ResourceBundle resourceBundle ) {
  // Initialize the columns
  TableColumn<Part, Integer> partsIdColumn    = new TableColumn<>( "id" );
  TableColumn<Part, String>  partsNameColumn  = new TableColumn<>( "Name" );
  TableColumn<Part, Double>  partsPriceColumn = new TableColumn<>( "Price" );
  TableColumn<Part, Integer> partsStockColumn = new TableColumn<>( "Stock" );
  partsTableView.getColumns( ).addAll( partsIdColumn, partsNameColumn, partsPriceColumn, partsStockColumn );
  
  TableColumn<Product, Integer> productsIdColumn    = new TableColumn<>( "id" );
  TableColumn<Product, String>  productsNameColumn  = new TableColumn<>( "Name" );
  TableColumn<Product, Double>  productsPriceColumn = new TableColumn<>( "Price" );
  TableColumn<Product, Integer> productsStockColumn = new TableColumn<>( "Stock" );
  productsTableView.getColumns( ).addAll( productsIdColumn, productsNameColumn, productsPriceColumn,
      productsStockColumn );
  
  // Define the data in an observable list
  Inventory.allParts.add( new InHouse( 505, "Paint", 30.25, 8, 5, 10, 5024 ) );
  Inventory.allParts.add( new InHouse( 1025, "Screwdrivers", 15.00, 8, 3, 18, 6996 ) );
  Inventory.allParts.add( new Outsourced( 88, "Trash Can", 30.25, 6, 3, 10, "The Company" ) );
  Inventory.allParts.add( new Outsourced( 89, "Wrench", 15.00, 17, 3, 18, "Acme" ) );
  
  
  ObservableList<Part> associatedPartsSample = FXCollections.observableArrayList(
      new InHouse( 652, "Screws", 14.00, 50, 10, 60, 40656 )
  );
  
  Inventory.allProducts = FXCollections.observableArrayList(
      new Product( 604, "Tool Set", 60.35, 10, 2, 15, associatedPartsSample ),
      new Product( 985, "Ladder", 45.80, 8, 2, 10, associatedPartsSample ),
      new Product( 562, "Drill Set", 60.35, 10, 2, 15, associatedPartsSample ),
      new Product( 235, "Wood Panel Set", 40.50, 8, 2, 10, associatedPartsSample )
  );
  
  // Associate the data with the columns
  partsIdColumn.setCellValueFactory( new PropertyValueFactory<>( ( "id" ) ) );
  partsNameColumn.setCellValueFactory( new PropertyValueFactory<Part, String>( ( "Name" ) ) );
  partsStockColumn.setCellValueFactory( new PropertyValueFactory<Part, Integer>( ( "Stock" ) ) );
  partsPriceColumn.setCellValueFactory( new PropertyValueFactory<Part, Double>( ( "Price" ) ) );
  
  productsIdColumn.setCellValueFactory( new PropertyValueFactory<>( ( "id" ) ) );
  productsNameColumn.setCellValueFactory( new PropertyValueFactory<Product, String>( ( "Name" ) ) );
  productsStockColumn.setCellValueFactory( new PropertyValueFactory<Product, Integer>( ( "Stock" ) ) );
  productsPriceColumn.setCellValueFactory( new PropertyValueFactory<Product, Double>( ( "Price" ) ) );
  
  // Add the data to the table
  partsTableView.setItems( Inventory.allParts );
  productsTableView.setItems( Inventory.allProducts );
  
  partsIdColumn.setCellValueFactory( cellData -> new SimpleIntegerProperty( cellData.getValue( ).getId( ) ).asObject( ) );
  partsNameColumn.setCellValueFactory( cellData -> new SimpleStringProperty( cellData.getValue( ).getName( ) ) );
  
  // Wrap the Observable list in a FilteredList
  FilteredList<Part> filteredData = new FilteredList<>( Inventory.allParts, t -> true );
  
  // Set the filter to change whenever the filter changes
  filterFieldParts.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
    
    filteredData.setPredicate( part -> {
//      partsErrorLabel.setText("123");
      
      if ( newValue == null || newValue.isEmpty( ) ) {
//        partsErrorLabel.setText( "345" );
        return true;
      }
      
      // Compare each part with the filter text
      String lowerCaseFilter = newValue.toLowerCase( );
      
      if ( part.getName( ).toLowerCase( ).contains( lowerCaseFilter ) ) {
//        partsErrorLabel.setText("LettersMatched");
        return true;
      }
      else if ( Integer.toString( ( part.getId( ) ) ).contains( lowerCaseFilter ) ) {
        partsErrorLabel.setText( "" );
        return true;
      }
      else if ( !part.getName( ).toLowerCase( ).contains( lowerCaseFilter ) && !Integer.toString( ( part.getId( ) ) ).contains( lowerCaseFilter ) ) {
        partsErrorLabel.setStyle( "-fx-text-fill: #ff0000;" );
//        partsErrorLabel.setText( "Error: No Matching Part Found!" );
      }
      return false;
    } );
  } );
  
  SortedList<Part> sortedData = new SortedList<>( filteredData );
  
  sortedData.comparatorProperty( ).bind( partsTableView.comparatorProperty( ) );
  
  partsTableView.setItems( sortedData );
  
  
  // Wrap the Observable list in a FilteredList
  FilteredList<Product> filteredProductData = new FilteredList<>( Inventory.allProducts, t -> true );
  
  // Set the filter to change whenever the filter changes
  filterFieldProducts.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
    
    filteredProductData.setPredicate( product -> {
      // If filter is empty, show all Products
      if ( newValue == null || newValue.isEmpty( ) ) {
//        productsErrorLabel.setText( "123" );
        return true;
      }
      
      // Compare each part with the filter text
      String lowerCaseFilter = newValue.toLowerCase( );
      
      if ( product.getName( ).toLowerCase( ).contains( lowerCaseFilter ) ) {
        productsErrorLabel.setText( "" );
        return true;
      }
      else if ( Integer.toString( ( product.getId( ) ) ).contains( lowerCaseFilter ) ) {
        productsErrorLabel.setText( "" );
        return true;
      }
      else {
//        productsErrorLabel.setText( "Error: No Matching Product Found!" );
        return false;
      }
    } );
  } );
  
  SortedList<Product> sortedProductData = new SortedList<>( filteredProductData );
  
  sortedProductData.comparatorProperty( ).bind( productsTableView.comparatorProperty( ) );
  
  productsTableView.setItems( sortedProductData );
}

/**
 * Creates a random number for the randomly generated {@link com.inventory.Part} or {@link com.inventory.Product} IDs
 * using the {@link java.util.Random} class.&nbsp; The
 * absolute
 * value is used so
 * that
 * there are no negative <code>id</code> numbers.
 * @return The random <code>id</code> number
 */
public int getRandomNumber( ) {
  Random randomNumbers = new Random( );
  return Math.abs( randomNumbers.nextInt( 1000 ) );
}

/**
 * Opens the Add Part menu
 * @param actionEvent the event that fires when the "Add" Button is clicked on the main form
 * @throws Exception if the fxml file is not found
 */

public void partsAddButtonListener( ActionEvent actionEvent ) throws Exception {
  Parent parent = null;
  // Clear the error message
  partsErrorLabel.setText( "" );
  // Load the FXML file
  try {
    parent = FXMLLoader.load( getClass( ).getResource( "addPart.fxml" ) );
  }
  catch ( Exception e ) {
    e.printStackTrace( );
  }
  Stage stage = new Stage( );
  
  // Build the scene graph
  Scene scene = new Scene( parent );
  
  // Display our window, using the scene graph.
  stage.setTitle( "Add Part" );
  stage.setScene( scene );
  stage.show( );
}

/**
 * Opens the Modify Part menu
 * @param actionEvent the event that fires when the "Modify" Button is clicked on the main form
 * @throws Exception if the fxml file is not found
 */
public void partsModifyButtonListener( ActionEvent actionEvent ) throws Exception {

//  sendData(actionEvent);
  // Clear the error message
  partsErrorLabel.setText( "" );
  
  // Get the currently selected part
  Part selectedPart = partsTableView.getSelectionModel( ).getSelectedItem( );
  
  // Display an error if no part is selected.
  if ( selectedPart == null ) {
    partsErrorLabel.setText( "Error: Please selected a part to modify!" );
  }
  else {
    int selectedPartId = selectedPart.getId( );
    
    // Place the ID of the selected part and the index into variables
    Inventory.selectedPart      = selectedPart;
    Inventory.selectedPartId    = selectedPartId;
    Inventory.selectedPartIndex = partsTableView.getItems( ).indexOf( selectedPart );
    
    Parent parent = null;
    // Load the FXML file
    try {
      parent = FXMLLoader.load( getClass( ).getResource( "modifyPart.fxml" ) );
    }
    catch ( Exception e ) {
      e.printStackTrace( );
    }
    
    // Create a new Stage
    Stage stage = new Stage( );
    
    // Build the scene graph
    Scene scene = new Scene( parent );
    
    // Display our window, using the scene graph.
    stage.setTitle( "Modify Part" );
    stage.setScene( scene );
    stage.show( );
  }
}


/**
 * Deletes the selected entry from the {@link #partsTableView}
 *
 * @param actionEvent the event that fires when the "Delete" Button is clicked on the main form
 */
public void partsDeleteButtonListener( ActionEvent actionEvent ) {
  // Present error message if Parts list is empty
  if ( Inventory.allParts.size( ) == 0 ) {
    partsErrorLabel.setText( "Error: Cannot Delete, the parts list is empty!" );
  }
  else {
    // Find out which part is selected in the table
    Part selectedPart = partsTableView.getSelectionModel( ).getSelectedItem( );
    // Delete the part
    Inventory.allParts.remove( selectedPart );
    partsErrorLabel.setText( "Part Deleted!" );
    partsErrorLabel.setStyle( "-fx-text-fill: #00ff00;" );
  }
}

/**
 * Opens the Add Product Menu
 * @param actionEvent is fired when the Add Product button is clicked
 * @throws Exception is thrown if the fxml file is not found
 */
public void productsAddButtonListener( ActionEvent actionEvent ) throws Exception {
  // Clear the error message
  productsErrorLabel.setText( "" );
  productsErrorLabel.setStyle( "-fx-text-fill: #ff0000;");
  // Load the FXML file
  Parent parent = FXMLLoader.load( getClass( ).getResource( "addProduct.fxml" ) );
  
  Stage stage = new Stage( );
  
  // Build the scene graph
  Scene scene = new Scene( parent );
  
  // Display our window, using the scene graph.
  stage.setTitle( "Add Product" );
  stage.setScene( scene );
  stage.show( );
}

/**
 * Opens the Modify Product Menu. It also passes the selectedProduct to the main {@link com.inventory.Inventory}
 * class.
 * @param actionEvent is fired when the Modify Product button is clicked
 * @throws Exception is thrown if the fxml file is not found
 *<br><br>
 * RUNTIME ERROR I was having trouble figuring out how to get the selectedProduct over to the modifyProduct
 * controller. I ended up having to declare the ObservableLists as static variables in the Inventory class so that
 * I could access them anywhere in the application.
 */
public void productsModifyButtonListener( ActionEvent actionEvent ) throws Exception {
  // Clear the error message
  productsErrorLabel.setText( "" );
  productsErrorLabel.setStyle( "-fx-text-fill: #ff0000;" );
  
  // Get the currently selected part
  Product selectedProduct = productsTableView.getSelectionModel( ).getSelectedItem( );
  
  // Display an error if there is no product selected.
  if ( selectedProduct == null ) {
    productsErrorLabel.setText( "Error: Please select a product to modify! " );
  }
  else {
    int selectedProductId = selectedProduct.getId( );
    
    // Place the ID of the selected part and the index into variables
    Inventory.selectedProduct      = selectedProduct;
    Inventory.selectedProductId    = selectedProductId;
    Inventory.selectedProductIndex = productsTableView.getItems( ).indexOf( selectedProduct );
    
    // Load the FXML file
    Parent parent = FXMLLoader.load( getClass( ).getResource( "modifyProduct.fxml" ) );
    
    Stage stage = new Stage( );
    
    // Build the scene graph
    Scene scene = new Scene( parent );
    
    // Display our window, using the scene graph.
    stage.setTitle( "Modify Product" );
    stage.setScene( scene );
    stage.show( );
  }
}

/**
 * Deletes a {@link com.inventory.Product} from the {@link #productsTableView}
 * @param actionEvent is fired when the delete product button is clicked
 */
public void productsDeleteButtonListener( ActionEvent actionEvent ) {
  // Check if the selected Product has any parts associated with it
  Product selectedProduct = productsTableView.getSelectionModel( ).getSelectedItem( );
  // Prevent deletion if a product has an associated part
  if ( selectedProduct.getAllAssociatedParts( ).size( ) != 0 ) {
    // Present the error message if there is an associated part.
    productsErrorLabel.setStyle( "-fx-text-fill: #ff0000;" );
    productsErrorLabel.setText( "Error: Cannot delete a part which has an associated part." );
  }
  else {
    // Present error message if Products list is empty
    if ( Inventory.allProducts.size( ) == 0 ) {
      productsErrorLabel.setStyle( "-fx-text-fill: #ff0000;" );
      productsErrorLabel.setText( "Error: Cannot Delete, the products list is empty!" );
    }
    else {
      // Delete the product
      Inventory.allProducts.remove( selectedProduct );
      productsErrorLabel.setText( "Product Deleted!" );
      productsErrorLabel.setStyle( "-fx-text-fill: #00ff00;" );
    }
  }
}

/**
 * Closes the window and exits the program
 * @param actionEvent fires when the "Exit" button is clicked
 */
public void exitButtonListener( ActionEvent actionEvent ) {
  // Exit the application
  Platform.exit( );
}

/**
 * Called when "Enter" is pressed when searching for a {@link com.inventory.Part} in the {@link #partsTableView}
 * .&nbsp;It
 * selects the
 * matching <code>Part</code>.
 * @param actionEvent fired when the user presses "Enter"
 *<br><br>
 * RUNTIME ERROR I originally had the Error message-setting code in the initialize() function, and struggled with
 * getting the error message to show up at the right time. I ended up having to move it to a separate function.
 */
public void partsSearchFieldListener( ActionEvent actionEvent ) {
  partsTableView.getSelectionModel( ).clearSelection( );
  partsTableView.getSelectionModel( ).selectFirst( );
  Part selected = partsTableView.getSelectionModel( ).getSelectedItem( );
  if ( selected == null ) {
    partsErrorLabel.setText( "Error: Part not found" );
  }
}

/**
 * Called when "Enter" is pressed when searching for a {@link com.inventory.Product} in the
 * {@link #productsTableView}.&nbsp;It
 * selects the
 * matching <code>Product</code>.
 * @param actionEvent fired when the user presses "Enter"
 */
public void productsSearchFieldListener( ActionEvent actionEvent ) {
  productsTableView.getSelectionModel( ).clearSelection( );
  productsTableView.getSelectionModel( ).selectFirst( );
  Product selected = productsTableView.getSelectionModel( ).getSelectedItem( );
  if ( selected == null ) {
    productsErrorLabel.setText( "Error: Product not found!" );
  }
}
}