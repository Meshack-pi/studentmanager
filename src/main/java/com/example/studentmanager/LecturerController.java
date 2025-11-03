package com.example.studentmanager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;
import com.example.studentmanager.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class LecturerController {
    // Marks update controls
    @FXML private ComboBox<String> marksCourseCombo;
    @FXML private ComboBox<String> marksStudentCombo;
    @FXML private TextField cat1Field;
    @FXML private TextField cat2Field;
    @FXML private TextField cat3Field;
    @FXML private TextField finalExamField;
    @FXML private Label marksMessageLabel;
    @FXML private TableView<MarkRow> marksTable;
    @FXML private TableColumn<MarkRow, String> marksCourseColumn;
    @FXML private TableColumn<MarkRow, String> marksStudentColumn;
    @FXML private TableColumn<MarkRow, Double> marksCat1Column;
    @FXML private TableColumn<MarkRow, Double> marksCat2Column;
    @FXML private TableColumn<MarkRow, Double> marksCat3Column;
    @FXML private TableColumn<MarkRow, Double> marksFinalColumn;
    @FXML private TableColumn<MarkRow, String> marksGradeColumn;
    
    // Attendance update controls
    @FXML private ComboBox<String> attendanceCourseCombo;
    @FXML private ComboBox<String> attendanceStudentCombo;
    @FXML private DatePicker attendanceDatePicker;
    @FXML private ComboBox<String> attendanceStatusCombo;
    @FXML private Label attendanceMessageLabel;
    @FXML private TableView<AttendanceRow> attendanceTable;
    @FXML private TableColumn<AttendanceRow, String> attendanceCourseColumn;
    @FXML private TableColumn<AttendanceRow, String> attendanceStudentColumn;
    @FXML private TableColumn<AttendanceRow, LocalDate> attendanceDateColumn;
    @FXML private TableColumn<AttendanceRow, String> attendanceStatusColumn;
    
    private DataManager dataManager;
    private Lecturer currentLecturer;
    
    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
        String username = dataManager.getCurrentUsername();
        currentLecturer = dataManager.getLecturerByUsername(username);
        
        if (currentLecturer != null) {
            loadMarksControls();
            loadAttendanceControls();
            loadMarksTable();
            loadAttendanceTable();
        }
    }
    
    private void loadMarksControls() {
        // Load assigned courses
        ObservableList<String> courses = FXCollections.observableArrayList(
            currentLecturer.getAssignedCourseIds().stream()
                .map(courseId -> {
                    Course course = dataManager.getCourseById(courseId);
                    return course != null ? course.getCourseId() + " - " + course.getCourseName() : courseId;
                })
                .collect(Collectors.toList())
        );
        marksCourseCombo.setItems(courses);
        
        // Update student list when course changes
        marksCourseCombo.setOnAction(e -> updateMarksStudentList());
    }
    
    private void updateMarksStudentList() {
        String selection = marksCourseCombo.getSelectionModel().getSelectedItem();
        if (selection == null) {
            marksStudentCombo.setItems(FXCollections.observableArrayList());
            return;
        }
        
        String courseId = selection.split(" - ")[0];
        Course course = dataManager.getCourseById(courseId);
        if (course == null) return;
        
        // Get students enrolled in this course
        ObservableList<String> students = FXCollections.observableArrayList(
            dataManager.getStudents().stream()
                .filter(s -> s.getEnrolledCourseIds().contains(courseId))
                .map(s -> s.getStudentId() + " - " + s.getName())
                .collect(Collectors.toList())
        );
        marksStudentCombo.setItems(students);
    }
    
    private void loadAttendanceControls() {
        // Load assigned courses
        ObservableList<String> courses = FXCollections.observableArrayList(
            currentLecturer.getAssignedCourseIds().stream()
                .map(courseId -> {
                    Course course = dataManager.getCourseById(courseId);
                    return course != null ? course.getCourseId() + " - " + course.getCourseName() : courseId;
                })
                .collect(Collectors.toList())
        );
        attendanceCourseCombo.setItems(courses);
        
        // Load attendance status options
        attendanceStatusCombo.setItems(FXCollections.observableArrayList(
            "present", "absent"
        ));
        
        // Update student list when course changes
        attendanceCourseCombo.setOnAction(e -> updateAttendanceStudentList());
        
        // Set default date to today
        attendanceDatePicker.setValue(LocalDate.now());
    }
    
    private void updateAttendanceStudentList() {
        String selection = attendanceCourseCombo.getSelectionModel().getSelectedItem();
        if (selection == null) {
            attendanceStudentCombo.setItems(FXCollections.observableArrayList());
            return;
        }
        
        String courseId = selection.split(" - ")[0];
        Course course = dataManager.getCourseById(courseId);
        if (course == null) return;
        
        // Get students enrolled in this course
        ObservableList<String> students = FXCollections.observableArrayList(
            dataManager.getStudents().stream()
                .filter(s -> s.getEnrolledCourseIds().contains(courseId))
                .map(s -> s.getStudentId() + " - " + s.getName())
                .collect(Collectors.toList())
        );
        attendanceStudentCombo.setItems(students);
    }
    
    @FXML
    public void handleUpdateMarks(ActionEvent event) {
        String courseSelection = marksCourseCombo.getSelectionModel().getSelectedItem();
        String studentSelection = marksStudentCombo.getSelectionModel().getSelectedItem();
        
        if (courseSelection == null || studentSelection == null) {
            marksMessageLabel.setText("Please select both course and student!");
            marksMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        String courseId = courseSelection.split(" - ")[0];
        String studentId = studentSelection.split(" - ")[0];
        
        try {
            Double cat1 = cat1Field.getText().isEmpty() ? null : Double.parseDouble(cat1Field.getText());
            Double cat2 = cat2Field.getText().isEmpty() ? null : Double.parseDouble(cat2Field.getText());
            Double cat3 = cat3Field.getText().isEmpty() ? null : Double.parseDouble(cat3Field.getText());
            Double finalExam = finalExamField.getText().isEmpty() ? null : Double.parseDouble(finalExamField.getText());
            
            // Validate scores are between 0 and 100
            if ((cat1 != null && (cat1 < 0 || cat1 > 100)) ||
                (cat2 != null && (cat2 < 0 || cat2 > 100)) ||
                (cat3 != null && (cat3 < 0 || cat3 > 100)) ||
                (finalExam != null && (finalExam < 0 || finalExam > 100))) {
                marksMessageLabel.setText("Scores must be between 0 and 100!");
                marksMessageLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            
            Mark mark = dataManager.getMarkByStudentAndCourse(studentId, courseId);
            if (mark == null) {
                mark = new Mark(studentId, courseId);
            }
            
            if (cat1 != null) mark.setCat1Score(cat1);
            if (cat2 != null) mark.setCat2Score(cat2);
            if (cat3 != null) mark.setCat3Score(cat3);
            if (finalExam != null) mark.setFinalExamScore(finalExam);
            
            mark.calculateOverallGrade();
            dataManager.addOrUpdateMark(mark);
            
            marksMessageLabel.setText("Marks updated successfully!");
            marksMessageLabel.setStyle("-fx-text-fill: green;");
            
            // Clear fields
            cat1Field.clear();
            cat2Field.clear();
            cat3Field.clear();
            finalExamField.clear();
            
            // Refresh table
            loadMarksTable();
            
        } catch (NumberFormatException e) {
            marksMessageLabel.setText("Please enter valid numbers for scores!");
            marksMessageLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    @FXML
    public void handleUpdateAttendance(ActionEvent event) {
        String courseSelection = attendanceCourseCombo.getSelectionModel().getSelectedItem();
        String studentSelection = attendanceStudentCombo.getSelectionModel().getSelectedItem();
        LocalDate date = attendanceDatePicker.getValue();
        String status = attendanceStatusCombo.getSelectionModel().getSelectedItem();
        
        if (courseSelection == null || studentSelection == null || date == null || status == null) {
            attendanceMessageLabel.setText("Please fill all fields!");
            attendanceMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        String courseId = courseSelection.split(" - ")[0];
        String studentId = studentSelection.split(" - ")[0];
        
        Attendance attendance = new Attendance(studentId, courseId, date, status);
        dataManager.addAttendance(attendance);
        
        attendanceMessageLabel.setText("Attendance recorded successfully!");
        attendanceMessageLabel.setStyle("-fx-text-fill: green;");
        
        // Refresh table
        loadAttendanceTable();
    }
    
    private void loadMarksTable() {
        // Setup columns
        marksCourseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        marksStudentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        marksCat1Column.setCellValueFactory(new PropertyValueFactory<>("cat1"));
        marksCat2Column.setCellValueFactory(new PropertyValueFactory<>("cat2"));
        marksCat3Column.setCellValueFactory(new PropertyValueFactory<>("cat3"));
        marksFinalColumn.setCellValueFactory(new PropertyValueFactory<>("finalExam"));
        marksGradeColumn.setCellValueFactory(new PropertyValueFactory<>("overallGrade"));
        
        // Load marks for assigned courses
        ObservableList<MarkRow> markRows = FXCollections.observableArrayList();
        for (String courseId : currentLecturer.getAssignedCourseIds()) {
            for (Mark mark : dataManager.getMarks()) {
                if (mark.getCourseId().equals(courseId)) {
                    Course course = dataManager.getCourseById(mark.getCourseId());
                    Student student = dataManager.getStudentById(mark.getStudentId());
                    String courseName = course != null ? course.getCourseName() : mark.getCourseId();
                    String studentName = student != null ? student.getName() : mark.getStudentId();
                    markRows.add(new MarkRow(courseName, studentName, mark.getCat1Score(), 
                        mark.getCat2Score(), mark.getCat3Score(), mark.getFinalExamScore(), mark.getOverallGrade()));
                }
            }
        }
        marksTable.setItems(markRows);
    }
    
    private void loadAttendanceTable() {
        // Setup columns
        attendanceCourseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        attendanceStudentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        attendanceDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        attendanceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Load attendance for assigned courses
        ObservableList<AttendanceRow> attendanceRows = FXCollections.observableArrayList();
        for (String courseId : currentLecturer.getAssignedCourseIds()) {
            for (Attendance att : dataManager.getAttendance()) {
                if (att.getCourseId().equals(courseId)) {
                    Course course = dataManager.getCourseById(att.getCourseId());
                    Student student = dataManager.getStudentById(att.getStudentId());
                    String courseName = course != null ? course.getCourseName() : att.getCourseId();
                    String studentName = student != null ? student.getName() : att.getStudentId();
                    attendanceRows.add(new AttendanceRow(courseName, studentName, att.getDate(), att.getStatus()));
                }
            }
        }
        attendanceTable.setItems(attendanceRows);
    }
    
    @FXML
    public void logout(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        SceneManager.loadLoginScene(stage);
    }
    
    // Helper classes for table display
    public static class MarkRow {
        private String courseName;
        private String studentName;
        private Double cat1;
        private Double cat2;
        private Double cat3;
        private Double finalExam;
        private String overallGrade;
        
        public MarkRow(String courseName, String studentName, Double cat1, Double cat2, 
                      Double cat3, Double finalExam, String overallGrade) {
            this.courseName = courseName;
            this.studentName = studentName;
            this.cat1 = cat1;
            this.cat2 = cat2;
            this.cat3 = cat3;
            this.finalExam = finalExam;
            this.overallGrade = overallGrade != null ? overallGrade : "N/A";
        }
        
        public String getCourseName() { return courseName; }
        public String getStudentName() { return studentName; }
        public Double getCat1() { return cat1; }
        public Double getCat2() { return cat2; }
        public Double getCat3() { return cat3; }
        public Double getFinalExam() { return finalExam; }
        public String getOverallGrade() { return overallGrade; }
    }
    
    public static class AttendanceRow {
        private String courseName;
        private String studentName;
        private LocalDate date;
        private String status;
        
        public AttendanceRow(String courseName, String studentName, LocalDate date, String status) {
            this.courseName = courseName;
            this.studentName = studentName;
            this.date = date;
            this.status = status;
        }
        
        public String getCourseName() { return courseName; }
        public String getStudentName() { return studentName; }
        public LocalDate getDate() { return date; }
        public String getStatus() { return status; }
    }
}
