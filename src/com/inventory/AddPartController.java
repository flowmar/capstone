package com.inventory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;

import static com.inventory.Inventory.getAllParts;

/**
 * @author Omar Imam
 * @version %I% %G%
 */

public class AddPartController implements Initializable {
	
	/*
	 * Fields
	 */
	
	/**
	 * a RadioButton that allows you to select the type of {@link com.inventory.Part}
	 */
	@FXML
	private RadioButton addPartInHouseRadio;
	
	/**
	 * saves the {@link com.inventory.Part} to the {@link com.inventory.Inventory}
	 */
	@FXML
	private Button addPartSaveButton;
	
	/**
	 * cancels the form and closes the window
	 */
	@FXML
	private Button addPartCancelButton;
	
	/**
	 * the label for the last field in the {@link com.inventory.Part} form
	 */
	@FXML
	private Label addPartExtraLabel;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the name of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField addPartNameTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the stock of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField addPartInventoryTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the price of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField addPartPriceTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the max of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField addPartMaxTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the min of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField addPartMinTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the extra of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField addPartExtraTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the id of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField addPartIdTextField;
	
	/**
	 * the {@link javafx.scene.control.Label} that is used to display error messages
	 */
	@FXML
	private Label addPartErrorLabel;
	
