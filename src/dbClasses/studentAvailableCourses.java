package dbClasses;

public class studentAvailableCourses {
    private String course_code;
    private String course_name;
    private int course_credits;
    private String offeredBY;
    private String semester;
    private int capacity;
    private int enrolledCount;

    public studentAvailableCourses(String course_code, String course_name, int course_credits, String offeredBY, String semester, int capacity, int enrolledCount) {
        this.course_code = course_code;
        this.course_name = course_name;
        this.course_credits = course_credits;
        this.offeredBY = offeredBY;
        this.semester = semester;
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
    }

    public String getCourse_code(){
        return course_code;
    }
    public String getCourse_name(){
        return course_name;
    }
    public int getCourse_credits(){
        return course_credits;
    }
    public String getOfferedBY(){
        return offeredBY;
    }
    public String getSemester(){
        return semester;
    }
    public int getCapacity(){
        return capacity;
    }
    public int getEnrolledCount(){
        return enrolledCount;
    }
}
