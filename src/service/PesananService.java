package service;

import dao.PesananDao;
import dao.mysql.PesananDaoMySql;
import model.Pesanan;
import java.util.List;

public class PesananService {

    private PesananDao dao = new PesananDaoMySql();

    public List<Pesanan> muatSemuaData(String keyword) throws Exception {
        return dao.findAll(keyword);
    }

    public boolean simpanPesanan(Pesanan p) throws Exception {
        return dao.insert(p);
    }

    public boolean ubahPesanan(Pesanan p) throws Exception {
        // FIX: jaga data lama agar tidak ter-reset (total jadi 0)
        Pesanan dataLama = dao.findById(p.getIdSewa());
        if (dataLama != null) {
            if (p.getTotalBiaya() <= 0) p.setTotalBiaya(dataLama.getTotalBiaya());
            if (p.getJumlah() <= 0) p.setJumlah(dataLama.getJumlah());
            if (p.getIdKostum() == null) p.setIdKostum(dataLama.getIdKostum());
            if (p.getNamaKostum() == null) p.setNamaKostum(dataLama.getNamaKostum());
            if (p.getNamaPenyewa() == null) p.setNamaPenyewa(dataLama.getNamaPenyewa());
            if (p.getTglPinjam() == null) p.setTglPinjam(dataLama.getTglPinjam());
        }
        return dao.update(p);
    }

    public String getIdKostum(String idSewa) throws Exception {
        return dao.getIdKostumByIdSewa(idSewa);
    }

    public boolean hapusPesanan(String id) throws Exception {
        return dao.delete(id);
    }

    // ==========================================
    // METHOD TAMBAHAN UNTUK PANEL ADD PESANAN
    // ==========================================

    /**
     * Mengambil daftar kostum untuk JComboBox
     */
    public List<String> getKostumTersedia() throws Exception {
        return dao.getAvailableCostumes(); 
    }

    /**
     * Mengambil daftar pelanggan untuk JComboBox
     */
    public List<String> getDaftarPelanggan() throws Exception {
        return dao.getPelangganNames(); // Pastikan method ini ada di PesananDao
    }

    /**
     * Mengambil harga sewa unit kostum berdasarkan ID
     */
    public double getHargaKostumById(String id) throws Exception {
        return dao.getCostumePrice(id); // Pastikan method ini ada di PesananDao
    }

    /**
     * Mengambil stok tersedia kostum berdasarkan ID
     */
    public int getStokKostumById(String id) throws Exception {
        return dao.getCostumeStock(id); // Pastikan method ini ada di PesananDao
    }
}