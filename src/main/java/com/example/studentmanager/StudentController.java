package com.example.studentmanager;

import java.io.IOException;
import com.example.studentmanager.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.stream.Collectors;

public class StudentController {
    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private TableView<MarkRow> marksTable;
    @FXML private TableColumn<MarkRow, String> courseColumn;
    @FXML private TableColumn<MarkRow, Double> cat1Column;
    @FXML private TableColumn<MarkRow, Double> cat2Column;
    @FXML private TableColumn<MarkRow, Double> cat3Column;
    @FXML private TableColumn<MarkRow, Double> finalColumn;
    @FXML private TableColumn<MarkRow, String> gradeColumn;
    @FXML private ComboBox<String> availableCoursesCombo;
    @FXML private ListView<String> enrolledCoursesList;
    @FXML private Label registerMessageLabel;
    
    private DataManager dataManager;
    private Student currentStudent;
    
    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
        String username = dataManager.getCurrentUsername();
        currentStudent = dataManager.getStudentByUsername(username);
        
        if (currentStudent != null) {
            // Display profile
            studentNameLabel.setText("Name: " + currentStudent.getName());
            studentIdLabel.setText("Student ID: " + currentStudent.getStudentId());
            
            // Load marks
            loadMarks();
            
            // Load course registration
            loadCourseRegistration();
        }
    }
    
    private void loadMarks() {
        // Setup table columns
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        cat1Column.setCellValueFactory(new PropertyValueFactory<>("cat1"));
        cat2Column.setCellValueFactory(new PropertyValueFactory<>("cat2"));
        cat3Column.setCellValueFactory(new PropertyValueFactory<>("cat3"));
        finalColumn.setCellValueFactory(new PropertyValueFactory<>("finalExam"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("overallGrade"));
        
        // Load marks for current student
        ObservableList<MarkRow> markRows = FXCollections.observableArrayList();
        for (Mark mark : dataManager.getMarksByStudentId(currentStudent.getStudentId())) {
            Course course = dataManager.getCourseById(mark.getCourseId());
            String courseName = course != null ? course.getCourseName() : mark.getCourseId();
            markRows.add(new MarkRow(courseName, mark.getCat1Score(), mark.getCat2Score(), 
                mark.getCat3Score(), mark.getFinalExamScore(), mark.getOverallGrade()));
        }
        marksTable.setItems(markRows);
    }
    
    private void loadCourseRegistration() {
        // Load available courses (courses not yet enrolled)
        ObservableList<String> availableCourses = FXCollections.observableArrayList(
            dataManager.getCourses().stream()
                .filter(c -> !currentStudent.getEnrolledCourseIds().contains(c.getCourseId()))
                .map(c -> c.getCourseId() + " - " + c.getCourseName())
                .collect(Collectors.toList())
        );
        availableCoursesCombo.setItems(availableCourses);
        
        // Load enrolled courses
        ObservableList<String> enrolledCourses = FXCollections.observableArrayList(
            currentStudent.getEnrolledCourseIds().stream()
                .map(id -> {
                    Course course = dataManager.getCourseById(id);
                    return course != null ? course.getCourseId() + " - " + course.getCourseName() : id;
                })
                .collect(Collectors.toList())
        );
        enrolledCoursesList.setItems(enrolledCourses);
    }
    
    @FXML
    public void handleRegisterCourse(ActionEvent event) {
        String selection = availableCoursesCombo.getSelectionModel().getSelectedItem();
        if (selection == null || selection.isEmpty()) {
            registerMessageLabel.setText("Please select a course!");
            registerMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        String courseId = selection.split(" - ")[0];
        Course course = dataManager.getCourseById(courseId);
        
        if (course == null) {
            registerMessageLabel.setText("Course not found!");
            registerMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (currentStudent.getEnrolledCourseIds().contains(courseId)) {
            registerMessageLabel.setText("Already enrolled in this course!");
            registerMessageLabel.setStyle("-fx-text-fill: orange;");
            return;
        }
        
        currentStudent.getEnrolledCourseIds().add(courseId);
        dataManager.saveAll();
        
        registerMessageLabel.setText("Successfully registered for " + course.getCourseName() + "!");
        registerMessageLabel.setStyle("-fx-text-fill: green;");
        
        // Refresh
        loadCourseRegistration();
        availableCoursesCombo.getSelectionModel().clearSelection();
    }
    
    @FXML
    public void logout(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        SceneManager.loadLoginScene(stage);
    }
    
    // Helper class for displaying marks in table
    public static class MarkRow {
        private String courseName;
        private Double cat1;
        private Double cat2;
        private Double cat3;
        private Double finalExam;
        private String overallGrade;
        
        public MarkRow(String courseName, Double cat1, Double cat2, Double cat3, Double finalExam, String overallGrade) {
            this.courseName = courseName;
            this.cat1 = cat1;
            this.cat2 = cat2;
            this.cat3 = cat3;
            this.finalExam = finalExam;
            this.overallGrade = overallGrade != null ? overallGrade : "N/A";
        }
        
        public String getCourseName() { return courseName; }
        public Double getCat1() { return cat1; }
        public Double getCat2() { return cat2; }
        public Double getCat3() { return cat3; }
        public Double getFinalExam() { return finalExam; }
        public String getOverallGrade() { return overallGrade; }
    }
}
