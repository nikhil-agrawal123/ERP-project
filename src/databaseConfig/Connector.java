package databaseConfig;

import java.sql.*;

public class Connector {
    private final static String url = "jdbc:mysql://localhost:3306/Auth";
    private final static String user = "root";
    private final static String password = "Nikhil@123";

    public void connector(){
        System.out.println("Trying to connect to database...");
        try(
                Connection connection = DriverManager.getConnection(url,user,password);
        ){
            System.out.println("Connected to database successfully! ✅");
        }catch (SQLException e){
            // This exception would likely be "No suitable driver found..."
            System.out.println("Connection failed! " + e.getMessage());
        }
    }
}