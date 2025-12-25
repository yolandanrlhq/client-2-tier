package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelPesanan extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JLabel title;
    private MigLayout mainLayout;

    public PanelPesanan() {
        initializeUI();
        loadData();

        // Listener untuk deteksi perubahan ukuran layar (Responsive Table)
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyTableResponsiveness();
            }
        });
    }

    private void initializeUI() {
        mainLayout = new MigLayout("fill, insets 30", "[grow]", "[]20[grow]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        title = new JLabel("Daftar Transaksi Sewa");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "split 2, growx");

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.setBackground(new Color(245, 245, 245));
        btnRefresh.addActionListener(e -> loadData());
        add(btnRefresh, "right, wrap");

        String[] columns = {"ID Sewa", "Penyewa", "Kostum", "Jumlah", "Tgl Pinjam", "Total", "Status", "Aksi"};
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Hanya kolom Aksi
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(250, 250, 250));
        
        // Memasang Renderer dan Editor pada kolom Aksi
        table.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        table.getColumn("Aksi").setCellEditor(new ActionEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, "grow");
    }

    private void applyTableResponsiveness() {
        Window window = SwingUtilities.getWindowAncestor(this);
if (window == null) return;

int w = window.getWidth();

        if (w <= 0) return;

        TableColumnModel tcm = table.getColumnModel();

        // ================= SPLIT VIEW =================
        if (w <= 768) {
            mainLayout.setLayoutConstraints("fill, insets 15");
            title.setFont(new Font("Inter", Font.BOLD, 20));

            // Kolom penting saja
            hideColumn(tcm, 3); // Jumlah
            hideColumn(tcm, 4); // Tgl Pinjam
            hideColumn(tcm, 5); // Total

            setColumnWidth(tcm, 0, 70);  // ID
            setColumnWidth(tcm, 1, 120); // Penyewa
            setColumnWidth(tcm, 2, 120); // Kostum
            setColumnWidth(tcm, 6, 90);  // Status
            setColumnWidth(tcm, 7, 70);  // Aksi

        // ================= DESKTOP SMALL =================
        } else if (w <= 1200) {
            mainLayout.setLayoutConstraints("fill, insets 30 4% 30 4%");
            title.setFont(new Font("Inter", Font.BOLD, 24));

            showColumn(tcm, 3, 60);   // Jumlah
            showColumn(tcm, 4, 110);  // Tanggal
            hideColumn(tcm, 5);       // Total (masih disembunyikan)

            setColumnWidth(tcm, 6, 100);
            setColumnWidth(tcm, 7, 80);

        // ================= DESKTOP STANDARD =================
        } else {
            // 1200 – 1400 (1366 optimal)
            mainLayout.setLayoutConstraints("fill, insets 40 6% 40 6%");
            title.setFont(new Font("Inter", Font.BOLD, 32));

            showColumn(tcm, 3, 70);   // Jumlah
            showColumn(tcm, 4, 130);  // Tanggal
            showColumn(tcm, 5, 120);  // Total

            setColumnWidth(tcm, 6, 110);
            setColumnWidth(tcm, 7, 90);
        }

        this.revalidate();
    }

    // Helper untuk mengubah lebar kolom secara dinamis
    private void setColumnWidth(TableColumnModel tcm, int index, int width) {
        tcm.getColumn(index).setMinWidth(width);
        tcm.getColumn(index).setMaxWidth(width == 0 ? 0 : 1000);
        tcm.getColumn(index).setPreferredWidth(width);
    }

    public void loadData() {
        model.setRowCount(0);
        try {
            Connection conn = DBConfig.getConnection();
            String sql = "SELECT p.*, k.nama_kostum FROM pesanan p " +
                         "JOIN kostum k ON p.id_kostum = k.id_kostum ORDER BY p.tgl_pinjam DESC";
            ResultSet res = conn.createStatement().executeQuery(sql);
            while (res.next()) {
                model.addRow(new Object[]{
                    res.getString("id_sewa"),
                    res.getString("nama_penyewa"),
                    res.getString("nama_kostum"),
                    res.getInt("jumlah"),
                    res.getDate("tgl_pinjam"),
                    "Rp " + String.format("%,.0f", res.getDouble("total_biaya")),
                    res.getString("status"),
                    "Aksi"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // --- LOGIKA EDIT & HAPUS (Tetap Sama dengan kode Anda) ---
    private void editPesanan(int row) {
        String idSewa = model.getValueAt(row, 0).toString();
        String namaKostumLama = model.getValueAt(row, 2).toString();
        
        JTextField txtPenyewa = new JTextField(model.getValueAt(row, 1).toString());
        JSpinner txtJumlah = new JSpinner(new SpinnerNumberModel((int)model.getValueAt(row, 3), 1, 100, 1));
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Disewa", "Selesai", "Dibatalkan"});
        cbStatus.setSelectedItem(model.getValueAt(row, 6).toString());

        JComboBox<String> cbKostum = new JComboBox<>();
        cbKostum.addItem(namaKostumLama);
        
        String idKostumLama = "";
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT id_kostum FROM pesanan WHERE id_sewa=?");
            pst.setString(1, idSewa);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) idKostumLama = rs.getString("id_kostum");

            ResultSet resK = conn.createStatement().executeQuery("SELECT id_kostum, nama_kostum FROM kostum WHERE status='Tersedia'");
            while(resK.next()){
                cbKostum.addItem(resK.getString("id_kostum") + " - " + resK.getString("nama_kostum"));
            }
        } catch (Exception e) {}

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Penyewa:"));    form.add(txtPenyewa);
        form.add(new JLabel("Ganti Kostum:")); form.add(cbKostum);
        form.add(new JLabel("Jumlah Unit:"));  form.add(txtJumlah);
        form.add(new JLabel("Status:"));       form.add(cbStatus);

        int ok = JOptionPane.showConfirmDialog(this, form, "Edit Transaksi", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                Connection conn = DBConfig.getConnection();
                String idKostumBaru = idKostumLama;
                String selectedK = cbKostum.getSelectedItem().toString();
                if (selectedK.contains(" - ")) idKostumBaru = selectedK.split(" - ")[0];

                PreparedStatement pstH = conn.prepareStatement("SELECT harga_sewa FROM kostum WHERE id_kostum=?");
                pstH.setString(1, idKostumBaru);
                ResultSet rsH = pstH.executeQuery();
                double harga = rsH.next() ? rsH.getDouble("harga_sewa") : 0;
                double totalBaru = harga * (int)txtJumlah.getValue();

                String sqlUpd = "UPDATE pesanan SET nama_penyewa=?, id_kostum=?, jumlah=?, total_biaya=?, status=? WHERE id_sewa=?";
                PreparedStatement psU = conn.prepareStatement(sqlUpd);
                psU.setString(1, txtPenyewa.getText());
                psU.setString(2, idKostumBaru);
                psU.setInt(3, (int)txtJumlah.getValue());
                psU.setDouble(4, totalBaru);
                psU.setString(5, cbStatus.getSelectedItem().toString());
                psU.setString(6, idSewa);
                psU.executeUpdate();

                if (!idKostumBaru.equals(idKostumLama)) {
                    conn.createStatement().executeUpdate("UPDATE kostum SET status='Tersedia' WHERE id_kostum='" + idKostumLama + "'");
                    conn.createStatement().executeUpdate("UPDATE kostum SET status='Disewa' WHERE id_kostum='" + idKostumBaru + "'");
                }

                if (!cbStatus.getSelectedItem().toString().equals("Disewa")) {
                    conn.createStatement().executeUpdate("UPDATE kostum SET status='Tersedia' WHERE id_kostum='" + idKostumBaru + "'");
                }
                loadData();
            } catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        }
    }

    private void hapusPesanan(int row) {
        String id = model.getValueAt(row, 0).toString();
        int ok = JOptionPane.showConfirmDialog(this, "Hapus transaksi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConfig.getConnection();
                ResultSet rs = conn.createStatement().executeQuery("SELECT id_kostum FROM pesanan WHERE id_sewa='"+id+"'");
                if(rs.next()) conn.createStatement().executeUpdate("UPDATE kostum SET status='Tersedia' WHERE id_kostum='"+rs.getString("id_kostum")+"'");
                
                PreparedStatement pst = conn.prepareStatement("DELETE FROM pesanan WHERE id_sewa=?");
                pst.setString(1, id);
                pst.executeUpdate();
                loadData();
            } catch (SQLException e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
        }
    }

    // --- RENDERER & EDITOR (Tetap Sama) ---
    class ActionRenderer extends JButton implements TableCellRenderer {
        public ActionRenderer() {
            setText("Aksi");
            setBackground(new Color(108, 155, 244));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setFont(new Font("Inter", Font.BOLD, 12));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("Aksi");
        public ActionEditor() {
            button.addActionListener(e -> {
                int row = table.getEditingRow();
                if (row != -1) {
                    String[] opsi = {"Edit", "Hapus"};
                    int pilih = JOptionPane.showOptionDialog(table, "Pilih aksi:", "Menu", 0, JOptionPane.PLAIN_MESSAGE, null, opsi, opsi[0]);
                    if (pilih == 0) editPesanan(row);
                    else if (pilih == 1) hapusPesanan(row);
                }
                fireEditingStopped();
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) { return button; }
        @Override
        public Object getCellEditorValue() { return null; }
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

}