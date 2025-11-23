package middleware;

import dbClasses.AddFaculty;
import dbClasses.NewStudent;
import dbEndpoints.adminPoints;
import dbEndpoints.AdminBackup;

import java.io.File;
import java.sql.SQLException;

public class adminService {
    private final adminPoints adminPoints;
    private final AdminBackup adminBackup;
    private final loggerService loggerService;

    public adminService() {
        this.adminPoints = new adminPoints();
        this.adminBackup = new AdminBackup();
        this.loggerService = new loggerService();
    }

    public boolean addStudent(NewStudent newStudent) throws SQLException {
        loggerService.log("Admin", "Add Student", "Admin Tried adding a student");
        return adminPoints.addStudent(newStudent);
    }

    public boolean addInstructor(AddFaculty faculty) throws SQLException {
        loggerService.log("Admin", "Add Instructor", "Admin Tried adding a instructor");
        return adminPoints.addFaculty(faculty);
    }

    public boolean performSystemBackup(File file) {
        boolean success = adminBackup.createBackup(file);
        if (success) {
            loggerService.log("CurrentAdmin", "BACKUP_CREATED", "Saved to: " + file.getName());
        } else {
            loggerService.log("CurrentAdmin", "BACKUP_FAILED", "Failed to save to: " + file.getName());
        }
        return success;
    }

    public boolean performSystemRestore(File file) {
        boolean success = adminBackup.restoreBackup(file);
        if (success) {
            loggerService.log("CurrentAdmin", "SYSTEM_RESTORE", "Restored from: " + file.getName());
        } else {
            loggerService.log("CurrentAdmin", "RESTORE_FAILED", "Failed to restore: " + file.getName());
        }
        return success;
    }
}
