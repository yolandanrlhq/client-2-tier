package view.menu;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import model.MenuItem;
import net.miginfocom.swing.MigLayout;

public class PanelMenu extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel panelKonten;
    private PanelMenuItem panelMenuItem;
    private PanelMenuItem panelDashboard = null;

    public PanelMenu(List<MenuItem> listDaftarMenuItem, CardLayout cardLayout, JPanel panelKonten) {
        this.cardLayout = cardLayout;
        this.panelKonten = panelKonten;

        initializeUI();
        buildMenu(listDaftarMenuItem);
        selectDefaultMenu();
    }

    private void initializeUI() {
        // Menggunakan MigLayout untuk menyusun menu secara vertikal
        setLayout(new MigLayout("fillx, wrap 1, insets 0, gap 0, hidemode 3", "[grow]", ""));
        setPreferredSize(new Dimension(280, 0));
        setBackground(new Color(245, 247, 250)); // Warna abu-abu sangat muda sesuai tema

        // Judul Aplikasi di Sidebar
        JPanel panelJudul = new JPanel(new MigLayout("insets 20 25 20 25"));
        panelJudul.setOpaque(false);
        JLabel labelJudul = new JLabel("COSTUME RENTAL");
        labelJudul.setFont(new Font("Inter", Font.BOLD, 18));
        labelJudul.setForeground(new Color(0, 48, 73));
        panelJudul.add(labelJudul);
        add(panelJudul, "growx");
        add(new JSeparator(), "growx, gapbottom 10");
    }

    private void buildMenu(List<MenuItem> listDaftarMenuItem) {
        for (MenuItem menu : listDaftarMenuItem) {
            // Membuat item menu
            PanelMenuItem itemPanel = new PanelMenuItem(menu, this);
            add(itemPanel, "growx, h 45!");

            // Jika punya sub-menu (seperti 'Tambah Produk' di bawah 'Produk')
            if (menu.hasSubMenuItem()) {
                JPanel panelSubMenu = itemPanel.getPanelCountainerSubMenu();
                add(panelSubMenu, "growx, gapleft 20");
                panelSubMenu.setVisible(false); // Sembunyikan dulu sampai diklik
            }

            if ("Dashboard".equals(menu.getJudul())) {
                panelDashboard = itemPanel;                
            }
        }
    }

    private void selectDefaultMenu() {
        if (panelDashboard != null) {
            selectMenuItem(panelDashboard);
        }
    }

    public void selectMenuItem(PanelMenuItem clickedPanel) {
        if (panelMenuItem != null) {
            panelMenuItem.setSelectedByParent(false);
        }
        clickedPanel.setSelectedByParent(true);
        panelMenuItem = clickedPanel;

        // Pindah halaman jika ada contentKey
        String key = clickedPanel.getContentKey();
        if (key != null && !key.isEmpty()) {
            cardLayout.show(panelKonten, key);
        }
    }
}