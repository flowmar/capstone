package com.inventory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import static com.inventory.Inventory.allParts;

public class AddPartController implements Initializable {


@FXML
private RadioButton addPartInHouseRadio;

@FXML
private Button addPartSaveButton;

@FXML
private Button addPartCancelButton;

@FXML
private Label addPartExtraLabel;

@FXML
private TextField addPartNameTextField;

@FXML
private TextField addPartInventoryTextField;

@FXML
private TextField addPartPriceTextField;

@FXML
private TextField addPartMaxTextField;

@FXML
private TextField addPartMinTextField;

@FXML
private TextField addPartExtraTextField;

@FXML
private TextField addPartIdTextField;

/**
 * This function saves the information in the text fields to the inventory
 *
 * @param actionEvent fired when the save button is clicked
 */
public void addPartSaveListener( ActionEvent actionEvent ) {
  // Get the information from the text fields and place them into variables
  String newPartName  = addPartNameTextField.getText( );
  int    newPartStock = Integer.parseInt( addPartInventoryTextField.getText( ) );
  double newPartPrice = Double.parseDouble( addPartPriceTextField.getText( ) );
  int    newPartMax   = Integer.parseInt( addPartMaxTextField.getText( ) );
  int    newPartMin   = Integer.parseInt( addPartMinTextField.getText( ) );
  int    randomId     = Integer.parseInt( addPartIdTextField.getText( ) );
  
  // Depending on which radio button is selected, the information is saved and a part is added to the inventory
  if ( addPartInHouseRadio.isSelected( ) ) {
    // If InHouse is selected, create an InHouse part
    int newPartExtra = Integer.parseInt( addPartExtraTextField.getText( ) );
    allParts.add( new InHouse( randomId, newPartName, newPartPrice, newPartStock, newPartMin, newPartMax,
        newPartExtra ) );
    Stage stage = ( Stage ) addPartSaveButton.getScene( ).getWindow( );
    stage.close( );
  }
  else {
    // Otherwise if Outsourced is selected, create an Outsourced part
    String newPartExtra = addPartExtraTextField.getText( );
    allParts.add( new Outsourced( randomId, newPartName, newPartPrice, newPartStock, newPartMin, newPartMax,
        newPartExtra ) );
    Stage stage = ( Stage ) addPartSaveButton.getScene( ).getWindow( );
    stage.close( );
  }
  
}

/**
 * Creates a random ID number
 * @return the random ID number
 */
public int getRandomNumber( ) {
  Random randomNumbers = new Random( );
  return Math.abs( randomNumbers.nextInt( 1000 ) );
}

/**
 * Cancels the add part form and closes out the window
 * @param actionEvent fired when the add part button is clicked
 */
public void addPartCancelButtonListener( ActionEvent actionEvent ) {
  Stage stage = ( Stage ) addPartCancelButton.getScene( ).getWindow( );
  stage.close( );
}

/**
 * Changes the text of the last text field depending on which radio button is selected
 * @param actionEvent fired when a radio button is selected
 */
public void addPartRadioListener( ActionEvent actionEvent ) {
  if ( addPartInHouseRadio.isSelected( ) ) {
    addPartExtraLabel.setText( "Machine ID" );
  }
  else {
    addPartExtraLabel.setText( "Company Name" );
  }
}


@Override
public void initialize( URL url, ResourceBundle resourceBundle ) {
  // Generate a random ID number
  int randomId = getRandomNumber( );
  addPartIdTextField.setText( Integer.toString( randomId ) );
}

}
