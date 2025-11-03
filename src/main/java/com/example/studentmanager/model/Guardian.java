package com.example.studentmanager.model;

import java.util.ArrayList;
import java.util.List;

public class Guardian {
    private String guardianId;
    private String name;
    private String email;
    private String username; // Linked to User
    private List<String> linkedStudentIds; // List of student IDs linked by admin
    
    public Guardian() {
        this.linkedStudentIds = new ArrayList<>();
    }
    
    public Guardian(String guardianId, String name, String email, String username) {
        this.guardianId = guardianId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.linkedStudentIds = new ArrayList<>();
    }
    
    public String getGuardianId() {
        return guardianId;
    }
    
    public void setGuardianId(String guardianId) {
        this.guardianId = guardianId;
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
    
    public List<String> getLinkedStudentIds() {
        return linkedStudentIds;
    }
    
    public void setLinkedStudentIds(List<String> linkedStudentIds) {
        this.linkedStudentIds = linkedStudentIds;
    }
}

