package project.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Myconnection {
    // Static instance of the Connection
    private static Connection connection;

    // Database credentials and URL - adjust these to match your setup
    private static final String URL = "jdbc:mysql://localhost:3306/worldcultura";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Private constructor to prevent instantiation
    private Myconnection() {
    }

    // Public method to get the single instance of the connection
    public static Connection getInstance() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database connection established.");
            } catch (SQLException e) {
                System.err.println("❌ Failed to connect to database: " + e.getMessage());
            }
        }
        return connection;
    }
}
