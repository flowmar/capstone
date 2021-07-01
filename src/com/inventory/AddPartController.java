package com.inventory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddPartController {

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

public void addPartCancelButtonListener( ActionEvent actionEvent ) {
    Stage stage = ( Stage ) addPartCancelButton.getScene( ).getWindow( );
    stage.close( );
}
}
