package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sunrise_dental_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static boolean messageShown = false;

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);

            if (!messageShown) {
                System.out.println("Database Connected Successfully!");
                messageShown = true;
            }

            return con;

        } catch (SQLException e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
            return null;
        }
    }
}