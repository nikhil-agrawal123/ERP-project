package middleware;

import databaseConfig.Connector; // Make sure this package/class name is correct
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles all business logic and database interactions related to faculty.
 * This class does NOT interact with the UI (e.g., no JOptionPanes).
 */
public class facultyService {

    /**
     * Authenticates a faculty member against the database.
     *
     * @param username The faculty ID (e.g., "aditri", "alok")
     * @param password The plain-text password to check
     * @return true if the username exists and the password matches, false otherwise.
     */
    public boolean loginFaculty(String username, String password) {

        // --- MODIFIED SQL ---
        // Changed columns to 'facultyPassword' and 'facultyId' to match your database image
        String sql = "SELECT facultyPassword FROM users.faculty WHERE facultyId = ?";

        Connector dbConnector = new Connector();

        // We use try-with-resources to ensure the connection and statement are closed
        // automatically, even if an error occurs.
        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Safely set the username parameter in the query
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {

                // Check if the query returned any result
                if (rs.next()) {
                    // --- User Found ---
                    // --- MODIFIED COLUMN NAME ---
                    String dbPassword = rs.getString("facultyPassword");

                    // Compare the provided password with the one from the database
                    return password.equals(dbPassword);

                } else {
                    // --- User Not Found ---
                    return false;
                }
            }

        } catch (SQLException e) {
            // Log the error for debugging
            e.printStackTrace();
            // Any database error should result in a failed login
            return false;
        } catch (Exception e) {
            // Catch any other potential errors (e.g., Connector issues)
            e.printStackTrace();
            return false;
        }
    }

    // You can add other faculty-related methods here in the future,
    // for example:
    // public FacultyDetails getFacultyDetails(String username) { ... }
    // public boolean updateFacultyPassword(String username, String newPassword) { ... }
}

