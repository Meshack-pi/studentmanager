package com.example.studentmanager.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSchema {
    
    public static void initializeDatabase() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS studentmanager");
            stmt.executeUpdate("USE studentmanager");
            
            // Create users table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role ENUM('admin', 'lecturer', 'student', 'guardian') NOT NULL
                )
            """);
            
            // Create students table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students (
                    student_id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    username VARCHAR(50),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
            """);
            
            // Create lecturers table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS lecturers (
                    lecturer_id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    username VARCHAR(50),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
            """);
            
            // Create guardians table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS guardians (
                    guardian_id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    username VARCHAR(50),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
            """);
            
            // Create courses table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS courses (
                    course_id VARCHAR(50) PRIMARY KEY,
                    course_name VARCHAR(100) NOT NULL,
                    code VARCHAR(50) NOT NULL,
                    lecturer_id VARCHAR(50),
                    FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id) ON DELETE SET NULL
                )
            """);
            
            // Create student_courses junction table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS student_courses (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    student_id VARCHAR(50) NOT NULL,
                    course_id VARCHAR(50) NOT NULL,
                    UNIQUE KEY unique_enrollment (student_id, course_id),
                    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
                )
            """);
            
            // Create guardian_students junction table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS guardian_students (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    guardian_id VARCHAR(50) NOT NULL,
                    student_id VARCHAR(50) NOT NULL,
                    UNIQUE KEY unique_link (guardian_id, student_id),
                    FOREIGN KEY (guardian_id) REFERENCES guardians(guardian_id) ON DELETE CASCADE,
                    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
                )
            """);
            
            // Create marks table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS marks (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    student_id VARCHAR(50) NOT NULL,
                    course_id VARCHAR(50) NOT NULL,
                    cat1_score DOUBLE,
                    cat2_score DOUBLE,
                    cat3_score DOUBLE,
                    final_exam_score DOUBLE,
                    overall_grade VARCHAR(10),
                    UNIQUE KEY unique_mark (student_id, course_id),
                    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
                )
            """);
            
            // Create attendance table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS attendance (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    student_id VARCHAR(50) NOT NULL,
                    course_id VARCHAR(50) NOT NULL,
                    date DATE NOT NULL,
                    status VARCHAR(20) NOT NULL,
                    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
                )
            """);
            
            // Create fees table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS fees (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    student_id VARCHAR(50) NOT NULL UNIQUE,
                    amount DOUBLE NOT NULL,
                    paid_amount DOUBLE DEFAULT 0,
                    balance DOUBLE,
                    due_date DATE,
                    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
                )
            """);
            
            System.out.println("Database schema initialized successfully.");
        }
    }
}

