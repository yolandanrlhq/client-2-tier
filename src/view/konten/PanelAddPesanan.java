package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelAddPesanan extends JPanel {
    private JTextField txtIDSewa, txtTotal;
    private JComboBox<String> cbKostum, cbPenyewa; // txtPenyewa diganti cbPenyewa
    private JSpinner txtJumlah;
    private JButton btnSimpan;
    private double hargaPerUnit = 0;
    
    private MigLayout mainLayout;
    private JLabel lblTitle;

    public PanelAddPesanan() {
        mainLayout = new MigLayout("fillx, insets 40", "[right]20[grow, fill]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        setupStaticComponents();
        loadKostumCombo();
        loadPelangganCombo(); // Load data pelanggan

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
        
        // Inisialisasi Combo Pelanggan
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
        btnSimpan.setFocusPainted(false);
        btnSimpan.setFont(new Font("Inter", Font.BOLD, 14));
        btnSimpan.addActionListener(e -> simpanPesananAsync());
    }

    private void refreshLayout() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window == null) return;
        int w = window.getWidth();
        if (w <= 0) return;

        removeAll();
        if (w <= 768) {
            mainLayout.setLayoutConstraints("fillx, insets 20");
            lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
            add(lblTitle, "center, wrap 30");
            add(new JLabel("ID Sewa")); add(txtIDSewa, "growx, wrap 15");
            add(new JLabel("Penyewa")); add(cbPenyewa, "growx, wrap 15");
            add(new JLabel("Kostum")); add(cbKostum, "growx, wrap 15");
            add(new JLabel("Jumlah")); add(txtJumlah, "w 100!, wrap 15");
            add(new JLabel("Total")); add(txtTotal, "growx, wrap 30");
            add(btnSimpan, "growx, h 45!");
        } else {
            mainLayout.setLayoutConstraints("fillx, insets 80 50 80 50");
            lblTitle.setFont(new Font("Inter", Font.BOLD, 32));
            add(lblTitle, "span 2, center, wrap 40");
            add(new JLabel("ID Sewa:")); add(txtIDSewa, "wrap 15");
            add(new JLabel("Nama Pelanggan:")); add(cbPenyewa, "wrap 15");
            add(new JLabel("Pilih Kostum:")); add(cbKostum, "wrap 15");
            add(new JLabel("Jumlah Unit:")); add(txtJumlah, "w 100!, wrap 15");
            add(new JLabel("Total Biaya:")); add(txtTotal, "wrap 30");
            add(btnSimpan, "span 2, center, w 250!, h 50!");
        }
        revalidate();
        repaint();
    }

    public void loadPelangganCombo() {
        cbPenyewa.removeAllItems();
        cbPenyewa.addItem("-- Pilih Pelanggan --");
        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT nama_pelanggan FROM pelanggan")) {
            while (rs.next()) cbPenyewa.addItem(rs.getString("nama_pelanggan"));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void loadKostumCombo() {
        cbKostum.removeAllItems();
        cbKostum.addItem("-- Pilih Kostum --");
        // Hanya munculkan yang stoknya > 0
        String sql = "SELECT id_kostum, nama_kostum, stok FROM kostum WHERE stok > 0";
        try (Connection conn = DBConfig.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                cbKostum.addItem(rs.getString("id_kostum") + " - " + rs.getString("nama_kostum"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void ambilHargaKostum() {
        Object selected = cbKostum.getSelectedItem();
        if (selected == null || selected.toString().equals("-- Pilih Kostum --")) {
            hargaPerUnit = 0; hitungTotal(); return;
        }
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

    private void simpanPesananAsync() {
        if (cbKostum.getSelectedIndex() <= 0 || cbPenyewa.getSelectedIndex() <= 0 || txtIDSewa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi data!");
            return;
        }

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        JDialog loadingDialog = createProgressDialog(progressBar);
        
        String idK = cbKostum.getSelectedItem().toString().split(" - ")[0];
        String namaPenyewa = cbPenyewa.getSelectedItem().toString();
        int jmlSewa = (int) txtJumlah.getValue();

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i += 25) { Thread.sleep(300); publish(i); }

                try (Connection conn = DBConfig.getConnection()) {
                    conn.setAutoCommit(false);
                    try {
                        // 1. Simpan Pesanan
                        String sql = "INSERT INTO pesanan (id_sewa, nama_penyewa, id_kostum, jumlah, total_biaya, status, tgl_pinjam) VALUES (?, ?, ?, ?, ?, 'Disewa', CURDATE())";
                        PreparedStatement pst = conn.prepareStatement(sql);
                        pst.setString(1, txtIDSewa.getText());
                        pst.setString(2, namaPenyewa);
                        pst.setString(3, idK);
                        pst.setInt(4, jmlSewa);
                        pst.setDouble(5, Double.parseDouble(txtTotal.getText()));
                        pst.executeUpdate();

                        // 2. Kurangi Stok Kostum
                        String updStok = "UPDATE kostum SET stok = stok - ? WHERE id_kostum = ?";
                        PreparedStatement pstStok = conn.prepareStatement(updStok);
                        pstStok.setInt(1, jmlSewa);
                        pstStok.setString(2, idK);
                        pstStok.executeUpdate();

                        // 3. Update status jika stok jadi 0
                        String updStat = "UPDATE kostum SET status = 'Disewa' WHERE id_kostum = ? AND stok <= 0";
                        PreparedStatement pstStat = conn.prepareStatement(updStat);
                        pstStat.setString(1, idK);
                        pstStat.executeUpdate();

                        conn.commit();
                    } catch (SQLException e) { conn.rollback(); throw e; }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                btnSimpan.setEnabled(true);
                try {
                    get();
                    JOptionPane.showMessageDialog(PanelAddPesanan.this, "Pesanan Berhasil Disimpan!");
                    resetForm();
                    loadKostumCombo();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PanelAddPesanan.this, "Gagal: " + ex.getMessage());
                }
            }
        };

        btnSimpan.setEnabled(false);
        worker.execute();
        loadingDialog.setVisible(true);
    }

    private JDialog createProgressDialog(JProgressBar bar) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Status Proses", Dialog.ModalityType.MODELESS);
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(new JLabel("Memproses transaksi sewa..."), BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);
        dialog.setContentPane(p);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private void resetForm() {
        txtIDSewa.setText(""); 
        cbPenyewa.setSelectedIndex(0);
        cbKostum.setSelectedIndex(0); 
        txtJumlah.setValue(1);
        txtTotal.setText("");
    }
}