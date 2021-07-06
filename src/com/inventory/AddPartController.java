package com.inventory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import static com.inventory.Inventory.allParts;

/**
 * @author Omar Imam
 * @version %I% %G%
 */

public class AddPartController implements Initializable {

/**
 * Fields
 */

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

@FXML
private Label addPartErrorLabel;

/**
 * @param actionEvent fired when the save button is clicked
 * @function This function saves the information in the text fields to the inventory
 */
public void addPartSaveListener( ActionEvent actionEvent ) {
  // Get the information from the text fields and place them into variables
  String newPartName  = addPartNameTextField.getText( );
  int    newPartStock = Integer.parseInt( addPartInventoryTextField.getText( ) );
  double newPartPrice = Double.parseDouble( addPartPriceTextField.getText( ) );
  int    newPartMax   = Integer.parseInt( addPartMaxTextField.getText( ) );
  int    newPartMin   = Integer.parseInt( addPartMinTextField.getText( ) );
  int    randomId     = Integer.parseInt( addPartIdTextField.getText( ) );
  
  // Input Validation Logic
  boolean passCheck = true;
  if ( newPartMin > newPartMax ) {
    addPartErrorLabel.setText( "Error: Minimum cannot be more than maximum!" );
    passCheck = false;
  }
  else if ( newPartStock > newPartMax ) {
    addPartErrorLabel.setText( "Error: Current Stock cannot be more than the maximum!" );
    passCheck = false;
  }
  else if ( newPartStock < newPartMin ) {
    addPartErrorLabel.setText( "Error: Current Stock cannot be less than the minimum!" );
    passCheck = false;
  }
  else {
    passCheck = true;
  }
  
  if ( passCheck == true ) {
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
  else {
    System.out.print( "Didn't pass." );
  }
}

/**
 * @return the random ID number
 *
 * @function Creates a random ID number for the new Part
 */
public int getRandomNumber( ) {
  Random randomNumbers = new Random( );
  return Math.abs( randomNumbers.nextInt( 1000 ) );
}

/**
 * @param actionEvent fired when the add part button is clicked
 * @function Cancels the add part form and closes out the window
 */
public void addPartCancelButtonListener( ActionEvent actionEvent ) {
  Stage stage = ( Stage ) addPartCancelButton.getScene( ).getWindow( );
  stage.close( );
}

/**
 * @param actionEvent fired when a radio button is selected
 * @function Changes the text of the last text field depending on which radio button is selected
 * @RUNTIME-ERROR: When creating the TextFormatters, I had a little bit of trouble with where to place the formatter
 *     for the MachineID/Company Name fields
 */
public void addPartRadioListener( ActionEvent actionEvent ) {
  if ( addPartInHouseRadio.isSelected( ) ) {
    addPartExtraLabel.setText( "Machine ID" );
  }
  else {
    addPartExtraLabel.setText( "Company Name" );
  }
}

/**
 * @function initialize() initializes the randomId number and the input validation for each of the text fields
 */
@Override
public void initialize( URL url, ResourceBundle resourceBundle ) {
  // Generate a random ID number
  int randomId = getRandomNumber( );
  addPartIdTextField.setText( Integer.toString( randomId ) );
  
  // Set TextFormatters on each TextField to apply input validation
  // Display an error message if incorrect characters are typed
  addPartInventoryTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      addPartErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      addPartErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      addPartErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  addPartMaxTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      addPartErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      addPartErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      addPartErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  addPartMinTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      addPartErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      addPartErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      addPartErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  addPartNameTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "[a-zA-Z]+" ) ) {
      addPartErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      addPartErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      addPartErrorLabel.setText( "Letters only!" );
      return change;
    }
  } ) );
  
  addPartPriceTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) || change.getText( ).matches( "\\." ) ) {
      addPartErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      addPartErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      addPartErrorLabel.setText( "Prices don't contain letters!!" );
      return change;
    }
  } ) );
  
  addPartExtraTextField.setTextFormatter( new TextFormatter<>( change ->
  {
    if ( addPartInHouseRadio.isSelected( ) ) {
      if ( change.getText( ).matches( "\\d+" ) ) {
        addPartErrorLabel.setText( "" );
        return change;
      }
      else if ( change.getText( ).equals( "" ) ) {
        change.setText( "" );
        return change;
      }
      else {
        change.setText( "" );
        addPartErrorLabel.setText( "Integers Only!" );
        return change;
      }
    }
    else {
      if ( !addPartInHouseRadio.isSelected( ) ) {
        if ( change.getText( ).matches( "[a-zA-Z]" ) ) {
          addPartErrorLabel.setText( "" );
          return change;
        }
        else if ( change.getText( ).equals( "" ) ) {
          change.setText( "" );
          return change;
        }
        else {
          change.setText( "" );
          addPartErrorLabel.setText( "Letters Only!" );
          return change;
        }
      }
    }
    return change;
  } ) );
//    } ) );
}
}
