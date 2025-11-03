package com.example.studentmanager.model;

import java.time.LocalDate;

public class Fee {
    private String studentId;
    private Double amount;
    private Double balance;
    private Double paidAmount;
    private LocalDate dueDate;
    
    public Fee() {
    }
    
    public Fee(String studentId, Double amount, LocalDate dueDate) {
        this.studentId = studentId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paidAmount = 0.0;
        this.balance = amount;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
        if (paidAmount != null && amount != null) {
            balance = amount - paidAmount;
        }
    }
    
    public Double getBalance() {
        return balance;
    }
    
    public void setBalance(Double balance) {
        this.balance = balance;
    }
    
    public Double getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
        if (amount != null && paidAmount != null) {
            balance = amount - paidAmount;
        }
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}

