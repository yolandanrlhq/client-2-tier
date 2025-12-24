package view.konten;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

public class PanelPesanan extends JPanel {

    public PanelPesanan() {
        initializeUI();
    }

    private void initializeUI() {
        // Menggunakan layout yang sama dengan standar aplikasi kamu
        setLayout(new MigLayout("fill, insets 30", "[grow]", "[]20[grow]"));
        setBackground(Color.WHITE);

        // Header Panel
        JLabel title = new JLabel("Daftar Pesanan & Sewa");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setForeground(new Color(0, 48, 73)); // Warna Navy sesuai tema
        add(title, "wrap");

        // Tabel Pesanan
        String[] columns = {
            "ID Sewa", "Penyewa", "Kostum Karakter", "Tgl Pinjam", "Tgl Kembali", "Total", "Status"
        };

        // Contoh Data Dummy (nanti bisa diambil dari Database)
        Object[][] data = {
            {"TRX-001", "Budi Utomo", "Naruto Sage Mode", "2023-10-01", "2023-10-03", "Rp 300.000", "Disewa"},
            {"TRX-002", "Siska Amelia", "Wonder Woman", "2023-10-05", "2023-10-06", "Rp 250.000", "Selesai"},
            {"TRX-003", "Andi Wijaya", "Tanjiro Kamado", "2023-10-10", "2023-10-12", "Rp 350.000", "Terlambat"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit langsung
            }
        };

        JTable table = new JTable(model);
        setupTableStyle(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, "grow");
    }

    private void setupTableStyle(JTable table) {
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(245, 247, 250));
        table.setSelectionBackground(new Color(234, 242, 235)); // Warna hijau soft sesuai tema
        table.setShowGrid(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
    }
}