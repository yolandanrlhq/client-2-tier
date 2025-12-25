package dao.mysql;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import config.DBConfig;
import dao.DashboardDao;

public class DashboardDaoMySql implements DashboardDao {
    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = new HashMap<>();
        String sql1 = "SELECT COUNT(*) FROM kostum";
        String sql2 = "SELECT COUNT(*) FROM pesanan WHERE status = 'Disewa'";
        String sql3 = "SELECT SUM(total_biaya) FROM pesanan";

        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement()) {
            
            ResultSet rs1 = st.executeQuery(sql1);
            if (rs1.next()) stats.put("total_kostum", rs1.getDouble(1));

            ResultSet rs2 = st.executeQuery(sql2);
            if (rs2.next()) stats.put("sedang_disewa", rs2.getDouble(1));

            ResultSet rs3 = st.executeQuery(sql3);
            if (rs3.next()) stats.put("total_pendapatan", rs3.getDouble(1));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}