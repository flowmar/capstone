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
    private RadioButton addPartInHouseRadio;

@FXML
private ToggleGroup addPart;

@FXML
private RadioButton addPartOutsourcedRadio;

@FXML
private Button addPartSaveButton;

@FXML
private Button modifyPartCancelButton;

public void modifyPartCancelButtonListener( ActionEvent actionEvent ) {
    Stage stage = ( Stage ) modifyPartCancelButton.getScene( ).getWindow( );
    stage.close( );
}
    
}
