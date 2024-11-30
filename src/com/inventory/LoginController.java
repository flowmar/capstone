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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        
        // For now, use a simple authentication
        // TODO: Implement proper authentication with database
        if (username.equals("admin") && password.equals("admin")) {
            try {
                // Initialize database connection and load data
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    errorLabel.setText("Failed to connect to database");
                    return;
                }

                // Create and initialize the Inventory instance
                Inventory inventory = new Inventory();
                
                // Load the inventory scene
                FXMLLoader loader = new FXMLLoader(getClass().getResource("inventoryScene.fxml"));
                Parent inventoryRoot = loader.load();
                
                // Get the current stage and set the new scene
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene inventoryScene = new Scene(inventoryRoot);
                stage.setTitle("Inventory Management System");
                stage.setScene(inventoryScene);
                
                // Load data only once
                inventory.loadPartsFromDatabase();
                inventory.loadProductsFromDatabase();
                
            } catch (IOException e) {
                System.out.println("Error loading inventory scene:");
                e.printStackTrace();
                errorLabel.setText("Error loading application");
            } catch (Exception e) {
                System.out.println("Error initializing application:");
                e.printStackTrace();
                errorLabel.setText("Error initializing application");
            }
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }
    
    @FXML
    private void onButtonHover() {
        loginButton.setStyle(loginButton.getStyle() + 
            "-fx-background-color: derive(#27B611, 10%); " +
            "-fx-cursor: hand;");
    }

    @FXML
    private void onButtonExit() {
        loginButton.setStyle(loginButton.getStyle().replaceAll("-fx-background-color: derive\\(#27B611, 10%\\);", ""));
    }

    @FXML
    private void onButtonPress() {
        loginButton.setStyle(loginButton.getStyle() + 
            "-fx-translate-y: 2px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1);");
    }

    @FXML
    private void onButtonRelease() {
        loginButton.setStyle(loginButton.getStyle()
            .replaceAll("-fx-translate-y: 2px;", "")
            .replaceAll("-fx-effect: dropshadow\\(three-pass-box, rgba\\(0,0,0,0.2\\), 3, 0, 0, 1\\);", 
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);"));
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

        // Add button hover and click animation
        loginButton.setOnMouseEntered(event -> onButtonHover());
        loginButton.setOnMouseExited(event -> onButtonExit());
        loginButton.setOnMousePressed(event -> onButtonPress());
        loginButton.setOnMouseReleased(event -> onButtonRelease());
    }
}
