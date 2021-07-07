package com.inventory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Omar Imam
 * @version %I% %G%
 */

public class ModifyPartController implements Initializable {

/*
 * Fields
 */

/**
 * a RadioButton that allows you to select the type of {@link com.inventory.Part}
 */
@FXML
private RadioButton modifyPartInHouseRadio;

/**
 * a RadioButton that allows you to select the type of {@link com.inventory.Part}
 */
@FXML
private RadioButton modifyPartOutsourcedRadio;

/**
 * saves the {@link com.inventory.Part} to the {@link com.inventory.Inventory}
 */
@FXML
private Button modifyPartSaveButton;

/**
 * cancels the form and closes the window
 */
@FXML
private Button modifyPartCancelButton;

/**
 * the label for the last field in the {@link com.inventory.Part} form
 */
@FXML
private Label modifyPartExtraLabel;

/**
 * the {@link javafx.scene.control.Label} that is used to display error messages
 */
@FXML
private Label modifyPartSaveErrorLabel;

/**
 * the {@link javafx.scene.control.TextField} for the id of the {@link com.inventory.Part}
 */
@FXML
private TextField modifyPartIdTextField;

/**
 * the {@link javafx.scene.control.TextField} for the name of the {@link com.inventory.Part}
 */
@FXML
private TextField modifyPartNameTextField;

/**
 * the {@link javafx.scene.control.TextField} for the price of the {@link com.inventory.Part}
 */
@FXML
private TextField modifyPartPriceTextField;

/**
 * the {@link javafx.scene.control.TextField} for the stock of the {@link com.inventory.Part}
 */
@FXML
private TextField modifyPartStockTextField;

/**
 * the {@link javafx.scene.control.TextField} for the max of the {@link com.inventory.Part}
 */
@FXML
private TextField modifyPartMaxTextField;

/**
 * the {@link javafx.scene.control.TextField} for the min of the {@link com.inventory.Part}
 */
@FXML
private TextField modifyPartMinTextField;

/**
 * the {@link javafx.scene.control.TextField} for the extra of the {@link com.inventory.Part}
 */
@FXML
private TextField modifyPartExtraTextField;

/**
 * Saves the information in the TextFields to the inventory.&nbsp;It also includes input validation for the
 *  Stock, Min, and Max fields.&nbsp;It also checks to see which RadioButton is selected at the top of the form, and
 *  adjusts the last TextField accordingly.
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
 * Changes the Label of the last TextField depending on the radio button that is selected.&nbsp;It also adds a
 *  TextFormatter to the TextField for input validation, changing between Integers-only and Letters-Only to fit
 *  the field
 *
 * @param actionEvent fired when a radio button is selected
 */
public void modifyPartRadioListener( ActionEvent actionEvent ) {
  // Set the text of the form based upon which radio button is selected
  if ( modifyPartInHouseRadio.isSelected( ) ) {
    modifyPartExtraLabel.setText( "Machine ID" );
  
//    modifyPartExtraTextField.setText( "" );
    modifyPartExtraTextField.setTextFormatter( new TextFormatter<>( change ->
    {
      if ( change.getText( ).matches( "\\d+" ) ) {
        modifyPartSaveErrorLabel.setText( "" );
        return change;
      }
      else if ( change.getText( ).equals( "" ) ) {
        change.setText( "" );
        return change;
      }
      else {
        change.setText( "" );
        modifyPartSaveErrorLabel.setText( "Integers Only!" );
        return change;
      }
    } ) );
  
  }
  else if ( modifyPartOutsourcedRadio.isSelected( ) ) {
    modifyPartExtraLabel.setText( "Company Name" );

//    modifyPartExtraTextField.setText( "" );
    {
      modifyPartExtraLabel.setText( "Company Name" );
      modifyPartExtraTextField.setTextFormatter( new TextFormatter<>( change ->
      {
        if ( change.getText( ).matches( "[a-zA-Z]" ) ) {
          modifyPartSaveErrorLabel.setText( "" );
          return change;
        }
        else if ( change.getText( ).equals( "" ) ) {
          change.setText( "" );
          return change;
        }
        else {
          change.setText( "" );
          modifyPartSaveErrorLabel.setText( "Letters Only!" );
          return change;
        }
      } ) );
    }
  }
}
/**
 * Closes the window if the cancel button is clicked
 *
 * @param actionEvent fired when the cancel button is clicked
 * <br><br>
 * RUNTIME ERROR Forgot to cast the Button as a stage before using stage.close();
 */
public void modifyPartCancelButtonListener( ActionEvent actionEvent ) {
  // Close the window if the cancel button is clicked
  Stage stage = ( Stage ) modifyPartCancelButton.getScene( ).getWindow( );
  stage.close( );
}


/**
 * Initializes the new scene by placing the selected part data into the UI.&nbsp;It also sets a TextFormatter
 *     to each TextField for input validation.
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
    System.out.println( Inventory.selectedPart );
    System.out.println( Inventory.selectedPart instanceof InHouse );
    System.out.println( ( ( InHouse ) Inventory.selectedPart ).getMachineId( ) );
    modifyPartExtraLabel.setText( "Machine ID" );
    modifyPartExtraTextField.setText( String.valueOf( ( ( InHouse ) Inventory.selectedPart ).getMachineId( ) ) );
  }
  // Otherwise, set it to the Company Name
  else if ( Inventory.selectedPart instanceof Outsourced ) {
    modifyPartOutsourcedRadio.setSelected( true );
    System.out.println( Inventory.selectedPart );
    System.out.println( Inventory.selectedPart instanceof Outsourced );
    System.out.println( ( ( Outsourced ) Inventory.selectedPart ).getCompanyName( ) );
    modifyPartExtraLabel.setText( "Company Name" );
    modifyPartExtraTextField.setText( ( ( Outsourced ) Inventory.selectedPart ).getCompanyName( ) );
  }


// Set TextFormatters on each TextField to apply input validation
// Display an error message if incorrect characters are typed
  modifyPartStockTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      modifyPartSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      modifyPartSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      modifyPartSaveErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  modifyPartMaxTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      modifyPartSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      modifyPartSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      modifyPartSaveErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  modifyPartMinTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) ) {
      modifyPartSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      modifyPartSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      modifyPartSaveErrorLabel.setText( "Integers only!" );
      return change;
    }
  } ) );
  
  modifyPartNameTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "[a-zA-Z]+" ) ) {
      modifyPartSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      modifyPartSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      modifyPartSaveErrorLabel.setText( "Letters only!" );
      return change;
    }
  } ) );
  
  modifyPartPriceTextField.setTextFormatter( new TextFormatter<>( change -> {
    if ( change.getText( ).matches( "\\d+" ) || change.getText( ).matches( "\\." ) ) {
      modifyPartSaveErrorLabel.setText( "" );
      return change;
    }
    else if ( change.getText( ).equals( "" ) ) {
      modifyPartSaveErrorLabel.setText( "" );
      return change;
    }
    else {
      change.setText( "" );
      modifyPartSaveErrorLabel.setText( "Prices don't contain letters!!" );
      return change;
    }
  } ) );
  
  if ( modifyPartInHouseRadio.isSelected( ) ) {
    modifyPartExtraTextField.setTextFormatter( new TextFormatter<>( change ->
    {
      if ( change.getText( ).matches( "\\d+" ) ) {
        modifyPartSaveErrorLabel.setText( "" );
        return change;
      }
      else if ( change.getText( ).equals( "" ) ) {
        change.setText( "" );
        return change;
      }
      else {
        change.setText( "" );
        modifyPartSaveErrorLabel.setText( "Integers Only!" );
        return change;
      }
    } ) );
  }
  else if ( modifyPartOutsourcedRadio.isSelected( ) ) {
    {
      modifyPartExtraLabel.setText( "Company Name" );
      modifyPartExtraTextField.setTextFormatter( new TextFormatter<>( change ->
      {
        if ( change.getText( ).matches( "[a-zA-Z]" ) ) {
          modifyPartSaveErrorLabel.setText( "" );
          return change;
        }
        else if ( change.getText( ).equals( "" ) ) {
          change.setText( "" );
          return change;
        }
        else {
          change.setText( "" );
          modifyPartSaveErrorLabel.setText( "Letters Only!" );
          return change;
        }
      } ) );
      
    }
  }
}
}