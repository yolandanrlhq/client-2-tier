package service;

import dao.PesananDao;
import dao.mysql.PesananDaoMySql;
import model.Pesanan;
import java.util.List;

public class PesananService {
    private PesananDao dao = new PesananDaoMySql();

    // 1. Ambil Data
    public List<Pesanan> muatSemuaData(String keyword) throws Exception {
        return dao.findAll(keyword);
    }

    // 2. Simpan Data (Add)
    public boolean simpanPesanan(Pesanan p) throws Exception {
        // Di sini kamu bisa tambah logika bisnis, misal:
        // if (p.getJumlah() > 10) throw new Exception("Maksimal sewa 10 unit");
        return dao.insert(p);
    }

    // 3. Update Data (Edit)
    public boolean ubahPesanan(Pesanan p) throws Exception {
        return dao.update(p);
    }

    // 4. Hapus Data
    public boolean hapusPesanan(int id) throws Exception {
        // Kita ubah parameter ke int agar konsisten dengan ID di database
        return dao.delete(String.valueOf(id));
    }
}