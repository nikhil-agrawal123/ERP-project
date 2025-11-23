package dbEndpoints;

import databaseConfig.Connector;
import ui.components.RoundedPanel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Maintenance {
    private Connector dbConnector;

    public Maintenance() {
        dbConnector = new Connector();
    }

    public boolean isMaintenanceModeEnabled() {
        String sql = "SELECT setting_value FROM users.system_settings WHERE setting_key = 'maintenance_mode'";
        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return Boolean.parseBoolean(rs.getString("setting_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to live if DB fails
    }

    /**
     * Updates the maintenance mode status.
     */
    public boolean setMaintenanceMode(boolean isEnabled) {
        String sql = "UPDATE users.system_settings SET setting_value = ? WHERE setting_key = 'maintenance_mode'";
        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(isEnabled));
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
