package middleware;
import dbEndpoints.studentPoints;
import java.sql.*;
import java.util.*;
import dbClasses.StudentRegisteredCourse;

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
}
