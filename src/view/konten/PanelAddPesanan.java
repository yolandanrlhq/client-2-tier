package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import view.FrameUtama;
import model.Pesanan;
import controller.PesananController;

public class PanelAddPesanan extends JPanel {

    private JTextField txtIDSewa, txtTotal;
    private JComboBox<String> cbKostum, cbPenyewa;
    private JSpinner txtJumlah;
    private JButton btnSimpan;
    private double hargaPerUnit = 0;
    private int stokTersedia = 0;

    // Gunakan controller untuk semua urusan data
    private PesananController controller = new PesananController(null);
    private FrameUtama frameUtama;
    private MigLayout mainLayout;
    private JLabel lblTitle;

    public PanelAddPesanan(FrameUtama frame) {
        this.frameUtama = frame;
        initializeUI();
        loadInitialData();
    }

    private void initializeUI() {
        mainLayout = new MigLayout("fillx, insets 40", "[right]20[grow, fill]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        setupStaticComponents();

        addComponentListener(new ComponentAdapter() {
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
        cbPenyewa = new JComboBox<>();
        
        cbKostum = new JComboBox<>();
        cbKostum.addActionListener(e -> updateInfoKostum());

        // Spinner awal dengan batas 1 (akan diupdate saat pilih kostum)
        txtJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        // Matikan input keyboard agar user tidak bisa mengetik angka melebihi stok
        ((JSpinner.DefaultEditor) txtJumlah.getEditor()).getTextField().setEditable(false);
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

    public void loadInitialData() {
        // Load Pelanggan via Controller
        cbPenyewa.removeAllItems();
        cbPenyewa.addItem("-- Pilih Pelanggan --");
        controller.ambilDaftarPelanggan().forEach(cbPenyewa::addItem);

        // Load Kostum via Controller
        cbKostum.removeAllItems();
        cbKostum.addItem("-- Pilih Kostum --");
        controller.ambilDaftarKostumTersedia().forEach(cbKostum::addItem);
    }

    private void updateInfoKostum() {
        if (cbKostum.getSelectedIndex() <= 0) {
            hargaPerUnit = 0;
            stokTersedia = 0;
            txtJumlah.setModel(new SpinnerNumberModel(1, 1, 1, 1));
            hitungTotal();
            return;
        }

        String selectedItem = cbKostum.getSelectedItem().toString();
        String idKostum = selectedItem.split(" - ")[0];

        // Minta detail harga dan stok ke controller
        hargaPerUnit = controller.getHargaKostum(idKostum);
        stokTersedia = controller.getStokKostum(idKostum);

        // Update Model Spinner agar MAKSIMAL sesuai stok
        int currentVal = (int) txtJumlah.getValue();
        if (currentVal > stokTersedia) currentVal = stokTersedia;
        if (stokTersedia < 1) currentVal = 0; // Jaga-jaga jika stok habis mendadak

        txtJumlah.setModel(new SpinnerNumberModel(Math.max(1, currentVal), 1, Math.max(1, stokTersedia), 1));
        hitungTotal();
    }

    private void hitungTotal() {
        int j = (int) txtJumlah.getValue();
        txtTotal.setText(String.format("%.0f", hargaPerUnit * j));
    }

    private void simpanPesananAsync() {
        if (cbKostum.getSelectedIndex() <= 0 || cbPenyewa.getSelectedIndex() <= 0 || txtIDSewa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi data!");
            return;
        }

        Pesanan p = new Pesanan();
        p.setIdSewa(txtIDSewa.getText().trim());
        p.setNamaPenyewa(cbPenyewa.getSelectedItem().toString());

        String[] kostumPart = cbKostum.getSelectedItem().toString().split(" - ");
        p.setIdKostum(kostumPart[0]);
        p.setNamaKostum(kostumPart[1]);
        p.setJumlah((int) txtJumlah.getValue());
        p.setTglPinjam(new java.util.Date());
        p.setTotalBiaya(Double.parseDouble(txtTotal.getText()));
        p.setStatus("Disewa");

        // UI Feedback (Loading Dialog)
        JProgressBar pb = new JProgressBar(0, 100);
        pb.setStringPainted(true);
        JLabel lblInfo = new JLabel("Menyimpan transaksi...");
        JDialog loading = createProgressDialog(pb, lblInfo);

        btnSimpan.setEnabled(false);
        
        new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                publish(20); Thread.sleep(300);
                publish(50);
                boolean success = controller.getService().simpanPesanan(p);
                publish(90); Thread.sleep(200);
                return success;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int val = chunks.get(chunks.size() - 1);
                pb.setValue(val);
                lblInfo.setText("Proses " + val + "%");
            }

            @Override
            protected void done() {
                loading.dispose();
                btnSimpan.setEnabled(true);
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(PanelAddPesanan.this, "Transaksi Berhasil!");
                        resetForm();
                        if (frameUtama != null) frameUtama.gantiPanel("pesanan");
                    } else {
                        JOptionPane.showMessageDialog(PanelAddPesanan.this, "Gagal simpan (Cek stok/ID)");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelAddPesanan.this, "Error: " + e.getMessage());
                }
            }
        }.execute();
        
        loading.setVisible(true);
    }

    private void resetForm() {
        txtIDSewa.setText("");
        cbPenyewa.setSelectedIndex(0);
        cbKostum.setSelectedIndex(0);
        txtJumlah.setModel(new SpinnerNumberModel(1, 1, 1, 1));
        txtTotal.setText("");
        hargaPerUnit = 0;
    }

    private JDialog createProgressDialog(JProgressBar bar, JLabel label) {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Proses", Dialog.ModalityType.APPLICATION_MODAL);
        d.setUndecorated(true);
        JPanel p = new JPanel(new MigLayout("fill, insets 20"));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        p.add(label, "wrap 10, center");
        p.add(bar, "growx, w 250!");
        d.add(p);
        d.pack();
        d.setLocationRelativeTo(this);
        return d;
    }

    private void refreshLayout() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w == null) return;
        removeAll();
        boolean isMobile = w.getWidth() <= 768;
        
        String constraints = isMobile ? "fillx, insets 20" : "fillx, insets 60 100";
        mainLayout.setLayoutConstraints(constraints);

        add(lblTitle, "span 2, center, wrap 30");
        add(new JLabel("ID Sewa")); add(txtIDSewa, "wrap");
        add(new JLabel("Pelanggan")); add(cbPenyewa, "wrap");
        add(new JLabel("Kostum")); add(cbKostum, "wrap");
        add(new JLabel("Jumlah (Max: " + stokTersedia + ")")); add(txtJumlah, "wrap");
        add(new JLabel("Total Harga")); add(txtTotal, "wrap 30");
        
        String btnSize = isMobile ? "span 2, growx, h 45!" : "span 2, center, w 250!, h 50!";
        add(btnSimpan, btnSize);

        revalidate(); repaint();
    }
}