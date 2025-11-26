package dbEndpoints;

import databaseConfig.Connector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class gradingPoints {
    private final Connector connector;

    public gradingPoints() {
        this.connector = new Connector();
    }

    public String getPolicyJson(String courseCode, String instructorId, String semester) {
        String sql = """
            SELECT grading_policy
            FROM users.coursebreakdown 
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

        // 1. CHECK: Does a policy already exist for this combination?
        String checkSql = "SELECT policy_id FROM users.coursebreakdown WHERE course_code = ? AND instructor_id = ? AND semester = ?";

        boolean exists = false;

        try (Connection conn = connector.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, courseCode);
            checkStmt.setString(2, instructorId);
            checkStmt.setString(3, semester);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                }
            }

            // 2. ACT: Update or Insert based on the check
            if (exists) {
                // --- UPDATE EXISTING ---
                String updateSql = """
                     UPDATE users.coursebreakdown 
                     SET grading_policy = ?, course_name = ?
                     WHERE course_code = ? AND instructor_id = ? AND semester = ?
                 """;
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, jsonPolicy);
                    updateStmt.setString(2, courseName); // Update name just in case
                    updateStmt.setString(3, courseCode);
                    updateStmt.setString(4, instructorId);
                    updateStmt.setString(5, semester);
                    updateStmt.executeUpdate();
                }
            } else {
                // --- INSERT NEW ---
                String insertSql = """
                     INSERT INTO users.coursebreakdown 
                     (course_code, course_name, instructor_id, semester, grading_policy)
                     VALUES (?, ?, ?, ?, ?)
                 """;
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, courseCode);
                    insertStmt.setString(2, courseName);
                    insertStmt.setString(3, instructorId);
                    insertStmt.setString(4, semester);
                    insertStmt.setString(5, jsonPolicy);
                    insertStmt.executeUpdate();
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
