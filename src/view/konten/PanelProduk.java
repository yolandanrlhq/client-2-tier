package view.konten;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import net.miginfocom.swing.MigLayout;

public class PanelProduk extends JPanel {
    public PanelProduk() {
        setLayout(new MigLayout("fill, insets 30", "[grow]", "[]20[grow]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Koleksi Kostum");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "wrap");

        String[] columns = {"ID", "Nama Kostum", "Kategori", "Ukuran", "Harga Sewa/Hari", "Status"};
        Object[][] data = {
            {"K-001", "Naruto Sage Mode", "Anime", "L", "Rp 150.000", "Tersedia"},
            {"K-002", "Spider-Man No Way Home", "Superhero", "M", "Rp 200.000", "Disewa"},
            {"K-003", "Kimono Tradisional", "Tradisional", "All Size", "Rp 120.000", "Tersedia"}
        };

        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, "grow");
    }
}