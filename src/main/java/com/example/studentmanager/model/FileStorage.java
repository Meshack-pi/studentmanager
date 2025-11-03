package com.example.studentmanager.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final String DATA_DIR = "data";
    private static final Gson gson;
    
    static {
        // Configure Gson with LocalDate adapter
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        gson = builder.create();
    }
    
    // Ensure data directory exists
    private static void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    // Generic save method
    public static <T> void saveList(String filename, List<T> list) {
        ensureDataDir();
        File file = new File(DATA_DIR, filename);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            System.err.println("Error saving " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Generic load method
    public static <T> List<T> loadList(String filename, Type type) {
        ensureDataDir();
        File file = new File(DATA_DIR, filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.err.println("Error loading " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Load Users
    public static List<User> loadUsers() {
        Type type = new TypeToken<List<User>>(){}.getType();
        return loadList("users.json", type);
    }
    
    // Save Users
    public static void saveUsers(List<User> users) {
        saveList("users.json", users);
    }
    
    // Load Students
    public static List<Student> loadStudents() {
        Type type = new TypeToken<List<Student>>(){}.getType();
        return loadList("students.json", type);
    }
    
    // Save Students
    public static void saveStudents(List<Student> students) {
        saveList("students.json", students);
    }
    
    // Load Lecturers
    public static List<Lecturer> loadLecturers() {
        Type type = new TypeToken<List<Lecturer>>(){}.getType();
        return loadList("lecturers.json", type);
    }
    
    // Save Lecturers
    public static void saveLecturers(List<Lecturer> lecturers) {
        saveList("lecturers.json", lecturers);
    }
    
    // Load Guardians
    public static List<Guardian> loadGuardians() {
        Type type = new TypeToken<List<Guardian>>(){}.getType();
        return loadList("guardians.json", type);
    }
    
    // Save Guardians
    public static void saveGuardians(List<Guardian> guardians) {
        saveList("guardians.json", guardians);
    }
    
    // Load Courses
    public static List<Course> loadCourses() {
        Type type = new TypeToken<List<Course>>(){}.getType();
        return loadList("courses.json", type);
    }
    
    // Save Courses
    public static void saveCourses(List<Course> courses) {
        saveList("courses.json", courses);
    }
    
    // Load Marks
    public static List<Mark> loadMarks() {
        Type type = new TypeToken<List<Mark>>(){}.getType();
        return loadList("marks.json", type);
    }
    
    // Save Marks
    public static void saveMarks(List<Mark> marks) {
        saveList("marks.json", marks);
    }
    
    // Load Attendance
    public static List<Attendance> loadAttendance() {
        Type type = new TypeToken<List<Attendance>>(){}.getType();
        return loadList("attendance.json", type);
    }
    
    // Save Attendance
    public static void saveAttendance(List<Attendance> attendance) {
        saveList("attendance.json", attendance);
    }
    
    // Load Fees
    public static List<Fee> loadFees() {
        Type type = new TypeToken<List<Fee>>(){}.getType();
        return loadList("fees.json", type);
    }
    
    // Save Fees
    public static void saveFees(List<Fee> fees) {
        saveList("fees.json", fees);
    }
}

