package dbEndpoints;

import databaseConfig.Connector;
import dbClasses.logClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class logPoints {

    private Connector dbConnector;

    public logPoints() {
        this.dbConnector = new Connector();
    }

    /**
     * Record an action in the database.
     */
    public void logAction(String userId, String actionType, String description) {
        String sql = "INSERT INTO users.audit_logs (user_id, action_type, description) VALUES (?, ?, ?)";

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, actionType);
            pstmt.setString(3, description);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }

    /**
     * Fetch all logs for the Admin View (Most recent first).
     */
    public List<logClass> getAllLogs() {
        List<logClass> logs = new ArrayList<>();
        String sql = "SELECT * FROM users.audit_logs ORDER BY timestamp DESC";

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                logs.add(new logClass(
                        rs.getString("user_id"),
                        rs.getString("action_type"),
                        rs.getString("description"),
                        rs.getTimestamp("timestamp")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}