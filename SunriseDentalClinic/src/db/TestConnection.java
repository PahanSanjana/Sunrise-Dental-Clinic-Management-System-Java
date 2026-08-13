package db;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        Connection conn = DBconnection.getConnection();
        if (conn != null) {
            System.out.println("✅ Connection successful!");
            try {
                conn.close();
                System.out.println("✅ Connection closed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ Connection failed!");
        }
    }
}