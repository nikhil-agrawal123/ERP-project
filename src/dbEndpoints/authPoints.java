package dbEndpoints;

import java.sql.*;
import databaseConfig.Connector;
import dbClasses.StudentAuth;

public class authPoints {

    public StudentAuth getAuthDataByUsername(String username) throws SQLException {
        String sql = "SELECT sa.studentPass, s.student_roll_no " +
                "FROM auth.studentAuth sa " +
                "JOIN users.students s ON sa.studentId = s.user_id " +
                "WHERE sa.studentId = ?";

        Connector dbConnector = new Connector();

        try (Connection conn = dbConnector.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                // 1. Check if a user was found
                if (rs.next()) {
                    // 2. Extract the data from the ResultSet
                    String storedHash = rs.getString("studentPass");
                    String storedRollNumber = rs.getString("student_roll_no");

                    // 3. Put data into the POJO
                    return new StudentAuth(storedHash, storedRollNumber);
                } else {
                    // 4. User not found, return null
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in AuthRepository: " + e.getMessage());
            throw new SQLException("Error fetching user data.", e);
        }
    }
}
