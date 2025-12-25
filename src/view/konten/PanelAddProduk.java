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

        /*
         * RESPONSIVE STRATEGY (MigLayout):
         * - wrap 2  → desktop besar = 2 kolom
         * - span 2  → otomatis turun ke 1 kolom saat sempit
         * - growx   → input melebar mengikuti layar
         */
        setLayout(new MigLayout(
            "fillx, insets 40, wrap 2",
            "[right, 120!]15[grow, fill]",
            "[]25[]15[]15[]15[]15[]15[]25[]"
        ));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Registrasi Kostum Baru");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "span 2, center, wrap");

        // ================= FORM =================
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

        // ================= BUTTON =================
        JButton btnSimpan = new JButton("Simpan ke Katalog");
        btnSimpan.setBackground(new Color(131, 188, 160));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);

        btnSimpan.addActionListener(e -> simpanDataAsync(btnSimpan));

        add(btnSimpan, "span 2, center, w 240!, h 45!");
    }

    // =========================
    // SIMPAN DATA (NON FREEZE)
    // =========================
    private void simpanDataAsync(JButton btn) {

        JDialog loading = createLoadingDialog();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(2000); // simulasi proses

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
                loading.dispose();
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
                        "Gagal simpan: " + ex.getMessage()
                    );
                }
            }
        };

        btn.setEnabled(false);
        worker.execute();
        loading.setVisible(true);
    }

    // =========================
    // LOADING DIALOG
    // =========================
    private JDialog createLoadingDialog() {
        JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Memproses...",
            Dialog.ModalityType.APPLICATION_MODAL
        );

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);

        JLabel lbl = new JLabel("Menyimpan data, mohon tunggu...");
        lbl.setFont(new Font("Inter", Font.PLAIN, 14));

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(lbl, BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);

        dialog.setContentPane(p);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        return dialog;
    }

    private void resetForm() {
        txtID.setText("");
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setValue(1);
    }
}
