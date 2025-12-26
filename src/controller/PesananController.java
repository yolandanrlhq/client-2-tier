package controller;

import java.util.List;
import javax.swing.JOptionPane;
import model.Pesanan;
import view.konten.PanelPesanan;
import worker.pesanan.*; // Import semua worker pesanan

public class PesananController {
    
    private PanelPesanan view;

    public PesananController(PanelPesanan view) {
        this.view = view;
    }

    // 1. LOAD DATA (Tampil ke Tabel)
    public void muatData(String keyword) {
        new LoadPesananWorker(keyword, listPesanan -> {
            view.updateTabel(listPesanan);
        }).execute();
    }

    // 2. SIMPAN DATA (Add Pesanan)
    public void simpanData(Pesanan p, Runnable onSuccess) {
        new SavePesananWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(null, "Pesanan berhasil disimpan!");
                onSuccess.run(); // Biasanya untuk reset form
                muatData("");    // Refresh tabel otomatis
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menyimpan pesanan.");
            }
        }).execute();
    }

    // 3. UPDATE DATA
    public void ubahData(Pesanan p) {
        new UpdatePesananWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(null, "Data berhasil diperbarui!");
                muatData(""); 
            } else {
                JOptionPane.showMessageDialog(null, "Gagal memperbarui data.");
            }
        }).execute();
    }

    // 4. DELETE DATA
    public void hapusData(int id) {
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new DeletePesananWorker(id, sukses -> {
                if (sukses) {
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
                    muatData(""); 
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal menghapus data.");
                }
            }).execute();
        }
    }
}