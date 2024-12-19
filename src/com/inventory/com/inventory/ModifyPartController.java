package com.inventory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * @author Omar Imam
 * @version %I% %G%
 */

public class ModifyPartController implements Initializable {
	
	/*
	 * Fields
	 */
	
	/**
	 * a RadioButton that allows you to select the type of {@link com.inventory.Part}
	 */
	@FXML
	private RadioButton modifyPartInHouseRadio;
	
	/**
	 * a RadioButton that allows you to select the type of {@link com.inventory.Part}
	 */
	@FXML
	private RadioButton modifyPartOutsourcedRadio;
	
	/**
	 * saves the {@link com.inventory.Part} to the {@link com.inventory.Inventory}
	 */
	@FXML
	private Button modifyPartSaveButton;
	
	/**
	 * cancels the form and closes the window
	 */
	@FXML
	private Button modifyPartCancelButton;
	
	/**
	 * the label for the last field in the {@link com.inventory.Part} form
	 */
	@FXML
	private Label modifyPartExtraLabel;
	
	/**
	 * the {@link javafx.scene.control.Label} that is used to display error messages
	 */
	@FXML
	private Label modifyPartSaveErrorLabel;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the id of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField modifyPartIdTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the name of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField modifyPartNameTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the price of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField modifyPartPriceTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the stock of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField modifyPartStockTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the max of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField modifyPartMaxTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the min of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField modifyPartMinTextField;
	
	/**
	 * the {@link javafx.scene.control.TextField} for the extra of the {@link com.inventory.Part}
	 */
	@FXML
	private TextField modifyPartExtraTextField;
	
	/**
	 * Saves the information in the TextFields to the inventory.&nbsp;It also includes input validation for the
	 * Stock, Min, and Max fields.&nbsp;It also checks to see which RadioButton is selected at the top of the form, and
	 * adjusts the last TextField accordingly.
	 *
	 * @param actionEvent fired when the save button is clicked
	 */
	public void modifyPartSaveListener(ActionEvent actionEvent) {
		// Replace the information for the selected part with the information in the text fields
		// Get the information from the text fields
		String modifyPartName = modifyPartNameTextField.getText();
		int modifyPartStock = Integer.parseInt(modifyPartStockTextField.getText());
		double modifyPartPrice = Double.parseDouble(modifyPartPriceTextField.getText());
		int modifyPartMax = Integer.parseInt(modifyPartMaxTextField.getText());
		int modifyPartMin = Integer.parseInt(modifyPartMinTextField.getText());
		int randomId = Integer.parseInt(modifyPartIdTextField.getText());
		
		boolean passCheck = true;
		
		// Input Validation Logic
		if ( modifyPartMin > modifyPartMax ) {
			modifyPartSaveErrorLabel.setText("Error: Minimum cannot be more than maximum!");
			passCheck = false;
		} else if ( modifyPartStock > modifyPartMax ) {
			modifyPartSaveErrorLabel.setText("Error: Current Stock cannot be more than the maximum!");
			passCheck = false;
		} else if ( modifyPartStock < modifyPartMin ) {
			modifyPartSaveErrorLabel.setText("Error: Current Stock cannot be less than the minimum!");
			passCheck = false;
		} else {
			modifyPartSaveErrorLabel.setText("");
		}
		
		if ( passCheck ) {
			try {
				// Prepare the SQL update statement
				String sql = "UPDATE parts SET name = ?, price = ?, stock = ?, min = ?, max = ?, type = ?, machine_id = ?, company_name = ? WHERE id = ?";
				PreparedStatement stmt = com.inventory.DatabaseConnection.getConnection().prepareStatement(sql);
				
				// Set the common fields
				stmt.setString(1, modifyPartName);
				stmt.setDouble(2, modifyPartPrice);
				stmt.setInt(3, modifyPartStock);
				stmt.setInt(4, modifyPartMin);
				stmt.setInt(5, modifyPartMax);
				
				// Depending on which radio button is selected, update as InHouse or Outsourced part
				if ( modifyPartInHouseRadio.isSelected() ) {
					int modifyPartExtra = Integer.parseInt(modifyPartExtraTextField.getText());
					stmt.setString(6, "InHouse");
					stmt.setInt(7, modifyPartExtra);
					stmt.setNull(8, java.sql.Types.VARCHAR);
					stmt.setInt(9, randomId);
					
					// Update database first
					int rowsAffected = stmt.executeUpdate();
					if ( rowsAffected > 0 ) {
						// If database update successful, update UI
						com.inventory.Inventory.allParts.set(com.inventory.Inventory.selectedPartIndex,
						                                     new com.inventory.InHouse(randomId,
						                                                               modifyPartName,
						                                                               modifyPartPrice,
						                                                               modifyPartStock,
						                                                               modifyPartMin,
						                                                               modifyPartMax,
						                                                               modifyPartExtra));
						// Close the window
						Stage stage = (Stage) modifyPartSaveButton.getScene().getWindow();
						stage.close();
					} else {
						modifyPartSaveErrorLabel.setText("Error: Part not found in database!");
					}
				} else {
					String modifyPartExtra = modifyPartExtraTextField.getText();
					stmt.setString(6, "Outsourced");
					stmt.setNull(7, java.sql.Types.INTEGER);
					stmt.setString(8, modifyPartExtra);
					stmt.setInt(9, randomId);
					
					// Update database first
					int rowsAffected = stmt.executeUpdate();
					if ( rowsAffected > 0 ) {
						// If database update successful, update UI
						com.inventory.Inventory.allParts.set(com.inventory.Inventory.selectedPartIndex,
						                                     new com.inventory.Outsourced(randomId,
						                                                                  modifyPartName,
						                                                                  modifyPartPrice,
						                                                                  modifyPartStock,
						                                                                  modifyPartMin,
						                                                                  modifyPartMax,
						                                                                  modifyPartExtra));
						// Close the window
						Stage stage = (Stage) modifyPartSaveButton.getScene().getWindow();
						stage.close();
					} else {
						modifyPartSaveErrorLabel.setText("Error: Part not found in database!");
					}
				}
			}
			catch ( SQLException e ) {
				System.out.println("Error updating part in database:");
				e.printStackTrace();
				modifyPartSaveErrorLabel.setText("Error updating database: " + e.getMessage());
			}
		} else {
			System.out.print("Didn't Pass.");
		}
	}
	
