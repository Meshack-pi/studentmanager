package com.example.studentmanager.model;

import com.example.studentmanager.database.DatabaseConfig;
import com.example.studentmanager.database.DatabaseSchema;
import com.example.studentmanager.util.PasswordUtil;
import java.sql.*;
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
    private String currentUsername;
    
    private DataManager() {
        try {
            DatabaseSchema.initializeDatabase();
            loadData();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            initializeEmptyLists();
        }
    }
    
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    private void initializeEmptyLists() {
        users = new ArrayList<>();
        students = new ArrayList<>();
        lecturers = new ArrayList<>();
        guardians = new ArrayList<>();
        courses = new ArrayList<>();
        marks = new ArrayList<>();
        attendance = new ArrayList<>();
        fees = new ArrayList<>();
    }
    
    private void loadData() {
        users = loadUsers();
        students = loadStudents();
        lecturers = loadLecturers();
        guardians = loadGuardians();
        courses = loadCourses();
        marks = loadMarks();
        attendance = loadAttendance();
        fees = loadFees();
    }
    
    public void saveAll() {
        loadData();
    }
    
    // USER METHODS
    private List<User> loadUsers() {
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username, password, role FROM users")) {
            while (rs.next()) {
                User u = new User();
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setRole(rs.getString("role"));
                list.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return list;
    }
    
    public List<User> getUsers() { return users; }
    
    public User getUserByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }
    
    public void addUser(User user) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
            users.add(user);
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }
    
    // STUDENT METHODS
    private List<Student> loadStudents() {
        List<Student> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT student_id, name, email, username FROM students")) {
            while (rs.next()) {
                Student s = new Student();
                s.setStudentId(rs.getString("student_id"));
                s.setName(rs.getString("name"));
                s.setEmail(rs.getString("email"));
                s.setUsername(rs.getString("username"));
                s.setEnrolledCourseIds(loadEnrolledCourses(conn, s.getStudentId()));
                list.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
        return list;
    }
    
    private List<String> loadEnrolledCourses(Connection conn, String studentId) throws SQLException {
        List<String> courseIds = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT course_id FROM student_courses WHERE student_id = ?")) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courseIds.add(rs.getString("course_id"));
                }
            }
        }
        return courseIds;
    }
    
    public List<Student> getStudents() { return students; }
    
    public Student getStudentById(String studentId) {
        return students.stream().filter(s -> s.getStudentId().equals(studentId)).findFirst().orElse(null);
    }
    
    public Student getStudentByUsername(String username) {
        return students.stream().filter(s -> s.getUsername().equals(username)).findFirst().orElse(null);
    }
    
    public void addStudent(Student student) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO students (student_id, name, email, username) VALUES (?, ?, ?, ?)")) {
                pstmt.setString(1, student.getStudentId());
                pstmt.setString(2, student.getName());
                pstmt.setString(3, student.getEmail());
                pstmt.setString(4, student.getUsername());
                pstmt.executeUpdate();
            }
            if (student.getEnrolledCourseIds() != null && !student.getEnrolledCourseIds().isEmpty()) {
                try (PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT IGNORE INTO student_courses (student_id, course_id) VALUES (?, ?)")) {
                    for (String courseId : student.getEnrolledCourseIds()) {
                        pstmt.setString(1, student.getStudentId());
                        pstmt.setString(2, courseId);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }
            students.add(student);
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
        }
    }
    
    // LECTURER METHODS
    private List<Lecturer> loadLecturers() {
        List<Lecturer> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT lecturer_id, name, email, username FROM lecturers")) {
            while (rs.next()) {
                Lecturer l = new Lecturer();
                l.setLecturerId(rs.getString("lecturer_id"));
                l.setName(rs.getString("name"));
                l.setEmail(rs.getString("email"));
                l.setUsername(rs.getString("username"));
                l.setAssignedCourseIds(loadAssignedCourses(conn, l.getLecturerId()));
                list.add(l);
            }
        } catch (SQLException e) {
            System.err.println("Error loading lecturers: " + e.getMessage());
        }
        return list;
    }
    
    private List<String> loadAssignedCourses(Connection conn, String lecturerId) throws SQLException {
        List<String> courseIds = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT course_id FROM courses WHERE lecturer_id = ?")) {
            pstmt.setString(1, lecturerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courseIds.add(rs.getString("course_id"));
                }
            }
        }
        return courseIds;
    }
    
    public List<Lecturer> getLecturers() { return lecturers; }
    
    public Lecturer getLecturerById(String lecturerId) {
        return lecturers.stream().filter(l -> l.getLecturerId().equals(lecturerId)).findFirst().orElse(null);
    }
    
    public Lecturer getLecturerByUsername(String username) {
        return lecturers.stream().filter(l -> l.getUsername().equals(username)).findFirst().orElse(null);
    }
    
    public void addLecturer(Lecturer lecturer) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO lecturers (lecturer_id, name, email, username) VALUES (?, ?, ?, ?)")) {
            pstmt.setString(1, lecturer.getLecturerId());
            pstmt.setString(2, lecturer.getName());
            pstmt.setString(3, lecturer.getEmail());
            pstmt.setString(4, lecturer.getUsername());
            pstmt.executeUpdate();
            lecturers.add(lecturer);
        } catch (SQLException e) {
            System.err.println("Error adding lecturer: " + e.getMessage());
        }
    }
    
    // GUARDIAN METHODS
    private List<Guardian> loadGuardians() {
        List<Guardian> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT guardian_id, name, email, username FROM guardians")) {
            while (rs.next()) {
                Guardian g = new Guardian();
                g.setGuardianId(rs.getString("guardian_id"));
                g.setName(rs.getString("name"));
                g.setEmail(rs.getString("email"));
                g.setUsername(rs.getString("username"));
                g.setLinkedStudentIds(loadLinkedStudents(conn, g.getGuardianId()));
                list.add(g);
            }
        } catch (SQLException e) {
            System.err.println("Error loading guardians: " + e.getMessage());
        }
        return list;
    }
    
    private List<String> loadLinkedStudents(Connection conn, String guardianId) throws SQLException {
        List<String> studentIds = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT student_id FROM guardian_students WHERE guardian_id = ?")) {
            pstmt.setString(1, guardianId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    studentIds.add(rs.getString("student_id"));
                }
            }
        }
        return studentIds;
    }
    
    public List<Guardian> getGuardians() { return guardians; }
    
    public Guardian getGuardianById(String guardianId) {
        return guardians.stream().filter(g -> g.getGuardianId().equals(guardianId)).findFirst().orElse(null);
    }
    
    public Guardian getGuardianByUsername(String username) {
        return guardians.stream().filter(g -> g.getUsername().equals(username)).findFirst().orElse(null);
    }
    
    public void addGuardian(Guardian guardian) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO guardians (guardian_id, name, email, username) VALUES (?, ?, ?, ?)")) {
                pstmt.setString(1, guardian.getGuardianId());
                pstmt.setString(2, guardian.getName());
                pstmt.setString(3, guardian.getEmail());
                pstmt.setString(4, guardian.getUsername());
                pstmt.executeUpdate();
            }
            if (guardian.getLinkedStudentIds() != null && !guardian.getLinkedStudentIds().isEmpty()) {
                try (PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT IGNORE INTO guardian_students (guardian_id, student_id) VALUES (?, ?)")) {
                    for (String studentId : guardian.getLinkedStudentIds()) {
                        pstmt.setString(1, guardian.getGuardianId());
                        pstmt.setString(2, studentId);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }
            guardians.add(guardian);
        } catch (SQLException e) {
            System.err.println("Error adding guardian: " + e.getMessage());
        }
    }
    
    // COURSE METHODS
    private List<Course> loadCourses() {
        List<Course> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT course_id, course_name, code, lecturer_id FROM courses")) {
            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getString("course_id"));
                c.setCourseName(rs.getString("course_name"));
                c.setCode(rs.getString("code"));
                c.setLecturerId(rs.getString("lecturer_id"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
        return list;
    }
    
    public List<Course> getCourses() { return courses; }
    
    public Course getCourseById(String courseId) {
        return courses.stream().filter(c -> c.getCourseId().equals(courseId)).findFirst().orElse(null);
    }
    
    public void addCourse(Course course) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO courses (course_id, course_name, code, lecturer_id) VALUES (?, ?, ?, ?)")) {
            pstmt.setString(1, course.getCourseId());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getCode());
            pstmt.setString(4, course.getLecturerId());
            pstmt.executeUpdate();
            courses.add(course);
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }
    
    // MARK METHODS
    private List<Mark> loadMarks() {
        List<Mark> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT student_id, course_id, cat1_score, cat2_score, cat3_score, final_exam_score, overall_grade FROM marks")) {
            while (rs.next()) {
                Mark m = new Mark();
                m.setStudentId(rs.getString("student_id"));
                m.setCourseId(rs.getString("course_id"));
                m.setCat1Score(getNullableDouble(rs, "cat1_score"));
                m.setCat2Score(getNullableDouble(rs, "cat2_score"));
                m.setCat3Score(getNullableDouble(rs, "cat3_score"));
                m.setFinalExamScore(getNullableDouble(rs, "final_exam_score"));
                m.setOverallGrade(rs.getString("overall_grade"));
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error loading marks: " + e.getMessage());
        }
        return list;
    }
    
    private Double getNullableDouble(ResultSet rs, String column) throws SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }
    
    public List<Mark> getMarks() { return marks; }
    
    public List<Mark> getMarksByStudentId(String studentId) {
        return marks.stream().filter(m -> m.getStudentId().equals(studentId)).collect(Collectors.toList());
    }
    
    public Mark getMarkByStudentAndCourse(String studentId, String courseId) {
        return marks.stream()
            .filter(m -> m.getStudentId().equals(studentId) && m.getCourseId().equals(courseId))
            .findFirst().orElse(null);
    }
    
    public void addOrUpdateMark(Mark mark) {
        mark.calculateOverallGrade();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO marks (student_id, course_id, cat1_score, cat2_score, cat3_score, final_exam_score, overall_grade) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                 "ON DUPLICATE KEY UPDATE " +
                 "cat1_score = VALUES(cat1_score), cat2_score = VALUES(cat2_score), " +
                 "cat3_score = VALUES(cat3_score), final_exam_score = VALUES(final_exam_score), " +
                 "overall_grade = VALUES(overall_grade)")) {
            pstmt.setString(1, mark.getStudentId());
            pstmt.setString(2, mark.getCourseId());
            pstmt.setObject(3, mark.getCat1Score(), Types.DOUBLE);
            pstmt.setObject(4, mark.getCat2Score(), Types.DOUBLE);
            pstmt.setObject(5, mark.getCat3Score(), Types.DOUBLE);
            pstmt.setObject(6, mark.getFinalExamScore(), Types.DOUBLE);
            pstmt.setString(7, mark.getOverallGrade());
            pstmt.executeUpdate();
            loadData();
        } catch (SQLException e) {
            System.err.println("Error adding/updating mark: " + e.getMessage());
        }
    }
    
    // ATTENDANCE METHODS
    private List<Attendance> loadAttendance() {
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT student_id, course_id, date, status FROM attendance")) {
            while (rs.next()) {
                Attendance a = new Attendance();
                a.setStudentId(rs.getString("student_id"));
                a.setCourseId(rs.getString("course_id"));
                a.setDate(rs.getDate("date").toLocalDate());
                a.setStatus(rs.getString("status"));
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error loading attendance: " + e.getMessage());
        }
        return list;
    }
    
    public List<Attendance> getAttendance() { return attendance; }
    
    public List<Attendance> getAttendanceByStudentAndCourse(String studentId, String courseId) {
        return attendance.stream()
            .filter(a -> a.getStudentId().equals(studentId) && a.getCourseId().equals(courseId))
            .collect(Collectors.toList());
    }
    
    public void addAttendance(Attendance att) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO attendance (student_id, course_id, date, status) VALUES (?, ?, ?, ?)")) {
            pstmt.setString(1, att.getStudentId());
            pstmt.setString(2, att.getCourseId());
            pstmt.setDate(3, Date.valueOf(att.getDate()));
            pstmt.setString(4, att.getStatus());
            pstmt.executeUpdate();
            attendance.add(att);
        } catch (SQLException e) {
            System.err.println("Error adding attendance: " + e.getMessage());
        }
    }
    
    // FEE METHODS
    private List<Fee> loadFees() {
        List<Fee> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT student_id, amount, paid_amount, balance, due_date FROM fees")) {
            while (rs.next()) {
                Fee f = new Fee();
                f.setStudentId(rs.getString("student_id"));
                f.setAmount(rs.getDouble("amount"));
                f.setPaidAmount(rs.getDouble("paid_amount"));
                f.setBalance(rs.getDouble("balance"));
                Date dueDate = rs.getDate("due_date");
                if (dueDate != null) {
                    f.setDueDate(dueDate.toLocalDate());
                }
                list.add(f);
            }
        } catch (SQLException e) {
            System.err.println("Error loading fees: " + e.getMessage());
        }
        return list;
    }
    
    public List<Fee> getFees() { return fees; }
    
    public Fee getFeeByStudentId(String studentId) {
        return fees.stream().filter(f -> f.getStudentId().equals(studentId)).findFirst().orElse(null);
    }
    
    public void addOrUpdateFee(Fee fee) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO fees (student_id, amount, paid_amount, balance, due_date) " +
                 "VALUES (?, ?, ?, ?, ?) " +
                 "ON DUPLICATE KEY UPDATE " +
                 "amount = VALUES(amount), paid_amount = VALUES(paid_amount), " +
                 "balance = VALUES(balance), due_date = VALUES(due_date)")) {
            pstmt.setString(1, fee.getStudentId());
            pstmt.setDouble(2, fee.getAmount());
            pstmt.setDouble(3, fee.getPaidAmount() != null ? fee.getPaidAmount() : 0.0);
            pstmt.setDouble(4, fee.getBalance() != null ? fee.getBalance() : fee.getAmount());
            if (fee.getDueDate() != null) {
                pstmt.setDate(5, Date.valueOf(fee.getDueDate()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            pstmt.executeUpdate();
            loadData();
        } catch (SQLException e) {
            System.err.println("Error adding/updating fee: " + e.getMessage());
        }
    }
    
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }
    
    public String getCurrentUsername() {
        return currentUsername;
    }
}
