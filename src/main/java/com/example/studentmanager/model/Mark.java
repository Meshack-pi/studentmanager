package com.example.studentmanager.model;

public class Mark {
    private String studentId;
    private String courseId;
    private Double cat1Score;
    private Double cat2Score;
    private Double cat3Score;
    private Double finalExamScore;
    private String overallGrade;
    
    public Mark() {
    }
    
    public Mark(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
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
    
    public Double getCat1Score() {
        return cat1Score;
    }
    
    public void setCat1Score(Double cat1Score) {
        this.cat1Score = cat1Score;
    }
    
    public Double getCat2Score() {
        return cat2Score;
    }
    
    public void setCat2Score(Double cat2Score) {
        this.cat2Score = cat2Score;
    }
    
    public Double getCat3Score() {
        return cat3Score;
    }
    
    public void setCat3Score(Double cat3Score) {
        this.cat3Score = cat3Score;
    }
    
    public Double getFinalExamScore() {
        return finalExamScore;
    }
    
    public void setFinalExamScore(Double finalExamScore) {
        this.finalExamScore = finalExamScore;
    }
    
    public String getOverallGrade() {
        return overallGrade;
    }
    
    public void setOverallGrade(String overallGrade) {
        this.overallGrade = overallGrade;
    }
    
    // Calculate overall grade based on scores (CAT 1, 2, 3 each 20%, Final 40%)
    public void calculateOverallGrade() {
        if (cat1Score == null || cat2Score == null || cat3Score == null || finalExamScore == null) {
            overallGrade = "N/A";
            return;
        }
        
        double total = (cat1Score * 0.20) + (cat2Score * 0.20) + (cat3Score * 0.20) + (finalExamScore * 0.40);
        
        if (total >= 90) {
            overallGrade = "A";
        } else if (total >= 80) {
            overallGrade = "B";
        } else if (total >= 70) {
            overallGrade = "C";
        } else if (total >= 60) {
            overallGrade = "D";
        } else {
            overallGrade = "F";
        }
    }
}

