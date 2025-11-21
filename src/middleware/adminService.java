package middleware;

import dbClasses.NewStudent;
import dbEndpoints.adminPoints;

import java.sql.SQLException;

public class adminService {
    private adminPoints adminPoints;

    public adminService() {
        this.adminPoints = new adminPoints();
    }

    public boolean addStudent(NewStudent newStudent) throws SQLException {
        return adminPoints.addStudent(newStudent);
    }
}
