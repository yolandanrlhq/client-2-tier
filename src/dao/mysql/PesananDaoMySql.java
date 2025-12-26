package dao.mysql;

import java.sql.*;
import java.util.*;
import config.DBConfig;
import dao.PesananDao;
import model.Pesanan;

public class PesananDaoMySql implements PesananDao {

    @Override
    public boolean insert(Pesanan p) throws Exception {
        String sqlSewa = "INSERT INTO pesanan (id_sewa, nama_penyewa, id_kostum, jumlah, tgl_pinjam, total_biaya, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlUpdateStok = "UPDATE kostum SET stok = stok - ? WHERE id_kostum = ?";
        String sqlUpdateStatus = "UPDATE kostum SET status = 'Disewa' WHERE id_kostum = ? AND stok <= 0";

        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false); 
            try {
                // 1. Simpan Pesanan
                try (PreparedStatement pst = conn.prepareStatement(sqlSewa)) {
                    pst.setString(1, p.getIdSewa());
                    pst.setString(2, p.getNamaPenyewa());
                    pst.setString(3, p.getIdKostum());
                    pst.setInt(4, p.getJumlah());
                    pst.setDate(5, new java.sql.Date(p.getTglPinjam().getTime()));
                    pst.setDouble(6, p.getTotalBiaya());
                    pst.setString(7, p.getStatus());
                    pst.executeUpdate();
                }

                // 2. Kurangi Stok
                try (PreparedStatement pstStok = conn.prepareStatement(sqlUpdateStok)) {
                    pstStok.setInt(1, p.getJumlah());
                    pstStok.setString(2, p.getIdKostum());
                    pstStok.executeUpdate();
                }

                // 3. Update Status Kostum jika habis
                try (PreparedStatement pstStat = conn.prepareStatement(sqlUpdateStatus)) {
                    pstStat.setString(1, p.getIdKostum());
                    pstStat.executeUpdate();
                }

                conn.commit(); 
                return true; // Berhasil!
            } catch (SQLException e) {
                conn.rollback(); 
                throw e;
            }
        }
    }

    @Override
    public List<Pesanan> findAll(String keyword) throws Exception {
        List<Pesanan> list = new ArrayList<>();
        String sql = "SELECT p.*, k.nama_kostum FROM pesanan p JOIN kostum k ON p.id_kostum = k.id_kostum " +
                     "WHERE p.id_sewa LIKE ? OR p.nama_penyewa LIKE ? ORDER BY p.tgl_pinjam DESC";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            String key = "%" + keyword + "%";
            pst.setString(1, key); pst.setString(2, key);
            ResultSet res = pst.executeQuery();
            while (res.next()) {
                Pesanan p = new Pesanan();
                p.setIdSewa(res.getString("id_sewa"));
                p.setNamaPenyewa(res.getString("nama_penyewa"));
                p.setNamaKostum(res.getString("nama_kostum"));
                p.setJumlah(res.getInt("jumlah"));
                p.setTglPinjam(res.getDate("tgl_pinjam"));
                p.setTotalBiaya(res.getDouble("total_biaya"));
                p.setStatus(res.getString("status"));
                list.add(p);
            }
        }
        return list;
    }

    @Override
    public boolean delete(String id) throws Exception {
        try (Connection conn = DBConfig.getConnection()) {
            String idK = getIdKostumByIdSewa(id);
            if (idK != null) {
                // Sebaiknya pakai Prepared Statement juga di sini
                String sqlUpdate = "UPDATE kostum SET status='Tersedia', stok = stok + 1 WHERE id_kostum=?";
                try (PreparedStatement pstU = conn.prepareStatement(sqlUpdate)) {
                    pstU.setString(1, idK);
                    pstU.executeUpdate();
                }
            }
            PreparedStatement pst = conn.prepareStatement("DELETE FROM pesanan WHERE id_sewa=?");
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Pesanan p) throws Exception {
        String sql = "UPDATE pesanan SET nama_penyewa=?, id_kostum=?, jumlah=?, total_biaya=?, status=? WHERE id_sewa=?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, p.getNamaPenyewa());
            pst.setString(2, p.getIdKostum());
            pst.setInt(3, p.getJumlah());
            pst.setDouble(4, p.getTotalBiaya());
            pst.setString(5, p.getStatus());
            pst.setString(6, p.getIdSewa());
            return pst.executeUpdate() > 0;
        }
    }

    @Override
    public String getIdKostumByIdSewa(String idSewa) throws Exception {
        String sql = "SELECT id_kostum FROM pesanan WHERE id_sewa=?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, idSewa);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? rs.getString("id_kostum") : null;
        }
    }
}