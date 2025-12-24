package view.konten;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelProduk extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public PanelProduk() {
        initializeUI();
        loadData(); // <--- PASTIKAN INI DIPANGGIL DI CONSTRUCTOR
    }

    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 30", "[grow]", "[]20[grow]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Katalog Kostum");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "wrap");

        // Tambahkan Tombol Refresh
        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.setBackground(new Color(240, 240, 240));
        btnRefresh.addActionListener(e -> loadData()); // Panggil fungsi loadData saat diklik
        add(btnRefresh, "right, wrap"); // Letakkan di kolom kedua (kanan)

        // Header harus sama jumlahnya dengan data di loadData
        String[] columns = {"ID", "Nama Kostum", "Kategori", "Stok", "Ukuran", "Harga Sewa", "Status"};
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit langsung (read-only)
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(35);
        
        add(new JScrollPane(table), "grow");
    }

    // Ubah jadi PUBLIC agar bisa dipanggil dari FrameUtama untuk refresh
    public void loadData() {
        // 1. Kosongkan tabel sebelum diisi ulang
        model.setRowCount(0); 

        try {
            // 2. Ambil koneksi
            Connection conn = DBConfig.getConnection();
            if (conn == null) {
                System.out.println("Koneksi ke database gagal!");
                return;
            }

            // 3. Eksekusi Query
            String sql = "SELECT * FROM kostum";
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery(sql);

            // 4. Looping hasil database ke tabel UI
            while(res.next()) {
                // Ambil nilai double dari database
                double harga = res.getDouble("harga_sewa");
                
                model.addRow(new Object[]{
                    res.getString("id_kostum"),
                    res.getString("nama_kostum"),
                    res.getString("kategori"),
                    res.getInt("stok"),
                    res.getString("ukuran"),
                    // Format %,.0f artinya: Beri pemisah ribuan, dan 0 angka di belakang koma
                    "Rp " + String.format("%,.0f", harga), 
                    res.getString("status")
                });
            }
            
            // Revalidasi UI
            table.repaint();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Load Data: " + e.getMessage());
        }
    }
}