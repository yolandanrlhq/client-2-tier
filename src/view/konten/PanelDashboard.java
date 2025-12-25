package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelDashboard extends JPanel {

    private JLabel lblTotalKostum, lblSedangDisewa, lblTotalPendapatan;
    private JPanel cardContainer;
    private JLabel title;

    private MigLayout mainLayout;
    private MigLayout containerLayout;

    public PanelDashboard() {
        mainLayout = new MigLayout("fillx, insets 40", "[grow]", "[]30[]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        initializeUI();
        refreshData();

        // RESPONSIVE LISTENER
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustResponsiveness();
            }
        });
    }

    private void initializeUI() {
        // HEADER
        title = new JLabel("Statistik Rental");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(0, 48, 73));
        add(title, "wrap");

        // CARD CONTAINER
        containerLayout = new MigLayout("fillx, gap 25, insets 0", "[grow][grow][grow]");
        cardContainer = new JPanel(containerLayout);
        cardContainer.setOpaque(false);

        lblTotalKostum = new JLabel("0");
        lblSedangDisewa = new JLabel("0");
        lblTotalPendapatan = new JLabel("0");

        renderCards();
        add(cardContainer, "growx, wrap");
    }

    private void renderCards() {
        cardContainer.removeAll();

        cardContainer.add(createStatCard(
                "Total Koleksi Kostum", lblTotalKostum,
                new Color(234, 242, 235), new Color(131, 188, 160)
        ), "grow");

        cardContainer.add(createStatCard(
                "Kostum Sedang Disewa", lblSedangDisewa,
                new Color(235, 241, 253), new Color(108, 155, 244)
        ), "grow");

        cardContainer.add(createStatCard(
                "Total Pendapatan (Rp)", lblTotalPendapatan,
                new Color(255, 250, 235), new Color(255, 193, 7)
        ), "grow");
    }

    private void adjustResponsiveness() {
        Window window = SwingUtilities.getWindowAncestor(this);
if (window == null) return;

int w = window.getWidth();

        if (w <= 0) return;

        cardContainer.removeAll();

        // ===== SPLIT VIEW =====
        if (w <= 768) {
            mainLayout.setLayoutConstraints("fillx, insets 20");
            containerLayout.setColumnConstraints("[grow]");
            title.setFont(new Font("Inter", Font.BOLD, 22));

            renderCardsStacked();

        // ===== DESKTOP SMALL =====
        } else if (w <= 1200) {
            mainLayout.setLayoutConstraints("fillx, insets 40 5% 40 5%");
            containerLayout.setColumnConstraints("[grow][grow]");
            title.setFont(new Font("Inter", Font.BOLD, 28));

            cardContainer.add(createStatCard(
                    "Total Koleksi Kostum", lblTotalKostum,
                    new Color(234, 242, 235), new Color(131, 188, 160)
            ), "grow");

            cardContainer.add(createStatCard(
                    "Kostum Sedang Disewa", lblSedangDisewa,
                    new Color(235, 241, 253), new Color(108, 155, 244)
            ), "grow, wrap");

            cardContainer.add(createStatCard(
                    "Total Pendapatan (Rp)", lblTotalPendapatan,
                    new Color(255, 250, 235), new Color(255, 193, 7)
            ), "grow, span 2");

        // ===== DESKTOP STANDARD =====
        } else {
            mainLayout.setLayoutConstraints("fillx, insets 60 10% 60 10%");
            containerLayout.setColumnConstraints("[grow][grow][grow]");
            title.setFont(new Font("Inter", Font.BOLD, 36));

            renderCards();
        }

        cardContainer.revalidate();
        cardContainer.repaint();
    }

    private void renderCardsStacked() {
        cardContainer.add(createStatCard(
                "Total Koleksi Kostum", lblTotalKostum,
                new Color(234, 242, 235), new Color(131, 188, 160)
        ), "grow, wrap 15");

        cardContainer.add(createStatCard(
                "Kostum Sedang Disewa", lblSedangDisewa,
                new Color(235, 241, 253), new Color(108, 155, 244)
        ), "grow, wrap 15");

        cardContainer.add(createStatCard(
                "Total Pendapatan (Rp)", lblTotalPendapatan,
                new Color(255, 250, 235), new Color(255, 193, 7)
        ), "grow");
    }

    public void refreshData() {
        try {
            Connection conn = DBConfig.getConnection();

            ResultSet rs1 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM kostum");
            if (rs1.next()) lblTotalKostum.setText(rs1.getString(1));

            ResultSet rs2 = conn.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM pesanan WHERE status = 'Disewa'"
            );
            if (rs2.next()) lblSedangDisewa.setText(rs2.getString(1));

            ResultSet rs3 = conn.createStatement().executeQuery(
                    "SELECT SUM(total_biaya) FROM pesanan"
            );
            if (rs3.next()) {
                lblTotalPendapatan.setText(String.format("%,.0f", rs3.getDouble(1)));
            }

        } catch (SQLException e) {
            System.err.println("Gagal update dashboard: " + e.getMessage());
        }
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color bgColor, Color accentColor) {
        JPanel card = new JPanel(new MigLayout("insets 20", "[]", "[]10[]"));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createLineBorder(accentColor, 1));

        valueLabel.setFont(new Font("Inter", Font.BOLD, 36));
        valueLabel.setForeground(new Color(0, 48, 73));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTitle.setForeground(new Color(98, 117, 138));

        card.add(valueLabel, "wrap");
        card.add(lblTitle);

        return card;
    }
}
