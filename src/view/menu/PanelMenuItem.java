package view.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import model.MenuItem;

public class PanelMenuItem extends JPanel {

    // Warna-warna tema
    private static final Color BG_SIDEBAR = new Color(245, 247, 250);
    private static final Color BG_HOVER = new Color(224, 230, 235);
    private static final Color BG_SELECTED = new Color(234, 242, 235);
    private static final Color TEXT_NORMAL = new Color(98, 117, 138);
    private static final Color TEXT_SELECTED = new Color(131, 188, 160);

    private final String contentKey;
    private boolean selected = false;
    private final JPanel panelContainerSubMenu;
    private final JLabel labelMenu;
    private final PanelMenu panelMenu;

    public PanelMenuItem(MenuItem item, PanelMenu panelMenu) {
        this.panelMenu = panelMenu;
        this.contentKey = item.getContentKey();
        this.panelContainerSubMenu = new JPanel(new MigLayout("fillx, wrap 1, insets 0, gap 0"));
        this.panelContainerSubMenu.setOpaque(false);

        setLayout(new MigLayout("insets 0 25 0 25", "[grow]", "[grow]"));
        setBackground(BG_SIDEBAR);

        labelMenu = new JLabel(item.getJudul());
        labelMenu.setFont(new Font("Inter", Font.PLAIN, 14));
        labelMenu.setForeground(TEXT_NORMAL);
        add(labelMenu, "aligny center");

        // Bangun Sub-Menu jika ada
        if (item.hasSubMenuItem()) {
            for (MenuItem subItem : item.getSubMenuItems()) {
                panelContainerSubMenu.add(new PanelMenuItem(subItem, panelMenu), "growx, h 35!");
            }
        }

        addEvents();
    }

    private void addEvents() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selected) setBackground(BG_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!selected) setBackground(BG_SIDEBAR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                panelMenu.selectMenuItem(PanelMenuItem.this);
                // Toggle sub-menu visibility
                if (panelContainerSubMenu.getComponentCount() > 0) {
                    panelContainerSubMenu.setVisible(!panelContainerSubMenu.isVisible());
                    revalidate();
                }
            }
        });
    }

    public void setSelectedByParent(boolean selected) {
        this.selected = selected;
        setBackground(selected ? BG_SELECTED : BG_SIDEBAR);
        labelMenu.setForeground(selected ? TEXT_SELECTED : TEXT_NORMAL);
        labelMenu.setFont(new Font("Inter", selected ? Font.BOLD : Font.PLAIN, 14));
    }

    public String getContentKey() { return contentKey; }
    public JPanel getPanelCountainerSubMenu() { return panelContainerSubMenu; }
}
