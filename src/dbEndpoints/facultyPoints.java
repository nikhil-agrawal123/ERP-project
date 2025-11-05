package dbEndpoints;

import databaseConfig.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dbClasses.facultyCourseClass;

public class facultyPoints {

    private final Connector connector;

    public facultyPoints() {
        this.connector = new Connector();
    }

    public String getFacultyId(String userId) {
        String instructorQuery = "SELECT instructor_id FROM users.instructors WHERE user_id = ?";

        try(Connection connection = connector.connect();
            PreparedStatement ptsm = connection.prepareStatement(instructorQuery);
        ){
            ptsm.setString(1, userId);
            ResultSet rs = ptsm.executeQuery();
            if(rs.next()){
                return rs.getString("instructor_id");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int getNumberCourses(String userId){
        String coursesQuery = "SELECT COUNT(*) AS course_count FROM users.sections WHERE instructor_id = ?";

        try(Connection conn = connector.connect();
            PreparedStatement pstm = conn.prepareStatement(coursesQuery);
        ){
            pstm.setString(1, userId);
            ResultSet rs = pstm.executeQuery();
            if(rs.next()){
                return rs.getInt("course_count");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public List<facultyCourseClass> getAllCourses(String userId){
        String sql = """
            SELECT 
                c.course_title,
                c.course_code,
                c.credits,
                s.department,
                COUNT(DISTINCT e.student_id) AS student_count
            FROM 
                users.sections s
            JOIN 
                users.courses c ON s.course_code = c.course_code
            LEFT JOIN 
                users.enrollments e ON e.course_code = c.course_code
            WHERE 
                s.instructor_id = ?
            GROUP BY 
                c.course_title, c.course_code, c.credits, s.department
            ORDER BY 
                c.course_title
            """;

        List<facultyCourseClass> courses = new ArrayList<>();

        try(Connection connection = connector.connect();
            PreparedStatement pstm = connection.prepareStatement(sql);
        ){
            pstm.setString(1, userId);
            ResultSet rs = pstm.executeQuery();

            while(rs.next()){
                courses.add(new facultyCourseClass(
                        rs.getString("course_title"),
                        rs.getString("course_code"),
                        rs.getInt("student_count"),
                        rs.getInt("credits"),
                        rs.getString("department")
                ));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return  courses;
    }

}
