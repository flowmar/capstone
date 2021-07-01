package com.inventory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

public class AddProductController {

@FXML
private MenuBar mainWindow;

@FXML
private Label mainFormTitle;

@FXML
private Button addPartSaveButton;

@FXML
private Button addProductCancelButton;

@FXML
private Button addPartSaveButton1;

@FXML
private Button addPartSaveButton2;


public void addProductCancelButtonListener( ActionEvent actionEvent ) {
    Stage stage = ( Stage ) addProductCancelButton.getScene( ).getWindow( );
    stage.close( );
    
}
}
