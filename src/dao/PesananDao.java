package dao;

import java.util.List;
import model.Pesanan;

public interface PesananDao {
    // Method Standar CRUD Pesanan
    List<Pesanan> findAll(String keyword) throws Exception;
    boolean insert(Pesanan p) throws Exception;
    boolean update(Pesanan p) throws Exception;
    boolean delete(String id) throws Exception;
    Pesanan findById(String id) throws Exception;
    
    // Method Helper untuk Relasi Data
    String getIdKostumByIdSewa(String idSewa) throws Exception;

    // =======================================================
    // TAMBAHAN UNTUK MENDUKUNG PANEL ADD PESANAN (FIX FINAL)
    // =======================================================
    
    /** Mengambil daftar ID - Nama Kostum yang stoknya > 0 */
    List<String> getAvailableCostumes() throws Exception;

    /** Mengambil semua nama pelanggan untuk JComboBox */
    List<String> getPelangganNames() throws Exception;

    /** Mengambil harga sewa per unit berdasarkan ID Kostum */
    double getCostumePrice(String idKostum) throws Exception;

    /** Mengambil sisa stok berdasarkan ID Kostum */
    int getCostumeStock(String idKostum) throws Exception;
}