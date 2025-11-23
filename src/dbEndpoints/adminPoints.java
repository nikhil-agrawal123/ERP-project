package dbEndpoints;

import databaseConfig.Connector;
import dbClasses.NewStudent;
import dbClasses.AddFaculty;

import dependancy.org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class adminPoints {
    private Connector connector;

    public adminPoints() {
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
        // SQL for the profile database
        String sqlProfile = "INSERT INTO users.instructors (user_id, instructor_id, full_name, department, email) VALUES (?, ?, ?, ?, ?)";

        // SQL for the auth database (Assuming table is auth.facultyAuth)
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
}
