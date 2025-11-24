package dbEndpoints;

import databaseConfig.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbClasses.*;

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
                s.semester,
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
                c.course_title, c.course_code, c.credits, s.department, s.semester
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
                        rs.getString("department"),
                        rs.getString("semester")
                ));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return  courses;
    }

    public List<EnrolledStudent> getEnrolledStudents(String courseCode, String semester) {
        List<EnrolledStudent> studentList = new ArrayList<>();

        String sql = """
            SELECT DISTINCT st.full_name, st.student_roll_no, st.student_email
            FROM users.sections sec
            JOIN users.enrollments e ON sec.section_id = e.section_id
            JOIN users.students st ON e.student_id = st.user_id
            WHERE sec.course_code = ? AND sec.semester = ?
            ORDER BY st.student_roll_no
        """;

        try (Connection conn = connector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseCode);
            pstmt.setString(2, semester);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                studentList.add(new EnrolledStudent(
                        rs.getString("full_name"),
                        rs.getString("student_roll_no"),
                        rs.getString("student_email")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentList;
    }
}
