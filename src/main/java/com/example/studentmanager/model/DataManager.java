package com.example.studentmanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;
    private List<User> users;
    private List<Student> students;
    private List<Lecturer> lecturers;
    private List<Guardian> guardians;
    private List<Course> courses;
    private List<Mark> marks;
    private List<Attendance> attendance;
    private List<Fee> fees;
    private String currentUsername; // Track current logged-in user
    
    private DataManager() {
        loadData();
        if (users.isEmpty()) {
            initializeSampleData();
            saveAll();
        }
    }
    
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    private void loadData() {
        users = FileStorage.loadUsers();
        students = FileStorage.loadStudents();
        lecturers = FileStorage.loadLecturers();
        guardians = FileStorage.loadGuardians();
        courses = FileStorage.loadCourses();
        marks = FileStorage.loadMarks();
        attendance = FileStorage.loadAttendance();
        fees = FileStorage.loadFees();
    }
    
    public void saveAll() {
        FileStorage.saveUsers(users);
        FileStorage.saveStudents(students);
        FileStorage.saveLecturers(lecturers);
        FileStorage.saveGuardians(guardians);
        FileStorage.saveCourses(courses);
        FileStorage.saveMarks(marks);
        FileStorage.saveAttendance(attendance);
        FileStorage.saveFees(fees);
    }
    
    private void initializeSampleData() {
        // Sample Users
        users = new ArrayList<>();
        users.add(new User("admin", "admin123", "admin"));
        users.add(new User("lecturer1", "1234", "lecturer"));
        users.add(new User("lecturer2", "1234", "lecturer"));
        users.add(new User("student1", "abcd", "student"));
        users.add(new User("student2", "abcd", "student"));
        users.add(new User("guardian1", "guard123", "guardian"));
        
        // Sample Students
        students = new ArrayList<>();
        students.add(new Student("ST001", "John Doe", "john@example.com", "student1"));
        students.add(new Student("ST002", "Jane Smith", "jane@example.com", "student2"));
        
        // Sample Lecturers
        lecturers = new ArrayList<>();
        lecturers.add(new Lecturer("LEC001", "Dr. Alice Brown", "alice@example.com", "lecturer1"));
        lecturers.add(new Lecturer("LEC002", "Prof. Bob Wilson", "bob@example.com", "lecturer2"));
        
        // Sample Guardians
        guardians = new ArrayList<>();
        Guardian g1 = new Guardian("GU001", "Mary Doe", "mary@example.com", "guardian1");
        g1.getLinkedStudentIds().add("ST001");
        guardians.add(g1);
        
        // Sample Courses
        courses = new ArrayList<>();
        courses.add(new Course("CS101", "Introduction to Computer Science", "CS101"));
        courses.add(new Course("MATH101", "Calculus I", "MATH101"));
        courses.add(new Course("ENG101", "English Composition", "ENG101"));
        
        // Assign lecturers to courses
        courses.get(0).setLecturerId("LEC001");
        courses.get(1).setLecturerId("LEC002");
        courses.get(2).setLecturerId("LEC001");
        
        // Update lecturer assignments
        lecturers.get(0).getAssignedCourseIds().add("CS101");
        lecturers.get(0).getAssignedCourseIds().add("ENG101");
        lecturers.get(1).getAssignedCourseIds().add("MATH101");
        
        // Enroll students in courses
        students.get(0).getEnrolledCourseIds().add("CS101");
        students.get(0).getEnrolledCourseIds().add("MATH101");
        students.get(1).getEnrolledCourseIds().add("CS101");
        students.get(1).getEnrolledCourseIds().add("ENG101");
        
        // Sample Marks
        marks = new ArrayList<>();
        Mark m1 = new Mark("ST001", "CS101");
        m1.setCat1Score(85.0);
        m1.setCat2Score(90.0);
        m1.setCat3Score(88.0);
        m1.setFinalExamScore(92.0);
        m1.calculateOverallGrade();
        marks.add(m1);
        
        Mark m2 = new Mark("ST001", "MATH101");
        m2.setCat1Score(78.0);
        m2.setCat2Score(82.0);
        m2.setCat3Score(80.0);
        m2.setFinalExamScore(85.0);
        m2.calculateOverallGrade();
        marks.add(m2);
        
        Mark m3 = new Mark("ST002", "CS101");
        m3.setCat1Score(90.0);
        m3.setCat2Score(88.0);
        m3.setCat3Score(91.0);
        m3.setFinalExamScore(89.0);
        m3.calculateOverallGrade();
        marks.add(m3);
        
        // Sample Attendance
        attendance = new ArrayList<>();
        attendance.add(new Attendance("ST001", "CS101", LocalDate.now().minusDays(5), "present"));
        attendance.add(new Attendance("ST001", "CS101", LocalDate.now().minusDays(3), "present"));
        attendance.add(new Attendance("ST001", "CS101", LocalDate.now().minusDays(1), "absent"));
        attendance.add(new Attendance("ST002", "CS101", LocalDate.now().minusDays(5), "present"));
        
        // Sample Fees
        fees = new ArrayList<>();
        fees.add(new Fee("ST001", 5000.0, LocalDate.now().plusMonths(1)));
        fees.add(new Fee("ST002", 5000.0, LocalDate.now().plusMonths(1)));
        fees.get(1).setPaidAmount(2000.0);
        fees.get(1).setBalance(3000.0);
    }
    
    // User methods
    public List<User> getUsers() {
        return users;
    }
    
    public User getUserByUsername(String username) {
        return users.stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
    
    public void addUser(User user) {
        users.add(user);
        saveAll();
    }
    
    // Student methods
    public List<Student> getStudents() {
        return students;
    }
    
    public Student getStudentById(String studentId) {
        return students.stream()
            .filter(s -> s.getStudentId().equals(studentId))
            .findFirst()
            .orElse(null);
    }
    
    public Student getStudentByUsername(String username) {
        return students.stream()
            .filter(s -> s.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
    
    public void addStudent(Student student) {
        students.add(student);
        saveAll();
    }
    
    // Lecturer methods
    public List<Lecturer> getLecturers() {
        return lecturers;
    }
    
    public Lecturer getLecturerById(String lecturerId) {
        return lecturers.stream()
            .filter(l -> l.getLecturerId().equals(lecturerId))
            .findFirst()
            .orElse(null);
    }
    
    public Lecturer getLecturerByUsername(String username) {
        return lecturers.stream()
            .filter(l -> l.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
    
    public void addLecturer(Lecturer lecturer) {
        lecturers.add(lecturer);
        saveAll();
    }
    
    // Guardian methods
    public List<Guardian> getGuardians() {
        return guardians;
    }
    
    public Guardian getGuardianById(String guardianId) {
        return guardians.stream()
            .filter(g -> g.getGuardianId().equals(guardianId))
            .findFirst()
            .orElse(null);
    }
    
    public Guardian getGuardianByUsername(String username) {
        return guardians.stream()
            .filter(g -> g.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
    
    public void addGuardian(Guardian guardian) {
        guardians.add(guardian);
        saveAll();
    }
    
    // Course methods
    public List<Course> getCourses() {
        return courses;
    }
    
    public Course getCourseById(String courseId) {
        return courses.stream()
            .filter(c -> c.getCourseId().equals(courseId))
            .findFirst()
            .orElse(null);
    }
    
    public void addCourse(Course course) {
        courses.add(course);
        saveAll();
    }
    
    // Mark methods
    public List<Mark> getMarks() {
        return marks;
    }
    
    public List<Mark> getMarksByStudentId(String studentId) {
        return marks.stream()
            .filter(m -> m.getStudentId().equals(studentId))
            .collect(Collectors.toList());
    }
    
    public Mark getMarkByStudentAndCourse(String studentId, String courseId) {
        return marks.stream()
            .filter(m -> m.getStudentId().equals(studentId) && m.getCourseId().equals(courseId))
            .findFirst()
            .orElse(null);
    }
    
    public void addOrUpdateMark(Mark mark) {
        Mark existing = getMarkByStudentAndCourse(mark.getStudentId(), mark.getCourseId());
        if (existing != null) {
            existing.setCat1Score(mark.getCat1Score());
            existing.setCat2Score(mark.getCat2Score());
            existing.setCat3Score(mark.getCat3Score());
            existing.setFinalExamScore(mark.getFinalExamScore());
            existing.calculateOverallGrade();
        } else {
            mark.calculateOverallGrade();
            marks.add(mark);
        }
        saveAll();
    }
    
    // Attendance methods
    public List<Attendance> getAttendance() {
        return attendance;
    }
    
    public List<Attendance> getAttendanceByStudentAndCourse(String studentId, String courseId) {
        return attendance.stream()
            .filter(a -> a.getStudentId().equals(studentId) && a.getCourseId().equals(courseId))
            .collect(Collectors.toList());
    }
    
    public void addAttendance(Attendance att) {
        attendance.add(att);
        saveAll();
    }
    
    // Fee methods
    public List<Fee> getFees() {
        return fees;
    }
    
    public Fee getFeeByStudentId(String studentId) {
        return fees.stream()
            .filter(f -> f.getStudentId().equals(studentId))
            .findFirst()
            .orElse(null);
    }
    
    public void addOrUpdateFee(Fee fee) {
        Fee existing = getFeeByStudentId(fee.getStudentId());
        if (existing != null) {
            existing.setAmount(fee.getAmount());
            existing.setPaidAmount(fee.getPaidAmount());
            existing.setDueDate(fee.getDueDate());
        } else {
            fees.add(fee);
        }
        saveAll();
    }
    
    // Current user management
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }
    
    public String getCurrentUsername() {
        return currentUsername;
    }
}

