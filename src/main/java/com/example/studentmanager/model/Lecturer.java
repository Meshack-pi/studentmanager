package com.example.studentmanager.model;

import java.util.ArrayList;
import java.util.List;

public class Lecturer {
    private String lecturerId;
    private String name;
    private String email;
    private String username; // Linked to User
    private List<String> assignedCourseIds; // List of course IDs
    
    public Lecturer() {
        this.assignedCourseIds = new ArrayList<>();
    }
    
    public Lecturer(String lecturerId, String name, String email, String username) {
        this.lecturerId = lecturerId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.assignedCourseIds = new ArrayList<>();
    }
    
    public String getLecturerId() {
        return lecturerId;
    }
    
    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
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
    
    public List<String> getAssignedCourseIds() {
        return assignedCourseIds;
    }
    
    public void setAssignedCourseIds(List<String> assignedCourseIds) {
        this.assignedCourseIds = assignedCourseIds;
    }
}

