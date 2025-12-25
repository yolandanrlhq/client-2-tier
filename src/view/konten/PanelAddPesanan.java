package view.konten;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelAddPesanan extends JPanel {
    private JTextField txtIDSewa, txtPenyewa, txtTotal;
    private JComboBox<String> cbKostum;
    private JSpinner txtJumlah;
    private double hargaPerUnit = 0;

    public PanelAddPesanan() {
        initializeUI();
        loadKostumCombo();
    }

    private void initializeUI() {
        setLayout(new MigLayout("insets 40", "[right]20[grow, fill]", "[]30[]15[]15[]15[]15[]15[]30[]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Input Penyewaan Baru");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "span 2, center, wrap");

        add(new JLabel("ID Sewa:"));
        txtIDSewa = new JTextField();
        add(txtIDSewa, "wrap");

        add(new JLabel("Nama Penyewa:"));
        txtPenyewa = new JTextField();
        add(txtPenyewa, "wrap");

        add(new JLabel("Pilih Kostum:"));
        cbKostum = new JComboBox<>();
        cbKostum.addItem("-- Pilih Kostum --");
        cbKostum.addActionListener(e -> ambilHargaKostum());
        add(cbKostum, "wrap");

        add(new JLabel("Jumlah Unit:"));
        txtJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        txtJumlah.addChangeListener(e -> hitungTotal());
        add(txtJumlah, "w 80!, wrap");

        add(new JLabel("Total Biaya (Rp):"));
        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        txtTotal.setBackground(new Color(245, 245, 245));
        add(txtTotal, "wrap");

        JButton btnSimpan = new JButton("Simpan & Sewakan");
        btnSimpan.setBackground(new Color(76, 175, 80));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Inter", Font.BOLD, 14));
        btnSimpan.addActionListener(e -> simpanPesanan());
        add(btnSimpan, "span 2, center, w 220!, h 45!");
    }

    private void loadKostumCombo() {
        cbKostum.removeAllItems();
        cbKostum.addItem("-- Pilih Kostum --");
        try {
            Connection conn = DBConfig.getConnection();
            String sql = "SELECT id_kostum, nama_kostum FROM kostum WHERE status = 'Tersedia'";
            ResultSet res = conn.createStatement().executeQuery(sql);
            while(res.next()) {
                cbKostum.addItem(res.getString("id_kostum") + " - " + res.getString("nama_kostum"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void ambilHargaKostum() {
        // 1. CEK NULL TERLEBIH DAHULU (PENTING!)
        Object selectedItem = cbKostum.getSelectedItem();
        
        if (selectedItem == null || selectedItem.toString().equals("-- Pilih Kostum --")) {
            hargaPerUnit = 0;
            hitungTotal();
            return;
        }

        // 2. Jika tidak null, baru jalankan logika ambil harga
        String selected = selectedItem.toString();
        String idKostum = selected.split(" - ")[0];
        
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT harga_sewa FROM kostum WHERE id_kostum = ?");
            pst.setString(1, idKostum);
            ResultSet res = pst.executeQuery();
            if (res.next()) {
                hargaPerUnit = res.getDouble("harga_sewa");
            }
            hitungTotal();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hitungTotal() {
        int jumlah = (int) txtJumlah.getValue();
        double total = hargaPerUnit * jumlah;
        txtTotal.setText(String.format("%.0f", total));
    }

    private void simpanPesanan() {
        if (cbKostum.getSelectedIndex() == 0 || txtIDSewa.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi data terlebih dahulu!");
            return;
        }
        try {
            Connection conn = DBConfig.getConnection();
            // Status default diset langsung sebagai 'Disewa'
            String sql = "INSERT INTO pesanan (id_sewa, nama_penyewa, id_kostum, jumlah, total_biaya, status, tgl_pinjam) VALUES (?, ?, ?, ?, ?, 'Disewa', CURDATE())";
            PreparedStatement pst = conn.prepareStatement(sql);
            String idK = cbKostum.getSelectedItem().toString().split(" - ")[0];

            pst.setString(1, txtIDSewa.getText());
            pst.setString(2, txtPenyewa.getText());
            pst.setString(3, idK);
            pst.setInt(4, (int) txtJumlah.getValue());
            pst.setDouble(5, Double.parseDouble(txtTotal.getText()));
            pst.executeUpdate();

            // Kostum otomatis jadi 'Disewa'
            conn.createStatement().executeUpdate("UPDATE kostum SET status='Disewa' WHERE id_kostum='" + idK + "'");

            JOptionPane.showMessageDialog(this, "Pesanan Berhasil Disimpan (Status: Disewa)");
            resetForm();
            loadKostumCombo(); // Refresh daftar kostum agar yang baru disewa hilang dari pilihan
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void resetForm() {
        txtIDSewa.setText(""); txtPenyewa.setText("");
        cbKostum.setSelectedIndex(0); txtJumlah.setValue(1);
        txtTotal.setText("");
    }
}