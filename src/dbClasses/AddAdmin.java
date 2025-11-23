package dbClasses;

/**
 * Data object to hold admin registration details.
 */
public class AddAdmin {
    private String fullName;
    private String adminId;
    private String email;
    private String role;

    public AddAdmin(String fullName, String adminId, String email, String role) {
        this.fullName = fullName;
        this.adminId = adminId;
        this.email = email;
        this.role = role;
    }

    public String getFullName() { return fullName; }
    public String getAdminId() { return adminId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}