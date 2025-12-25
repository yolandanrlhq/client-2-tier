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

public class FrameUtama extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelKonten;
    private PanelMenu panelMenu;
    
    // Simpan referensi panel agar bisa di-refresh datanya
    private PanelDashboard pDashboard;
    private PanelProduk pProduk;
    private PanelPesanan pPesanan;
    private PanelPelanggan pPelanggan; // 🔹 TAMBAHAN

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
        setLayout(new MigLayout("fill, insets 0, gap 0", "[280!]0[grow]", "[grow]"));
    }

    private void setupPanelKonten() {
        cardLayout = new CardLayout();
        panelKonten = new JPanel(cardLayout);
        panelKonten.setBackground(Color.WHITE);

        // Inisialisasi panel
        pDashboard = new PanelDashboard();
        pProduk = new PanelProduk();
        pPesanan = new PanelPesanan();
        pPelanggan = new PanelPelanggan(); // 🔹 TAMBAHAN

        // DASHBOARD
        panelKonten.add(pDashboard, "dashboard");

        // PRODUK
        panelKonten.add(pProduk, "produk");
        panelKonten.add(new PanelAddProduk(), "add_produk");

        // PESANAN
        panelKonten.add(pPesanan, "pesanan");
        panelKonten.add(new PanelAddPesanan(), "add_pesanan");

        // PELANGGAN (SAMA SEPERTI PRODUK)
        panelKonten.add(pPelanggan, "pelanggan");
        panelKonten.add(new PanelAddPelanggan(), "add_pelanggan");
    }

    private void setupPanelMenu() {
        List<MenuItem> listMenu = new ArrayList<>();

        // DASHBOARD
        listMenu.add(new MenuItem("Dashboard", "dashboard"));

        // PRODUK
        MenuItem menuProduk = new MenuItem("Produk");
        menuProduk.addSubMenuItem(new MenuItem("Daftar Kostum", "produk"));
        menuProduk.addSubMenuItem(new MenuItem("Tambah Kostum", "add_produk"));
        listMenu.add(menuProduk);

        // PESANAN
        MenuItem menuPesanan = new MenuItem("Pesanan");
        menuPesanan.addSubMenuItem(new MenuItem("Data Pesanan", "pesanan"));
        menuPesanan.addSubMenuItem(new MenuItem("Tambah Pesanan", "add_pesanan"));
        listMenu.add(menuPesanan);

        // 🔹 PELANGGAN (IDENTIK DENGAN PRODUK)
        MenuItem menuPelanggan = new MenuItem("Pelanggan");
        menuPelanggan.addSubMenuItem(new MenuItem("Daftar Pelanggan", "pelanggan"));
        menuPelanggan.addSubMenuItem(new MenuItem("Tambah Pelanggan", "add_pelanggan"));
        listMenu.add(menuPelanggan);

        panelMenu = new PanelMenu(listMenu, cardLayout, panelKonten);
    }

    private void addComponents() {
        add(panelMenu, "growy");
        add(panelKonten, "grow");
    }

    /**
     * Refresh data saat menu diklik
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
            case "pelanggan":
                pPelanggan.loadData();
                break;
        }
    }

    public void gantiPanel(String key) {
        cardLayout.show(panelKonten, key);

        if (key.equals("produk")) {
            pProduk.loadData();
        } else if (key.equals("pesanan")) {
            pPesanan.loadData();
        } else if (key.equals("pelanggan")) {
            pPelanggan.loadData();
        } else if (key.equals("dashboard")) {
            pDashboard.refreshData();
        }
    }

    public static void main(String[] args) {
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
