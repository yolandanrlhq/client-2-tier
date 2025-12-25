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

        // ===== TABEL (TAMBAH KOLOM AKSI) =====
        String[] columns = {"ID", "Nama Pelanggan", "No WhatsApp", "Alamat", "Aksi"};

        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Kolom Aksi (index 4) HARUS true agar tombol bisa diklik
                return column == 4;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45); // Ditinggikan sedikit agar tombol proporsional
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

        // Column alignment & Pasang Renderer/Editor
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        
        // --- PASANG AKSI DISINI ---
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionEditor());
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

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
                    rs.getString("alamat"),
                    "Aksi" // Isi string untuk kolom aksi
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    // Fungsi Logika
    private void editPelanggan(int row) {
        String id = model.getValueAt(row, 0).toString();
        JOptionPane.showMessageDialog(this, "Edit Pelanggan ID: " + id);
    }

    private void hapusPelanggan(int row) {
        String id = model.getValueAt(row, 0).toString();
        int tanya = JOptionPane.showConfirmDialog(this, "Hapus pelanggan ID " + id + "?", "Hapus", JOptionPane.YES_NO_OPTION);
        if (tanya == JOptionPane.YES_OPTION) {
            // Logika SQL Delete disini
            loadData();
        }
    }

    // ===== INNER CLASS RENDERER & EDITOR (Kodingan kamu) =====
    class ActionRenderer extends JButton implements TableCellRenderer {
        public ActionRenderer() {
            setText("Aksi");
            setMargin(new Insets(2, 2, 2, 2));
            setBackground(Color.WHITE);
            // Meniru style gambar pertama
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
            ));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("Aksi");
        public ActionEditor() {
            button.setMargin(new Insets(2, 2, 2, 2));
            button.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String[] opsi = {"Edit", "Hapus"};
                    int pilih = JOptionPane.showOptionDialog(table, "Pilih aksi:", "Menu", 0, JOptionPane.PLAIN_MESSAGE, null, opsi, opsi[0]);
                    if (pilih == 0) editPelanggan(row);
                    else if (pilih == 1) hapusPelanggan(row);
                }
                fireEditingStopped();
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) { return button; }
        @Override
        public Object getCellEditorValue() { return null; }
    }
}