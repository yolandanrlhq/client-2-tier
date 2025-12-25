package dao;
import java.util.Map;

public interface DashboardDao {
    Map<String, Double> getStatistics();
}