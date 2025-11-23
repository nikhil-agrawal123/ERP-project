package dbClasses;

public class facultyClass {
    private String passwordHash;

    public facultyClass(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}