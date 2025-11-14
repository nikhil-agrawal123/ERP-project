package dbEndpoints;

import databaseConfig.Connector;
import dbClasses.*;
import java.util.*;
import java.sql.*;

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

    public List<studentAvailableCourses> AllCourses(String sem) throws SQLException {
        List<studentAvailableCourses> allCourses = new ArrayList<>();

        String sql = "SELECT * FROM USERS.courses WHERE semester =?";

        try (Connection connection = connector.connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sem);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                studentAvailableCourses course = new studentAvailableCourses(
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getInt("credits"),
                        rs.getString("offeredBy"),
                        rs.getString("semester")
                );
                allCourses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  allCourses;
    }
}

