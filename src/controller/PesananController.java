package controller;

import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import model.Pesanan;
import service.PesananService;
import view.konten.PanelPesanan;
import worker.pesanan.*; 

public class PesananController {
    
    private PanelPesanan view;
    private PesananService service;

    public PesananController(PanelPesanan view) {
        this.view = view;
        this.service = new PesananService();
    }

    public PesananService getService() {
        return this.service;
    }

    // ==========================================
    // METODE UNTUK PANEL ADD PESANAN (FIX)
    // ==========================================

    public List<String> ambilDaftarPelanggan() {
        try {
            return service.getDaftarPelanggan(); // Pastikan di Service ada method ini
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> ambilDaftarKostumTersedia() {
        try {
            return service.getKostumTersedia();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public double getHargaKostum(String id) {
        try {
            return service.getHargaKostumById(id); // Pastikan di Service ada method ini
        } catch (Exception e) {
            return 0;
        }
    }

    public int getStokKostum(String id) {
        try {
            return service.getStokKostumById(id); // Pastikan di Service ada method ini
        } catch (Exception e) {
            return 0;
        }
    }

    // ==========================================
    // DATA WORKERS (TABEL PESANAN)
    // ==========================================

    public void muatData(String keyword) {
        new LoadPesananWorker(keyword, listPesanan -> {
            if (view != null) view.updateTabel(listPesanan);
        }).execute();
    }

    public void ubahData(Pesanan p) {
        new UpdatePesananWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(view, "Data berhasil diperbarui!");
                muatData(""); 
            } else {
                JOptionPane.showMessageDialog(view, "Gagal memperbarui data.");
            }
        }).execute();
    }

    public void hapusDataString(String id) {
        int confirm = JOptionPane.showConfirmDialog(view, 
            "Hapus transaksi " + id + "?\nStok kostum akan dikembalikan otomatis.", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new DeletePesananWorker(id, sukses -> {
                if (sukses) {
                    JOptionPane.showMessageDialog(view, "Data dihapus!");
                    muatData(""); 
                } else {
                    JOptionPane.showMessageDialog(view, "Gagal menghapus data.");
                }
            }).execute();
        }
    }

    public void simpanData(Pesanan p, Runnable callback) {
        new SavePesananWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(null, "Transaksi Berhasil Disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menyimpan transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            if (callback != null) {
                callback.run();
            }
        }).execute();
    }
}