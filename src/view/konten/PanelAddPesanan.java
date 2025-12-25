package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelAddPesanan extends JPanel {
    private JTextField txtIDSewa, txtPenyewa, txtTotal;
    private JComboBox<String> cbKostum;
    private JSpinner txtJumlah;
    private JButton btnSimpan; // Jadikan variabel class agar tidak dibuat ulang terus
    private double hargaPerUnit = 0;
    
    private MigLayout mainLayout;
    private JLabel lblTitle;

    public PanelAddPesanan() {
        // Gunakan layout manager yang konsisten
        mainLayout = new MigLayout("fillx, insets 40", "[right]20[grow, fill]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        setupStaticComponents(); // Inisialisasi komponen sekali saja
        loadKostumCombo();

        // Listener Responsif
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshLayout();
            }
        });
    }

    private void setupStaticComponents() {
        lblTitle = new JLabel("Input Penyewaan Baru");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 28));
        
        txtIDSewa = new JTextField();
        txtPenyewa = new JTextField();
        
        cbKostum = new JComboBox<>();
        cbKostum.addItem("-- Pilih Kostum --");
        cbKostum.addActionListener(e -> ambilHargaKostum());

        txtJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        txtJumlah.addChangeListener(e -> hitungTotal());

        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        txtTotal.setBackground(new Color(245, 245, 245));

        btnSimpan = new JButton("Simpan & Sewakan");
        btnSimpan.setBackground(new Color(76, 175, 80));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Inter", Font.BOLD, 14));
        btnSimpan.addActionListener(e -> simpanPesanan());
    }

    private void refreshLayout() {
        Window window = SwingUtilities.getWindowAncestor(this);
if (window == null) return;

int w = window.getWidth();

        if (w <= 0) return;

        removeAll();

        // ================= SPLIT VIEW =================
        if (w <= 768) {
            mainLayout.setLayoutConstraints("fillx, insets 20");
            mainLayout.setColumnConstraints("[grow, fill]");
            lblTitle.setFont(new Font("Inter", Font.BOLD, 22));

            add(lblTitle, "center, wrap 30");

            add(new JLabel("ID Sewa")); add(txtIDSewa, "growx, wrap 15");
            add(new JLabel("Nama Penyewa")); add(txtPenyewa, "growx, wrap 15");
            add(new JLabel("Pilih Kostum")); add(cbKostum, "growx, wrap 15");
            add(new JLabel("Jumlah Unit")); add(txtJumlah, "w 100!, wrap 15");
            add(new JLabel("Total Biaya")); add(txtTotal, "growx, wrap 30");

            add(btnSimpan, "growx, span 2, h 45!");

        // ================= DESKTOP SMALL =================
        } else if (w <= 1200) {
            mainLayout.setLayoutConstraints("fillx, insets 40 8% 40 8%");
            mainLayout.setColumnConstraints("[right]20[grow, fill]");
            lblTitle.setFont(new Font("Inter", Font.BOLD, 26));

            add(lblTitle, "span 2, center, wrap 40");

            add(new JLabel("ID Sewa")); add(txtIDSewa, "wrap 15");
            add(new JLabel("Nama Penyewa")); add(txtPenyewa, "wrap 15");
            add(new JLabel("Pilih Kostum")); add(cbKostum, "wrap 15");
            add(new JLabel("Jumlah Unit")); add(txtJumlah, "w 90!, wrap 15");
            add(new JLabel("Total Biaya")); add(txtTotal, "wrap 30");

            add(btnSimpan, "span 2, center, h 45!");

        // ================= DESKTOP STANDARD =================
        } else {
            mainLayout.setLayoutConstraints("fillx, insets 60 20% 60 20%");
            mainLayout.setColumnConstraints("[right]25[grow, fill]");
            lblTitle.setFont(new Font("Inter", Font.BOLD, 32));

            add(lblTitle, "span 2, center, wrap 45");

            add(new JLabel("ID Sewa")); add(txtIDSewa, "wrap 18");
            add(new JLabel("Nama Penyewa")); add(txtPenyewa, "wrap 18");
            add(new JLabel("Pilih Kostum")); add(cbKostum, "wrap 18");
            add(new JLabel("Jumlah Unit")); add(txtJumlah, "w 100!, wrap 18");
            add(new JLabel("Total Biaya")); add(txtTotal, "wrap 40");

            add(btnSimpan, "span 2, center, h 48!");
        }

        revalidate();
        repaint();
    }

    // --- SEMUA LOGIKA DATABASE KAMU DI BAWAH INI TETAP SAMA ---
    public void loadKostumCombo() {
        cbKostum.removeAllItems();

        String sql = "SELECT nama_kostum FROM kostum";

        try (Connection conn = DBConfig.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                cbKostum.addItem(rs.getString("nama_kostum"));
            }

        } catch (SQLException e) {
            System.err.println("Gagal load kostum: " + e.getMessage());
        }
    }

    private void ambilHargaKostum() {
        Object selectedItem = cbKostum.getSelectedItem();
        if (selectedItem == null || selectedItem.toString().equals("-- Pilih Kostum --")) {
            hargaPerUnit = 0;
            hitungTotal();
            return;
        }
        String idKostum = selectedItem.toString().split(" - ")[0];
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT harga_sewa FROM kostum WHERE id_kostum = ?");
            pst.setString(1, idKostum);
            ResultSet res = pst.executeQuery();
            if (res.next()) hargaPerUnit = res.getDouble("harga_sewa");
            hitungTotal();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void hitungTotal() {
        int jumlah = (int) txtJumlah.getValue();
        txtTotal.setText(String.format("%.0f", hargaPerUnit * jumlah));
    }

    private void simpanPesanan() {
        if (cbKostum.getSelectedIndex() <= 0 || txtIDSewa.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi data terlebih dahulu!");
            return;
        }
        try {
            Connection conn = DBConfig.getConnection();
            String sql = "INSERT INTO pesanan (id_sewa, nama_penyewa, id_kostum, jumlah, total_biaya, status, tgl_pinjam) VALUES (?, ?, ?, ?, ?, 'Disewa', CURDATE())";
            PreparedStatement pst = conn.prepareStatement(sql);
            String idK = cbKostum.getSelectedItem().toString().split(" - ")[0];
            pst.setString(1, txtIDSewa.getText());
            pst.setString(2, txtPenyewa.getText());
            pst.setString(3, idK);
            pst.setInt(4, (int) txtJumlah.getValue());
            pst.setDouble(5, Double.parseDouble(txtTotal.getText()));
            pst.executeUpdate();
            conn.createStatement().executeUpdate("UPDATE kostum SET status='Disewa' WHERE id_kostum='" + idK + "'");
            JOptionPane.showMessageDialog(this, "Pesanan Berhasil Disimpan!");
            resetForm();
            loadKostumCombo();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void resetForm() {
        txtIDSewa.setText(""); txtPenyewa.setText("");
        cbKostum.setSelectedIndex(0); txtJumlah.setValue(1);
        txtTotal.setText("");
    }
}