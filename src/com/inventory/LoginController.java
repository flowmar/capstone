package com.inventory;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }
        
        // Simple hardcoded admin authentication
        if (username.equals("admin") && password.equals("admin")) {
            try {
                // Load the inventory scene
                FXMLLoader loader = new FXMLLoader(getClass().getResource("inventoryScene.fxml"));
                Parent inventoryRoot = loader.load();
                
                // Get the current stage and set the new scene
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene inventoryScene = new Scene(inventoryRoot);
                stage.setTitle("Inventory Management System");
                stage.setScene(inventoryScene);
                
                // Load data 
                Inventory inventory = new Inventory();
                inventory.loadPartsFromDatabase();
                inventory.loadProductsFromDatabase();
                
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Error loading application");
            }
        } else {
            errorLabel.setText("Invalid username or password");
            passwordField.clear();
        }
    }
    
    @FXML
    private void onButtonHover() {
        loginButton.setStyle("-fx-background-color: #1E8A0E; " +
                             "-fx-text-fill: white; " +
                             "-fx-padding: 10 20; " +
                             "-fx-background-radius: 5px; " +
                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
    }
    
    @FXML
    private void onButtonExit() {
        loginButton.setStyle("-fx-background-color: #27B611; " +
                             "-fx-text-fill: white; " +
                             "-fx-padding: 10 20; " +
                             "-fx-background-radius: 5px; " +
                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
    }
    
    @FXML
    private void onButtonPress() {
        loginButton.setStyle("-fx-background-color: #1A7A0A; " +
                             "-fx-text-fill: white; " +
                             "-fx-padding: 10 20; " +
                             "-fx-background-radius: 5px; " +
                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 3, 0, 0, 1);");
    }
    
    @FXML
    private void onButtonRelease() {
        loginButton.setStyle("-fx-background-color: #27B611; " +
                             "-fx-text-fill: white; " +
                             "-fx-padding: 10 20; " +
                             "-fx-background-radius: 5px; " +
                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
    }
    
    @FXML
    private void initialize() {
        // Add event handler for Enter key on username and password fields
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }
}
