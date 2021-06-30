package com.inventory;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

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
    private Button addPartCancelButton;
    
}
