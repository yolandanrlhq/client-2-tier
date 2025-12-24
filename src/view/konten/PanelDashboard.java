package view.konten;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class PanelDashboard extends JPanel {
    public PanelDashboard() {
        setLayout(new MigLayout("fill, insets 40", "[grow]", "[]30[]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Dashboard Penyewaan");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(0, 48, 73));
        add(title, "wrap");

        // Stat Cards Container
        JPanel cardPanel = new JPanel(new MigLayout("fillx, gap 20", "[grow][grow][grow]"));
        cardPanel.setBackground(Color.WHITE);

        cardPanel.add(createStatCard("Total Kostum", "124"), "grow");
        cardPanel.add(createStatCard("Pesanan Aktif", "12"), "grow");
        cardPanel.add(createStatCard("Kostum Keluar", "8"), "grow");

        add(cardPanel, "growx");
    }

    private JPanel createStatCard(String label, String value) {
        JPanel card = new JPanel(new MigLayout("insets 20", "[]", "[]10[]"));
        card.setBackground(new Color(245, 247, 250));
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Inter", Font.BOLD, 28));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setForeground(new Color(98, 117, 138));
        
        card.add(lblValue, "wrap");
        card.add(lblLabel);
        return card;
    }
}