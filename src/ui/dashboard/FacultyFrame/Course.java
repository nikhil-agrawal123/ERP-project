package ui.dashboard.FacultyFrame;

/**
 * A unified model class for a Course.
 * Contains all fields needed by TAStats and MyCoursesFrame.
 */
public class Course {
    private String courseId;
    private String courseCode;
    private String courseName;
    private String facultyId;
    private String semester;
    private String program;
    private int studentCount;

    /**
     * Constructor for basic course info (used by TAStats)
     */
    public Course(String courseCode, String courseName, String facultyId) {
        this.courseId = courseCode; // Using code as ID for this example
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.facultyId = facultyId;
        // Set defaults for other fields
        this.semester = "Unknown";
        this.program = "Unknown";
        this.studentCount = 0;
    }

    /**
     * Constructor for detailed course info (used by MyCoursesFrame)
     */
    public Course(String courseCode, String courseName, String semester, String program, int studentCount, String facultyId) {
        this.courseId = courseCode; // Using code as ID
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.semester = semester;
        this.program = program;
        this.studentCount = studentCount;
        this.facultyId = facultyId;
    }

    // --- Getters for all fields ---
    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getFacultyId() { return facultyId; }
    public String getSemester() { return semester; }
    public String getProgram() { return program; }
    public int getStudentCount() { return studentCount; }

    @Override
    public String toString() {
        return courseCode + ": " + courseName;
    }
}