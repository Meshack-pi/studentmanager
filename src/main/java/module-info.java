module com.example.studentmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.zaxxer.hikari;
    requires jbcrypt;

    opens com.example.studentmanager to javafx.fxml;
    exports com.example.studentmanager;
    exports com.example.studentmanager.model;
    exports com.example.studentmanager.database;
    exports com.example.studentmanager.util;
}