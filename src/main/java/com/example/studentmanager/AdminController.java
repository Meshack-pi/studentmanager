package com.example.studentmanager;

import java.io.IOException;
import com.example.studentmanager.model.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdminController {
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField idField;
    @FXML private Label accountMessageLabel;
    
    @FXML private ComboBox<String> guardianIdCombo;
    @FXML private ComboBox<String> studentIdCombo;
    @FXML private Label linkMessageLabel;
    
    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField courseCodeField;
    @FXML private Label courseMessageLabel;
    
    @FXML private ComboBox<String> lecturerCombo;
    @FXML private ComboBox<String> courseCombo;
    @FXML private Label allocateMessageLabel;
    
    private DataManager dataManager;
    
    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
        loadComboBoxes();
    }
    
    private void loadComboBoxes() {
        // Load account types
        accountTypeCombo.setItems(FXCollections.observableArrayList(
            "student", "lecturer", "guardian"
        ));
        
        // Load guardians for linking
        guardianIdCombo.setItems(FXCollections.observableArrayList(
            dataManager.getGuardians().stream()
                .map(Guardian::getGuardianId)
                .toList()
        ));
        
        // Load students for linking
        studentIdCombo.setItems(FXCollections.observableArrayList(
            dataManager.getStudents().stream()
                .map(Student::getStudentId)
                .toList()
        ));
        
        // Load lecturers for allocation
        lecturerCombo.setItems(FXCollections.observableArrayList(
            dataManager.getLecturers().stream()
                .map(l -> l.getLecturerId() + " - " + l.getName())
                .toList()
        ));
        
        // Load courses for allocation
        courseCombo.setItems(FXCollections.observableArrayList(
            dataManager.getCourses().stream()
                .map(c -> c.getCourseId() + " - " + c.getCourseName())
                .toList()
        ));
    }
    
    @FXML
    public void handleCreateAccount(ActionEvent event) {
        String accountType = accountTypeCombo.getSelectionModel().getSelectedItem();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String id = idField.getText().trim();
        
        if (accountType == null || username.isEmpty() || password.isEmpty() || 
            name.isEmpty() || email.isEmpty() || id.isEmpty()) {
            accountMessageLabel.setText("Please fill all fields!");
            accountMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Check if username already exists
        if (dataManager.getUserByUsername(username) != null) {
            accountMessageLabel.setText("Username already exists!");
            accountMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Check if ID already exists
        if (accountType.equals("student") && dataManager.getStudentById(id) != null) {
            accountMessageLabel.setText("Student ID already exists!");
            accountMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        } else if (accountType.equals("lecturer") && dataManager.getLecturerById(id) != null) {
            accountMessageLabel.setText("Lecturer ID already exists!");
            accountMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        } else if (accountType.equals("guardian") && dataManager.getGuardianById(id) != null) {
            accountMessageLabel.setText("Guardian ID already exists!");
            accountMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Create user
        User user = new User(username, password, accountType);
        dataManager.addUser(user);
        
        // Create specific account type
        if (accountType.equals("student")) {
            Student student = new Student(id, name, email, username);
            dataManager.addStudent(student);
        } else if (accountType.equals("lecturer")) {
            Lecturer lecturer = new Lecturer(id, name, email, username);
            dataManager.addLecturer(lecturer);
        } else if (accountType.equals("guardian")) {
            Guardian guardian = new Guardian(id, name, email, username);
            dataManager.addGuardian(guardian);
        }
        
        // Clear fields
        usernameField.clear();
        passwordField.clear();
        nameField.clear();
        emailField.clear();
        idField.clear();
        
        accountMessageLabel.setText("Account created successfully!");
        accountMessageLabel.setStyle("-fx-text-fill: green;");
        loadComboBoxes(); // Refresh combos
    }
    
    @FXML
    public void handleLinkGuardian(ActionEvent event) {
        String guardianId = guardianIdCombo.getSelectionModel().getSelectedItem();
        String studentId = studentIdCombo.getSelectionModel().getSelectedItem();
        
        if (guardianId == null || studentId == null) {
            linkMessageLabel.setText("Please select both guardian and student!");
            linkMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        Guardian guardian = dataManager.getGuardianById(guardianId);
        if (guardian == null) {
            linkMessageLabel.setText("Guardian not found!");
            linkMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (!guardian.getLinkedStudentIds().contains(studentId)) {
            guardian.getLinkedStudentIds().add(studentId);
            dataManager.saveAll();
            linkMessageLabel.setText("Guardian linked to student successfully!");
            linkMessageLabel.setStyle("-fx-text-fill: green;");
        } else {
            linkMessageLabel.setText("Guardian already linked to this student!");
            linkMessageLabel.setStyle("-fx-text-fill: orange;");
        }
    }
    
    @FXML
    public void handleCreateCourse(ActionEvent event) {
        String courseId = courseIdField.getText().trim();
        String courseName = courseNameField.getText().trim();
        String courseCode = courseCodeField.getText().trim();
        
        if (courseId.isEmpty() || courseName.isEmpty() || courseCode.isEmpty()) {
            courseMessageLabel.setText("Please fill all fields!");
            courseMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (dataManager.getCourseById(courseId) != null) {
            courseMessageLabel.setText("Course ID already exists!");
            courseMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        Course course = new Course(courseId, courseName, courseCode);
        dataManager.addCourse(course);
        
        courseIdField.clear();
        courseNameField.clear();
        courseCodeField.clear();
        
        courseMessageLabel.setText("Course created successfully!");
        courseMessageLabel.setStyle("-fx-text-fill: green;");
        loadComboBoxes(); // Refresh combos
    }
    
    @FXML
    public void handleAllocateLecturer(ActionEvent event) {
        String lecturerSelection = lecturerCombo.getSelectionModel().getSelectedItem();
        String courseSelection = courseCombo.getSelectionModel().getSelectedItem();
        
        if (lecturerSelection == null || courseSelection == null) {
            allocateMessageLabel.setText("Please select both lecturer and course!");
            allocateMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        String lecturerId = lecturerSelection.split(" - ")[0];
        String courseId = courseSelection.split(" - ")[0];
        
        Lecturer lecturer = dataManager.getLecturerById(lecturerId);
        Course course = dataManager.getCourseById(courseId);
        
        if (lecturer == null || course == null) {
            allocateMessageLabel.setText("Lecturer or course not found!");
            allocateMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Assign lecturer to course
        course.setLecturerId(lecturerId);
        
        // Add course to lecturer's assigned courses if not already there
        if (!lecturer.getAssignedCourseIds().contains(courseId)) {
            lecturer.getAssignedCourseIds().add(courseId);
        }
        
        dataManager.saveAll();
        allocateMessageLabel.setText("Lecturer allocated to course successfully!");
        allocateMessageLabel.setStyle("-fx-text-fill: green;");
    }
    
    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        SceneManager.loadLoginScene(stage);
    }
}

