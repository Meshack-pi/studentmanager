module com.example.studentmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.example.studentmanager to javafx.fxml;
    opens com.example.studentmanager.model to com.google.gson;
    exports com.example.studentmanager;
    exports com.example.studentmanager.model;
}