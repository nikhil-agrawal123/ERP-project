package middleware;

import dbClasses.EnrolledStudent;
import dbEndpoints.facultyPoints;
import dbClasses.facultyCourseClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

public class facultyService {
    private static final Logger log = LoggerFactory.getLogger(facultyService.class);
    private facultyPoints faculty;
    private maintenanceService maintenance;
    private loggerService logger;

    public facultyService() {
        this.faculty = new facultyPoints();
        this.maintenance = new maintenanceService();
        this.logger = new loggerService();
    }

    public String facultyId(String userid){
        logger.log(userid,"facultyId","faculty id is fetched","Faculty");
        return faculty.getFacultyId(userid);
    }

    public int getFacultyCourse(String facultyId){
        logger.log(facultyId,"Courses Fetched" ,"faculty courses were fetched","Faculty");
        return faculty.getNumberCourses(facultyId);
    }

    public List<facultyCourseClass> getAllCourses(String facultyId){
        logger.log(facultyId,"Course List","List of courses were fetched","Faculty");
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
        logger.log(instructorId,"Course Fetched","All courses were fetched and converted to HashMap to display","Faculty");
        return organizedCourses;
    }

    public List<EnrolledStudent> getClassList(String courseCode, String Semester){
        logger.log(courseCode,"Enrolled Student List","Faculty fetched list of enrolled students ","Faculty");
        return faculty.getEnrolledStudents(courseCode, Semester);
    }

    public String getFullNmae(String instructor_id){
        logger.log("Admin" ,"Fetch Name" ,"Admin fetched instructor name with id" + instructor_id,"Faculty");
        return faculty.getFacultyName(instructor_id);
    }
}