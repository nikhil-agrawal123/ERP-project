package databaseConfig;

import java.sql.*;

public class Connector {
    private final static String url = "jdbc:mysql://localhost:3306/Auth";
    private final static String user = "root";
    private final static String password = "Nikhil@123";

    /**
     * Creates and returns a new database connection.
     * Returns null if the connection fails.
     */
    public Connection connect() { // Renamed for clarity
        Connection connection = null;
        try {
            System.out.println("Trying to connect to database...");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully! ✅");
        } catch (SQLException e) {
            System.out.println("Connection failed! " + e.getMessage());
        }
        return connection;
    }
}