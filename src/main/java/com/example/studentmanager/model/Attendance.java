package com.example.studentmanager.model;

import java.time.LocalDate;

public class Attendance {
    private String studentId;
    private String courseId;
    private LocalDate date;
    private String status; // "present" or "absent"
    
    public Attendance() {
    }
    
    public Attendance(String studentId, String courseId, LocalDate date, String status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.date = date;
        this.status = status;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

