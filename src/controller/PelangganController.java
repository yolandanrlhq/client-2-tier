package controller;

import service.PelangganService;
import view.konten.PanelPelanggan;
import model.Pelanggan;
import java.util.List;
import javax.swing.SwingUtilities;

public class PelangganController {
    private PanelPelanggan view;
    private PelangganService service;

    public PelangganController(PanelPelanggan view) {
        this.view = view;
        this.service = new PelangganService();
    }

    public void displayData() {
        List<Pelanggan> list = service.findAll();
        if (view != null) {
            view.getModel().setRowCount(0);
            for (Pelanggan p : list) {
                // p.getId() sekarang mengembalikan "HD001"
                view.getModel().addRow(new Object[]{p.getId(), p.getNama(), p.getNoWa(), p.getAlamat(), "Aksi"});
            }
        }
    }

    public void saveData(Pelanggan p, Runnable callback) {
        new Thread(() -> {
            service.save(p);
            SwingUtilities.invokeLater(callback);
        }).start();
    }

    public void deleteData(String id) { // Ubah parameter jadi String
        service.remove(id);
        displayData();
    }
}