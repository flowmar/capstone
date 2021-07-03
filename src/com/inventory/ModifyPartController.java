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
import java.util.ResourceBundle;

public class ModifyPartController implements Initializable {

/**
 * Fields
 */

@FXML
private RadioButton modifyPartInHouseRadio;

@FXML
private RadioButton modifyPartOutsourcedRadio;

@FXML
private Button modifyPartSaveButton;

@FXML
private Button modifyPartCancelButton;

@FXML
private Label modifyPartExtraLabel;

@FXML
private Label modifyPartSaveErrorLabel;

@FXML
private TextField modifyPartIdTextField;

@FXML
private TextField modifyPartNameTextField;

@FXML
private TextField modifyPartPriceTextField;

@FXML
private TextField modifyPartStockTextField;

@FXML
private TextField modifyPartMaxTextField;

@FXML
private TextField modifyPartMinTextField;

@FXML
private TextField modifyPartExtraTextField;

/**
 * This function saves the information in the TextFields to the inventory
 *
 * @param actionEvent fired when the save button is clicked
 */
public void modifyPartSaveListener( ActionEvent actionEvent ) {
  // Replace the information for the selected part with the information in the text fields
  // Get the information from the text fields
  String modifyPartName  = modifyPartNameTextField.getText( );
  int    modifyPartStock = Integer.parseInt( modifyPartStockTextField.getText( ) );
  double modifyPartPrice = Double.parseDouble( modifyPartPriceTextField.getText( ) );
  int    modifyPartMax   = Integer.parseInt( modifyPartMaxTextField.getText( ) );
  int    modifyPartMin   = Integer.parseInt( modifyPartMinTextField.getText( ) );
  int    randomId        = Integer.parseInt( modifyPartIdTextField.getText( ) );
  
  boolean passCheck = true;
  
  // Input Validation Logic
  if ( modifyPartMin > modifyPartMax ) {
    modifyPartSaveErrorLabel.setText( "Error: Minimum cannot be more than maximum!" );
    passCheck = false;
  }
  else if ( modifyPartStock > modifyPartMax ) {
    modifyPartSaveErrorLabel.setText( "Error: Current Stock cannot be more than the maximum!" );
    passCheck = false;
  }
  else if ( modifyPartStock < modifyPartMin ) {
    modifyPartSaveErrorLabel.setText( "Error: Current Stock cannot be less than the minimum!" );
    passCheck = false;
  }
  else {
    passCheck = true;
  }
  
  if ( passCheck == true ) {
    // Depending on which radio button is selected, add an InHouse or Outsourced part
    if ( modifyPartInHouseRadio.isSelected( ) ) {
      int modifyPartExtra = Integer.parseInt( modifyPartExtraTextField.getText( ) );
      Inventory.allParts.set( Inventory.selectedPartIndex, new InHouse( randomId, modifyPartName, modifyPartPrice,
          modifyPartStock, modifyPartMin,
          modifyPartMax, modifyPartExtra ) );
      // Close the window
      Stage stage = ( Stage ) modifyPartSaveButton.getScene( ).getWindow( );
      stage.close( );
    }
    else {
      String modifyPartExtra = modifyPartExtraTextField.getText( );
      Inventory.allParts.set( Inventory.selectedPartIndex, new Outsourced( randomId, modifyPartName, modifyPartPrice,
          modifyPartStock,
          modifyPartMin, modifyPartMax, modifyPartExtra ) );
      
      // Close the window
      Stage stage = ( Stage ) modifyPartSaveButton.getScene( ).getWindow( );
      stage.close( );
    }
  }
  else {
    System.out.print( "Didn't Pass." );
  }
}

/**
 * This function changes the Label of the last TextField depending on the radio button that is selected
 * @param actionEvent fired when a radio button is selected
 */
public void modifyPartRadioListener( ActionEvent actionEvent ) {
  // Set the text of the form based upon which radio button is selected
  if ( modifyPartInHouseRadio.isSelected( ) ) {
    modifyPartExtraLabel.setText( "Machine ID" );
  }
  else {
    modifyPartExtraLabel.setText( "Company Name" );
  }
}

/**
 * This function closes the window if the cancel button is clicked
 *
 * @param actionEvent fired when the cancel button is clicked
 */
public void modifyPartCancelButtonListener( ActionEvent actionEvent ) {
  // Close the window if the cancel button is clicked
  Stage stage = ( Stage ) modifyPartCancelButton.getScene( ).getWindow( );
  stage.close( );
}


/**
 * This function initializes the new scene by placing the selected part data into the UI
 */
@Override
public void initialize( URL url, ResourceBundle resourceBundle ) {
  // Set the ID TextField to the selected part id
  modifyPartIdTextField.setText( Integer.toString( Inventory.selectedPartId ) );
  // Place the data from the selected part into the text field
  modifyPartNameTextField.setText( Inventory.selectedPart.getName( ) );
  modifyPartStockTextField.setText( Integer.toString( Inventory.selectedPart.getStock( ) ) );
  modifyPartPriceTextField.setText( Double.toString( Inventory.selectedPart.getPrice( ) ) );
  modifyPartMaxTextField.setText( Integer.toString( Inventory.selectedPart.getMax( ) ) );
  modifyPartMinTextField.setText( Integer.toString( Inventory.selectedPart.getMin( ) ) );
  // If the selected part is an instance of InHouse, set the text to the machine Id and select the radio button
  if ( Inventory.selectedPart instanceof InHouse ) {
    modifyPartInHouseRadio.setSelected( true );
    modifyPartExtraLabel.setText( "Machine ID" );
    modifyPartExtraTextField.setText( Integer.toString( ( ( InHouse ) Inventory.selectedPart ).getMachineId( ) ) );
  }
  // Otherwise, set it to the Company Name
  else {
    modifyPartOutsourcedRadio.setSelected( true );
    modifyPartExtraLabel.setText( "Company Name" );
    modifyPartExtraTextField.setText( ( ( Outsourced ) Inventory.selectedPart ).getCompanyName( ) );
  }
}

}
