package worker.pesanan;

import javax.swing.*;
import java.util.List;
import model.Pesanan;
import service.PesananService;

public class LoadPesananWorker extends SwingWorker<List<Pesanan>, Void> {
    private PesananService service = new PesananService();
    private String keyword;
    private java.util.function.Consumer<List<Pesanan>> callback;

    public LoadPesananWorker(String keyword, java.util.function.Consumer<List<Pesanan>> callback) {
        this.keyword = keyword;
        this.callback = callback;
    }

    @Override
    protected List<Pesanan> doInBackground() throws Exception {
        return service.muatSemuaData(keyword); // Kerja keras di background
    }

    @Override
    protected void done() {
        try {
            callback.accept(get()); // Kirim hasil ke UI kalau sudah selesai
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}