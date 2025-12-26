package view.konten;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;
import controller.PelangganController;

public class PanelPelanggan extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private PelangganController controller;
    private JLabel title;

    public PanelPelanggan() {
        initializeUI();
        this.controller = new PelangganController(this);
        loadData(); // Inisialisasi data awal

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { applyResponsiveness(); }
        });
    }

    // Method ini harus PUBLIC agar FrameUtama tidak merah
    public void loadData() {
        if (controller != null) {
            controller.displayData();
        }
    }

    // Method ini harus PUBLIC agar Controller bisa akses tabel
    public DefaultTableModel getModel() {
        return model;
    }

    private void initializeUI() {
        setLayout(new MigLayout("fillx, insets 30", "[grow]", "[]10[]20[grow]"));
        setBackground(Color.WHITE);

        title = new JLabel("Daftar Pelanggan");
        title.setFont(new Font("Inter", Font.BOLD, 28));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());

        add(title, "split 2, growx");
        add(btnRefresh, "right, wrap");

        txtSearch = new JTextField();
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { searchData(); }
            @Override public void removeUpdate(DocumentEvent e) { searchData(); }
            @Override public void changedUpdate(DocumentEvent e) { searchData(); }
        });
        add(new JLabel("Pencarian: "), "split 2");
        add(txtSearch, "growx, h 38!, wrap");

        // Kolom tetap sama, tapi data ID di dalamnya sekarang String
        String[] columns = {"ID", "Nama Pelanggan", "No WhatsApp", "Alamat", "Aksi"};
        model = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        table = new JTable(model);
        table.setRowHeight(50);
        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        table.getColumnModel().getColumn(4).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionEditor());

        add(new JScrollPane(table), "grow, push");
    }

    private void searchData() {
        String text = txtSearch.getText();
        rowSorter.setRowFilter(text.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + text, 0, 1, 3));
    }

    private void applyResponsiveness() {
        int w = this.getWidth();
        TableColumnModel tcm = table.getColumnModel();
        if (w < 750) {
            tcm.getColumn(3).setMinWidth(0); tcm.getColumn(3).setMaxWidth(0);
        } else {
            tcm.getColumn(3).setMinWidth(150); tcm.getColumn(3).setMaxWidth(Integer.MAX_VALUE);
        }
    }

    class ActionRenderer extends JButton implements TableCellRenderer {
        public ActionRenderer() { setText("Aksi"); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hF, int r, int c) { return this; }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("Aksi");
        public ActionEditor() {
            button.addActionListener(e -> {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                
                // PERBAIKAN: Ambil ID sebagai String (HD001), bukan int lagi
                String id = model.getValueAt(row, 0).toString();
                
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Hapus Pelanggan ID: " + id + "?", "Konfirmasi Hapus", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.deleteData(id); // Memanggil deleteData(String id)
                }
                fireEditingStopped();
            });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) { return button; }
        @Override public Object getCellEditorValue() { return "Aksi"; }
    }
}