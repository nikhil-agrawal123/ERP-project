package dbClasses;

public class AddCourse {
    private String courseCode;
    private String courseTitle;
    private int credits;
    private String department;
    private String instructorId;
    private String semester;
    private int year;
    private int capacity;

    public AddCourse(String courseCode, String courseTitle, int credits, String department, String instructorId, String semester, int year, int capacity) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credits = credits;
        this.department = department;
        this.instructorId = instructorId;
        this.semester = semester;
        this.year = year;
        this.capacity = capacity;
    }

    // Getters
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getCredits() { return credits; }
    public String getDepartment() { return department; }
    public String getInstructorId() { return instructorId; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }
    public int getCapacity() { return capacity; }
}