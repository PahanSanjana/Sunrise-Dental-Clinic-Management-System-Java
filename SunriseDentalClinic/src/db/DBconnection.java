/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author HP
 */
public class DBconnection {
    
    public static Connection getConnection() {

        Connection con = null;

        try {
            // Using the modern driver class name for Connector/J 8.x / 9.x
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Updated database name to sunrise_dental
            String url = "jdbc:mysql://127.0.0.1:3306/sunrise_dental?useSSL=false&serverTimezone=UTC";

            con = DriverManager.getConnection(url, "root", "");

            System.out.println("Database Connected Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return con;
    }
}