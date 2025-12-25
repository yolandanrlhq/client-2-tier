package controller;

import dao.PesananDao;
import dao.mysql.PesananDaoMySql;
import model.Pesanan;
import javax.swing.*;

public class PesananController {
    private PesananDao dao = new PesananDaoMySql();

    public void simpanPesanan(Pesanan p, Runnable onSuccess) {
        new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Proses DAO (Koki bekerja di dapur)
                dao.insert(p); 
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Cek jika ada error
                    onSuccess.run(); // Jalankan reset form di View
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Gagal Simpan: " + e.getMessage());
                }
            }
        }.execute();
    }
}