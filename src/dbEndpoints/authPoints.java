package dbEndpoints;

import java.sql.*;
import databaseConfig.Connector;
import dbClasses.*;
import dependancy.org.mindrot.jbcrypt.BCrypt;

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

    public boolean forgetPass(String userEmail, String userType, String newHash) throws SQLException {
        String getUserIdSQL = "";
        String updatePassSQL = "";

        if(userType.equals("parent")){
            getUserIdSQL = "SELECT user_id FROM users.students WHERE student_email = ?";
            updatePassSQL = "UPDATE auth.parentAuth SET parentPass = ? WHERE studentId = ?";
        } else if(userType.equals("faculty")){
            getUserIdSQL = "SELECT user_id FROM users.instructors WHERE email = ?";
            updatePassSQL = "UPDATE auth.facultyAuth SET facultyPass = ? WHERE facultyId = ?";
        }else if(userType.equals("admin")){
            getUserIdSQL = "SELECT adminID FROM users.admin WHERE adminEmail = ?";
            updatePassSQL = "UPDATE auth.adminAuth SET adminPass = ? WHERE adminId = ?";
        }else{
            getUserIdSQL = "SELECT user_id FROM users.students WHERE student_email = ?";
            updatePassSQL = "UPDATE auth.studentAuth SET studentPass = ? WHERE studentId = ?";
        }

        try(Connection conn = dbConnector.connect()){
            PreparedStatement preparedStatement = conn.prepareStatement(getUserIdSQL);
            preparedStatement.setString(1, userEmail);
            try{
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    String studentId = rs.getString("user_id");
                    System.out.println("Found student ID: " + studentId);

                    PreparedStatement ps = conn.prepareStatement(updatePassSQL);
                    ps.setString(1, newHash);
                    ps.setString(2, studentId);

                    int rowsAffected = ps.executeUpdate();

                    if(rowsAffected > 0){
                        return true;
                    }
                }
            } catch (SQLException e) {
                throw new SQLException("Error fetching user data.", e);
            }
        }
        return false;
    }

    public String getDataByParent(String username) throws SQLException {
        String sql = "SELECT parentPass FROM parentAuth WHERE studentId = ?";
        try (Connection conn = dbConnector.connect();
        PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("parentPass");
            }
        }catch (SQLException e){
            throw new SQLException("Error fetching user data.", e);
        }
        return null;
    }
}