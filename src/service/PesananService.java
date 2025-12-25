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

    public void hapusTransaksi(String id) throws Exception {
        // Di sini bisa tambah validasi sebelum hapus
        dao.delete(id);
    }
}