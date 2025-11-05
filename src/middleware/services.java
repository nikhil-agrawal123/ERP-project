package middleware;

import dbEndpoints.authPoints;
import dbClasses.*;
import dependancy.org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class services {

    private authPoints authRepository;

    public services() {
        this.authRepository = new authPoints();
    }

    public String loginStudent(String username, String password) {
        try {
            studentClass authData = authRepository.getAuthDataByUsername(username);

            if (authData == null) {
                System.out.println("Login attempt failed: User not found.");
                return null; // User not found
            }

            if (BCrypt.checkpw(password, authData.getPasswordHash())) {
                // Return the student's roll number from the related table
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

    // --- NEW METHOD 1: VERIFY CURRENT PASSWORD ---
    /**
     * Verifies if the provided current password matches the hash in the database.
     * @param username The student's auth ID (e.g., "nikhil24380")
     * @param currentPassword The plaintext password to check.
     * @return true if the password matches, false otherwise.
     */
    public boolean verifyCurrentPassword(String username, String currentPassword) {
        try {
            // Re-use the getAuthDataByUsername method to get the user's data
            studentClass authData = authRepository.getAuthDataByUsername(username);

            if (authData == null) {
                return false; // User not found
            }
            // Use BCrypt to check the password
            return BCrypt.checkpw(currentPassword, authData.getPasswordHash());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- NEW METHOD 2: UPDATE PASSWORD ---
    /**
     * Hashes a new password and updates it in the database.
     * @param username The student's auth ID (e.g., "nikhil24380")
     * @param newPassword The new plaintext password to hash and save.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updatePassword(String username, String newPassword) {
        try {
            // Hash the new password with a new salt
            // A log_rounds of 10 is a good default
            String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(10));

            // Call a new method in authPoints to execute the UPDATE query
            return authRepository.updatePasswordHash(username, newHashedPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // --- (Rest of your methods... loginFaculty, parentLogin, etc.) ---

    public boolean loginFaculty(String username, String password) {
        // ... (your existing code)
        try {
            facultyClass authData = authRepository.getAuthDataByFaculty(username);
            if(authData == null) {
                return false;
            }else  {
                if (BCrypt.checkpw(password, authData.getPasswordHash())) {
                    return true;
                }else{
                    System.out.println("Login attempt failed: Incorrect password.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean parentLogin(String username, String password) {
        // ... (your existing code)
        try {
            String gethash = authRepository.getDataByParent(username);
            if(gethash == null) {
                System.out.println("Login attempt failed: User not found.");
            }else {
                if (BCrypt.checkpw(password, gethash)) {
                    return true;
                }else  {
                    System.out.println("Login attempt failed: Incorrect password.");
                    return false;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean forgetPass(String userEmail , String userType,String newHash) {
        // ... (your existing code)
        try {
            return authRepository.forgetPass(userEmail,userType,newHash);
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean adminLogin(String username, String password) {
        // ... (your existing code)
        try{
            String hash = authRepository.getDataByAdmin(username);
            if(hash == null) {
                System.out.println("Login attempt failed: User not found.");
            }else{
                if (BCrypt.checkpw(password, hash)) {
                    return true;
                }else{
                    System.out.println("Login attempt failed: Incorrect password.");
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}