package dbClasses;

public class studentAvailableCourses {
    private String course_code;
    private String course_name;
    private int course_credits;
    private String offeredBY;
    private String semester;

    public studentAvailableCourses(String course_code, String course_name, int course_credits, String offeredBY, String semester){
        this.course_code = course_code;
        this.course_name = course_name;
        this.course_credits = course_credits;
        this.offeredBY = offeredBY;
        this.semester = semester;
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
}
