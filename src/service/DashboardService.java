package service;
import dao.mysql.DashboardDaoMySql;
import java.util.Map;

public class DashboardService {
    private DashboardDaoMySql dao = new DashboardDaoMySql();
    
    public Map<String, Double> muatStatistik() {
        return dao.getStatistics();
    }
}