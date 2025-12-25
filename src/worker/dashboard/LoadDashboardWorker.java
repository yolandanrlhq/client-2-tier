package worker.dashboard;
import javax.swing.SwingWorker;
import java.util.Map;
import service.DashboardService;

public class LoadDashboardWorker extends SwingWorker<Map<String, Double>, Void> {
    private DashboardService service = new DashboardService();
    private java.util.function.Consumer<Map<String, Double>> callback;

    public LoadDashboardWorker(java.util.function.Consumer<Map<String, Double>> callback) {
        this.callback = callback;
    }

    @Override
    protected Map<String, Double> doInBackground() {
        return service.muatStatistik();
    }

    @Override
    protected void done() {
        try {
            callback.accept(get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}