	/**
	 * Changes the Label of the last TextField depending on the radio button that is selected.&nbsp;It also adds a
	 * TextFormatter to the TextField for input validation, changing between Integers-only and Letters-Only to fit
	 * the field
	 *
	 * @param actionEvent fired when a radio button is selected
	 */
	public void modifyPartRadioListener(ActionEvent actionEvent) {
		// Set the text of the form based upon which radio button is selected
		if ( modifyPartInHouseRadio.isSelected() ) {
			modifyPartExtraLabel.setText("Machine ID");
			
			modifyPartExtraTextField.setText("");
			modifyPartExtraTextField.setTextFormatter(new TextFormatter<>(change ->
			                                                              {
				                                                              if ( change.getText().matches("\\d+") ) {
					                                                              modifyPartSaveErrorLabel.setText("");
					                                                              return change;
				                                                              } else if ( change.getText()
				                                                                                .equals("") ) {
					                                                              change.setText("");
					                                                              return change;
				                                                              } else {
					                                                              change.setText("");
					                                                              modifyPartSaveErrorLabel.setText(
							                                                              "Integers Only!");
					                                                              return change;
				                                                              }
			                                                              }));
			
		} else if ( modifyPartOutsourcedRadio.isSelected() ) {
			modifyPartExtraLabel.setText("Company Name");
			
			modifyPartExtraTextField.setText("");
			{
				modifyPartExtraLabel.setText("Company Name");
				modifyPartExtraTextField.setTextFormatter(new TextFormatter<>(change -> {
					if ( change.getText().matches("^[a-zA-Z0-9 _-]+$") ) {
						modifyPartSaveErrorLabel.setText("");
						return change;
					} else if ( change.getText().equals("") ) {
						change.setText("");
						return change;
					} else {
						change.setText("");
						modifyPartSaveErrorLabel.setText("Letters, numbers, spaces, hyphens, and underscores only!");
						return change;
					}
				}));
			}
		}
	}
	
	/**
	 * Closes the window if the cancel button is clicked
	 *
	 * @param actionEvent fired when the cancel button is clicked
	 *                    <br><br>
	 *                    RUNTIME ERROR Forgot to cast the Button as a stage before using stage.close();
	 */
	public void modifyPartCancelButtonListener(ActionEvent actionEvent) {
		// Close the window if the cancel button is clicked
		Stage stage = (Stage) modifyPartCancelButton.getScene().getWindow();
		stage.close();
	}
	
