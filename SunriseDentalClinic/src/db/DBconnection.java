package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/sunrise_dental?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static Connection getConnection() {
        try {
            // Register driver (no need for Class.forName in newer versions)
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database Connected Successfully!");
            return con;
        } catch (SQLException e) {
            System.err.println("❌ Database Connection Failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}