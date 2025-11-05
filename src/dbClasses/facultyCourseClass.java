package dbClasses;

public class facultyCourseClass {

    private String courseNane;
    private String courseCode;
    private int studentCount;
    private int courseCredits;
    private String department;

    public facultyCourseClass(String courseNane, String courseCode, int studentCount, int courseCredits, String department) {
        this.courseNane = courseNane;
        this.courseCode = courseCode;
        this.studentCount = studentCount;
        this.courseCredits = courseCredits;
        this.department = department;
    }

    public String getCourseName() {
        return courseNane;
    }
    public void setCourseNane(String courseNane) {
        this.courseNane = courseNane;
    }
    public String getCourseCode() {
        return courseCode;
    }
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    public int getStudentCount() {
        return studentCount;
    }
    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }
    public int getCourseCredits() {
        return courseCredits;
    }
    public void setCourseCredits(int courseCredits) {
        this.courseCredits = courseCredits;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
}
