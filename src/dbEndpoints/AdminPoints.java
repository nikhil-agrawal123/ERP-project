package dbEndpoints;

import databaseConfig.Connector;
import dbClasses.*;

import dependancy.org.mindrot.jbcrypt.BCrypt;
import middleware.facultyService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminPoints {
    private Connector connector;

    public AdminPoints() {
        this.connector = new  Connector();
    }

    public boolean addStudent(NewStudent user) throws SQLException {
        String sql = "INSERT INTO users.students (student_roll_no, full_name,program,enrollment_year, student_email,  currentSem,user_id) VALUES (?,?,?,?,?,?,?)";
        String sqlAuth = "INSERT INTO auth.studentAuth (studentId, studentPass) VALUES (?, ?)";

        Connection conn = null;

        try{

             conn = connector.connect();
            conn.setAutoCommit(false);

            try(PreparedStatement pstm = conn.prepareStatement(sql)){
                pstm.setString(1, user.getStudent_roll_no());
                pstm.setString(2, user.getStudent_name());
                pstm.setString(3, user.getStudent_program());
                pstm.setInt(4, user.getStudent_enrollment_year());
                pstm.setString(5, user.getStudent_email());
                pstm.setInt(6,user.getStudent_current_sem());
                pstm.setString(7,user.getStudent_id());
                pstm.executeUpdate();
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlAuth)) {
                String defaultPassword = user.getStudent_roll_no();
                String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());

                pstmt2.setString(1, user.getStudent_id());
                pstmt2.setString(2, hashedPassword);

                pstmt2.executeUpdate();
            }
            conn.commit();
            return true;
        }

        catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.out.println("Rolling back transaction due to error...");
                    conn.rollback(); // Undo insertions if anything failed
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        }finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restore default
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean addFaculty(AddFaculty faculty) {
        String sqlProfile = "INSERT INTO users.instructors (user_id, instructor_id, full_name, department, email) VALUES (?, ?, ?, ?, ?)";

        String sqlAuth = "INSERT INTO auth.facultyAuth (facultyId, facultyPass) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = connector.connect();
            conn.setAutoCommit(false);

            // 1. Insert Profile
            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlProfile)) {
                pstmt1.setString(1, faculty.getUserId());
                pstmt1.setString(2, faculty.getInstructorId());
                pstmt1.setString(3, faculty.getFullName());
                pstmt1.setString(4, faculty.getDepartment());
                pstmt1.setString(5, faculty.getEmail());
                pstmt1.executeUpdate();
            }

            // 2. Insert Auth
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlAuth)) {
                // Default password is the Instructor ID
                String defaultPassword = faculty.getInstructorId();
                String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());

                pstmt2.setString(1, faculty.getUserId());
                pstmt2.setString(2, hashedPassword);
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean addAdmin(AddAdmin admin) {
        String sqlProfile = "INSERT INTO users.admins ( admin_id, full_name, role, email) VALUES ( ?,?, ?, ?, ?)";

        String sqlAuth = "INSERT INTO auth.adminAuth (adminId, adminPass) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = connector.connect();
            conn.setAutoCommit(false);

            // 1. Insert Profile
            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlProfile)) {
                pstmt1.setString(1, admin.getAdminId());
                pstmt1.setString(2, admin.getUserId());
                pstmt1.setString(3, admin.getFullName());
                pstmt1.setString(4, admin.getRole());
                pstmt1.setString(5, admin.getEmail());
                pstmt1.executeUpdate();
            }

            // 2. Insert Auth
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlAuth)) {
                // Default password is the Admin ID
                String defaultPassword = admin.getAdminId();
                String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());

                pstmt2.setString(1, admin.getAdminId());
                pstmt2.setString(2, hashedPassword);
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean addCourseAndSection(AddCourse data) {
        // 1. Insert into Catalog (Updated with Department)
        String sqlCourse = "INSERT IGNORE INTO users.courses (course_code, course_title, credits,semester,currenCap,offeredBy) VALUES (?, ?, ?,?,?,?)";

        String sqlSection = "INSERT INTO users.sections (course_code, instructor_id, semester, year, capacity, department) VALUES (?, ?, ?, ?, ?, ?)";

        String instructorName = new facultyService().getFullNmae(data.getInstructorId());

        Connection conn = null;
        try {
            conn = connector.connect();
            conn.setAutoCommit(false); // Start Transaction

            // Insert Course
            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlCourse)) {
                pstmt1.setString(1, data.getCourseCode());
                pstmt1.setString(2, data.getCourseTitle());
                pstmt1.setInt(3, data.getCredits());
                pstmt1.setString(4, data.getSemester()+data.getYear());
                pstmt1.setInt(5, 0);
                pstmt1.setString(6, instructorName);
                pstmt1.executeUpdate();
            }

            // Insert Section
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlSection)) {
                pstmt2.setString(1, data.getCourseCode());
                pstmt2.setString(2, data.getInstructorId());
                pstmt2.setString(3, data.getSemester());
                pstmt2.setInt(4, data.getYear());
                pstmt2.setInt(5, data.getCapacity());
                pstmt2.setString(6, data.getDepartment()); // Set Dept
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
