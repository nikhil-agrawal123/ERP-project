package dbEndpoints;

import databaseConfig.Connector;
import dbClasses.NewStudent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class adminPoints {
    private Connector connector;

    public adminPoints() {
        this.connector = new  Connector();
    }

    public boolean addStudent(NewStudent user) throws SQLException {
        String sql = "INSERT INTO users.students (student_roll_no, full_name,program,enrollment_year, student_email,  currentSem,user_id) VALUES (?,?,?,?,?,?,?)";

        try(Connection conn = connector.connect();
            PreparedStatement pstm = conn.prepareStatement(sql)
        ){
            pstm.setString(1, user.getStudent_roll_no());
            pstm.setString(2, user.getStudent_name());
            pstm.setString(3, user.getStudent_program());
            pstm.setInt(4, user.getStudent_enrollment_year());
            pstm.setString(5, user.getStudent_email());
            pstm.setInt(6,user.getStudent_current_sem());
            pstm.setString(7,user.getStudent_id());

            if(pstm.executeUpdate()>0) return true;

        }catch (SQLException e){
            e.printStackTrace();
            }
        return false;
    }
}
