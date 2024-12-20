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
    private Label loadingLabel;
    
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
                // Disable login button and clear error
                loginButton.setDisable(true);
                errorLabel.setText("");
                loadingLabel.setText("Initializing...");
                
                // Create a custom inventory instance that will update our loading label
                com.inventory.Inventory inventory = new com.inventory.Inventory() {
                    @Override
                    protected void updateLoadingMessage(String message) {
                        super.updateLoadingMessage(message);  // This will print to console
                        // Update UI on JavaFX thread
                        javafx.application.Platform.runLater(() -> loadingLabel.setText(message));
                    }
                };
                
                // Load data in a background thread
                new Thread(() -> {
                    try {
                        // Load data 
                        inventory.loadPartsFromDatabase();
                        inventory.loadProductsFromDatabase();
                        
                        // Switch to inventory scene on JavaFX thread
                        javafx.application.Platform.runLater(() -> {
                            try {
                                // Load the inventory scene
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/inventoryScene.fxml"));
                                Parent inventoryRoot = loader.load();
                                
                                // Get the current stage and set the new scene
                                Stage stage = (Stage) loginButton.getScene().getWindow();
                                Scene inventoryScene = new Scene(inventoryRoot);
                                stage.setTitle("Inventory Management System");
                                stage.setScene(inventoryScene);
                            } catch (IOException e) {
                                e.printStackTrace();
                                errorLabel.setText("Error loading application");
                                loginButton.setDisable(false);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        javafx.application.Platform.runLater(() -> {
                            errorLabel.setText("Error loading data: " + e.getMessage());
                            loginButton.setDisable(false);
                        });
                    }
                }).start();
                
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Error initializing application");
                loginButton.setDisable(false);
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
