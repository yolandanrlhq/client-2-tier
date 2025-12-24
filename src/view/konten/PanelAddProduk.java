package view.konten;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelAddProduk extends JPanel {
    private JTextField txtID, txtNama, txtHarga;
    private JComboBox<String> cbKategori, cbUkuran;
    private JSpinner txtStok; // Komponen baru untuk jumlah stok

    public PanelAddProduk() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new MigLayout("insets 40", "[right]20[grow, fill]", "[]30[]15[]15[]15[]15[]15[]30[]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Registrasi Kostum Baru");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "span 2, center, wrap");

        add(new JLabel("ID Kostum:"));
        txtID = new JTextField();
        add(txtID, "wrap");

        add(new JLabel("Nama Kostum:"));
        txtNama = new JTextField();
        add(txtNama, "wrap");

        add(new JLabel("Kategori:"));
        cbKategori = new JComboBox<>(new String[]{"Anime", "Superhero", "Tradisional", "Game"});
        add(cbKategori, "wrap");

        add(new JLabel("Jumlah Stok:"));
        txtStok = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        add(txtStok, "w 60!, wrap");

        add(new JLabel("Ukuran:"));
        cbUkuran = new JComboBox<>(new String[]{"S", "M", "L", "XL", "All Size"});
        add(cbUkuran, "wrap");

        add(new JLabel("Harga Sewa:"));
        txtHarga = new JTextField();
        add(txtHarga, "wrap");

        JButton btnSimpan = new JButton("Simpan ke Katalog");
        btnSimpan.setBackground(new Color(131, 188, 160));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.addActionListener(e -> simpanData());
        add(btnSimpan, "span 2, center, w 200!, h 45!");
    }

    private void simpanData() {
        try {
            Connection conn = DBConfig.getConnection();
            String sql = "INSERT INTO kostum (id_kostum, nama_kostum, kategori, stok, ukuran, harga_sewa, status) VALUES (?, ?, ?, ?, ?, ?, 'Tersedia')";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setString(1, txtID.getText());
            pst.setString(2, txtNama.getText());
            pst.setString(3, cbKategori.getSelectedItem().toString());
            pst.setInt(4, (int) txtStok.getValue());
            pst.setString(5, cbUkuran.getSelectedItem().toString());
            pst.setDouble(6, Double.parseDouble(txtHarga.getText()));

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kostum berhasil didaftarkan!");
            resetForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + ex.getMessage());
        }
    }

    private void resetForm() {
        txtID.setText("");
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setValue(1);
    }
}