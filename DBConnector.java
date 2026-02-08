/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SchoolManagementSystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnector {
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/project", 
                "root",                               
                "presidentofTz1!"                     
            );
            System.out.println("Connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver Not Found");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Could not connect to MySQL");
            e.printStackTrace();
        }
        return con;
    }

    public static void main(String[] args) {
        Connection conn = DBConnector.getConnection();
        if (conn == null) {
            System.out.println("Failed to establish connection.");
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users;");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
            }
        } catch (Exception e) {
            System.out.println("Error executing query");
            e.printStackTrace();
        }
    } 
}


