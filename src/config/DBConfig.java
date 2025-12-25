package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    
    public static Connection getConnection() throws SQLException {
        // Jangan simpan di variabel static agar tidak terjadi 'Connection Closed'
        String url = "jdbc:mysql://localhost:3306/db_kostum";
        String user = "root";
        String pass = "";
        
        try {
            // Langsung kembalikan koneksi baru setiap kali dipanggil
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.err.println("Koneksi Gagal: " + e.getMessage());
            throw e; // Lemparkan error agar diketahui oleh pemanggil
        }
    }
}