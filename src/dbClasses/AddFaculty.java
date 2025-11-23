package dbClasses;

/**
 * Data object to hold faculty registration details.
 */
public class AddFaculty {
    private String fullName;
    private String instructorId; // e.g., INST-CS-001
    private String userId;       // Login ID (e.g., alok_g)
    private String email;
    private String department;

    public AddFaculty(String fullName, String instructorId, String userId, String email, String department) {
        this.fullName = fullName;
        this.instructorId = instructorId;
        this.userId = userId;
        this.email = email;
        this.department = department;
    }

    public String getFullName() { return fullName; }
    public String getInstructorId() { return instructorId; }
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
}