package dbClasses;

import java.sql.Timestamp;

public class logClass {

    private String userId;
    private String actionType;
    private String description;
    private Timestamp date;
    private String userType;

    public logClass( String userId, String actionType, String description, Timestamp timestamp,String userType) {
        this.userId = userId;
        this.actionType = actionType;
        this.description = description;
        this.date = timestamp;
        this.userType = userType;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getActionType() { return actionType; }
    public String getDescription() { return description; }
    public Timestamp getDate() { return date; }
    public String getUserType() { return userType; }
}