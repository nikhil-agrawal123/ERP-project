package middleware;
import dbClasses.StudentCgCredits;
import dbEndpoints.studentPoints;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import dbClasses.*;

import dbEndpoints.SystemSettings;

public class studentService {
    private studentPoints student;
    private SystemSettings systemSettings;

    public studentService() {
        this.systemSettings = new SystemSettings();
        this.student = new studentPoints();
    }

    public Map<Integer, List<StudentRegisteredCourse>> getSemesterData(String username) {
        Map<Integer, List<StudentRegisteredCourse>> semesterData = new HashMap<>();

        try {
            List<StudentRegisteredCourse> allCourses = student.findCoursesByStudent(username);

            for (StudentRegisteredCourse course : allCourses) {
                int semester = course.getSemester();

                semesterData.putIfAbsent(semester, new ArrayList<>());

                semesterData.get(semester).add(course);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return semesterData;
    }

    public StudentCgCredits getCgData(String rollNumber) {
        int credits = 0;
        double cg = 0;

        try{
            List<StudentCgCredits> studentCgCredits = student.getCgCreditsByStudent(rollNumber);

            for(StudentCgCredits d:studentCgCredits){
                credits += d.getCredits();
                cg += (d.getCg()/10.0)*d.getCredits();
            }
            return new StudentCgCredits(credits, Math.round(cg*100.0)/100.0);
    }catch (SQLException e){
        e.printStackTrace();
        }
        return null;
    }

    public List<studentAvailableCourses> AllCourses(String sem){
        try{
            List<studentAvailableCourses> courses = student.AllCourses(sem);
            return courses;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean RegisterCourse(List<studentAvailableCourses> courses, String username) {
            for(studentAvailableCourses course : courses){
                if(!CheckRegister(username,course.getCourse_code())){
                    return false;
                }
            }
            return student.UpdateRegisteredCourses(courses, username);
    }

    public boolean CheckRegister(String username, String courseCode) {
        return student.Check(username, courseCode) != 0;
    }

    public String getStudentProgram(String rollNumber) {
        try {
            return student.getStudentProgram(rollNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            return ""; // Return empty string if something goes wrong
        }
    }

    public String getCurrentSystemSemester() {
        return systemSettings.getCurrentSystemSemester();
    }

    public Map<String, String> getRegistrationSchedule() {
        return systemSettings.getRegistrationDates();
    }

    public String dropCourse(String studentId, int sectionId,String courseCode) throws SQLException {
        // 1. Fetch Dates
        Map<String, String> dates = systemSettings.getAllDates();
        String dropStr = dates.get("drop_deadline");
        String lateDropStr = dates.get("late_drop_deadline");

        if (dropStr == null || lateDropStr == null) {
            return "Error: Drop deadlines not set by Admin.";
        }

        LocalDate dropDeadline = LocalDate.parse(dropStr);
        LocalDate lateDropDeadline = LocalDate.parse(lateDropStr);
        LocalDate today = LocalDate.now();

        // 2. Check Logic
        if (today.isEqual(dropDeadline) || today.isBefore(dropDeadline)) {
            // CASE 1: Standard Drop (Remove Record)
            boolean success = student.hardDropCourse(studentId, sectionId,courseCode);
            return success ? "Course dropped successfully (Removed from record)." : "Error dropping course.";

        } else if (today.isEqual(lateDropDeadline) || today.isBefore(lateDropDeadline)) {
            // CASE 2: Late Drop (Mark 'X')
            boolean success = student.lateDropCourse(studentId, sectionId);
            return success ? "Course late-dropped. Grade marked as 'X'." : "Error processing late drop.";

        } else {
            // CASE 3: Deadline Passed
            return "Drop failed: The deadline for dropping courses has passed.";
        }
    }
}
