package dbEndpoints;

import java.sql.*;
import databaseConfig.Connector;
import dbClasses.*;

public class authPoints {
    Connector dbConnector = new Connector();

    // --- STUDENT FETCH ---
    public studentClass getAuthDataByUsername(String username) throws SQLException {
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
                    return new studentClass(storedHash, storedRollNumber);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in AuthRepository: " + e.getMessage());
            throw new SQLException("Error fetching user data.", e);
        }
    }

    // --- FACULTY FETCH ---
    public facultyClass getAuthDataByFaculty(String facultyId) throws SQLException {
        // Updated to use 'auth.' schema for consistency
        String sql = "SELECT facultyPass FROM auth.facultyAuth WHERE facultyId = ?";

        try (Connection conn = dbConnector.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, facultyId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("facultyPass");
                    return new facultyClass(storedHash);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in AuthRepository: " + e.getMessage());
            throw new SQLException("Error fetching user data.", e);
        }
    }

    // --- PARENT LOGIN FETCH ---
    public String getDataByParent(String username) throws SQLException {
        String sql = "SELECT parentPass FROM auth.parentAuth WHERE studentId = ?";
        try (Connection conn = dbConnector.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("parentPass");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching user data.", e);
        }
        return null;
    }

    // --- ADMIN LOGIN FETCH ---
    public String getDataByAdmin(String username) throws SQLException {
        String sql = "SELECT adminPass FROM auth.adminAuth WHERE adminId = ?";

        try (Connection conn = dbConnector.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("adminPass");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching user data.", e);
        }
        return null;
    }

    // --- STUDENT PASSWORD UPDATE ---
    public boolean updatePasswordHash(String username, String newHash) throws SQLException {
        String sql = "UPDATE auth.studentAuth SET studentPass = ? WHERE studentId = ?";

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newHash);
            pstmt.setString(2, username);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --- FACULTY PASSWORD UPDATE (Fixed) ---
    public boolean updateFacultyPasswordHash(String facultyId, String newHash) throws SQLException {
        // Corrected table name and column names based on your schema
        String sql = "UPDATE auth.facultyAuth SET facultyPass = ? WHERE facultyId = ?";

        try (Connection conn = dbConnector.connect();
             PreparedStatement ppt = conn.prepareStatement(sql)) {

            ppt.setString(1, newHash);
            ppt.setString(2, facultyId);

            int rowsAffected = ppt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // --- FORGET PASSWORD ---
    public boolean forgetPass(String userEmail, String userType, String newHash) throws SQLException {
        String getUserIdSQL = "";
        String updatePassSQL = "";

        if(userType.equals("parent")){
            getUserIdSQL = "SELECT user_id FROM users.students WHERE student_email = ?";
            updatePassSQL = "UPDATE auth.parentAuth SET parentPass = ? WHERE studentId = ?";
        } else if(userType.equals("faculty")){
            getUserIdSQL = "SELECT user_id FROM users.instructors WHERE email = ?";
            updatePassSQL = "UPDATE auth.facultyAuth SET facultyPass = ? WHERE facultyId = ?";
        } else if(userType.equals("admin")){
            getUserIdSQL = "SELECT user_id FROM users.admins WHERE email = ?";
            updatePassSQL = "UPDATE auth.adminAuth SET adminPass = ? WHERE adminId = ?";
        } else {
            getUserIdSQL = "SELECT user_id FROM users.students WHERE student_email = ?";
            updatePassSQL = "UPDATE auth.studentAuth SET studentPass = ? WHERE studentId = ?";
        }

        try(Connection conn = dbConnector.connect()){
            try (PreparedStatement preparedStatement = conn.prepareStatement(getUserIdSQL)) {
                preparedStatement.setString(1, userEmail);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String userId = rs.getString("user_id");
                        System.out.println("Found user ID: " + userId);

                        try (PreparedStatement ps = conn.prepareStatement(updatePassSQL)) {
                            ps.setString(1, newHash);
                            ps.setString(2, userId);

                            int rowsAffected = ps.executeUpdate();
                            return rowsAffected > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching user data.", e);
        }
        return false;
    }
}