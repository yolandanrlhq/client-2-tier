package view.konten;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelPesanan extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public PanelPesanan() {
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 30", "[grow]", "[]20[grow]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Daftar Transaksi Sewa");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "wrap");

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.setBackground(new Color(240, 240, 240));
        btnRefresh.addActionListener(e -> loadData());
        add(btnRefresh, "right, wrap");

        // Kolom ditambah "Jumlah"
        String[] columns = {"ID Sewa", "Penyewa", "Kostum", "Jumlah", "Tgl Pinjam", "Total", "Status"};
        model = new DefaultTableModel(null, columns);
        table = new JTable(model);
        
        // Styling tabel
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        
        add(new JScrollPane(table), "grow");
    }

    public void loadData() {
        model.setRowCount(0);
        try {
            Connection conn = DBConfig.getConnection();
            String sql = "SELECT p.*, k.nama_kostum FROM pesanan p " +
                         "JOIN kostum k ON p.id_kostum = k.id_kostum";
            ResultSet res = conn.createStatement().executeQuery(sql);

            while(res.next()) {
                double total = res.getDouble("total_biaya");
                
                model.addRow(new Object[]{
                    res.getString("id_sewa"),
                    res.getString("nama_penyewa"),
                    res.getString("nama_kostum"),
                    res.getInt("jumlah"),
                    res.getDate("tgl_pinjam"),
                    "Rp " + String.format("%,.0f", total), // Gunakan format ini
                    res.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}