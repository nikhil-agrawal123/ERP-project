package dbClasses;

public class StudentRegisteredCourse {
    private String course_code;
    private String course_name;
    private int course_credits;
    private String offeredBY;
    private double gradePoint;
    private int semester;

    public StudentRegisteredCourse(String course_code, String course_name, int course_credits, String offeredBY, double gradePoint, int semester) {
        this.course_code = course_code;
        this.course_name = course_name;
        this.course_credits = course_credits;
        this.offeredBY = offeredBY;
        this.gradePoint = gradePoint;
        this.semester = semester;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }
    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }
    public void setCourse_credits(int course_credits) {
        this.course_credits = course_credits;
    }
    public void setOfferedBY(String offeredBY) {
        this.offeredBY = offeredBY;
    }
    public void setGradePoint(double gradePoint) {
        this.gradePoint = gradePoint;
    }
    public String getCourseCode() {
        return course_code;
    }
    public String getCourseName() {
        return course_name;
    }
    public int getCourseCredits() {
        return course_credits;
    }
    public String getOfferedBy() {
        return offeredBY;
    }
    public double getGradePoint() {
        return gradePoint;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getSemester() {
        return semester;
    }

}