	/**
	 * Saves the information in the text fields to the inventory and applies input validation
	 *
	 * @param actionEvent fired when the save button is clicked
	 */
	public void addPartSaveListener(ActionEvent actionEvent) {
		try {
			String name = addPartNameTextField.getText();
			int stock = Integer.parseInt(addPartInventoryTextField.getText());
			double price = Double.parseDouble(addPartPriceTextField.getText());
			int max = Integer.parseInt(addPartMaxTextField.getText());
			int min = Integer.parseInt(addPartMinTextField.getText());
			
			// Input validation
			if (name.isEmpty()) {
				addPartErrorLabel.setText("Name cannot be empty");
				return;
			}
			if (min > max) {
				addPartErrorLabel.setText("Min cannot be greater than Max");
				return;
			}
			if (stock < min || stock > max) {
				addPartErrorLabel.setText("Stock must be between Min and Max");
				return;
			}
			
			com.inventory.Part newPart;
			if (addPartInHouseRadio.isSelected()) {
				int machineId = Integer.parseInt(addPartExtraTextField.getText());
				newPart = new com.inventory.InHouse(generateNewId(), name, price, stock, min, max, machineId);
			} else {
				String companyName = addPartExtraTextField.getText();
				if (companyName.isEmpty()) {
					addPartErrorLabel.setText("Company name cannot be empty");
					return;
				}
				newPart = new com.inventory.Outsourced(generateNewId(), name, price, stock, min, max, companyName);
			}
			
			// Add to database first
			addPartToDatabase(newPart);
			
			// If database update successful, update UI
			getAllParts().add(newPart);
			
			// Close the window
			Stage stage = (Stage) addPartSaveButton.getScene().getWindow();
			stage.close();
			
		} catch (NumberFormatException e) {
			addPartErrorLabel.setText("Please enter valid numbers");
		} catch (Exception e) {
			addPartErrorLabel.setText("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Cancels the add part form and closes out the window
	 *
	 * @param actionEvent fired when the add part button is clicked
	 */
	public void addPartCancelButtonListener(ActionEvent actionEvent) {
		Stage stage = (Stage) addPartCancelButton.getScene().getWindow();
		stage.close();
	}
	
	/**
	 * Changes the text of the last text field depending on which radio button is selected
	 *
	 * @param actionEvent fired when a radio button is selected
	 *                    <br><br>
	 *                    RUNTIME ERROR When creating the TextFormatters, I had a little bit of trouble with where to place
	 *                    the formatter for the MachineID/Company Name fields
	 */
	public void addPartRadioListener(ActionEvent actionEvent) {
		if (addPartInHouseRadio.isSelected()) {
			addPartExtraLabel.setText("Machine ID");
		} else {
			addPartExtraLabel.setText("Company Name");
		}
	}
	
	/**
	 * Initializes the randomId number and the input validation for each {@link javafx.scene.control.TextField}
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// Generate a random ID number
		int randomId = getRandomNumber();
		addPartIdTextField.setText(Integer.toString(randomId));
		
		// Set TextFormatters on each TextField to apply input validation
		// Display an error message if incorrect characters are typed
		addPartInventoryTextField.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().matches("\\d+")) {
				addPartErrorLabel.setText("");
				return change;
			} else if (change.getText().equals("")) {
				addPartErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				addPartErrorLabel.setText("Integers only!");
				return change;
			}
		}));
		
		addPartMaxTextField.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().matches("\\d+")) {
				addPartErrorLabel.setText("");
				return change;
			} else if (change.getText().equals("")) {
				addPartErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				addPartErrorLabel.setText("Integers only!");
				return change;
			}
		}));
		
		addPartMinTextField.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().matches("\\d+")) {
				addPartErrorLabel.setText("");
				return change;
			} else if (change.getText().equals("")) {
				addPartErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				addPartErrorLabel.setText("Integers only!");
				return change;
			}
		}));
		
		addPartNameTextField.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().matches("^[a-zA-Z0-9 ]+$")) {
				addPartErrorLabel.setText("");
				return change;
			} else if (change.getText().equals("")) {
				addPartErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				addPartErrorLabel.setText("Letters, numbers, and spaces only!");
				return change;
			}
		}));
		
		addPartPriceTextField.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().matches("\\d+") || change.getText().matches("\\.")) {
				addPartErrorLabel.setText("");
				return change;
			} else if (change.getText().equals("")) {
				addPartErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				addPartErrorLabel.setText("Prices don't contain letters!!");
				return change;
			}
		}));
		
		addPartExtraTextField.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().matches("^[a-zA-Z0-9 _-]+$")) {
				addPartErrorLabel.setText("");
				return change;
			} else if (change.getText().equals("")) {
				addPartErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				addPartErrorLabel.setText("Letters, numbers, spaces, hyphens, and underscores only!");
				return change;
			}
		}));
		
		// Add button hover and click animation with consistent styling
		addPartSaveButton.setStyle(addPartSaveButton.getStyle() +
		                           "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
		addPartCancelButton.setStyle(addPartCancelButton.getStyle() +
		                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
		
		// Add button hover and click animation
		addPartSaveButton.setOnMouseEntered(event -> onButtonHover(event));
		addPartSaveButton.setOnMouseExited(event -> onButtonExit(event));
		addPartSaveButton.setOnMousePressed(event -> onButtonPress(event));
		addPartSaveButton.setOnMouseReleased(event -> onButtonRelease(event));
		
		addPartCancelButton.setOnMouseEntered(event -> onButtonHover(event));
		addPartCancelButton.setOnMouseExited(event -> onButtonExit(event));
		addPartCancelButton.setOnMousePressed(event -> onButtonPress(event));
		addPartCancelButton.setOnMouseReleased(event -> onButtonRelease(event));
	}
	
	/**
	 * Creates a random ID number for the new {@link com.inventory.Part}
	 *
	 * @return the random ID number
	 */
	public int getRandomNumber() {
		Random randomNumbers = new Random();
		return Math.abs(randomNumbers.nextInt(1000));
	}
	
	@FXML
	private void onButtonHover(MouseEvent event) {
		Button sourceButton = (Button) event.getSource();
		sourceButton.setStyle(sourceButton.getStyle() +
		                      "-fx-background-color: derive(" + getButtonColor(sourceButton) + ", 10%); " +
		                      "-fx-cursor: hand;");
	}
	
	@FXML
	private void onButtonExit(MouseEvent event) {
		Button sourceButton = (Button) event.getSource();
		sourceButton.setStyle(sourceButton.getStyle().replaceAll("-fx-background-color: derive\\([^;]+\\);", ""));
	}
	
	@FXML
	private void onButtonPress(MouseEvent event) {
		Button sourceButton = (Button) event.getSource();
		sourceButton.setStyle(sourceButton.getStyle() +
		                      "-fx-translate-y: 2px; " +
		                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1);");
	}
	
	@FXML
	private void onButtonRelease(MouseEvent event) {
		Button sourceButton = (Button) event.getSource();
		sourceButton.setStyle(sourceButton.getStyle()
		                                  .replaceAll("-fx-translate-y: 2px;", "")
		                                  .replaceAll(
				                                  "-fx-effect: dropshadow\\(three-pass-box, rgba\\(0,0,0,0.2\\), 3, 0, 0, 1\\);",
				                                  "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);"));
	}
	
	private String getButtonColor(Button button) {
		if (button == addPartSaveButton) {
			return "#27B611";
		} else if (button == addPartCancelButton) {
			return "#ff0000";
		}
		return "#000000";
	}
	
	private int generateNewId() {
		Random randomNumbers = new Random();
		return Math.abs(randomNumbers.nextInt(1000));
	}
	
	private void addPartToDatabase(com.inventory.Part newPart) throws SQLException {
		Connection conn = com.inventory.DatabaseConnection.getConnection();
		String sql = "INSERT INTO parts (id, name, price, stock, min, max, type, machine_id, company_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		stmt.setInt(1, newPart.getId());
		stmt.setString(2, newPart.getName());
		stmt.setDouble(3, newPart.getPrice());
		stmt.setInt(4, newPart.getStock());
		stmt.setInt(5, newPart.getMin());
		stmt.setInt(6, newPart.getMax());
		
		if (newPart instanceof com.inventory.InHouse) {
			com.inventory.InHouse inHousePart = (com.inventory.InHouse) newPart;
			stmt.setString(7, "InHouse");
			stmt.setInt(8, inHousePart.getMachineId());
			stmt.setNull(9, java.sql.Types.VARCHAR);
		} else {
			com.inventory.Outsourced outsourcedPart = (com.inventory.Outsourced) newPart;
			stmt.setString(7, "Outsourced");
			stmt.setNull(8, java.sql.Types.INTEGER);
			stmt.setString(9, outsourcedPart.getCompanyName());
		}
		
		stmt.executeUpdate();
	}
}
