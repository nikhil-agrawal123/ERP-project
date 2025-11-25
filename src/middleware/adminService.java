package middleware;

import databaseConfig.Connector;
import dbClasses.AddCourse;
import dbClasses.AddFaculty;
import dbClasses.NewStudent;
import dbEndpoints.SystemSettings;
import dbEndpoints.adminPoints;
import dbEndpoints.AdminBackup;
import dbEndpoints.studentPoints;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.SQLException;
import dbClasses.AddAdmin;

public class adminService {
    private final adminPoints adminPoints;
    private final AdminBackup adminBackup;
    private final loggerService loggerService;
    private final SystemSettings systemSettings;
    private final studentPoints studentService;

    public adminService() {
        this.adminPoints = new adminPoints();
        this.adminBackup = new AdminBackup();
        this.loggerService = new loggerService();
        this.systemSettings = new SystemSettings();
        this.studentService = new studentPoints();
    }

    public boolean addStudent(NewStudent newStudent) throws SQLException {
        loggerService.log("Admin", "Add Student", "Admin Tried adding a student","Admin");
        return adminPoints.addStudent(newStudent);
    }

    public boolean addInstructor(AddFaculty faculty) throws SQLException {
        loggerService.log("Admin", "Add Instructor", "Admin Tried adding a instructor","Admin");
        return adminPoints.addFaculty(faculty);
    }

    public boolean registerAdmin(AddAdmin admin) {
        boolean success = adminPoints.addAdmin(admin);
        if (success) {
            loggerService.log("CurrentAdmin", "ADD_ADMIN", "Added Admin: " + admin.getAdminId(),"Admin");
        } else {
            loggerService.log("CurrentAdmin", "ADD_ADMIN_FAIL", "Failed: " + admin.getAdminId(),"Admin");
        }
        return success;
    }

    public boolean performSystemBackup(File file) {
        boolean success = adminBackup.createBackup(file);
        if (success) {
            loggerService.log("CurrentAdmin", "BACKUP_CREATED", "Saved to: " + file.getName(),"Admin");
        } else {
            loggerService.log("CurrentAdmin", "BACKUP_FAILED", "Failed to save to: " + file.getName(),"Admin");
        }
        return success;
    }

    public boolean performSystemRestore(File file) {
        boolean success = adminBackup.restoreBackup(file);
        if (success) {
            loggerService.log("CurrentAdmin", "SYSTEM_RESTORE", "Restored from: " + file.getName(),"Admin");
        } else {
            loggerService.log("CurrentAdmin", "RESTORE_FAILED", "Failed to restore: " + file.getName(),"Admin");
        }
        return success;
    }

    public boolean createCourseOffering(AddCourse data) {
        boolean success = adminPoints.addCourseAndSection(data);
        if (success) {
            loggerService.log("CurrentAdmin", "ADD_COURSE", "Created section for: " + data.getCourseCode(),"Admin");
        } else {
            loggerService.log("CurrentAdmin", "ADD_COURSE_FAIL", "Failed to create: " + data.getCourseCode(),"Admin");
        }
        return success;
    }

    public boolean changeSystemSemester(String newSemester, int newYear) {
        Connection conn = null;
        try {
            Connector dbConnector = new Connector();
            conn = dbConnector.connect();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Update System Settings
            systemSettings.updateSystemSettings(newSemester, newYear, conn);

            // 2. Logic: Increment Student Semesters?
            // If it is NOT Summer, we promote students (e.g. Sem 1 -> Sem 2)
            if (!"summer".equalsIgnoreCase(newSemester)) {
                studentService.incrementAllStudentSemesters();
                studentService.updateSemesterName(newSemester);
                studentService.updateCurrentYear(newYear);
                loggerService.log("CurrentAdmin", "SEM_CHANGE", "System moved to " + newSemester + " " + newYear + ". Students promoted.","Admin");
            } else {
                loggerService.log("CurrentAdmin", "SEM_CHANGE", "System moved to " + newSemester + " " + newYear + ". No promotion (Summer).","Admin");
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public String getCurrentSemesterLabel() {
        return systemSettings.getCurrentSystemSemester();
    }

    public boolean setRegistrationPeriod(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            System.out.println("Error: Start date cannot be after End date.");
            return false;
        }

        // Format to String (YYYY-MM-DD) for DB storage
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startStr = sdf.format(startDate);
        String endStr = sdf.format(endDate);

        boolean success = systemSettings.updateRegistrationDates(startStr, endStr);

        if (success) {
            loggerService.log("CurrentAdmin", "REG_PERIOD_UPDATE", "Set to: " + startStr + " to " + endStr,"Admin");
        }
        return success;
    }
}
