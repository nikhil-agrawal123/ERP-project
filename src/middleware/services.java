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

    public boolean loginFaculty(String username, String password) {
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
        try {
            return authRepository.forgetPass(userEmail,userType,newHash);
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean adminLogin(String username, String password) {
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
