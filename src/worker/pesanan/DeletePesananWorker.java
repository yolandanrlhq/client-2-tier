package worker.pesanan;

import javax.swing.SwingWorker;
import service.PesananService;

public class DeletePesananWorker extends SwingWorker<Boolean, Void> {
    private int id;
    private PesananService service = new PesananService();
    private java.util.function.Consumer<Boolean> callback;

    public DeletePesananWorker(int id, java.util.function.Consumer<Boolean> callback) {
        this.id = id;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        return service.hapusPesanan(id);
    }

    @Override
    protected void done() {
        try {
            callback.accept(get());
        } catch (Exception e) {
            e.printStackTrace();
            callback.accept(false);
        }
    }
}