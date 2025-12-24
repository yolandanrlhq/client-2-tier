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

// Import semua panel konten yang sudah kita buat
import view.konten.PanelDashboard;
import view.konten.PanelProduk;
import view.konten.PanelAddProduk;
import view.konten.PanelPesanan;
import view.konten.PanelAddPesanan;
import view.menu.PanelMenu;

public class FrameUtama extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelKonten;
    private PanelMenu panelMenu;
    private List<MenuItem> listDaftarMenuItem;

    public FrameUtama() {
        initializeUI();
        setupPanelKonten();
        setupPanelMenu();
        addComponents();
    }

    private void initializeUI() {
        setTitle("Sistem Penyewaan Kostum Karakter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 720));
        setMinimumSize(new Dimension(1024, 600));
        setLayout(new MigLayout("fill, insets 0, gap 0", "[280!]0[grow]", "[grow]"));
    }

    private void setupPanelKonten() {
        cardLayout = new CardLayout();
        panelKonten = new JPanel(cardLayout);
        panelKonten.setBackground(Color.WHITE);

        // Mendaftarkan semua halaman ke CardLayout
        panelKonten.add(new PanelDashboard(), "dashboard");
        panelKonten.add(new PanelProduk(), "produk");
        panelKonten.add(new PanelAddProduk(), "add_produk");
        panelKonten.add(new PanelPesanan(), "pesanan");
        panelKonten.add(new PanelAddPesanan(), "add_pesanan");
    }

    private void setupPanelMenu() {
        listDaftarMenuItem = new ArrayList<>();

        // 1. Menu Dashboard
        listDaftarMenuItem.add(new MenuItem("Dashboard", "dashboard"));

        // 2. Menu Produk dengan Sub-Menu
        MenuItem menuProduk = new MenuItem("Produk");
        menuProduk.addSubMenuItem(new MenuItem("Daftar Kostum", "produk"));
        menuProduk.addSubMenuItem(new MenuItem("Tambah Kostum", "add_produk"));
        listDaftarMenuItem.add(menuProduk);

        // 3. Menu Pesanan dengan Sub-Menu
        MenuItem menuPesanan = new MenuItem("Pesanan");
        menuPesanan.addSubMenuItem(new MenuItem("Data Pesanan", "pesanan"));
        menuPesanan.addSubMenuItem(new MenuItem("Tambah Pesanan", "add_pesanan"));
        listDaftarMenuItem.add(menuPesanan);

        // Inisialisasi Sidebar
        panelMenu = new PanelMenu(listDaftarMenuItem, cardLayout, panelKonten);
    }

    private void addComponents() {
        // Tambahkan sidebar di kolom pertama (index 0)
        add(panelMenu, "growy");
        // Tambahkan konten di kolom kedua (index 1)
        add(panelKonten, "grow");
    }

    public static void main(String[] args) {
        // Menjalankan aplikasi
        SwingUtilities.invokeLater(() -> {
            FrameUtama frame = new FrameUtama();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}