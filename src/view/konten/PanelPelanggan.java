package view.konten;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelPelanggan extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public PanelPelanggan() {
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 30", "[grow]", "[]20[grow]"));
        setBackground(Color.WHITE);

        // ===== JUDUL =====
        JLabel title = new JLabel("Daftar Pelanggan");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setForeground(new Color(33, 37, 41));
        add(title, "wrap");

        // ===== TOOLBAR =====
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());
        toolbar.add(btnRefresh);

        add(toolbar, "growx, wrap");

        // ===== TABEL =====
        String[] columns = {"ID", "Nama Pelanggan", "No WhatsApp", "Alamat"};

        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(230, 245, 238));
        table.setSelectionForeground(Color.BLACK);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 14));
        header.setBackground(new Color(245, 247, 250));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));

        // Column alignment
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);

        // ScrollPane styling
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        add(scroll, "grow");
    }

    public void loadData() {
        model.setRowCount(0);

        try {
            Connection conn = DBConfig.getConnection();
            String sql = "SELECT id_pelanggan, nama_pelanggan, no_wa, alamat FROM pelanggan";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_pelanggan"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("no_wa"),
                    rs.getString("alamat")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }
}
