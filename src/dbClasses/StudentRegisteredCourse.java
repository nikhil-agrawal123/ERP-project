package dbClasses;

public class StudentRegisteredCourse {
    private int sectionId; // --- NEW ---
    private String courseCode;
    private String courseName;
    private int courseCredits;
    private String offeredBy;
    private double gradePoint;
    private String gradeLetter; // --- NEW: Stores 'A', 'B', 'X' ---
    private int semester;

    public StudentRegisteredCourse(int sectionId, int semester, String courseCode, String courseName, int courseCredits, String offeredBy, double gradePoint, String gradeLetter) {
        this.sectionId = sectionId;
        this.semester = semester;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseCredits = courseCredits;
        this.offeredBy = offeredBy;
        this.gradePoint = gradePoint;
        this.gradeLetter = gradeLetter;
    }

    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCourseCredits() { return courseCredits; }
    public String getOfferedBy() { return offeredBy; }
    public double getGradePoint() { return gradePoint; }
    public String getGradeLetter() { return gradeLetter; }
    public int getSemester() { return semester; }
}