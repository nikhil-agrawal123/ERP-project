package middleware;

import dbEndpoints.facultyPoints;
import dbClasses.facultyCourseClass;
import java.util.List;

public class facultyService {
    private facultyPoints faculty;

    public facultyService() {
        this.faculty = new facultyPoints();
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
}