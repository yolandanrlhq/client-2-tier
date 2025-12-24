package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    private static Connection conn;
    public static Connection getConnection() {
        if (conn == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/db_kostum";
                String user = "root";
                String pass = "";
                conn = DriverManager.getConnection(url, user, pass);
            } catch (SQLException e) {
                System.err.println("Koneksi Gagal: " + e.getMessage());
            }
        }
        return conn;
    }
}