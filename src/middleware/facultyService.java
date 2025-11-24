package middleware;

import dbClasses.EnrolledStudent;
import dbEndpoints.facultyPoints;
import dbClasses.facultyCourseClass;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

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
        System.out.println(faculty.getAllCourses(facultyId).size());
        return faculty.getAllCourses(facultyId);
    }

    public Map<String, List<facultyCourseClass>> getCoursesBySemester(String instructorId) {
        List<facultyCourseClass> rawList = getAllCourses(instructorId);
        Map<String, List<facultyCourseClass>> organizedCourses = new LinkedHashMap<>();

        for (facultyCourseClass course : rawList) {
            String semKey = course.getSemester();
            if (!organizedCourses.containsKey(semKey)) {
                organizedCourses.put(semKey, new ArrayList<>());
            }
            organizedCourses.get(semKey).add(course);
        }
        return organizedCourses;
    }

    public List<EnrolledStudent> getClassList(String courseCode, String Semester){
        return faculty.getEnrolledStudents(courseCode, Semester);
    }
}