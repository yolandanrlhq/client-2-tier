package view;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.MenuItem;
import net.miginfocom.swing.MigLayout;
import view.konten.*;
import view.menu.PanelMenu;

public class FrameUtama extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelKonten;
    private PanelMenu panelMenu;
    private MigLayout mainLayout;
    private JButton btnHamburgerOverlay;

    // PANEL KONTEN (TETAP AMAN, TIDAK DIHAPUS)
    private PanelDashboard pDashboard;
    private PanelProduk pProduk;
    private PanelPesanan pPesanan;
    private PanelPelanggan pPelanggan;
    private PanelAddPesanan pAddPesanan;

    private boolean isMenuShow = true;

    public FrameUtama() {
        initializeUI();
        setupPanelKonten();
        setupPanelMenu();
        setupFloatingButton(); 
        addComponents();
        setupResponsiveListener();
        
        refreshLayout();
    }

    private void initializeUI() {
        setTitle("Sistem Penyewaan Kostum");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 720));
        setMinimumSize(new Dimension(600, 500));
        mainLayout = new MigLayout("fill, insets 0, gap 0", "[280!]0[grow]", "[grow]");
        setLayout(mainLayout);
    }

    private void setupFloatingButton() {
        btnHamburgerOverlay = new JButton("≡");
        btnHamburgerOverlay.setFont(new Font("Inter", Font.BOLD, 22));
        btnHamburgerOverlay.setFocusable(false);
        btnHamburgerOverlay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnHamburgerOverlay.setBackground(Color.WHITE);
        btnHamburgerOverlay.setForeground(new Color(0, 48, 73));
        btnHamburgerOverlay.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        btnHamburgerOverlay.addActionListener(e -> toggleMenu());
        
        // Gunakan DRAG_LAYER agar selalu di depan konten, tapi di bawah sidebar jika diperlukan
        getLayeredPane().add(btnHamburgerOverlay, JLayeredPane.DRAG_LAYER);
        btnHamburgerOverlay.setBounds(15, 25, 50, 45);
        btnHamburgerOverlay.setVisible(false);
    }

    private void setupPanelKonten() {
        cardLayout = new CardLayout();
        panelKonten = new JPanel(cardLayout);
        
        // Klik di konten otomatis tutup menu (Hanya di mode small)
        panelKonten.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (getWidth() < 900 && isMenuShow) {
                    toggleMenu();
                }
            }
        });

        // INISIALISASI SEMUA PANEL (LENGKAP!)
        pDashboard = new PanelDashboard();
        pProduk = new PanelProduk();
        pPesanan = new PanelPesanan();
        pPelanggan = new PanelPelanggan();
        pAddPesanan = new PanelAddPesanan(this);

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

        panelMenu = new PanelMenu(listMenu, cardLayout, panelKonten);
        
        // PENTING: Kita jangan masukkan panelMenu ke LayeredPane secara permanen. 
        // Biarkan MigLayout yang atur di refreshLayout.
    }

    private void addComponents() {
        add(panelMenu, "growy");
        add(panelKonten, "grow");
    }

    public void toggleMenu() {
        isMenuShow = !isMenuShow;
        refreshLayout();
    }

    private void refreshLayout() {
        if (getWidth() < 900) {
            // --- MODE SMALL (MOBILE) ---
            
            // 1. Hilangkan pembagian kolom (Reset ke 1 kolom saja untuk konten)
            mainLayout.setColumnConstraints("[grow]"); 
            
            if (isMenuShow) {
                // Sidebar melayang di atas konten (pos 0 0)
                mainLayout.setComponentConstraints(panelMenu, "pos 0 0, w 280!, h 100%, external");
                panelMenu.setVisible(true);
                btnHamburgerOverlay.setVisible(false); 
            } else {
                // Sidebar dibuang jauh ke luar layar
                mainLayout.setComponentConstraints(panelMenu, "pos -280 0, w 280!, h 100%, external");
                panelMenu.setVisible(false);
                btnHamburgerOverlay.setVisible(true);
            }
        } else {
            // --- MODE NORMAL (DESKTOP) ---
            isMenuShow = true; 
            btnHamburgerOverlay.setVisible(false);
            panelMenu.setVisible(true);
            
            // 2. Kembalikan pembagian 2 kolom (Sidebar 280px + Konten grow)
            mainLayout.setColumnConstraints("[280!]0[grow]");
            mainLayout.setComponentConstraints(panelMenu, "growy");
        }
        
        btnHamburgerOverlay.putClientProperty("JButton.arc", 15);
        btnHamburgerOverlay.putClientProperty("JButton.focusWidth", 0);
        // Jaga posisi tombol hamburger di pojok kiri atas
        btnHamburgerOverlay.setBounds(15, 30, 40, 40);
        
        // Refresh tampilan secara paksa
        revalidate();
        repaint();
    }

    private void setupResponsiveListener() {
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (getWidth() < 900 && isMenuShow) {
                    isMenuShow = false;
                }
                refreshLayout();
            }
        });
    }

    public void gantiPanel(String key) {
        cardLayout.show(panelKonten, key);
        
        // TUTUP OTOMATIS JIKA PILIH MENU DI LAYAR KECIL
        if (getWidth() < 900 && isMenuShow) {
            toggleMenu();
        }

        // REFRESH DATA TETAP AMAN
        switch (key) {
            case "dashboard" -> pDashboard.refreshData();
            case "produk" -> pProduk.loadData("");
            case "pesanan" -> pPesanan.loadData("");
            case "add_pesanan" -> pAddPesanan.loadKostumCombo();
            case "pelanggan" -> pPelanggan.loadData();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { com.formdev.flatlaf.FlatLightLaf.setup(); } catch (Exception e) {}
            FrameUtama frame = new FrameUtama();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}