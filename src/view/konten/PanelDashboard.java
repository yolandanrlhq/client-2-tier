package view.konten;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelDashboard extends JPanel {

    private JLabel lblTotalKostum, lblSedangDisewa, lblTotalPendapatan;

    public PanelDashboard() {
        initializeUI();
        refreshData(); // Ambil data dari DB saat dibuka
    }

    private void initializeUI() {
        setLayout(new MigLayout("fillx, insets 40", "[grow]", "[]30[]"));
        setBackground(Color.WHITE);

        // --- HEADER ---
        JLabel title = new JLabel("Statistik Rental");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(0, 48, 73));
        add(title, "wrap");

        // --- STAT CARDS CONTAINER ---
        JPanel cardContainer = new JPanel(new MigLayout("fillx, gap 25, insets 0", "[grow][grow][grow]"));
        cardContainer.setOpaque(false);

        // Inisialisasi Label Statis dulu dengan angka 0
        lblTotalKostum = new JLabel("0");
        lblSedangDisewa = new JLabel("0");
        lblTotalPendapatan = new JLabel("0");

        cardContainer.add(createStatCard("Total Koleksi Kostum", lblTotalKostum, new Color(234, 242, 235), new Color(131, 188, 160)), "grow");
        cardContainer.add(createStatCard("Kostum Sedang Disewa", lblSedangDisewa, new Color(235, 241, 253), new Color(108, 155, 244)), "grow");
        cardContainer.add(createStatCard("Total Pendapatan (Rp)", lblTotalPendapatan, new Color(255, 250, 235), new Color(255, 193, 7)), "grow");

        add(cardContainer, "growx, wrap");
    }

    // Fungsi untuk mengambil data asli dari Database
    public void refreshData() {
        try {
            Connection conn = DBConfig.getConnection();
            
            // 1. Hitung Total Kostum
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM kostum");
            if (rs1.next()) lblTotalKostum.setText(rs1.getString(1));

            // 2. Hitung Kostum yang statusnya 'Disewa'
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM pesanan WHERE status = 'Disewa'");
            if (rs2.next()) lblSedangDisewa.setText(rs2.getString(1));

            // 3. Hitung Total Uang Masuk dari Pesanan
            ResultSet rs3 = conn.createStatement().executeQuery("SELECT SUM(total_biaya) FROM pesanan");
            if (rs3.next()) {
                double total = rs3.getDouble(1);
                lblTotalPendapatan.setText(String.format("%,.0f", total));
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