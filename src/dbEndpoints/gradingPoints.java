package dbEndpoints;

import databaseConfig.Connector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class gradingPoints {
    private Connector connector;

    public gradingPoints() {
        this.connector = new Connector();
    }

    public String getPolicyJson(String courseCode, String instructorId, String semester) {
        String sql = """
            SELECT grading_policy
            FROM users.gradingpolicy 
            WHERE course_code = ? AND instructor_id = ? AND semester = ?
        """;

        try (Connection conn = connector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseCode);
            pstmt.setString(2, instructorId);
            pstmt.setString(3, semester);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("grading_policy");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No policy found
    }

    public boolean savePolicyJson(String courseCode, String courseName, String instructorId, String semester, String jsonPolicy) {

        String insertSql = """
            INSERT INTO users.gradingpolicy 
            (course_code, course_name, instructor_id, semester, grading_policy)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE grading_policy = VALUES(grading_policy)
        """;

        try (Connection conn = connector.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            pstmt.setString(1, courseCode);
            pstmt.setString(2, courseName);
            pstmt.setString(3, instructorId);
            pstmt.setString(4, semester);
            pstmt.setString(5, jsonPolicy);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
