package com.example.studentmanager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import com.example.studentmanager.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.stream.Collectors;

public class GuardianController {
    @FXML private ComboBox<String> childCombo;
    @FXML private Label feeAmountLabel;
    @FXML private Label feePaidLabel;
    @FXML private Label feeBalanceLabel;
    @FXML private Label feeDueDateLabel;
    @FXML private TableView<MarkRow> marksTable;
    @FXML private TableColumn<MarkRow, String> guardianCourseColumn;
    @FXML private TableColumn<MarkRow, Double> guardianCat1Column;
    @FXML private TableColumn<MarkRow, Double> guardianCat2Column;
    @FXML private TableColumn<MarkRow, Double> guardianCat3Column;
    @FXML private TableColumn<MarkRow, Double> guardianFinalColumn;
    @FXML private TableColumn<MarkRow, String> guardianGradeColumn;
    
    private DataManager dataManager;
    private Guardian currentGuardian;
    private Student selectedChild;
    
    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
        String username = dataManager.getCurrentUsername();
        currentGuardian = dataManager.getGuardianByUsername(username);
        
        if (currentGuardian != null) {
            loadChildren();
        }
    }
    
    private void loadChildren() {
        // Load linked children
        ObservableList<String> children = FXCollections.observableArrayList(
            currentGuardian.getLinkedStudentIds().stream()
                .map(studentId -> {
                    Student student = dataManager.getStudentById(studentId);
                    return student != null ? studentId + " - " + student.getName() : studentId;
                })
                .collect(Collectors.toList())
        );
        childCombo.setItems(children);
        
        // Select first child if available
        if (!children.isEmpty()) {
            childCombo.getSelectionModel().select(0);
            handleChildSelection(null);
        }
    }
    
    @FXML
    public void handleChildSelection(ActionEvent event) {
        String selection = childCombo.getSelectionModel().getSelectedItem();
        if (selection == null || selection.isEmpty()) {
            selectedChild = null;
            clearDisplay();
            return;
        }
        
        String studentId = selection.split(" - ")[0];
        selectedChild = dataManager.getStudentById(studentId);
        
        if (selectedChild != null) {
            loadMarks();
            loadFees();
        }
    }
    
    private void loadMarks() {
        if (selectedChild == null) {
            marksTable.setItems(FXCollections.observableArrayList());
            return;
        }
        
        // Setup table columns
        guardianCourseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        guardianCat1Column.setCellValueFactory(new PropertyValueFactory<>("cat1"));
        guardianCat2Column.setCellValueFactory(new PropertyValueFactory<>("cat2"));
        guardianCat3Column.setCellValueFactory(new PropertyValueFactory<>("cat3"));
        guardianFinalColumn.setCellValueFactory(new PropertyValueFactory<>("finalExam"));
        guardianGradeColumn.setCellValueFactory(new PropertyValueFactory<>("overallGrade"));
        
        // Load marks for selected child
        ObservableList<MarkRow> markRows = FXCollections.observableArrayList();
        for (Mark mark : dataManager.getMarksByStudentId(selectedChild.getStudentId())) {
            Course course = dataManager.getCourseById(mark.getCourseId());
            String courseName = course != null ? course.getCourseName() : mark.getCourseId();
            markRows.add(new MarkRow(courseName, mark.getCat1Score(), mark.getCat2Score(), 
                mark.getCat3Score(), mark.getFinalExamScore(), mark.getOverallGrade()));
        }
        marksTable.setItems(markRows);
    }
    
    private void loadFees() {
        if (selectedChild == null) {
            clearFeeDisplay();
            return;
        }
        
        Fee fee = dataManager.getFeeByStudentId(selectedChild.getStudentId());
        if (fee != null) {
            feeAmountLabel.setText("Total Amount: $" + String.format("%.2f", fee.getAmount()));
            feePaidLabel.setText("Paid: $" + String.format("%.2f", fee.getPaidAmount() != null ? fee.getPaidAmount() : 0.0));
            feeBalanceLabel.setText("Balance: $" + String.format("%.2f", fee.getBalance() != null ? fee.getBalance() : fee.getAmount()));
            
            if (fee.getDueDate() != null) {
                feeDueDateLabel.setText("Due Date: " + fee.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } else {
                feeDueDateLabel.setText("Due Date: Not set");
            }
        } else {
            feeAmountLabel.setText("Total Amount: Not set");
            feePaidLabel.setText("Paid: -");
            feeBalanceLabel.setText("Balance: -");
            feeDueDateLabel.setText("Due Date: -");
        }
    }
    
    private void clearDisplay() {
        marksTable.setItems(FXCollections.observableArrayList());
        clearFeeDisplay();
    }
    
    private void clearFeeDisplay() {
        feeAmountLabel.setText("Total Amount: -");
        feePaidLabel.setText("Paid: -");
        feeBalanceLabel.setText("Balance: -");
        feeDueDateLabel.setText("Due Date: -");
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

