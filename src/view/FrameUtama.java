package view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.MenuItem;
import net.miginfocom.swing.MigLayout;
import view.konten.*;
import view.menu.PanelMenu;

public class FrameUtama extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelKonten;
    private PanelMenu panelMenu;

    // Referensi panel agar bisa direfresh datanya
    private PanelDashboard pDashboard;
    private PanelProduk pProduk;
    private PanelPesanan pPesanan;
    private PanelPelanggan pPelanggan;
    private PanelAddPesanan pAddPesanan;

    public FrameUtama() {
        initializeUI();
        setupPanelKonten();
        setupPanelMenu();
        addComponents();
    }

    private void initializeUI() {
        setTitle("Sistem Penyewaan Kostum - Pro Version");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 720));
        setMinimumSize(new Dimension(850, 500));

        setLayout(new MigLayout(
                "fill, insets 0, gap 0",
                "[250:280:300][grow]", // Sidebar tetap di range 250-300px
                "[grow]"
        ));
    }

    private void setupPanelKonten() {
        cardLayout = new CardLayout();
        panelKonten = new JPanel(cardLayout);
        panelKonten.setBackground(Color.WHITE);

        pDashboard = new PanelDashboard();
        pProduk = new PanelProduk();
        pPesanan = new PanelPesanan();
        pPelanggan = new PanelPelanggan();
        pAddPesanan = new PanelAddPesanan();

        panelKonten.add(pDashboard, "dashboard");
        panelKonten.add(pProduk, "produk");
        panelKonten.add(new PanelAddProduk(), "add_produk");
        panelKonten.add(pPesanan, "pesanan");
        panelKonten.add(pAddPesanan, "add_pesanan");
        panelKonten.add(pPelanggan, "pelanggan");
        panelKonten.add(new PanelAddPelanggan(), "add_pelanggan");
    }

    private void setupPanelMenu() {
    List<MenuItem> listMenu = new ArrayList<>();

    listMenu.add(new MenuItem("Dashboard", "dashboard"));

    MenuItem menuProduk = new MenuItem("Produk");
    menuProduk.addSubMenuItem(new MenuItem("Daftar Kostum", "produk"));
    menuProduk.addSubMenuItem(new MenuItem("Tambah Kostum", "add_produk"));
    listMenu.add(menuProduk);

    MenuItem menuPesanan = new MenuItem("Pesanan");
    menuPesanan.addSubMenuItem(new MenuItem("Data Pesanan", "pesanan"));
    menuPesanan.addSubMenuItem(new MenuItem("Tambah Pesanan", "add_pesanan"));
    listMenu.add(menuPesanan);

    MenuItem menuPelanggan = new MenuItem("Pelanggan");
    menuPelanggan.addSubMenuItem(new MenuItem("Daftar Pelanggan", "pelanggan"));
    menuPelanggan.addSubMenuItem(new MenuItem("Tambah Pelanggan", "add_pelanggan"));
    listMenu.add(menuPelanggan);

    // PERBAIKAN DI SINI:
    // Sesuaikan parameter dengan constructor PanelMenu kamu (List, CardLayout, JPanel)
    panelMenu = new PanelMenu(listMenu, cardLayout, panelKonten);
}

    private void addComponents() {
        add(panelMenu, "growy");
        add(panelKonten, "grow");
    }

    public void gantiPanel(String key) {
        cardLayout.show(panelKonten, key);

        // Logika Refresh Otomatis saat panel dibuka
        switch (key) {
            case "dashboard" -> pDashboard.refreshData();
            case "produk" -> pProduk.loadData();
            case "pesanan" -> pPesanan.loadData();
            case "add_pesanan" -> pAddPesanan.loadKostumCombo();
            case "pelanggan" -> pPelanggan.loadData();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                com.formdev.flatlaf.FlatLightLaf.setup();
            } catch (Exception e) {
                System.out.println("Gagal memuat tema FlatLaf.");
            }

            FrameUtama frame = new FrameUtama();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}