package dbEndpoints;

import databaseConfig.Connector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;

public class SystemSettings {

    private Connector dbConnector;

    public SystemSettings() {
        this.dbConnector = new Connector();
    }

    /**
     * Updates the global semester and year settings.
     */
    public boolean updateSystemSettings(String semesterName, int year, Connection conn) throws SQLException {
        String sql = "INSERT INTO users.system_settings (setting_key, setting_value) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Update Semester Name
            pstmt.setString(1, "current_semester_name");
            pstmt.setString(2, semesterName);
            pstmt.executeUpdate();

            // Update Year
            pstmt.setString(1, "current_year");
            pstmt.setString(2, String.valueOf(year));
            pstmt.executeUpdate();
        }
        return true;
    }

    /**
     * Gets the current system info (Semester + Year).
     */
    public String getCurrentSystemSemester() {
        String sql = "SELECT setting_key, setting_value FROM users.system_settings WHERE setting_key IN ('current_semester_name', 'current_year')";
        String sem = "Unknown";
        String year = "0000";

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String key = rs.getString("setting_key");
                if ("current_semester_name".equals(key)) sem = rs.getString("setting_value");
                if ("current_year".equals(key)) year = rs.getString("setting_value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sem + " " + year;
    }

    public boolean updateRegistrationDates(String startDate, String endDate) {
        String sql = "INSERT INTO users.system_settings (setting_key, setting_value) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Update Start Date
            pstmt.setString(1, "reg_start");
            pstmt.setString(2, startDate);
            pstmt.executeUpdate();

            // Update End Date
            pstmt.setString(1, "reg_end");
            pstmt.setString(2, endDate);
            pstmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, String> getRegistrationDates() {
        Map<String, String> dates = new HashMap<>();
        String sql = "SELECT setting_key, setting_value FROM users.system_settings WHERE setting_key IN ('reg_start', 'reg_end')";

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                dates.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }
}