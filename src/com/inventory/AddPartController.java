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

public class AddPartController implements Initializable {

@FXML
private MenuBar mainWindow;

@FXML
private Label mainFormTitle;

@FXML
private RadioButton addPartInHouseRadio;

@FXML
private ToggleGroup addPart;

@FXML
private RadioButton addPartOutsourcedRadio;

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

public void addPartSaveListener( ActionEvent actionEvent ) {
  // Get the information from the text fields and place them into variables
  String newPartName  = addPartNameTextField.getText( );
  int    newPartStock = Integer.parseInt( addPartInventoryTextField.getText( ) );
  double newPartPrice = Double.parseDouble( addPartPriceTextField.getText( ) );
  int    newPartMax   = Integer.parseInt( addPartMaxTextField.getText( ) );
  int    newPartMin   = Integer.parseInt( addPartMinTextField.getText( ) );
  
  if ( addPartInHouseRadio.isSelected( ) ) {
    int newPartExtra = Integer.parseInt( addPartExtraTextField.getText( ) );
    int randomId     = getRandomNumber( );
    allParts.add( new InHouse( randomId, newPartName, newPartPrice, newPartStock, newPartMin, newPartMax,
        newPartExtra ) );
//       System.out.println( allParts );
    Stage stage = ( Stage ) addPartSaveButton.getScene( ).getWindow( );
    stage.close( );
  }
  else {
    String newPartExtra = addPartExtraTextField.getText( );
    int    randomId     = getRandomNumber( );
    allParts.add( new Outsourced( randomId, newPartName, newPartPrice, newPartStock, newPartMin, newPartMax,
        newPartExtra ) );
    Stage stage = ( Stage ) addPartSaveButton.getScene( ).getWindow( );
    stage.close( );
  }
  
}

public int getRandomNumber( ) {
  Random randomNumbers = new Random( );
  return Math.abs( randomNumbers.nextInt( 1000 ) );
}

public void addPartCancelButtonListener( ActionEvent actionEvent ) {
  Stage stage = ( Stage ) addPartCancelButton.getScene( ).getWindow( );
  stage.close( );
}

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

}


}
