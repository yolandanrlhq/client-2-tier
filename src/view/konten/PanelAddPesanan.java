package view.konten;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelAddPesanan extends JPanel {
    private JTextField txtIDSewa, txtPenyewa, txtTotal;
    private JComboBox<String> cbKostum;
    private JSpinner txtJumlah;
    private double hargaPerUnit = 0; // Untuk menyimpan harga kostum yang dipilih

    public PanelAddPesanan() {
        initializeUI();
        loadKostumCombo();
    }

    private void initializeUI() {
        setLayout(new MigLayout("insets 40", "[right]20[grow, fill]", "[]30[]15[]15[]15[]15[]15[]30[]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Tambah Pesanan Baru");
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
        // Listener saat kostum dipilih (ambil harga)
        cbKostum.addActionListener(e -> ambilHargaKostum());
        add(cbKostum, "wrap");

        add(new JLabel("Jumlah:"));
        txtJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        // Listener saat jumlah berubah (hitung total)
        txtJumlah.addChangeListener(e -> hitungTotal());
        add(txtJumlah, "w 60!, wrap");

        add(new JLabel("Total Biaya (Rp):"));
        txtTotal = new JTextField();
        txtTotal.setEditable(false); // Biar dihitung sistem, bukan diketik user
        txtTotal.setBackground(new Color(245, 245, 245));
        add(txtTotal, "wrap");

        JButton btnSimpan = new JButton("Proses Penyewaan");
        btnSimpan.setBackground(new Color(131, 188, 160));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.addActionListener(e -> simpanPesanan());
        add(btnSimpan, "span 2, center, w 200!, h 45!");
    }

    private void loadKostumCombo() {
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
        String selected = cbKostum.getSelectedItem().toString();
        if (selected.equals("-- Pilih Kostum --")) {
            hargaPerUnit = 0;
            hitungTotal();
            return;
        }
        
        String idKostum = selected.split(" - ")[0];
        try {
            Connection conn = DBConfig.getConnection();
            String sql = "SELECT harga_sewa FROM kostum WHERE id_kostum = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, idKostum);
            ResultSet res = pst.executeQuery();
            if (res.next()) {
                hargaPerUnit = res.getDouble("harga_sewa");
            }
            hitungTotal();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void hitungTotal() {
        int jumlah = (int) txtJumlah.getValue();
        double total = hargaPerUnit * jumlah;
        txtTotal.setText(String.valueOf(total));
    }

    private void simpanPesanan() {
        if (cbKostum.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih kostum dulu!");
            return;
        }

        try {
            Connection conn = DBConfig.getConnection();
            String sql = "INSERT INTO pesanan (id_sewa, nama_penyewa, id_kostum, jumlah, total_biaya, status, tgl_pinjam) VALUES (?, ?, ?, ?, ?, ?, CURDATE())";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            String idKostum = cbKostum.getSelectedItem().toString().split(" - ")[0];

            pst.setString(1, txtIDSewa.getText());
            pst.setString(2, txtPenyewa.getText());
            pst.setString(3, idKostum);
            pst.setInt(4, (int) txtJumlah.getValue());
            pst.setDouble(5, Double.parseDouble(txtTotal.getText()));
            pst.setString(6, "Disewa");

            pst.executeUpdate();
            
            // Update status kostum jadi 'Disewa'
            conn.createStatement().executeUpdate("UPDATE kostum SET status='Disewa' WHERE id_kostum='" + idKostum + "'");
            
            JOptionPane.showMessageDialog(this, "Sewa Berhasil Disimpan!");
            resetForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal: " + e.getMessage());
        }
    }

    private void resetForm() {
        txtIDSewa.setText("");
        txtPenyewa.setText("");
        cbKostum.setSelectedIndex(0);
        txtJumlah.setValue(1);
        txtTotal.setText("");
    }
}