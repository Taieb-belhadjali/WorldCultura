package WorldCultura.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static MyDatabase instance;
    private final String URL = "jdbc:mysql://127.0.0.1:3306/worldculturaaa";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private Connection cnx;

    private MyDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Force load MySQL driver
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ Connected to database.");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to the database: " + e.getMessage());
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            synchronized (MyDatabase.class) {
                if (instance == null) {
                    instance = new MyDatabase();
                }
            }
        }
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
}
