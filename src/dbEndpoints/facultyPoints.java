package dbEndpoints;

import databaseConfig.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
            SELECT DISTINCT st.full_name, st.student_roll_no, st.student_email, e.enrollment_id
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
                        rs.getString("student_email"),
                        rs.getInt("enrollment_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentList;
    }

    public String getFacultyName(String instructorCode){
        String facultyName = "SELECT full_name FROM users.instructors WHERE instructor_id = ?";

        try(Connection conn = connector.connect();
            PreparedStatement pstm = conn.prepareStatement(facultyName);
        ){
            pstm.setString(1, instructorCode);
            ResultSet rs = pstm.executeQuery();
            if(rs.next()){
                return rs.getString("full_name");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveStudentScore(int enrollmentId, String componentName, double score) {
        String sql = """
            INSERT INTO users.student_component_scores (enrollment_id, component_name, score_obtained)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE score_obtained = VALUES(score_obtained)
        """;

        try (Connection conn = connector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, enrollmentId);
            pstmt.setString(2, componentName);
            pstmt.setDouble(3, score);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches all saved scores for a list of enrollments.
     * Returns a Map: EnrollmentID -> Map<ComponentName, Score>
     */
    public Map<Integer, Map<String, Double>> getStudentScores(List<Integer> enrollmentIds) {
        Map<Integer, Map<String, Double>> scoresMap = new HashMap<>();

        if (enrollmentIds.isEmpty()) return scoresMap;

        // Construct "IN (?, ?, ?)" clause dynamically
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<enrollmentIds.size(); i++) builder.append("?,");
        String placeholders = builder.deleteCharAt(builder.length()-1).toString();

        String sql = "SELECT enrollment_id, component_name, score_obtained FROM users.student_component_scores WHERE enrollment_id IN (" + placeholders + ")";

        try (Connection conn = connector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for(int i=0; i<enrollmentIds.size(); i++) {
                pstmt.setInt(i+1, enrollmentIds.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int eid = rs.getInt("enrollment_id");
                String comp = rs.getString("component_name");
                double val = rs.getDouble("score_obtained");

                scoresMap.putIfAbsent(eid, new HashMap<>());
                scoresMap.get(eid).put(comp, val);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scoresMap;
    }
}
