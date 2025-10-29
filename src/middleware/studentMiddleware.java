package middleware;

import dbEndpoints.authPoints;
import dbClasses.StudentAuth;
import dependancy.org.mindrot.jbcrypt.BCrypt; // Make sure this import is correct

import java.sql.SQLException;

public class studentMiddleware {

    private authPoints authRepository;

    public studentMiddleware() {
        this.authRepository = new authPoints();
    }

    public String loginStudent(String username, String password) {
        try {
            StudentAuth authData = authRepository.getAuthDataByUsername(username);

            if (authData == null) {
                System.out.println("Login attempt failed: User not found.");
                return null; // User not found
            }

            if (BCrypt.checkpw(password, authData.getPasswordHash())) {
                return authData.getStudentRollNo();
            } else {
                System.out.println("Login attempt failed: Incorrect password.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
