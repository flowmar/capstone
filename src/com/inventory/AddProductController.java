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
import java.util.Random;
import java.util.ResourceBundle;

/**
 * @author Omar Imam
 * @version %I% %G%
 */

public class AddProductController implements Initializable {

/*
 * Fields
 */

/**
 * a button that cancels the form and closes the window
 */
@FXML
private Button addProductCancelButton;

/**
 * a button that saves the {@link com.inventory.Product} information to the {@link com.inventory.Inventory}
 */
@FXML
private Button addProductSaveButton;

/**
 * a {@link javafx.scene.control.Label} that is used to display error messages
 */
@FXML
private Label addProductErrorLabel;

/**
 * a {@link javafx.scene.control.Label} that is used to display error messages relating to saving a {@link
 * com.inventory.Product}
 */
@FXML
private Label addProductSaveErrorLabel;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product id
 */
@FXML
private TextField addProductIdTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product name
 */
@FXML
private TextField addProductNameTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product price
 */
@FXML
private TextField addProductPriceTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product stock
 */
@FXML
private TextField addProductStockTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product max
 */
@FXML
private TextField addProductMaxTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the product min
 */
@FXML
private TextField addProductMinTextField;

/**
 * the {@link javafx.scene.control.TextField} that is used for the filtering the {@link #addProductTableView}
 */
@FXML
private TextField addProductFilterField;

/**
 * the {@link javafx.scene.control.TableView} that is used to display each {@link com.inventory.Part} in the
 * {@link com.inventory.Inventory}
 */
@FXML
private TableView<Part> addProductTableView;

/**
 * the {@link javafx.scene.control.TableView} that is used to display each associated {@link com.inventory.Part}
*/
@FXML
private TableView<Part> associatedPartTableView;

/**
 * a list used to hold the products associated parts
 */
@FXML
// Create an ObservableList Object to hold the Associated Parts
    ObservableList<Part> associatedPartsList = FXCollections.observableArrayList( );

/**
 * Creates a random ID number for the new product
 *
 * @return the random ID number
 */
public int getRandomNumber( ) {
  Random randomNumbers = new Random( );
  return Math.abs( randomNumbers.nextInt( 1000 ) );
}

/**
 * Associates a {@link com.inventory.Part} with {@link com.inventory.Product}
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
 * Removes a part-product association
 * @param actionEvent fired when the remove associated part button is clicked
 */
public void removeAssociatedPartButtonListener( ActionEvent actionEvent ) {
  // Get the selected Part in the bottom TableView
  Part selectedPart = ( Part ) associatedPartTableView.getSelectionModel( ).getSelectedItem( );
  
  if ( selectedPart == null ) {
    addProductErrorLabel.setText( "Error: No part selected." );
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
        addProductSaveErrorLabel.setText( "Part association removed!" );
        addProductSaveErrorLabel.setStyle( "-fx-text-fill: #00ff00;" );
      }
      else if ( response == ButtonType.CANCEL ) {
        // If no, close out the window
        System.out.println( "Association removal cancelled" );
      }
    } );
  }
}

/**
 * Saves a {@link com.inventory.Product}to the {@link com.inventory.Inventory}
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
    try {
      Connection conn = DatabaseConnection.getConnection();
      
      // First insert the product
      String productSql = "INSERT INTO products (id, name, price, stock, min, max) VALUES (?, ?, ?, ?, ?, ?)";
      PreparedStatement productStmt = conn.prepareStatement(productSql);
      productStmt.setInt(1, randomId);
      productStmt.setString(2, newProductName);
      productStmt.setDouble(3, newProductPrice);
      productStmt.setInt(4, newProductStock);
      productStmt.setInt(5, newProductMin);
      productStmt.setInt(6, newProductMax);
      
      int productRowsAffected = productStmt.executeUpdate();
      
      if (productRowsAffected > 0) {
        // If product was added successfully, add the associated parts
        String associationSql = "INSERT INTO product_parts (product_id, part_id) VALUES (?, ?)";
        PreparedStatement associationStmt = conn.prepareStatement(associationSql);
        
        // Add each associated part
        for (Part part : associatedPartsList) {
          associationStmt.setInt(1, randomId);
          associationStmt.setInt(2, part.getId());
          associationStmt.executeUpdate();
        }
        
        // If everything was successful, update the UI
        Product newProduct = new Product(randomId, newProductName, newProductPrice, newProductStock,
            newProductMin, newProductMax, associatedPartsList);
        Inventory.allProducts.add(newProduct);
        
        Stage stage = (Stage) addProductSaveButton.getScene().getWindow();
        stage.close();
      } else {
        addProductSaveErrorLabel.setText("Error: Failed to save product to database!");
        addProductSaveErrorLabel.setStyle("-fx-text-fill: #ff0000");
      }
    } catch ( SQLException e) {
      System.out.println("Error saving product to database:");
      e.printStackTrace();
      addProductSaveErrorLabel.setText("Error: " + e.getMessage());
      addProductSaveErrorLabel.setStyle("-fx-text-fill: #ff0000");
    }
  } else {
    System.out.print("Didn't Pass.");
  }
}

/**
 * Cancels the add product form and closes out the window
 * @param actionEvent fires when the cancel button is clicked
 */
public void addProductCancelButtonListener( ActionEvent actionEvent ) {
  Stage stage = ( Stage ) addProductCancelButton.getScene( ).getWindow( );
  stage.close( );
}

/**
 * Create the TableViews, Filter TextFields, and set a TextFormatter on each TextField for input
 * validation
 * <br><br>
 * RUNTIME ERROR I had trouble making the data show up on the TableViews. I incorrectly named the PropertyValues and
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
  
  // Add button hover and click animation with consistent styling
  addProductSaveButton.setStyle(addProductSaveButton.getStyle() + 
      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
  addProductCancelButton.setStyle(addProductCancelButton.getStyle() + 
      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
  
  // Add button hover and click animation
  addProductSaveButton.setOnMouseEntered(event -> onButtonHover(event));
  addProductSaveButton.setOnMouseExited(event -> onButtonExit(event));
  addProductSaveButton.setOnMousePressed(event -> onButtonPress(event));
  addProductSaveButton.setOnMouseReleased(event -> onButtonRelease(event));

  addProductCancelButton.setOnMouseEntered(event -> onButtonHover(event));
  addProductCancelButton.setOnMouseExited(event -> onButtonExit(event));
  addProductCancelButton.setOnMousePressed(event -> onButtonPress(event));
  addProductCancelButton.setOnMouseReleased(event -> onButtonRelease(event));
}

/**
 * Called when "Enter" is pressed when searching for a part in the parts table.&nbsp;It selects the matching
 * part.
 *
 * @param actionEvent fired when the user presses "Enter"
 */
public void addProductsSearchFieldListener( ActionEvent actionEvent ) {
  addProductTableView.getSelectionModel( ).clearSelection( );
  addProductTableView.getSelectionModel( ).selectFirst( );
  Part selected = addProductTableView.getSelectionModel( ).getSelectedItem( );
  if ( selected == null ) {
    addProductErrorLabel.setText( "Error: Part not found" );
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
  if (button == addProductSaveButton) {
    return "#27B611";
  } else if (button == addProductCancelButton) {
    return "#ff0000";
  }
  return "#000000";
}
}
