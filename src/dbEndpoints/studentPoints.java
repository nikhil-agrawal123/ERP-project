package dbEndpoints;

import databaseConfig.Connector;
import dbClasses.*;
import java.util.*;
import java.sql.*;

import java.time.LocalDate;

public class studentPoints {
    private final Connector connector;

    public studentPoints() {
        this.connector = new Connector();
    }

    public List<StudentRegisteredCourse> findCoursesByStudent(String username) throws SQLException {
        List<StudentRegisteredCourse> allCourses = new ArrayList<>();

        String sql = """
            SELECT
                e.semester,
                e.course_code,
                e.course_name,
                e.course_credits,
                e.gradePoint,
                c.offeredBy
            FROM
                users.enrollments e
            JOIN
                users.courses c ON e.course_code = c.course_code
            WHERE
                e.student_id = ?
            ORDER BY
                e.semester, e.course_name
        """;

        try (Connection connection = connector.connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Create the data object from the query results
                StudentRegisteredCourse course = new StudentRegisteredCourse(
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("course_credits"),
                        rs.getString("offeredBy"),
                        rs.getDouble("gradePoint"),
                        rs.getInt("semester")

                );
                allCourses.add(course);
            }
        }

        return allCourses;
    }

    public List<StudentCgCredits> getCgCreditsByStudent(String username) throws SQLException {
        List<StudentCgCredits> allCgCredits = new ArrayList<>();
        String sql = "SELECT g.score, c.credits " +
                "FROM users.grades g " +
                "JOIN users.courses c ON g.course_code = c.course_code " +
                "WHERE g.student_roll_no = ?";
        try (Connection connection = connector.connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                StudentCgCredits c = new StudentCgCredits(
                        rs.getInt("credits"),
                        rs.getDouble("score")
                );
                allCgCredits.add(c);
            }
        }
        return allCgCredits;
    }

    public List<studentAvailableCourses> AllCourses(String sem) throws  SQLException {
        List<studentAvailableCourses> allCourses = new ArrayList<>();

        String sql = """
            SELECT 
                s.semester,
                c.course_code, 
                c.course_title, 
                c.credits, 
                c.offeredBy,
                s.capacity,
                (SELECT COUNT(*) FROM users.enrollments e WHERE e.section_id = s.section_id) AS enrolled_count
            FROM 
                users.sections s
            JOIN 
                users.courses c ON s.course_code = c.course_code
            WHERE 
                s.semester = ?
        """;

        try (Connection connection = connector.connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, sem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // --- THIS IS THE CORRECTED CONSTRUCTOR ---
                // It now passes all 8 arguments required by your data class
                studentAvailableCourses course = new studentAvailableCourses(
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getInt("credits"),
                        rs.getString("offeredBy"),
                        rs.getString("semester"),
                        rs.getInt("capacity"),
                        rs.getInt("enrolled_count")
                );
                allCourses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allCourses;
    }

    public boolean UpdateRegisteredCourses(List<studentAvailableCourses> selectedCourses, String studentId) {

        Map<String,String> system = new SystemSettings().getRegistrationDates();
        String start = system.get("reg_start");
        String end = system.get("reg_end");

        if (start != null && end != null) {
            try {
                LocalDate startD = LocalDate.parse(start); // Format yyyy-MM-dd
                LocalDate endD = LocalDate.parse(end);
                LocalDate today = LocalDate.now();

                if (today.isAfter(startD) || today.isBefore(endD)) {
                    System.out.println("Registration Blocked: Outside window (" + start + " to " + end + ")");
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Error parsing registration dates: " + e.getMessage());
            }
        }

        String studentName = "";
        int sem = 0;
        String nameSql = "SELECT full_name FROM users.students WHERE user_id = ?";

        try (Connection conn = connector.connect();
             PreparedStatement namePstmt = conn.prepareStatement(nameSql)) {

            namePstmt.setString(1, studentId);
            try (ResultSet rs = namePstmt.executeQuery()) {
                if (rs.next()) {
                    studentName = rs.getString("full_name");
                } else {
                    throw new SQLException("Student not found: " + studentId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Failed to get the student's name
        }

        String Semsql = "SELECT MAX(semester) FROM users.enrollments WHERE student_id = ?";
        try (Connection conn = connector.connect();
            PreparedStatement pstm = conn.prepareStatement(Semsql)
        ){
            pstm.setString(1, studentId);
            ResultSet rs = pstm.executeQuery();
            if(rs.next()){
                sem = rs.getInt(1);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        String sql = "INSERT INTO users.enrollments " +
                "(student_id, student_name, course_code, course_name, course_credits, semester, completion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = connector.connect();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (studentAvailableCourses course : selectedCourses) {
                    pstmt.setString(1, studentId);
                    pstmt.setString(2, studentName);
                    pstmt.setString(3, course.getCourse_code());
                    pstmt.setString(4, course.getCourse_name());
                    pstmt.setInt(5, course.getCourse_credits());
                    pstmt.setInt(6, sem);
                    pstmt.setBoolean(7, false);

                    pstmt.addBatch();
                }

                pstmt.executeBatch();

                conn.commit();

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // If anything fails, roll back all changes
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restore default behavior
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int Check(String userid, String course){
        String sql = """
            SELECT COUNT(*)
            FROM users.enrollments
            WHERE student_id = ? AND course_code = ?
        """;

        try (Connection conn = connector.connect();
             PreparedStatement pstm = conn.prepareStatement(sql)
        ){
            pstm.setString(1, userid);
            pstm.setString(2, course);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public void incrementAllStudentSemesters() throws SQLException {
        // Simply add 1 to the current_semester for every student
        String sql = "UPDATE users.students SET currentSem = currentSem + 1";

        try (PreparedStatement pstmt = connector.connect().prepareStatement(sql)) {
            int rows = pstmt.executeUpdate();
            System.out.println("Promoted " + rows + " students to next semester.");
        }
    }

    public void updateSemesterName(String semName) throws SQLException {
        String sql = "UPDATE users.students SET semester_name = ?";

        try(PreparedStatement pstm = connector.connect().prepareStatement(sql)){
            pstm.setString(1, semName);
            int rows = pstm.executeUpdate();
            System.out.println("Promoted " + rows + " students to next semester.");
        }
    }

    public void updateCurrentYear(int currentYear) throws SQLException {
        String sql = "UPDATE users.students SET current_year = ?";
        try(PreparedStatement pstm = connector.connect().prepareStatement(sql)){
            pstm.setInt(1, currentYear);
            int rows = pstm.executeUpdate();
            System.out.println("Promoted " + rows + " students to next year.");
        }
    }

    public String getStudentProgram(String rollNumber) throws SQLException {
        String program = "";
        // Query assumes your table is users.students and column is student_roll_no
        String sql = "SELECT program FROM users.students WHERE student_roll_no = ?";

        try (Connection connection = connector.connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, rollNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                program = rs.getString("program");
            }
        }
        return program;
    }
}

