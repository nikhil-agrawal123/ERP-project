package dbEndpoints;

import databaseConfig.Connector;
import dbClasses.*;

import dependancy.org.mindrot.jbcrypt.BCrypt;
import middleware.facultyService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminPoints {
    private Connector connector;

    public AdminPoints() {
        this.connector = new  Connector();
    }

    public boolean addStudent(NewStudent user) throws SQLException {
        String sql = "INSERT INTO users.students (student_roll_no, full_name,program,enrollment_year, student_email,  currentSem,user_id) VALUES (?,?,?,?,?,?,?)";
        String sqlAuth = "INSERT INTO auth.studentAuth (studentId, studentPass) VALUES (?, ?)";

        Connection conn = null;

        try{

             conn = connector.connect();
            conn.setAutoCommit(false);

            try(PreparedStatement pstm = conn.prepareStatement(sql)){
                pstm.setString(1, user.getStudent_roll_no());
                pstm.setString(2, user.getStudent_name());
                pstm.setString(3, user.getStudent_program());
                pstm.setInt(4, user.getStudent_enrollment_year());
                pstm.setString(5, user.getStudent_email());
                pstm.setInt(6,user.getStudent_current_sem());
                pstm.setString(7,user.getStudent_id());
                pstm.executeUpdate();
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlAuth)) {
                String defaultPassword = user.getStudent_roll_no();
                String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());

                pstmt2.setString(1, user.getStudent_id());
                pstmt2.setString(2, hashedPassword);

                pstmt2.executeUpdate();
            }
            conn.commit();
            return true;
        }

        catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.out.println("Rolling back transaction due to error...");
                    conn.rollback(); // Undo insertions if anything failed
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        }finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restore default
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean addFaculty(AddFaculty faculty) {
        String sqlProfile = "INSERT INTO users.instructors (user_id, instructor_id, full_name, department, email) VALUES (?, ?, ?, ?, ?)";

        String sqlAuth = "INSERT INTO auth.facultyAuth (facultyId, facultyPass) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = connector.connect();
            conn.setAutoCommit(false);

            // 1. Insert Profile
            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlProfile)) {
                pstmt1.setString(1, faculty.getUserId());
                pstmt1.setString(2, faculty.getInstructorId());
                pstmt1.setString(3, faculty.getFullName());
                pstmt1.setString(4, faculty.getDepartment());
                pstmt1.setString(5, faculty.getEmail());
                pstmt1.executeUpdate();
            }

            // 2. Insert Auth
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlAuth)) {
                // Default password is the Instructor ID
                String defaultPassword = faculty.getInstructorId();
                String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());

                pstmt2.setString(1, faculty.getUserId());
                pstmt2.setString(2, hashedPassword);
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean addAdmin(AddAdmin admin) {
        String sqlProfile = "INSERT INTO users.admins ( admin_id, full_name, role, email) VALUES ( ?,?, ?, ?, ?)";

        String sqlAuth = "INSERT INTO auth.adminAuth (adminId, adminPass) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = connector.connect();
            conn.setAutoCommit(false);

            // 1. Insert Profile
            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlProfile)) {
                pstmt1.setString(1, admin.getAdminId());
                pstmt1.setString(2, admin.getUserId());
                pstmt1.setString(3, admin.getFullName());
                pstmt1.setString(4, admin.getRole());
                pstmt1.setString(5, admin.getEmail());
                pstmt1.executeUpdate();
            }

            // 2. Insert Auth
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlAuth)) {
                // Default password is the Admin ID
                String defaultPassword = admin.getAdminId();
                String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());

                pstmt2.setString(1, admin.getAdminId());
                pstmt2.setString(2, hashedPassword);
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean addCourseAndSection(AddCourse data) {
        // 1. Insert into Catalog (Updated with Department)
        String sqlCourse = "INSERT IGNORE INTO users.courses (course_code, course_title, credits,semester,currenCap,offeredBy) VALUES (?, ?, ?,?,?,?)";

        String sqlSection = "INSERT INTO users.sections (course_code, instructor_id, semester, year, capacity, department) VALUES (?, ?, ?, ?, ?, ?)";

        String instructorName = new facultyService().getFullNmae(data.getInstructorId());

        Connection conn = null;
        try {
            conn = connector.connect();
            conn.setAutoCommit(false); // Start Transaction

            // Insert Course
            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlCourse)) {
                pstmt1.setString(1, data.getCourseCode());
                pstmt1.setString(2, data.getCourseTitle());
                pstmt1.setInt(3, data.getCredits());
                pstmt1.setString(4, data.getSemester()+data.getYear());
                pstmt1.setInt(5, 0);
                pstmt1.setString(6, instructorName);
                pstmt1.executeUpdate();
            }

            // Insert Section
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlSection)) {
                pstmt2.setString(1, data.getCourseCode());
                pstmt2.setString(2, data.getInstructorId());
                pstmt2.setString(3, data.getSemester());
                pstmt2.setInt(4, data.getYear());
                pstmt2.setInt(5, data.getCapacity());
                pstmt2.setString(6, data.getDepartment()); // Set Dept
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public List<CourseDTO> searchCourses(String query) {
        List<CourseDTO> list = new ArrayList<>();
        String sql = """
            SELECT 
                s.section_id, 
                c.course_code, c.course_title, c.department, c.credits,
                s.instructor_id, i.full_name,
                s.semester, s.year, s.capacity,
                (SELECT COUNT(*) FROM users.enrollments e WHERE e.section_id = s.section_id) as enrolled
            FROM users.sections s
            JOIN users.courses c ON s.course_code = c.course_code
            JOIN users.instructors i ON s.instructor_id = i.user_id
            WHERE 
                c.course_title LIKE ? OR 
                c.course_code LIKE ? OR 
                i.full_name LIKE ?
            ORDER BY s.year DESC, s.semester, c.course_title
        """;

        try (Connection conn = connector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String search = "%" + query + "%";
            pstmt.setString(1, search);
            pstmt.setString(2, search);
            pstmt.setString(3, search);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new CourseDTO(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getString("department"),
                        rs.getInt("credits"),
                        rs.getString("instructor_id"),
                        rs.getString("full_name"),
                        rs.getString("semester"),
                        rs.getInt("year"),
                        rs.getInt("capacity"),
                        rs.getInt("enrolled")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CourseDTO> getCourseCatalog() {
        List<CourseDTO> list = new ArrayList<>();

        // --- UPDATED QUERY ---
        // 1. Get basic info from COURSES
        // 2. Get scheduling info from SECTIONS
        // 3. Get instructor names from INSTRUCTORS
        String sql = """
            SELECT 
                s.section_id, 
                c.course_code, 
                c.course_title, 
                c.department, 
                c.credits,
                c.offeredBy,
                s.instructor_id, 
                s.semester, 
                s.year, 
                s.capacity,
                (SELECT COUNT(*) FROM users.enrollments e WHERE e.section_id = s.section_id) as enrolled
            FROM users.courses c
            JOIN users.sections s ON c.course_code = s.course_code
            ORDER BY c.course_code, s.year DESC, s.semester
        """;

        try (Connection conn = connector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // We populate the DTO fully now
                list.add(new CourseDTO(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getString("department"),
                        rs.getInt("credits"),
                        rs.getString("instructor_id"),
                        rs.getString("offeredBy"),
                        rs.getString("semester"),
                        rs.getInt("year"),
                        rs.getInt("capacity"),
                        rs.getInt("enrolled")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<CourseDTO> getAllCourseOfferings(String semesterFilter) {
        List<CourseDTO> list = new ArrayList<>();

        String sql = """
            SELECT 
                s.section_id, 
                c.course_code, 
                c.course_title, 
                c.department, 
                c.credits,
                s.instructor_id, 
                i.full_name,
                s.semester, 
                s.year, 
                s.capacity,
                (SELECT COUNT(*) FROM users.enrollments e WHERE e.section_id = s.section_id) as enrolled
            FROM users.sections s
            JOIN users.courses c ON s.course_code = c.course_code
            LEFT JOIN users.instructors i ON s.instructor_id = i.user_id
            WHERE c.semester = ?
            ORDER BY s.year DESC, s.semester, c.course_code
        """;

        try (Connection conn = connector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1,  semesterFilter);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String fullSem = rs.getString("semester");

                list.add(new CourseDTO(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getString("department"),
                        rs.getInt("credits"),
                        rs.getString("instructor_id"),
                        rs.getString("full_name"),
                        fullSem,
                        rs.getInt("year"),
                        rs.getInt("capacity"),
                        rs.getInt("enrolled")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
