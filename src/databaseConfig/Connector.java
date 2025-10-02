package databaseConfig;

import java.sql.*;

public class Connector {
    private final static String url = System.getenv("SQL_URL");
    private final static String user = System.getenv("SQL_USER");
    private final static String password = System.getenv("SQL_PASSWORD");

    public void connector(){
        System.out.println("Trying to connect to database...");
        try(
                Connection connection = DriverManager.getConnection(url,user,password);
        ){
            System.out.println("Connected to database successfully! ✅");
        }catch (SQLException e){
            System.out.println("Connection failed! " + e.getMessage());
        }
    }
}