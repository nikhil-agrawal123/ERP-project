package middleware;

import dbClasses.AddCourse;
import dbClasses.AddFaculty;
import dbClasses.NewStudent;
import dbEndpoints.adminPoints;
import dbEndpoints.AdminBackup;

import java.io.File;
import java.sql.SQLException;
import dbClasses.AddAdmin;

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

    public boolean registerAdmin(AddAdmin admin) {
        boolean success = adminPoints.addAdmin(admin);
        if (success) {
            loggerService.log("CurrentAdmin", "ADD_ADMIN", "Added Admin: " + admin.getAdminId());
        } else {
            loggerService.log("CurrentAdmin", "ADD_ADMIN_FAIL", "Failed: " + admin.getAdminId());
        }
        return success;
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

    public boolean createCourseOffering(AddCourse data) {
        boolean success = adminPoints.addCourseAndSection(data);
        if (success) {
            loggerService.log("CurrentAdmin", "ADD_COURSE", "Created section for: " + data.getCourseCode());
        } else {
            loggerService.log("CurrentAdmin", "ADD_COURSE_FAIL", "Failed to create: " + data.getCourseCode());
        }
        return success;
    }
}
