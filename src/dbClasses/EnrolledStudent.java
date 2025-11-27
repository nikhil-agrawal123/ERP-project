package dbClasses;

public class EnrolledStudent {
    private String studentName;
    private String rollNumber;
    private String email;
    private int enrollmentId;

    public EnrolledStudent(String studentName, String rollNumber, String email,int enrollmentId) {
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.email = email;
        this.enrollmentId = enrollmentId;
    }

    public String getStudentName() { return studentName; }
    public String getRollNumber() { return rollNumber; }
    public String getEmail() { return email; }
    public int getEnrollmentId() { return enrollmentId; }
}
