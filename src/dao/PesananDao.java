package dao;

import java.util.List;
import model.Pesanan;

public interface PesananDao {
    // Ubah void menjadi boolean agar Worker bisa menangkap status sukses
    boolean insert(Pesanan p) throws Exception; 
    
    List<Pesanan> findAll(String keyword) throws Exception;
    
    boolean delete(String id) throws Exception;
    
    boolean update(Pesanan p) throws Exception;
    
    String getIdKostumByIdSewa(String idSewa) throws Exception;
}