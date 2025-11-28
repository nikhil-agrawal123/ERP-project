package middleware;

import databaseConfig.Connector;
import dbClasses.*;
import dbEndpoints.SystemSettings;
import dbEndpoints.AdminPoints;
import dbEndpoints.AdminBackup;
import dbEndpoints.studentPoints;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class adminService {
    private final AdminPoints adminPoints;
    private final AdminBackup adminBackup;
    private final loggerService loggerService;
    private final SystemSettings systemSettings;
    private final studentPoints studentService;

    public adminService() {
        this.adminPoints = new AdminPoints();
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

    public boolean setDropDeadlines(java.util.Date dropDate, java.util.Date lateDropDate) {
        if (dropDate.after(lateDropDate)) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return systemSettings.updateDropDeadlines(sdf.format(dropDate), sdf.format(lateDropDate));
    }

    public List<CourseDTO> getAllCourseOfferings(String semesterFilter) {
        return adminPoints.getAllCourseOfferings(semesterFilter);
    }

    public List<CourseDTO> searchCourses(String query) {
        return adminPoints.searchCourses(query == null ? "" : query);
    }

    public List<CourseDTO> getCourseCatalog() {
        return adminPoints.getCourseCatalog();
    }

    public boolean updateCourseOffering(int id, String code, String instructor, int cap, int credits) {
        boolean success = adminPoints.updateCourseOffering(id, code, instructor, cap, credits);
        if (success) loggerService.log("CurrentAdmin", "UPDATE_COURSE", "Updated: " + code,"Admin");
        return success;
    }

    public boolean deleteCourseOffering(int id) {
        boolean success = adminPoints.deleteCourseOffering(id);
        if (success) loggerService.log("CurrentAdmin", "DELETE_COURSE", "Removed ID: " + id,"Admin");
        return success;
    }
}