	/**
	 * Initializes the new scene by placing the selected part data into the UI.&nbsp;It also sets a TextFormatter
	 * to each TextField for input validation.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// Set the ID TextField to the selected part id
		modifyPartIdTextField.setText(Integer.toString(com.inventory.Inventory.selectedPartId));
		// Place the data from the selected part into the text field
		modifyPartNameTextField.setText(com.inventory.Inventory.selectedPart.getName());
		modifyPartStockTextField.setText(Integer.toString(com.inventory.Inventory.selectedPart.getStock()));
		modifyPartPriceTextField.setText(Double.toString(com.inventory.Inventory.selectedPart.getPrice()));
		modifyPartMaxTextField.setText(Integer.toString(com.inventory.Inventory.selectedPart.getMax()));
		modifyPartMinTextField.setText(Integer.toString(com.inventory.Inventory.selectedPart.getMin()));
		
		// Add button hover and click animation with consistent styling
		modifyPartSaveButton.setStyle(modifyPartSaveButton.getStyle() +
		                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
		modifyPartCancelButton.setStyle(modifyPartCancelButton.getStyle() +
		                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);");
		
		// Add button hover and click animation
		modifyPartSaveButton.setOnMouseEntered(event -> onButtonHover(event));
		modifyPartSaveButton.setOnMouseExited(event -> onButtonExit(event));
		modifyPartSaveButton.setOnMousePressed(event -> onButtonPress(event));
		modifyPartSaveButton.setOnMouseReleased(event -> onButtonRelease(event));
		
		modifyPartCancelButton.setOnMouseEntered(event -> onButtonHover(event));
		modifyPartCancelButton.setOnMouseExited(event -> onButtonExit(event));
		modifyPartCancelButton.setOnMousePressed(event -> onButtonPress(event));
		modifyPartCancelButton.setOnMouseReleased(event -> onButtonRelease(event));
		
		// Set TextFormatters on each TextField to apply input validation
		// Set the text of the form based upon which radio button is selected
		if ( com.inventory.Inventory.selectedPart instanceof com.inventory.InHouse ) {
			modifyPartInHouseRadio.setSelected(true);
			modifyPartExtraLabel.setText("Machine ID");
			modifyPartExtraTextField.setText(String.valueOf(((com.inventory.InHouse) com.inventory.Inventory.selectedPart).getMachineId()));
			
			// Set TextFormatter for Machine ID (Integers only)
			modifyPartExtraTextField.setTextFormatter(new TextFormatter<>(change -> {
				if ( change.getText().matches("\\d+") ) {
					modifyPartSaveErrorLabel.setText("");
					return change;
				} else if ( change.getText().equals("") ) {
					modifyPartSaveErrorLabel.setText("");
					return change;
				} else {
					change.setText("");
					modifyPartSaveErrorLabel.setText("Integers only!");
					return change;
				}
			}));
		}
		// Otherwise, set it to the Company Name
		else if ( com.inventory.Inventory.selectedPart instanceof com.inventory.Outsourced ) {
			modifyPartOutsourcedRadio.setSelected(true);
			modifyPartExtraLabel.setText("Company Name");
			modifyPartExtraTextField.setText(((com.inventory.Outsourced) com.inventory.Inventory.selectedPart).getCompanyName());
			
			// Set TextFormatter for Company Name
			modifyPartExtraTextField.setTextFormatter(new TextFormatter<>(change -> {
				if ( change.getText().matches("^[a-zA-Z0-9 _-]+$") ) {
					modifyPartSaveErrorLabel.setText("");
					return change;
				} else if ( change.getText().equals("") ) {
					modifyPartSaveErrorLabel.setText("");
					return change;
				} else {
					change.setText("");
					modifyPartSaveErrorLabel.setText("Letters, numbers, spaces, hyphens, and underscores only!");
					return change;
				}
			}));
		}
		
		// Set TextFormatter for Name TextField
		modifyPartNameTextField.setTextFormatter(new TextFormatter<>(change -> {
			if ( change.getText().matches("^[a-zA-Z0-9 ]+$") ) {
				modifyPartSaveErrorLabel.setText("");
				return change;
			} else if ( change.getText().equals("") ) {
				modifyPartSaveErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				modifyPartSaveErrorLabel.setText("Letters, numbers, and spaces only!");
				return change;
			}
		}));
		
		// Set TextFormatter for other numeric fields
		modifyPartStockTextField.setTextFormatter(new TextFormatter<>(change -> {
			if ( change.getText().matches("\\d+") ) {
				modifyPartSaveErrorLabel.setText("");
				return change;
			} else if ( change.getText().equals("") ) {
				modifyPartSaveErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				modifyPartSaveErrorLabel.setText("Integers only!");
				return change;
			}
		}));
		
		modifyPartMaxTextField.setTextFormatter(new TextFormatter<>(change -> {
			if ( change.getText().matches("\\d+") ) {
				modifyPartSaveErrorLabel.setText("");
				return change;
			} else if ( change.getText().equals("") ) {
				modifyPartSaveErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				modifyPartSaveErrorLabel.setText("Integers only!");
				return change;
			}
		}));
		
		modifyPartMinTextField.setTextFormatter(new TextFormatter<>(change -> {
			if ( change.getText().matches("\\d+") ) {
				modifyPartSaveErrorLabel.setText("");
				return change;
			} else if ( change.getText().equals("") ) {
				modifyPartSaveErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				modifyPartSaveErrorLabel.setText("Integers only!");
				return change;
			}
		}));
		
		modifyPartPriceTextField.setTextFormatter(new TextFormatter<>(change -> {
			if ( change.getText().matches("\\d+") || change.getText().matches("\\.") ) {
				modifyPartSaveErrorLabel.setText("");
				return change;
			} else if ( change.getText().equals("") ) {
				modifyPartSaveErrorLabel.setText("");
				return change;
			} else {
				change.setText("");
				modifyPartSaveErrorLabel.setText("Numbers and decimal point only!");
				return change;
			}
		}));
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
		if ( button == modifyPartSaveButton ) {
			return "#27B611";
		} else if ( button == modifyPartCancelButton ) {
			return "#ff0000";
		}
		return "#000000";
	}
}