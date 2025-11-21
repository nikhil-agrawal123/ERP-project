package dbClasses;

public class NewStudent {

    private String student_name;
    private String student_roll_no;
    private String student_program;
    private String student_email;
    private int student_enrollment_year;
    private int student_current_sem;
    private String student_id;

    public NewStudent(String student_name,  String student_roll_no, String student_program, String student_email, int student_enrollment_year, int student_current_sem, String student_id) {
        this.student_name = student_name;
        this.student_roll_no = student_roll_no;
        this.student_program = student_program;
        this.student_email = student_email;
        this.student_enrollment_year = student_enrollment_year;
        this.student_current_sem = student_current_sem;
        this.student_id = student_id;
    }

    public int getStudent_current_sem() {
        return student_current_sem;
    }

    public String getStudent_name() {
        return student_name;
    }
    public String getStudent_roll_no() {
        return student_roll_no;
    }
    public String getStudent_program() {
        return student_program;
    }
    public String getStudent_email() {
        return student_email;
    }
    public int getStudent_enrollment_year() {
        return student_enrollment_year;
    }
    public String getStudent_id() {
        return student_id;
    }
}
