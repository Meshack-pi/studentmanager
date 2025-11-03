package com.example.studentmanager;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    
    public static void loadScene(Stage stage, String fxmlFile, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));
        Scene scene = new Scene(loader.load(), width, height);
        stage.setScene(scene);
        stage.show();
    }
    
    public static void loadScene(Stage stage, String fxmlFile) throws IOException {
        loadScene(stage, fxmlFile, 800, 600);
    }
    
    public static void loadLoginScene(Stage stage) throws IOException {
        loadScene(stage, "login.fxml", 560, 500);
        stage.setTitle("Student Manager Login");
    }
    
    public static void loadAdminScene(Stage stage) throws IOException {
        loadScene(stage, "admin-view.fxml", 900, 700);
        stage.setTitle("Admin Dashboard");
    }
    
    public static void loadStudentScene(Stage stage) throws IOException {
        loadScene(stage, "student-view.fxml", 800, 600);
        stage.setTitle("Student Dashboard");
    }
    
    public static void loadLecturerScene(Stage stage) throws IOException {
        loadScene(stage, "lecturer-view.fxml", 900, 700);
        stage.setTitle("Lecturer Dashboard");
    }
    
    public static void loadGuardianScene(Stage stage) throws IOException {
        loadScene(stage, "guardian-view.fxml", 800, 600);
        stage.setTitle("Guardian Dashboard");
    }
}

