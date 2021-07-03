package com.inventory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModifyPartController {
    
    @FXML
    private MenuBar mainWindow;
    
    @FXML
    private Label mainFormTitle;

@FXML
private RadioButton modifyPartInHouseRadio;

@FXML
private ToggleGroup modifyPart;

@FXML
private RadioButton modifyPartOutsourcedRadio;

@FXML
private Button modifyPartSaveButton;

@FXML
private Button modifyPartCancelButton;

@FXML
private Label modifyPartExtraLabel;

public void modifyPartSaveListener( ActionEvent actionEvent ) {
    // Replace the information for the selected part with the information in the text fields
}

public void modifyPartRadioListener( ActionEvent actionEvent ) {
    // Set the text of the form based upon which radio button is selected
    if ( modifyPartInHouseRadio.isSelected( ) ) {
        modifyPartExtraLabel.setText( "Machine ID" );
    }
    else {
        modifyPartExtraLabel.setText( "Company Name" );
    }
}

public void modifyPartCancelButtonListener( ActionEvent actionEvent ) {
    // Close the window if the cancel button is clicked
    Stage stage = ( Stage ) modifyPartCancelButton.getScene( ).getWindow( );
    stage.close( );
}
    
}
