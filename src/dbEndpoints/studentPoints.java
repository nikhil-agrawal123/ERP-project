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

        // This single, efficient query gets all data at once.
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
}

