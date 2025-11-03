package com.example.studentmanager;

import java.io.IOException;
import com.example.studentmanager.model.DataManager;
import com.example.studentmanager.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    
    private DataManager dataManager;
    
    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
    }
    
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password!");
            return;
        }
        
        User found = dataManager.getUserByUsername(username);
        
        if (found == null || !found.getPassword().equals(password)) {
            errorLabel.setText("Invalid Username or password!");
            return;
        }
        
        try {
            dataManager.setCurrentUsername(username); // Store current user
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            String role = found.getRole();
            
            switch (role) {
                case "admin":
                    SceneManager.loadAdminScene(stage);
                    break;
                case "lecturer":
                    SceneManager.loadLecturerScene(stage);
                    break;
                case "student":
                    SceneManager.loadStudentScene(stage);
                    break;
                case "guardian":
                    SceneManager.loadGuardianScene(stage);
                    break;
                default:
                    errorLabel.setText("Unknown user role!");
                    return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error loading the view!");
        }
    }
}

