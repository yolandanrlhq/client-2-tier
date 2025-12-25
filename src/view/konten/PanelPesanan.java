package view.konten;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;
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
        btnRefresh.addActionListener(e -> loadData());
        add(btnRefresh, "right, wrap");

        String[] columns = {"ID Sewa", "Penyewa", "Kostum", "Jumlah", "Tgl Pinjam", "Total", "Status", "Aksi"};
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Hanya kolom Aksi yang bisa diklik
            }
        };

        table = new JTable(model);
        table.setRowHeight(40); // Ukuran baris lebih lega untuk tombol
        
        // Memasang Renderer dan Editor pada kolom Aksi
        table.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        table.getColumn("Aksi").setCellEditor(new ActionEditor());

        add(new JScrollPane(table), "grow");
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
            JOptionPane.showMessageDialog(this, "Gagal Load: " + e.getMessage());
        }
    }

    // ==========================================
    // LOGIKA EDIT (Termasuk Ganti Kostum)
    // ==========================================
    private void editPesanan(int row) {
        String idSewa = model.getValueAt(row, 0).toString();
        String namaKostumLama = model.getValueAt(row, 2).toString();
        
        JTextField txtPenyewa = new JTextField(model.getValueAt(row, 1).toString());
        JSpinner txtJumlah = new JSpinner(new SpinnerNumberModel((int)model.getValueAt(row, 3), 1, 100, 1));
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Disewa", "Selesai", "Dibatalkan"});
        cbStatus.setSelectedItem(model.getValueAt(row, 6).toString());

        JComboBox<String> cbKostum = new JComboBox<>();
        cbKostum.addItem(namaKostumLama); // Pilihan pertama adalah kostum saat ini
        
        String idKostumLama = "";
        try {
            Connection conn = DBConfig.getConnection();
            // Cari ID kostum yang sedang dipakai
            PreparedStatement pst = conn.prepareStatement("SELECT id_kostum FROM pesanan WHERE id_sewa=?");
            pst.setString(1, idSewa);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) idKostumLama = rs.getString("id_kostum");

            // Load kostum lain yang 'Tersedia'
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

        int ok = JOptionPane.showConfirmDialog(this, form, "Edit Transaksi " + idSewa, JOptionPane.OK_CANCEL_OPTION);
        
        if (ok == JOptionPane.OK_OPTION) {
            try {
                Connection conn = DBConfig.getConnection();
                String idKostumBaru = idKostumLama;
                String selectedK = cbKostum.getSelectedItem().toString();
                if (selectedK.contains(" - ")) idKostumBaru = selectedK.split(" - ")[0];

                // Hitung ulang biaya
                PreparedStatement pstH = conn.prepareStatement("SELECT harga_sewa FROM kostum WHERE id_kostum=?");
                pstH.setString(1, idKostumBaru);
                ResultSet rsH = pstH.executeQuery();
                double harga = rsH.next() ? rsH.getDouble("harga_sewa") : 0;
                double totalBaru = harga * (int)txtJumlah.getValue();

                // Update data pesanan
                String sqlUpd = "UPDATE pesanan SET nama_penyewa=?, id_kostum=?, jumlah=?, total_biaya=?, status=? WHERE id_sewa=?";
                PreparedStatement psU = conn.prepareStatement(sqlUpd);
                psU.setString(1, txtPenyewa.getText());
                psU.setString(2, idKostumBaru);
                psU.setInt(3, (int)txtJumlah.getValue());
                psU.setDouble(4, totalBaru);
                psU.setString(5, cbStatus.getSelectedItem().toString());
                psU.setString(6, idSewa);
                psU.executeUpdate();

                // Logic pertukaran status kostum
                if (!idKostumBaru.equals(idKostumLama)) {
                    conn.createStatement().executeUpdate("UPDATE kostum SET status='Tersedia' WHERE id_kostum='" + idKostumLama + "'");
                    conn.createStatement().executeUpdate("UPDATE kostum SET status='Disewa' WHERE id_kostum='" + idKostumBaru + "'");
                }

                // Jika diselesaikan
                if (!cbStatus.getSelectedItem().toString().equals("Disewa")) {
                    conn.createStatement().executeUpdate("UPDATE kostum SET status='Tersedia' WHERE id_kostum='" + idKostumBaru + "'");
                }

                loadData();
                JOptionPane.showMessageDialog(this, "Berhasil Diperbarui!");
            } catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        }
    }

    private void hapusPesanan(int row) {
        String id = model.getValueAt(row, 0).toString();
        int ok = JOptionPane.showConfirmDialog(this, "Hapus transaksi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConfig.getConnection();
                // Set kostum jadi tersedia lagi sebelum dihapus
                ResultSet rs = conn.createStatement().executeQuery("SELECT id_kostum FROM pesanan WHERE id_sewa='"+id+"'");
                if(rs.next()) conn.createStatement().executeUpdate("UPDATE kostum SET status='Tersedia' WHERE id_kostum='"+rs.getString("id_kostum")+"'");
                
                PreparedStatement pst = conn.prepareStatement("DELETE FROM pesanan WHERE id_sewa=?");
                pst.setString(1, id);
                pst.executeUpdate();
                loadData();
            } catch (SQLException e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
        }
    }

    // ==========================================
    // RENDERER & EDITOR (Fixed Position)
    // ==========================================
    class ActionRenderer extends JButton implements TableCellRenderer {
        public ActionRenderer() {
            setText("Aksi");
            setMargin(new Insets(2, 2, 2, 2));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : UIManager.getColor("Button.background"));
            return this;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("Aksi");
        public ActionEditor() {
            button.setMargin(new Insets(2, 2, 2, 2));
            button.addActionListener(e -> {
                int row = table.getEditingRow();
                if (row != -1) {
                    String[] opsi = {"Edit", "Hapus"};
                    int pilih = JOptionPane.showOptionDialog(table, "Pilih aksi:", "Menu", 0, JOptionPane.PLAIN_MESSAGE, null, opsi, opsi[0]);
                    if (pilih == 0) editPesanan(row);
                    else if (pilih == 1) hapusPesanan(row);
                }
                fireEditingStopped(); // Kembali ke tampilan Renderer
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) { return button; }
        @Override
        public Object getCellEditorValue() { return null; }
    }
}