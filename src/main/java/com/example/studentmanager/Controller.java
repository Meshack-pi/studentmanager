package com.example.studentmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.studentmanager.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private List<User> users = new ArrayList<>();
    @FXML
    public void initialize(){
        //sample users
        users.add(new User("lecturer1", "1234", "lecturer"));
        users.add(new User("student1", "abcd", "student"));
    }
    @FXML
    public void handleLogin(ActionEvent event){
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        User found = users.stream()
            .filter(u->u.getUsername().equals(username) && u.getPassword().equals(password))
            .findFirst().orElse(null);
            if (found == null){
                errorLabel.setText("Invalid Username or password!");
                return;
            }
            try {
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                FXMLLoader loader;
                Scene scene;
                if(found.getRole().equals("lecturer")){
                    loader = new FXMLLoader(getClass().getResource("lecturer-view.fxml"));
                    scene = new Scene(loader.load());
                } else{
                    loader = new FXMLLoader(getClass().getResource("student-view.fxml"));
                    scene = new Scene(loader.load());
                }
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Error Loading the view!");
            }
    }
}