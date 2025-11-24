package dbClasses;

public class facultyCourseClass {

    private String courseNane;
    private String courseCode;
    private int studentCount;
    private int courseCredits;
    private String department;
    private String semester;

    public facultyCourseClass(String courseNane, String courseCode, int studentCount, int courseCredits, String department,String semester) {
        this.courseNane = courseNane;
        this.courseCode = courseCode;
        this.studentCount = studentCount;
        this.courseCredits = courseCredits;
        this.department = department;
        this.semester = semester;
    }

    public String getCourseName() {
        return courseNane;
    }
    public String getCourseCode() {
        return courseCode;
    }
    public int getStudentCount() {
        return studentCount;
    }
    public int getCourseCredits() {
        return courseCredits;
    }
    public String getDepartment() {
        return department;
    }
    public String getSemester() {return semester;}
}
