package dbClasses;

public class CourseDTO {
    private int id; // This is technically the section_id, but we treat it as the unique ID for this offering
    private String courseCode;
    private String courseName;
    private String department;
    private int credits;
    private String instructorId;
    private String instructorName;
    private String semester;
    private int year;
    private int capacity;
    private int enrolled;

    public CourseDTO(int id, String courseCode, String courseName, String department, int credits,
                     String instructorId, String instructorName, String semester, int year, int capacity, int enrolled) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.department = department;
        this.credits = credits;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.semester = semester;
        this.year = year;
        this.capacity = capacity;
        this.enrolled = enrolled;
    }

    // Getters
    public int getId() { return id; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getDepartment() { return department; }
    public int getCredits() { return credits; }
    public String getInstructorId() { return instructorId; }
    public String getInstructorName() { return instructorName; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }
    public int getCapacity() { return capacity; }
    public int getEnrolled() { return enrolled; }

    public String getFullSemester() { return semester + " " + year; }
}