package db;

import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {
        Connection conn = DBconnection.getConnection();

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}