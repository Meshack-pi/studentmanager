package com.example.studentmanager.model;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String studentId;
    private String name;
    private String email;
    private String username; // Linked to User
    private List<String> enrolledCourseIds; // List of course IDs
    
    public Student() {
        this.enrolledCourseIds = new ArrayList<>();
    }
    
    public Student(String studentId, String name, String email, String username) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.enrolledCourseIds = new ArrayList<>();
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public List<String> getEnrolledCourseIds() {
        return enrolledCourseIds;
    }
    
    public void setEnrolledCourseIds(List<String> enrolledCourseIds) {
        this.enrolledCourseIds = enrolledCourseIds;
    }
}

