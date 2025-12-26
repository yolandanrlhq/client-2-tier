package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import net.miginfocom.swing.MigLayout;
import model.Pesanan;
import controller.PesananController; // Import Controller

public class PanelPesanan extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JLabel title;
    private MigLayout mainLayout;
    
    // Hapus DAO, ganti dengan Controller
    private PesananController controller;

    public PanelPesanan() {
        // Inisialisasi controller
        this.controller = new PesananController(this);
        
        initializeUI();
        loadData(""); 

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyTableResponsiveness();
            }
        });
    }

    private void initializeUI() {
        mainLayout = new MigLayout("fill, insets 30", "[grow]", "[]15[]20[grow]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        title = new JLabel("Daftar Transaksi Sewa");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "wrap");

        JPanel toolbar = new JPanel(new MigLayout("insets 0", "[grow]10[]10[]"));
        toolbar.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari ID Sewa / Nama Penyewa / Kostum...");
        txtSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));

        JButton btnSearch = new JButton("Cari");
        btnSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.setBackground(new Color(245, 245, 245));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData("");
        });

        toolbar.add(txtSearch, "growx, h 35!");
        toolbar.add(btnSearch, "w 80!, h 35!");
        toolbar.add(btnRefresh, "w 120!, h 35!");

        add(toolbar, "growx, wrap");

        String[] columns = {"ID Sewa", "Penyewa", "Kostum", "Jumlah", "Tgl Pinjam", "Total", "Status", "Aksi"};
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(250, 250, 250));
        
        table.setDefaultRenderer(Object.class, new ZebraRenderer());
        table.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        table.getColumn("Aksi").setCellEditor(new ActionEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, "grow");
    }

    // SEKARANG: View tinggal minta ke Controller
    public void loadData(String keyword) {
        controller.muatData(keyword);
    }

    /**
     * Method ini akan dipanggil oleh Controller setelah LoadPesananWorker selesai
     */
    public void updateTabel(List<Pesanan> listPesanan) {
        model.setRowCount(0);
        for (Pesanan p : listPesanan) {
            model.addRow(new Object[]{
                p.getIdSewa(),
                p.getNamaPenyewa(),
                p.getNamaKostum(),
                p.getJumlah(),
                p.getTglPinjam(),
                "Rp " + String.format("%,.0f", p.getTotalBiaya()),
                p.getStatus(),
                "Aksi"
            });
        }
    }

    private void hapusPesanan(int row) {
        // Ambil ID dari tabel
        Object idObj = model.getValueAt(row, 0);
        
        // Serahkan tugas hapus ke controller
        if (idObj != null) {
            // Jika ID berupa String (misal: "TRX001")
            String idStr = idObj.toString(); 
            // Kalau di database ID-mu Integer, nanti Controller yang sesuaikan
            controller.hapusData(Integer.parseInt(idStr)); 
        }
    }

    private void editPesanan(int row) {
        // Logika membuka Form Edit tetap di sini (View)
        // Namun saat tombol "Update" diklik di form, panggil:
        // controller.ubahData(pesananBaru);
    }

    // --- SISA KODE (Responsive & Renderers) ---
    private void applyTableResponsiveness() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window == null) return;
        int w = window.getWidth();
        if (w <= 0) return;
        TableColumnModel tcm = table.getColumnModel();

        if (w <= 768) {
            mainLayout.setLayoutConstraints("fill, insets 15");
            title.setFont(new Font("Inter", Font.BOLD, 20));
            hideColumn(tcm, 3); hideColumn(tcm, 4); hideColumn(tcm, 5);
            setColumnWidth(tcm, 0, 70); setColumnWidth(tcm, 1, 120); setColumnWidth(tcm, 2, 120);
            setColumnWidth(tcm, 6, 90); setColumnWidth(tcm, 7, 70);
        } else if (w <= 1200) {
            mainLayout.setLayoutConstraints("fill, insets 30 4% 30 4%");
            title.setFont(new Font("Inter", Font.BOLD, 24));
            showColumn(tcm, 3, 60); showColumn(tcm, 4, 110); hideColumn(tcm, 5);
            setColumnWidth(tcm, 6, 100); setColumnWidth(tcm, 7, 80);
        } else {
            mainLayout.setLayoutConstraints("fill, insets 40 6% 40 6%");
            title.setFont(new Font("Inter", Font.BOLD, 32));
            showColumn(tcm, 3, 70); showColumn(tcm, 4, 130); showColumn(tcm, 5, 120);
            setColumnWidth(tcm, 6, 110); setColumnWidth(tcm, 7, 90);
        }
        this.revalidate();
    }

    private void setColumnWidth(TableColumnModel tcm, int index, int width) {
        tcm.getColumn(index).setMinWidth(width);
        tcm.getColumn(index).setMaxWidth(width == 0 ? 0 : 1000);
        tcm.getColumn(index).setPreferredWidth(width);
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

    class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                setBackground(new Color(200, 220, 255));
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 250));
            }
            return this;
        }
    }

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
}