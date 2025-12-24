package view.konten;

import javax.swing.*;
import java.awt.*;
import net.miginfocom.swing.MigLayout;

public class PanelAddPesanan extends JPanel {
    public PanelAddPesanan() {
        setLayout(new MigLayout("insets 40, fillx", "[grow]", "[]20[]20[]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Form Penyewaan Kostum");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "center, wrap");

        // Bagian Data Penyewa
        JPanel pnlPenyewa = new JPanel(new MigLayout("fillx, insets 15", "[right]15[grow, fill]"));
        pnlPenyewa.setBorder(BorderFactory.createTitledBorder("Data Penyewa"));
        pnlPenyewa.add(new JLabel("Nama Lengkap:"));
        pnlPenyewa.add(new JTextField(), "wrap");
        pnlPenyewa.add(new JLabel("No. WhatsApp:"));
        pnlPenyewa.add(new JTextField(), "wrap");
        pnlPenyewa.add(new JLabel("Jaminan (KTP/Lainnya):"));
        pnlPenyewa.add(new JTextField(), "wrap");
        add(pnlPenyewa, "growx, wrap");

        // Bagian Detail Sewa
        JPanel pnlSewa = new JPanel(new MigLayout("fillx, insets 15", "[right]15[grow, fill]"));
        pnlSewa.setBorder(BorderFactory.createTitledBorder("Detail Kostum"));
        pnlSewa.add(new JLabel("Pilih Kostum:"));
        pnlSewa.add(new JComboBox<>(new String[]{"Pilih Kostum...", "Naruto Sage Mode", "Spider-Man"}), "wrap");
        pnlSewa.add(new JLabel("Durasi (Hari):"));
        pnlSewa.add(new JSpinner(new SpinnerNumberModel(1, 1, 30, 1)), "w 50!, wrap");
        add(pnlSewa, "growx, wrap");

        JButton btnProses = new JButton("Proses Penyewaan");
        btnProses.setFont(new Font("Inter", Font.BOLD, 14));
        add(btnProses, "center, w 200!, h 45!");
    }
}