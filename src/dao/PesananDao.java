package dao;

import java.util.List;
import model.Pesanan;

public interface PesananDao {
    void insert(Pesanan p) throws Exception; // TAMBAHKAN INI
    List<Pesanan> findAll(String keyword) throws Exception;
    void delete(String id) throws Exception;
    void update(Pesanan p) throws Exception;
    String getIdKostumByIdSewa(String idSewa) throws Exception;
}