package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelProduk extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private MigLayout mainLayout;

    public PanelProduk() {
        initializeUI();
        loadData();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyResponsiveTable();
            }
        });
    }

    private void initializeUI() {
        mainLayout = new MigLayout("fill, insets 30", "[grow]", "[]20[grow]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Katalog Kostum");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "wrap");

        // ===== TOOLBAR =====
        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow]10[]10[]"));
        toolbar.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.putClientProperty(
                "JTextField.placeholderText",
                "Cari ID / Nama / Kategori..."
        );
        txtSearch.addActionListener(e -> search());

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> search());

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });

        toolbar.add(txtSearch, "grow");
        toolbar.add(btnSearch, "w 90!");
        toolbar.add(btnRefresh, "w 120!");

        add(toolbar, "growx, wrap");

        // ===== TABLE =====
        String[] columns = {
            "ID", "Nama Kostum", "Kategori", "Stok",
            "Ukuran", "Harga Sewa", "Status", "Aksi"
        };

        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(new Color(60, 60, 60));

        // ===== GARIS TABEL =====
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));

        // ===== SORTING =====
        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // ===== ZEBRA ROW RENDERER =====
        table.setDefaultRenderer(Object.class, new ZebraRenderer());

        // ===== AKSI =====
        table.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        table.getColumn("Aksi").setCellEditor(new ActionEditor());
        table.getColumn("Aksi").setMaxWidth(90);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(sp, "grow");
    }

    // =========================
    // LOAD DATA
    // =========================
    public void loadData() {
        loadData("");
    }

    public void loadData(String keyword) {
        model.setRowCount(0);

        try {
            Connection conn = DBConfig.getConnection();

            String sql = """
                SELECT * FROM kostum
                WHERE id_kostum LIKE ?
                   OR nama_kostum LIKE ?
                   OR kategori LIKE ?
                ORDER BY nama_kostum ASC
            """;

            PreparedStatement pst = conn.prepareStatement(sql);
            String key = "%" + keyword + "%";
            pst.setString(1, key);
            pst.setString(2, key);
            pst.setString(3, key);

            ResultSet res = pst.executeQuery();

            while (res.next()) {
                model.addRow(new Object[]{
                    res.getString("id_kostum"),
                    res.getString("nama_kostum"),
                    res.getString("kategori"),
                    res.getInt("stok"),
                    res.getString("ukuran"),
                    "Rp " + String.format("%,.0f", res.getDouble("harga_sewa")),
                    res.getString("status"),
                    "Aksi"
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void search() {
        loadData(txtSearch.getText().trim());
    }

    // =========================
    // RESPONSIVE TABLE
    // =========================
    private void applyResponsiveTable() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w == null) return;

        int width = w.getWidth();
        TableColumnModel tcm = table.getColumnModel();

        if (width <= 768) {
            hideColumn(tcm, 4);
            hideColumn(tcm, 5);
            hideColumn(tcm, 6);
        } else if (width <= 1200) {
            showColumn(tcm, 4, 70);
            hideColumn(tcm, 5);
            showColumn(tcm, 6, 90);
        } else {
            showColumn(tcm, 4, 70);
            showColumn(tcm, 5, 120);
            showColumn(tcm, 6, 100);
        }
        revalidate();
    }

    private void hideColumn(TableColumnModel tcm, int index) {
        tcm.getColumn(index).setMinWidth(0);
        tcm.getColumn(index).setMaxWidth(0);
        tcm.getColumn(index).setPreferredWidth(0);
    }

    private void showColumn(TableColumnModel tcm, int index, int width) {
        tcm.getColumn(index).setMinWidth(50);
        tcm.getColumn(index).setMaxWidth(1000);
        tcm.getColumn(index).setPreferredWidth(width);
    }

    // =========================
    // ZEBRA RENDERER
    // =========================
    class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                setBackground(new Color(200, 220, 255));
            } else {
                setBackground(row % 2 == 0
                        ? Color.WHITE
                        : new Color(245, 248, 250));
            }
            return this;
        }
    }

    // =========================
    // AKSI
    // =========================
    class ActionRenderer extends JButton implements TableCellRenderer {
        public ActionRenderer() {
            setText("Aksi");
        }
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("Aksi");

        public ActionEditor() {
            button.addActionListener(e -> {
                int row = table.getEditingRow();
                String status = model.getValueAt(row, 6).toString();

                String[] opsi = status.equalsIgnoreCase("Disewa")
                        ? new String[]{"Edit"}
                        : new String[]{"Edit", "Hapus"};

                int pilih = JOptionPane.showOptionDialog(
                        table, "Pilih aksi:", "Aksi Kostum",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, opsi, opsi[0]
                );

                if (pilih == 0) editProduk(row);
                else if (pilih == 1) hapusProduk(row);

                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private void hapusProduk(int row) {
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement pst =
                    conn.prepareStatement("DELETE FROM kostum WHERE id_kostum=?");
            pst.setString(1, model.getValueAt(row, 0).toString());
            pst.executeUpdate();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void editProduk(int row) {
        JTextField txtNama = new JTextField(model.getValueAt(row, 1).toString());
        JTextField txtStok = new JTextField(model.getValueAt(row, 3).toString());
        JTextField txtHarga = new JTextField(
                model.getValueAt(row, 5).toString()
                        .replace("Rp", "")
                        .replace(",", "")
                        .trim()
        );

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Nama")); form.add(txtNama);
        form.add(new JLabel("Stok")); form.add(txtStok);
        form.add(new JLabel("Harga")); form.add(txtHarga);

        int ok = JOptionPane.showConfirmDialog(
                this, form, "Edit Kostum", JOptionPane.OK_CANCEL_OPTION
        );
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE kostum SET nama_kostum=?, stok=?, harga_sewa=? WHERE id_kostum=?"
            );
            pst.setString(1, txtNama.getText());
            pst.setInt(2, Integer.parseInt(txtStok.getText()));
            pst.setDouble(3, Double.parseDouble(txtHarga.getText()));
            pst.setString(4, model.getValueAt(row, 0).toString());
            pst.executeUpdate();
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
