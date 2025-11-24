package middleware;
import dbClasses.StudentCgCredits;
import dbEndpoints.studentPoints;
import java.sql.*;
import java.util.*;
import dbClasses.*;

public class studentService {
    private studentPoints student;

    public studentService() {
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
           return student.UpdateRegisteredCourses(courses, username);
    }

    public boolean CheckRegister(String username, String courseCode) {
        return student.Check(username, courseCode) != 0;
    }
    // Add this inside middleware/studentService.java

    public String getStudentProgram(String rollNumber) {
        try {
            return student.getStudentProgram(rollNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            return ""; // Return empty string if something goes wrong
        }
    }

}
