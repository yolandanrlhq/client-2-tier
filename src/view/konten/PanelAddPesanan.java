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
        
        // Ganti ke method Async
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
            add(new JLabel("Penyewa")); add(txtPenyewa, "growx, wrap 15");
            add(new JLabel("Kostum")); add(cbKostum, "growx, wrap 15");
            add(new JLabel("Jumlah")); add(txtJumlah, "w 100!, wrap 15");
            add(new JLabel("Total")); add(txtTotal, "growx, wrap 30");
            add(btnSimpan, "growx, h 45!");
        } else {
            // UI TETAP SEPERTI CODINGANMU
            mainLayout.setLayoutConstraints("fillx, insets 80 50 80 50");
            lblTitle.setFont(new Font("Inter", Font.BOLD, 32));
            add(lblTitle, "span 2, center, wrap 40");
            add(new JLabel("ID Sewa:")); add(txtIDSewa, "wrap 15");
            add(new JLabel("Nama Penyewa:")); add(txtPenyewa, "wrap 15");
            add(new JLabel("Pilih Kostum:")); add(cbKostum, "wrap 15");
            add(new JLabel("Jumlah Unit:")); add(txtJumlah, "w 100!, wrap 15");
            add(new JLabel("Total Biaya:")); add(txtTotal, "wrap 30");
            add(btnSimpan, "span 2, center, w 250!, h 50!");
        }
        revalidate();
        repaint();
    }

    // LOGIKA MULTITHREADING DENGAN PERSENTASE
    private void simpanPesananAsync() {
        if (cbKostum.getSelectedIndex() <= 0 || txtIDSewa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi data!");
            return;
        }

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true); // Menampilkan angka %
        
        JDialog loadingDialog = createProgressDialog(progressBar);
        String idK = cbKostum.getSelectedItem().toString().split(" - ")[0];

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simulasi loading bertahap agar persentase terlihat (0% - 100%)
                for (int i = 0; i <= 100; i += 20) {
                    Thread.sleep(400); // Simulasi delay proses berat
                    publish(i); 
                }

                // Proses Database
                try (Connection conn = DBConfig.getConnection()) {
                    conn.setAutoCommit(false);
                    String sql = "INSERT INTO pesanan (id_sewa, nama_penyewa, id_kostum, jumlah, total_biaya, status, tgl_pinjam) VALUES (?, ?, ?, ?, ?, 'Disewa', CURDATE())";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, txtIDSewa.getText());
                    pst.setString(2, txtPenyewa.getText());
                    pst.setString(3, idK);
                    pst.setInt(4, (int) txtJumlah.getValue());
                    pst.setDouble(5, Double.parseDouble(txtTotal.getText()));
                    pst.executeUpdate();

                    PreparedStatement pstUpd = conn.prepareStatement("UPDATE kostum SET status='Disewa' WHERE id_kostum=?");
                    pstUpd.setString(1, idK);
                    pstUpd.executeUpdate();
                    conn.commit();
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                // Update bar secara real-time
                int val = chunks.get(chunks.size() - 1);
                progressBar.setValue(val);
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
        p.add(new JLabel("Menyimpan data, mohon tunggu..."), BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);
        dialog.setContentPane(p);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    // Method lainnya (loadKostumCombo, ambilHarga, dll) tidak berubah
    public void loadKostumCombo() {
        cbKostum.removeAllItems();
        cbKostum.addItem("-- Pilih Kostum --");
        String sql = "SELECT id_kostum, nama_kostum FROM kostum WHERE status = 'Tersedia'";
        try (Connection conn = DBConfig.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) cbKostum.addItem(rs.getString("id_kostum") + " - " + rs.getString("nama_kostum"));
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

    private void resetForm() {
        txtIDSewa.setText(""); txtPenyewa.setText("");
        cbKostum.setSelectedIndex(0); txtJumlah.setValue(1);
        txtTotal.setText("");
    }
}