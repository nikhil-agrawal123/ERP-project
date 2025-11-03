package dbClasses;

public class studentClass {
    private final String passwordHash;
    private final String studentRollNo;

    public studentClass(String passwordHash, String studentRollNo) {
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