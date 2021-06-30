package com.inventory;

/**
 *
 * @author Omar Imam
 */

/**
 * Fields
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;


public class InventoryController implements Initializable {

@FXML
private MenuBar   mainWindow;
@FXML
private Label     mainFormTitle;
@FXML
private Pane      partsPane;
@FXML
private ButtonBar partsButtonBar;
@FXML
private Button    partsAddButton;
@FXML
private Button    partsModifyButton;
@FXML
private Button    partsDeleteButton;
@FXML
private Random    randomNumbers = new Random( );
@FXML
public TableView<Part> partsTableView = new TableView<Part>( );

@FXML
public TableView<Product> productsTableView = new TableView<Product>( );


// Columns
@FXML
private TableColumn<Part, String> partsIdColumn   = new TableColumn<Part, String>( "ID" );
@FXML
public  TableColumn<Part, String> partsNameColumn = new TableColumn<>( "Name" );
@FXML
public  TableColumn<Part, Double>  partsPriceColumn = new TableColumn<>( "Price" );
@FXML
public  TableColumn<Part, Integer> partsStockColumn = new TableColumn<>( "Stock" );


// Parts List
@FXML
ObservableList<Part> allParts = FXCollections.observableArrayList(
    new InHouse( randomNumbers.nextInt( 10000 ), "Hammer", 30.25, 15, 3, 2, "The Company" ),
    new InHouse( randomNumbers.nextInt( 10000 ), "Wrench", 15.00, 17, 3, 18, "Acme" ),
    new Outsourced( randomNumbers.nextInt( 10000 ), "Hammer", 30.25, 15, 3, 2, "TheCompany" ),
    new Outsourced( randomNumbers.nextInt( 10000 ), "Wrench", 15.00, 17, 3, 18, "Acme" )
);

// Products List
@FXML
ObservableList<Product> allProducts = FXCollections.observableArrayList(
    new Product( randomNumbers.nextInt( 10000 ), "Tool Set", 60.35, 10, 2, 15, 4000 ),
    new Product( randomNumbers.nextInt( 10000 ), "Wood Panel Set", 40.50, 8, 2, 10, 4000 ),
    new Product( randomNumbers.nextInt( 10000 ), "Tool Set", 60.35, 10, 2, 15, 2058 ),
    new Product( randomNumbers.nextInt( 10000 ), "Wood Panel Set", 40.50, 8, 2, 10, 2076 )
);



@FXML
private Pane productsPane;

@FXML
public Stage stage = new Stage( );

@FXML
private ButtonBar productsButtonBar;

@FXML
private Button productsAddButton;

@FXML
private Button productsModifyButton;

@FXML
private Button productsDeleteButton;


@FXML
public void setProductData( ObservableList<Product> productsList, TableView<Product> productsTableView ) {
  productsTableView.setItems( productsList );
}

@FXML
public void setData( ObservableList<Part> partsList, TableView<Part> partsTableView ) {
  partsTableView.setItems( partsList );
}


@FXML
public Button mainFormExitButton;

/**
 * This function opens the Add Part menu
 *
 * @param actionEvent the event that fires when the "Add" Button is clicked on the main form
 * @throws Exception if the file is not found
 */

@FXML
public void partsAddButtonListener( ActionEvent actionEvent ) throws Exception {
  // Load the FXML file
  Parent parent = FXMLLoader.load(
      getClass( ).getResource( "addPart.fxml" ) );
  Stage stage = new Stage( );
  
  // Build the scene graph
  Scene scene1 = new Scene( parent );
  
  // Display our window, using the scene graph.
  stage.setTitle( "Add Part" );
  stage.setScene( scene1 );
  stage.show( );
}

/**
 * This function opens the Modify Part menu
 *
 * @param actionEvent the event that fires when the "Add" Button is clicked on the main form
 * @throws Exception if the file is not found
 */
@FXML
public void partsModifyButtonListener( ActionEvent actionEvent ) throws Exception {
  // Load the FXML file
  Parent parent = FXMLLoader.load( getClass( ).getResource( "modifyPart.fxml" ) );
  Stage  stage  = new Stage( );
  
  // Build the scene graph
  Scene scene = new Scene( parent );
  
  // Display our window, using the scene graph.
  stage.setTitle( "Modify Part" );
  stage.setScene( scene );
  stage.show( );
  
}

/**
 * This function deletes the selected entry from the parts TableView
 *
 * @param actionEvent the event that fires when the "Delete" Button is clicked on the main form
 */
@FXML
public void partsDeleteButtonListener( ActionEvent actionEvent ) {
  System.out.print( actionEvent );
}

@FXML
public void productsAddButtonListener( ActionEvent actionEvent ) throws Exception {
  // Load the FXML file
  Parent parent = FXMLLoader.load(
      getClass( ).getResource( "addProduct.fxml" ) );
  
  Stage stage = new Stage( );
  
  // Build the scene graph
  Scene scene = new Scene( parent );
  
  // Display our window, using the scene graph.
  stage.setTitle( "Add Product" );
  stage.setScene( scene );
  stage.show( );
  
}

@FXML
public void productsModifyButtonListener( ActionEvent actionEvent ) throws Exception {
  // Load the FXML file
  Parent parentsWillbet = FXMLLoader.load(
      getClass( ).getResource( "modifyProduct.fxml" ) );
  
  Stage stage = new Stage( );
  
  // Build the scene graph
  Scene scene = new Scene( parent );
  
  // Display our window, using the scene graph.
  stage.setTitle( "Modify Product" );
  stage.setScene( scene );
  stage.show( );
  
}

@FXML
public void productsDeleteButtonListener( ) throws Exception {
  // Find out which product is selected in the table
  // Delete the entry
}

// Get the Exit button from the main form

@FXML
public static void exitButtonListener( ActionEvent actionEvent, Stage stage ) {
  stage.close( );
}

@FXML
public void exitButtonListener( MouseEvent actionEvent ) {
  stage.close( );
  
}

@Override
public void initialize( URL url, ResourceBundle resourceBundle ) {
  partsIdColumn.setCellValueFactory( new PropertyValueFactory<Part, String>(("Id")));
  partsNameColumn.setCellValueFactory( new PropertyValueFactory<Part, String>( ( "Name" ) ) );
  partsStockColumn.setCellValueFactory( new PropertyValueFactory<Part, Integer>( ( "Stock" ) ) );
  partsPriceColumn.setCellValueFactory( new PropertyValueFactory<Part, Double>( ( "Price" ) ) );
}
}

