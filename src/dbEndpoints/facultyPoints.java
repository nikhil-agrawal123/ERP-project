package dbEndpoints;

import databaseConfig.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class facultyPoints {

    private final Connector connector;

    public facultyPoints() {
        this.connector = new Connector();
    }

    public String getFacultyId(String userId) {
        String instructorQuery = "SELECT instructor_id FROM users.instructors WHERE user_id = ?";

        try(Connection connection = connector.connect();
            PreparedStatement ptsm = connection.prepareStatement(instructorQuery);
        ){
            ptsm.setString(1, userId);
            ResultSet rs = ptsm.executeQuery();
            if(rs.next()){
                return rs.getString("instructor_id");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
