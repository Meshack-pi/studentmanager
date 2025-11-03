package com.example.studentmanager.model;

public class Course {
    private String courseId;
    private String courseName;
    private String code;
    private String lecturerId; // ID of assigned lecturer
    
    public Course() {
    }
    
    public Course(String courseId, String courseName, String code) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.code = code;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getLecturerId() {
        return lecturerId;
    }
    
    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }
}

