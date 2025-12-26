package view.konten;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;
import controller.PelangganController;
import model.Pelanggan;

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
        loadData();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { applyResponsiveness(); }
        });
    }

    public void loadData() {
        if (controller != null) {
            controller.displayData();
        }
    }

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

        String[] columns = {"ID", "Nama Pelanggan", "No WhatsApp", "Alamat", "Aksi"};
        model = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        
        table = new JTable(model);
        table.setRowHeight(60);
        
        // --- REVISI 1: AKTIFKAN GRID TABEL ---
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230)); 
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // --- REVISI 2: WARNA SELANG-SELING (ZEBRA STRIPING) ---
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    // Baris genap putih, baris ganjil abu-abu muda
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }
                // Memberikan margin teks agar tidak menempel garis
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        table.getColumnModel().getColumn(4).setCellRenderer(new ActionPanelRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionPanelEditor());
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

        add(new JScrollPane(table), "grow, push");
    }

    private void searchData() {
        String text = txtSearch.getText();
        rowSorter.setRowFilter(text.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
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

    // RENDERER: Tampilan dua tombol
    class ActionPanelRenderer extends JPanel implements TableCellRenderer {
        public ActionPanelRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
            setOpaque(true);
            JButton bEdit = new JButton("Edit");
            JButton bDel = new JButton("Hapus");
            bEdit.setBackground(new Color(33, 150, 243)); bEdit.setForeground(Color.WHITE);
            bDel.setBackground(new Color(244, 67, 54)); bDel.setForeground(Color.WHITE);
            add(bEdit); add(bDel);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hF, int r, int c) {
            // Mengikuti warna background baris (putih/abu-abu) agar rapi
            if (isS) {
                setBackground(t.getSelectionBackground());
            } else {
                setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
            }
            return this;
        }
    }

    // EDITOR: Logika klik tombol
    class ActionPanelEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 15));
        private JButton btnEdit = new JButton("Edit");
        private JButton btnDelete = new JButton("Hapus");

        public ActionPanelEditor() {
            btnEdit.setBackground(new Color(33, 150, 243)); btnEdit.setForeground(Color.WHITE);
            btnDelete.setBackground(new Color(244, 67, 54)); btnDelete.setForeground(Color.WHITE);

            btnEdit.addActionListener(e -> {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                String id = model.getValueAt(row, 0).toString();
                String nama = model.getValueAt(row, 1).toString();
                String wa = model.getValueAt(row, 2).toString();
                String alamat = model.getValueAt(row, 3).toString();

                JTextField fNama = new JTextField(nama);
                JTextField fWa = new JTextField(wa);
                JTextField fAlamat = new JTextField(alamat);
                Object[] msg = {"Nama:", fNama, "WhatsApp:", fWa, "Alamat:", fAlamat};

                int opt = JOptionPane.showConfirmDialog(null, msg, "Edit Pelanggan " + id, JOptionPane.OK_CANCEL_OPTION);
                if (opt == JOptionPane.OK_OPTION) {
                    controller.updateData(new Pelanggan(id, fNama.getText(), fWa.getText(), fAlamat.getText()), null);
                }
                fireEditingStopped();
            });

            btnDelete.addActionListener(e -> {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                String id = model.getValueAt(row, 0).toString();
                
                // --- REVISI 3: UBAH DIALOG MENJADI YES_NO_OPTION (TANPA CANCEL) ---
                int confirm = JOptionPane.showConfirmDialog(
                    null, 
                    "Hapus data " + id + "?", 
                    "Konfirmasi Hapus", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    controller.deleteData(id);
                }
                
                fireEditingStopped();
            });

            panel.add(btnEdit); panel.add(btnDelete);
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }
        @Override public Object getCellEditorValue() { return ""; }
    }
}