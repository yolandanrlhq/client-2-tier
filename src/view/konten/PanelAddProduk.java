package view.konten;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelAddProduk extends JPanel {

    private JTextField txtID, txtNama, txtHarga;
    private JComboBox<String> cbKategori, cbUkuran;
    private JSpinner txtStok;

    public PanelAddProduk() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new MigLayout(
            "fillx, insets 40, wrap 2",
            "[right,120!]15[grow,fill]",
            "[]25[]15[]15[]15[]15[]15[]25[]"
        ));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Registrasi Kostum Baru");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "span 2, center, wrap");

        add(new JLabel("ID Kostum:"));
        txtID = new JTextField();
        add(txtID, "growx");

        add(new JLabel("Nama Kostum:"));
        txtNama = new JTextField();
        add(txtNama, "growx");

        add(new JLabel("Kategori:"));
        cbKategori = new JComboBox<>(new String[]{
            "Anime", "Superhero", "Tradisional", "Game"
        });
        add(cbKategori, "growx");

        add(new JLabel("Jumlah Stok:"));
        txtStok = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        add(txtStok, "w 120!");

        add(new JLabel("Ukuran:"));
        cbUkuran = new JComboBox<>(new String[]{
            "S", "M", "L", "XL", "All Size"
        });
        add(cbUkuran, "growx");

        add(new JLabel("Harga Sewa:"));
        txtHarga = new JTextField();
        add(txtHarga, "growx");

        JButton btnSimpan = new JButton("Simpan ke Katalog");
        btnSimpan.setBackground(new Color(131,188,160));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);

        btnSimpan.addActionListener(e -> simpanAsync(btnSimpan));
        add(btnSimpan, "span 2, center, w 240!, h 45!");
    }

    // =========================
    // SIMPAN DATA + PROGRESS %
    // =========================
    private void simpanAsync(JButton btn) {

        JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Memproses...",
            Dialog.ModalityType.MODELESS   // 🔥 penting: TIDAK MEMBLOK UI
        );

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);

        JLabel lbl = new JLabel("Menyimpan data...");
        lbl.setFont(new Font("Inter", Font.PLAIN, 14));

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(bar, BorderLayout.CENTER);

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {

                // ===== SIMULASI PROSES 5 DETIK =====
                for (int i = 0; i <= 100; i += 5) {
                    Thread.sleep(250); // 5 detik total
                    setProgress(i);
                }

                // ===== SIMPAN DATABASE =====
                Connection conn = DBConfig.getConnection();
                String sql = """
                    INSERT INTO kostum
                    (id_kostum, nama_kostum, kategori, stok, ukuran, harga_sewa, status)
                    VALUES (?, ?, ?, ?, ?, ?, 'Tersedia')
                """;

                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtID.getText());
                pst.setString(2, txtNama.getText());
                pst.setString(3, cbKategori.getSelectedItem().toString());
                pst.setInt(4, (int) txtStok.getValue());
                pst.setString(5, cbUkuran.getSelectedItem().toString());
                pst.setDouble(6, Double.parseDouble(txtHarga.getText()));
                pst.executeUpdate();

                return null;
            }

            @Override
            protected void done() {
                dialog.dispose();
                btn.setEnabled(true);

                try {
                    get();
                    JOptionPane.showMessageDialog(
                        PanelAddProduk.this,
                        "Kostum berhasil didaftarkan!"
                    );
                    resetForm();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        PanelAddProduk.this,
                        "Gagal: " + ex.getMessage()
                    );
                }
            }
        };

        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                bar.setValue((Integer) evt.getNewValue());
            }
        });

        btn.setEnabled(false);
        worker.execute();
        dialog.setVisible(true);
    }

    private void resetForm() {
        txtID.setText("");
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setValue(1);
    }
}
