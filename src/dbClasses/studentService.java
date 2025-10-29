package dbClasses;

public class studentService {
    private final String passwordHash;
    private final String studentRollNo;

    public studentService(String passwordHash, String studentRollNo) {
        this.passwordHash = passwordHash;
        this.studentRollNo = studentRollNo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }
}