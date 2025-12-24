package view.konten;

import javax.swing.*;
import java.awt.*;
import net.miginfocom.swing.MigLayout;

public class PanelAddProduk extends JPanel {
    public PanelAddProduk() {
        setLayout(new MigLayout("insets 40", "[right]20[grow, fill]", "[]30[]15[]15[]15[]30[]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Tambah Kostum Baru");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "span 2, center, wrap");

        add(new JLabel("Nama Kostum:"));
        add(new JTextField(20), "wrap");

        add(new JLabel("Kategori:"));
        add(new JComboBox<>(new String[]{"Anime", "Superhero", "Tradisional", "Game"}), "wrap");

        add(new JLabel("Ukuran:"));
        add(new JComboBox<>(new String[]{"S", "M", "L", "XL", "All Size"}), "wrap");

        add(new JLabel("Harga Sewa:"));
        add(new JTextField(15), "wrap");

        JButton btnSimpan = new JButton("Simpan Produk");
        btnSimpan.setBackground(new Color(131, 188, 160)); // Mengambil tema warna selected
        add(btnSimpan, "span 2, center, w 150!, h 40!");
    }
}