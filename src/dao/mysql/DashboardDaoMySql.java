package dao.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DBConfig;
import dao.DashboardDao;
import model.DashboardModel;

public class DashboardDaoMySql implements DashboardDao {
    
    @Override
    public DashboardModel getStatistics() {
        // Menggunakan teknik Subquery agar hanya 1x jalan ke database (Efisiensi Tinggi)
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM kostum) as tk, " +
                     "(SELECT COUNT(*) FROM pesanan WHERE status = 'Disewa') as sd, " +
                     "(SELECT SUM(total_biaya) FROM pesanan) as tp";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return new DashboardModel(
                    rs.getInt("tk"), 
                    rs.getInt("sd"), 
                    rs.getDouble("tp")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new DashboardModel(0, 0, 0.0);
    }

    // Melengkapi method yang tadi kamu buat draftnya
    public List<String> getAvailableCostumes() throws Exception {
        List<String> list = new ArrayList<>();
        String sql = "SELECT id_kostum, nama_kostum FROM kostum WHERE stok > 0";
        
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(rs.getString("id_kostum") + " - " + rs.getString("nama_kostum"));
            }
        }
        return list;
    }
}