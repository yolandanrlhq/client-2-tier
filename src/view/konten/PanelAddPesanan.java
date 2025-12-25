package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import view.FrameUtama;
import config.DBConfig;
import model.Pesanan;
import controller.PesananController;

public class PanelAddPesanan extends JPanel {
    private JTextField txtIDSewa, txtTotal;
    private JComboBox<String> cbKostum, cbPenyewa;
    private JSpinner txtJumlah;
    private JButton btnSimpan;
    private double hargaPerUnit = 0;
    private PesananController controller = new PesananController();
    private FrameUtama frameUtama;

    private MigLayout mainLayout;
    private JLabel lblTitle;

    public PanelAddPesanan(FrameUtama frame) {
        this.frameUtama = frame;
        
        mainLayout = new MigLayout("fillx, insets 40", "[right]20[grow, fill]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        setupStaticComponents();
        loadKostumCombo();
        loadPelangganCombo();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { refreshLayout(); }
        });
        
    }

    private void setupStaticComponents() {
        lblTitle = new JLabel("Input Penyewaan Baru");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 28));
        txtIDSewa = new JTextField();
        cbPenyewa = new JComboBox<>();
        cbKostum = new JComboBox<>();
        cbKostum.addActionListener(e -> ambilHargaKostum());
        txtJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        txtJumlah.addChangeListener(e -> hitungTotal());
        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        txtTotal.setFont(new Font("Inter", Font.BOLD, 16));
        txtTotal.setForeground(new Color(20, 100, 40));
        txtTotal.setBackground(new Color(245, 245, 245));

        btnSimpan = new JButton("Simpan & Sewakan");
        btnSimpan.setBackground(new Color(76, 175, 80));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Inter", Font.BOLD, 14));
        btnSimpan.addActionListener(e -> simpanPesananAsync());
    }

    private void simpanPesananAsync() {
        if (cbKostum.getSelectedIndex() <= 0 || cbPenyewa.getSelectedIndex() <= 0 || txtIDSewa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi data!");
            return;
        }

        // 1. Ambil data dari UI dan bungkus ke Objek Model
        Pesanan p = new Pesanan();
        p.setIdSewa(txtIDSewa.getText().trim());
        p.setNamaPenyewa(cbPenyewa.getSelectedItem().toString());
        p.setIdKostum(cbKostum.getSelectedItem().toString().split(" - ")[0]);
        p.setJumlah((int) txtJumlah.getValue());
        p.setTglPinjam(new java.util.Date()); // Hari ini
        p.setTotalBiaya(Double.parseDouble(txtTotal.getText()));
        p.setStatus("Disewa");

        // 2. Tampilkan Progress Dialog
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        JDialog loadingDialog = createProgressDialog(progressBar);

        // 3. Jalankan via Controller
        btnSimpan.setEnabled(false);
        controller.simpanPesanan(p, () -> {
            loadingDialog.dispose();
            btnSimpan.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Pesanan Berhasil Disimpan!");
            resetForm();
            
            // PINDAH KE DAFTAR PESANAN OTOMATIS
            if(frameUtama != null) {
                frameUtama.gantiPanel("pesanan"); 
            }
        });
        
        loadingDialog.setVisible(true);
    }

    // Helper methods (load combo, hitung total, dsb tetap sama namun pastikan rapi)
    public void loadPelangganCombo() {
        cbPenyewa.removeAllItems();
        cbPenyewa.addItem("-- Pilih Pelanggan --");
        try (Connection conn = DBConfig.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT nama_pelanggan FROM pelanggan")) {
            while (rs.next()) cbPenyewa.addItem(rs.getString("nama_pelanggan"));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void loadKostumCombo() {
        cbKostum.removeAllItems();
        cbKostum.addItem("-- Pilih Kostum --");
        try (Connection conn = DBConfig.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT id_kostum, nama_kostum FROM kostum WHERE stok > 0")) {
            while (rs.next()) cbKostum.addItem(rs.getString("id_kostum") + " - " + rs.getString("nama_kostum"));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void ambilHargaKostum() {
        Object selected = cbKostum.getSelectedItem();
        if (selected == null || selected.toString().equals("-- Pilih Kostum --")) { hargaPerUnit = 0; hitungTotal(); return; }
        String idK = selected.toString().split(" - ")[0];
        try (Connection conn = DBConfig.getConnection(); PreparedStatement pst = conn.prepareStatement("SELECT harga_sewa FROM kostum WHERE id_kostum = ?")) {
            pst.setString(1, idK);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) hargaPerUnit = rs.getDouble("harga_sewa");
            hitungTotal();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void hitungTotal() {
        int jumlah = (int) txtJumlah.getValue();
        txtTotal.setText(String.format("%.0f", hargaPerUnit * jumlah));
    }

    private void resetForm() {
        txtIDSewa.setText(""); cbPenyewa.setSelectedIndex(0);
        cbKostum.setSelectedIndex(0); txtJumlah.setValue(1);
        txtTotal.setText("");
    }

    private JDialog createProgressDialog(JProgressBar bar) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Proses", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        p.add(new JLabel("Sedang memproses transaksi..."), BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);
        dialog.add(p);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private void refreshLayout() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window == null) return;
        int w = window.getWidth();
        removeAll();
        if (w <= 768) {
            mainLayout.setLayoutConstraints("fillx, insets 20");
            add(lblTitle, "center, wrap 30");
            add(new JLabel("ID Sewa")); add(txtIDSewa, "growx, wrap 15");
            add(new JLabel("Penyewa")); add(cbPenyewa, "growx, wrap 15");
            add(new JLabel("Kostum")); add(cbKostum, "growx, wrap 15");
            add(new JLabel("Jumlah")); add(txtJumlah, "w 100!, wrap 15");
            add(new JLabel("Total")); add(txtTotal, "growx, wrap 30");
            add(btnSimpan, "growx, h 45!");
        } else {
            mainLayout.setLayoutConstraints("fillx, insets 80 50 80 50");
            add(lblTitle, "span 2, center, wrap 40");
            add(new JLabel("ID Sewa:")); add(txtIDSewa, "wrap 15");
            add(new JLabel("Nama Pelanggan:")); add(cbPenyewa, "wrap 15");
            add(new JLabel("Pilih Kostum:")); add(cbKostum, "wrap 15");
            add(new JLabel("Jumlah Unit:")); add(txtJumlah, "w 100!, wrap 15");
            add(new JLabel("Total Biaya:")); add(txtTotal, "wrap 30");
            add(btnSimpan, "span 2, center, w 250!, h 50!");
        }
        revalidate(); repaint();
    }
}