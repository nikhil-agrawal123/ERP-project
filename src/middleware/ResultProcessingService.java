package middleware;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dbEndpoints.gradingPoints;
import databaseConfig.Connector;
import dbClasses.GradeRange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultProcessingService {

    private Connector dbConnector;
    private gradingPoints gradingRepo;

    public ResultProcessingService() {
        this.dbConnector = new Connector();
        this.gradingRepo = new gradingPoints();
    }

    /**
     * Calculates and publishes results for a specific semester.
     * 1. Finds all sections.
     * 2. Calculates totals from component scores.
     * 3. Determines Grade based on cutoffs.
     * 4. Updates the main 'enrollments' table (Official Record).
     */
    public String publishResults(String semester) {
        int processedCount = 0;
        // Note: Constructing the semester string match might depend on your DB data format (e.g. "Monsoon 2025")
        String sectionSql = "SELECT section_id, course_code, instructor_id FROM users.sections WHERE semester= ?";

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sectionSql)) {

            pstmt.setString(1, semester);
            ResultSet rs = pstmt.executeQuery();

            System.out.println(rs.getFetchSize());


            while(rs.next()) {
                int sectionId = rs.getInt("section_id");
                String code = rs.getString("course_code");
                String instId = rs.getString("instructor_id");

                processedCount += processSection(sectionId, code, instId, semester, conn);
            }

            return "Results published! Updated grades for " + processedCount + " students.";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error publishing results: " + e.getMessage();
        }
    }

    private int processSection(int sectionId, String code, String instId, String sem, Connection conn) throws SQLException {
        String cutoffJson = gradingRepo.getCutoffsJson(code, instId, sem);
        System.out.println("Cutoffs JSON: " + cutoffJson);
        if (cutoffJson == null) return 0;

        Gson gson = new Gson();
        List<GradeRange> cutoffs = gson.fromJson(cutoffJson, new TypeToken<ArrayList<GradeRange>>(){}.getType());

        // 2. Sum scores per student for this section
        String scoreSql = """
            SELECT e.enrollment_id, SUM(sc.score_obtained) as total_score
            FROM users.enrollments e
            JOIN users.student_component_scores sc ON e.enrollment_id = sc.enrollment_id
            WHERE e.section_id = ?
            GROUP BY e.enrollment_id
        """;

        int updateCount = 0;
        try (PreparedStatement scoreStmt = conn.prepareStatement(scoreSql)) {
            scoreStmt.setInt(1, sectionId);
            ResultSet rs = scoreStmt.executeQuery();

            while(rs.next()) {
                int enrollId = rs.getInt("enrollment_id");
                double total = rs.getDouble("total_score");

                // 3. Determine Grade
                String letter = "F";
                double gp = 0.0;

                // Check against cutoffs (Assuming descending order 10 -> 0)
                for (GradeRange r : cutoffs) {
                    if (total >= r.getMinScore()) {
                        // Convert "10" (String) to 10.0 (Double) for CG
                        try {
                            gp = Double.parseDouble(r.getGradeLetter());
                            letter = getLetterForCG(gp);
                        } catch(Exception e) {
                            // Fallback if pure Letter grades were used
                            letter = r.getGradeLetter();
                        }
                        break; // Match found
                    }
                }

                // 4. Update Official Record
                updateEnrollment(enrollId, letter, gp, conn);
                updateCount++;
            }
        }
        return updateCount;
    }

    private String getLetterForCG(double cg) {
        if(cg >= 10) return "A";
        if(cg >= 9) return "A-";
        if(cg >= 8) return "B";
        if(cg >= 7) return "B-";
        if(cg >= 6) return "C";
        if(cg >= 5) return "C-";
        return "F";
    }

    private void updateEnrollment(int enrollId, String letter, double gp, Connection conn) throws SQLException {
        // Updates the official grade columns and marks course as completed
        String sql = "UPDATE users.enrollments SET grade = ?, gradePoint = ?, completion = 1 WHERE enrollment_id = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, letter);
            pstmt.setDouble(2, gp);
            pstmt.setInt(3, enrollId);
            pstmt.executeUpdate();
        }
    }
}