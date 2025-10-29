package dbEndpoints;

import java.sql.*;
import databaseConfig.Connector;
import dbClasses.*;

public class authPoints {
    Connector dbConnector = new Connector();

    public studentService getAuthDataByUsername(String username) throws SQLException {
        String sql = "SELECT sa.studentPass, s.student_roll_no " +
                "FROM auth.studentAuth sa " +
                "JOIN users.students s ON sa.studentId = s.user_id " +
                "WHERE sa.studentId = ?";


        try (Connection conn = dbConnector.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("studentPass");
                    String storedRollNumber = rs.getString("student_roll_no");

                    return new studentService(storedHash, storedRollNumber);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in AuthRepository: " + e.getMessage());
            throw new SQLException("Error fetching user data.", e);
        }
    }

    public facultyService getAuthDataByFaculty(String faculty) throws SQLException {
        String sql = "SELECT facultyPass FROM facultyAuth WHERE facultyId = ?";

        try (Connection conn = dbConnector.connect()){
            PreparedStatement preparedStatement = conn.prepareStatement(sql) ;
            preparedStatement.setString(1, faculty);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("facultyPass");
                return new facultyService(storedHash);
            }else{
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Database error in AuthRepository: " + e.getMessage());
            throw new SQLException("Error fetching user data.", e);
        }
    }
}