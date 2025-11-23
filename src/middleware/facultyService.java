package middleware;

import dbClasses.EnrolledStudent;
import dbEndpoints.facultyPoints;
import dbClasses.facultyCourseClass;
import java.util.List;

public class facultyService {
    private facultyPoints faculty;
    private maintenanceService maintenance;

    public facultyService() {
        this.faculty = new facultyPoints();
        this.maintenance = new maintenanceService();
    }

    public String facultyId(String userid){
        return faculty.getFacultyId(userid);
    }

    public int getFacultyCourse(String facultyId){
        return faculty.getNumberCourses(facultyId);
    }

    public List<facultyCourseClass> getAllCourses(String facultyId){
        return faculty.getAllCourses(facultyId);
    }

    public List<EnrolledStudent> getClassList(String courseCode, String Semester){
        return faculty.getEnrolledStudents(courseCode, Semester);
    }
}