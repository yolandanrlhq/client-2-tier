package dao.mysql;

import dao.PesananDao;
import model.Pesanan;
import config.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PesananDaoMySql implements PesananDao {

    // ==========================================
    // METHOD STANDAR CRUD (SUDAH ADA)
    // ==========================================

    @Override
    public boolean insert(Pesanan p) throws Exception {
        Connection conn = DBConfig.getConnection();
        conn.setAutoCommit(false);
        try {
            // 1. Ambil nama kostum (snapshot)
            String namaKostum = null;
            PreparedStatement psNama = conn.prepareStatement("SELECT nama_kostum FROM kostum WHERE id_kostum=?");
            psNama.setString(1, p.getIdKostum());
            ResultSet rsNama = psNama.executeQuery();
            if (rsNama.next()) {
                namaKostum = rsNama.getString("nama_kostum");
            } else {
                throw new Exception("Kostum tidak ditemukan");
            }

            // 2. Insert pesanan
            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO pesanan (id_sewa, nama_penyewa, id_kostum, nama_kostum, jumlah, tgl_pinjam, total_biaya, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            pst.setString(1, p.getIdSewa());
            pst.setString(2, p.getNamaPenyewa());
            pst.setString(3, p.getIdKostum());
            pst.setString(4, namaKostum);
            pst.setInt(5, p.getJumlah());
            pst.setDate(6, new java.sql.Date(p.getTglPinjam().getTime()));
            pst.setDouble(7, p.getTotalBiaya());
            pst.setString(8, "Disewa");
            pst.executeUpdate();

            // 3. Kurangi stok
            PreparedStatement pstStok = conn.prepareStatement("UPDATE kostum SET stok = stok - ? WHERE id_kostum=?");
            pstStok.setInt(1, p.getJumlah());
            pstStok.setString(2, p.getIdKostum());
            pstStok.executeUpdate();

            // 4. Update status kostum jika stok habis
            PreparedStatement pstStatus = conn.prepareStatement("UPDATE kostum SET status='Disewa' WHERE id_kostum=? AND stok <= 0");
            pstStatus.setString(1, p.getIdKostum());
            pstStatus.executeUpdate();

            conn.commit();
            return true;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    @Override
    public List<Pesanan> findAll(String keyword) throws Exception {
        List<Pesanan> list = new ArrayList<>();
        String sql = "SELECT * FROM pesanan WHERE id_sewa LIKE ? OR nama_penyewa LIKE ? OR nama_kostum LIKE ? ORDER BY tgl_pinjam DESC";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            String key = "%" + keyword + "%";
            pst.setString(1, key);
            pst.setString(2, key);
            pst.setString(3, key);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Pesanan p = new Pesanan();
                p.setIdSewa(rs.getString("id_sewa"));
                p.setNamaPenyewa(rs.getString("nama_penyewa"));
                p.setIdKostum(rs.getString("id_kostum"));
                p.setNamaKostum(rs.getString("nama_kostum"));
                p.setJumlah(rs.getInt("jumlah"));
                p.setTglPinjam(rs.getDate("tgl_pinjam"));
                p.setTotalBiaya(rs.getDouble("total_biaya"));
                p.setStatus(rs.getString("status"));
                list.add(p);
            }
        }
        return list;
    }

    @Override
    public boolean update(Pesanan p) throws Exception {
        Connection conn = DBConfig.getConnection();
        conn.setAutoCommit(false);
        try {
            PreparedStatement pst = conn.prepareStatement("UPDATE pesanan SET status=? WHERE id_sewa=?");
            pst.setString(1, p.getStatus());
            pst.setString(2, p.getIdSewa());
            pst.executeUpdate();

            if (p.getStatus().equalsIgnoreCase("Selesai") || p.getStatus().equalsIgnoreCase("Dibatalkan")) {
                PreparedStatement psGet = conn.prepareStatement("SELECT id_kostum, jumlah FROM pesanan WHERE id_sewa=?");
                psGet.setString(1, p.getIdSewa());
                ResultSet rs = psGet.executeQuery();
                if (rs.next()) {
                    String idK = rs.getString("id_kostum");
                    int jml = rs.getInt("jumlah");
                    PreparedStatement psUpStok = conn.prepareStatement("UPDATE kostum SET stok = stok + ?, status='Tersedia' WHERE id_kostum=?");
                    psUpStok.setInt(1, jml);
                    psUpStok.setString(2, idK);
                    psUpStok.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        Connection conn = DBConfig.getConnection();
        conn.setAutoCommit(false);
        try {
            Pesanan p = findById(id);
            PreparedStatement psDel = conn.prepareStatement("DELETE FROM pesanan WHERE id_sewa=?");
            psDel.setString(1, id);
            psDel.executeUpdate();

            PreparedStatement psRest = conn.prepareStatement("UPDATE kostum SET stok = stok + ?, status='Tersedia' WHERE id_kostum=?");
            psRest.setInt(1, p.getJumlah());
            psRest.setString(2, p.getIdKostum());
            psRest.executeUpdate();

            conn.commit();
            return true;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    @Override
    public Pesanan findById(String id) throws Exception {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM pesanan WHERE id_sewa=?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pesanan p = new Pesanan();
                p.setIdSewa(rs.getString("id_sewa"));
                p.setNamaPenyewa(rs.getString("nama_penyewa"));
                p.setIdKostum(rs.getString("id_kostum"));
                p.setNamaKostum(rs.getString("nama_kostum"));
                p.setJumlah(rs.getInt("jumlah"));
                p.setTglPinjam(rs.getDate("tgl_pinjam"));
                p.setTotalBiaya(rs.getDouble("total_biaya"));
                p.setStatus(rs.getString("status"));
                return p;
            }
        }
        throw new Exception("Pesanan tidak ditemukan");
    }

    @Override
    public String getIdKostumByIdSewa(String idSewa) throws Exception {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id_kostum FROM pesanan WHERE id_sewa=?")) {
            ps.setString(1, idSewa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("id_kostum");
        }
        throw new Exception("ID Kostum tidak ditemukan");
    }

    // ==========================================
    // METHOD BARU UNTUK INPUT PESANAN (FIX FINAL)
    // ==========================================

    @Override
    public List<String> getAvailableCostumes() throws Exception {
        List<String> list = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_kostum, nama_kostum FROM kostum WHERE stok > 0")) {
            while (rs.next()) {
                list.add(rs.getString("id_kostum") + " - " + rs.getString("nama_kostum"));
            }
        }
        return list;
    }

    @Override
    public List<String> getPelangganNames() throws Exception {
        List<String> list = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT nama_pelanggan FROM pelanggan ORDER BY nama_pelanggan ASC")) {
            while (rs.next()) {
                list.add(rs.getString("nama_pelanggan"));
            }
        }
        return list;
    }

    @Override
    public double getCostumePrice(String idKostum) throws Exception {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT harga_sewa FROM kostum WHERE id_kostum=?")) {
            ps.setString(1, idKostum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("harga_sewa");
        }
        return 0;
    }

    @Override
    public int getCostumeStock(String idKostum) throws Exception {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT stok FROM kostum WHERE id_kostum=?")) {
            ps.setString(1, idKostum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("stok");
        }
        return 0;
    }
}