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

// Import semua panel konten
import view.konten.*;
import view.menu.PanelMenu;
import view.menu.PanelMenuItem;

public class FrameUtama extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelKonten;
    private PanelMenu panelMenu;
    
    // Simpan referensi panel agar bisa di-refresh datanya
    private PanelDashboard pDashboard;
    private PanelProduk pProduk;
    private PanelPesanan pPesanan;

    public FrameUtama() {
        initializeUI();
        setupPanelKonten();
        setupPanelMenu();
        addComponents();
    }

    private void initializeUI() {
        setTitle("Sistem Penyewaan Kostum Karakter - 2 Tier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 720));
        setMinimumSize(new Dimension(1100, 650));
        // Mengatur layout utama: Sidebar (280px) dan Konten (Sisa ruang)
        setLayout(new MigLayout("fill, insets 0, gap 0", "[280!]0[grow]", "[grow]"));
    }

    private void setupPanelKonten() {
        cardLayout = new CardLayout();
        panelKonten = new JPanel(cardLayout);
        panelKonten.setBackground(Color.WHITE);

        // Inisialisasi panel-panel
        pDashboard = new PanelDashboard();
        pProduk = new PanelProduk();
        pPesanan = new PanelPesanan();
        
        // Daftarkan ke CardLayout dengan Key
        panelKonten.add(pDashboard, "dashboard");
        panelKonten.add(pProduk, "produk");
        panelKonten.add(new PanelAddProduk(), "add_produk");
        panelKonten.add(pPesanan, "pesanan");
        panelKonten.add(new PanelAddPesanan(), "add_pesanan");
    }

    private void setupPanelMenu() {
        List<MenuItem> listMenu = new ArrayList<>();

        // Menu Dashboard
        listMenu.add(new MenuItem("Dashboard", "dashboard"));

        // Menu Produk dengan Sub-Menu
        MenuItem menuProduk = new MenuItem("Produk");
        menuProduk.addSubMenuItem(new MenuItem("Daftar Kostum", "produk"));
        menuProduk.addSubMenuItem(new MenuItem("Tambah Kostum", "add_produk"));
        listMenu.add(menuProduk);

        // Menu Pesanan dengan Sub-Menu
        MenuItem menuPesanan = new MenuItem("Pesanan");
        menuPesanan.addSubMenuItem(new MenuItem("Data Pesanan", "pesanan"));
        menuPesanan.addSubMenuItem(new MenuItem("Tambah Pesanan", "add_pesanan"));
        listMenu.add(menuPesanan);

        // Buat sidebar menu
        panelMenu = new PanelMenu(listMenu, cardLayout, panelKonten);
    }

    private void addComponents() {
        add(panelMenu, "growy");
        add(panelKonten, "grow");
    }

    /**
     * Fungsi krusial untuk refresh data saat menu diklik
     * Pastikan di PanelMenu.java, fungsi ini dipanggil
     */
    public void refreshPanel(String key) {
        switch (key) {
            case "dashboard":
                pDashboard.refreshData();
                break;
            case "produk":
                pProduk.loadData();
                break;
            case "pesanan":
                pPesanan.loadData();
                break;
        }
    }

    public static void main(String[] args) {
        // Jalankan aplikasi dengan tema FlatLaf (pastikan library sudah terpasang)
        SwingUtilities.invokeLater(() -> {
            try {
                com.formdev.flatlaf.FlatLightLaf.setup();
            } catch (Exception e) {
                System.out.println("Gagal memuat tema.");
            }
            FrameUtama frame = new FrameUtama();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}