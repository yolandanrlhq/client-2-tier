package view.konten;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelProduk extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public PanelProduk() {
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 30", "[grow]", "[]20[grow]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Katalog Kostum");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "wrap");

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.addActionListener(e -> loadData());
        add(btnRefresh, "right, wrap");

        // Kolom + Aksi
        String[] columns = {
            "ID", "Nama Kostum", "Kategori", "Stok", "Ukuran", "Harga Sewa", "Status", "Aksi"
        };

        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // hanya kolom AKSI
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);

        table.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        table.getColumn("Aksi").setCellEditor(new ActionEditor());

        add(new JScrollPane(table), "grow");
    }

    public void loadData() {
        model.setRowCount(0);
        try {
            Connection conn = DBConfig.getConnection();
            ResultSet res = conn.createStatement().executeQuery("SELECT * FROM kostum");
            while (res.next()) {
                double harga = res.getDouble("harga_sewa");
                model.addRow(new Object[]{
                    res.getString("id_kostum"),
                    res.getString("nama_kostum"),
                    res.getString("kategori"),
                    res.getInt("stok"),
                    res.getString("ukuran"),
                    "Rp " + String.format("%,.0f", harga),
                    res.getString("status"),
                    "Aksi"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // =========================
    // RENDERER (TAMPILAN)
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

    // =========================
    // EDITOR (AKSI)
    // =========================
    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("Aksi");

        public ActionEditor() {
            button.addActionListener(e -> showMenu());
        }

        private void showMenu() {
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

    // =========================
    // DELETE
    // =========================
    private void hapusProduk(int row) {
        String id = model.getValueAt(row, 0).toString();
        int ok = JOptionPane.showConfirmDialog(
            this, "Hapus kostum ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION
        );
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement pst =
                conn.prepareStatement("DELETE FROM kostum WHERE id_kostum=?");
            pst.setString(1, id);
            pst.executeUpdate();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // =========================
    // EDIT
    // =========================
    private void editProduk(int row) {
        JTextField txtNama = new JTextField(model.getValueAt(row, 1).toString());
        JTextField txtStok = new JTextField(model.getValueAt(row, 3).toString());
        JTextField txtHarga = new JTextField(
            model.getValueAt(row, 5).toString().replace("Rp", "").replace(",", "").trim()
        );

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Nama"));  form.add(txtNama);
        form.add(new JLabel("Stok"));  form.add(txtStok);
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
