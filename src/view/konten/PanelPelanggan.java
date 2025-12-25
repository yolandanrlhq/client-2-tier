package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelPelanggan extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private MigLayout mainLayout;
    private JLabel title;

    public PanelPelanggan() {
        initializeUI();
        loadData();

        // Listener untuk mendeteksi perubahan ukuran layar (Responsif)
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyResponsiveness();
            }
        });
    }

    private void initializeUI() {
        mainLayout = new MigLayout("fillx, insets 30", "[grow]", "[]10[]20[grow]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        // HEADER
        title = new JLabel("Daftar Pelanggan");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setForeground(new Color(33, 37, 41));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });

        add(title, "split 2, growx");
        add(btnRefresh, "right, wrap");

        // SEARCH PANEL
        JPanel searchPanel = new JPanel(new MigLayout("insets 0", "[grow]"));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari nama pelanggan atau alamat...");
        txtSearch.setFont(new Font("Inter", Font.PLAIN, 14));
        
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { searchData(); }
            @Override public void removeUpdate(DocumentEvent e) { searchData(); }
            @Override public void changedUpdate(DocumentEvent e) { searchData(); }
        });

        searchPanel.add(new JLabel("Pencarian: "), "split 2");
        searchPanel.add(txtSearch, "growx, h 38!");
        add(searchPanel, "growx, wrap");

        // TABLE SETUP
        String[] columns = {"ID", "Nama Pelanggan", "No WhatsApp", "Alamat", "Aksi"};
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Hanya kolom Aksi yang bisa diklik
            }
        };

        table = new JTable(model);
        table.setRowHeight(50); // Baris lebih tinggi agar lega
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(240, 245, 255));
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        
        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        // Header Table Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 14));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // Center Align Kolom ID
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // Action Buttons
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionEditor());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scroll.getViewport().setBackground(Color.WHITE);

        add(scroll, "grow, push");
    }

    private void searchData() {
        String text = txtSearch.getText();
        if (text.trim().length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            // Filter pada kolom Nama (indeks 1) dan Alamat (indeks 3)
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 3));
        }
    }

    private void applyResponsiveness() {
        int w = this.getWidth();
        TableColumnModel tcm = table.getColumnModel();
        if (tcm.getColumnCount() < 5) return;
        
        if (w < 750) {
            // Tampilan Layar Kecil
            title.setFont(new Font("Inter", Font.BOLD, 22));
            tcm.getColumn(3).setMinWidth(0); // Sembunyikan Alamat jika terlalu sempit
            tcm.getColumn(3).setMaxWidth(0);
            tcm.getColumn(3).setPreferredWidth(0);
        } else {
            // Tampilan Layar Lebar
            title.setFont(new Font("Inter", Font.BOLD, 28));
            tcm.getColumn(3).setMinWidth(150);
            tcm.getColumn(3).setMaxWidth(Integer.MAX_VALUE);
            tcm.getColumn(3).setPreferredWidth(300);
        }
    }

    public void loadData() {
        model.setRowCount(0);
        try {
            Connection conn = DBConfig.getConnection();
            String sql = "SELECT id_pelanggan, nama_pelanggan, no_wa, alamat FROM pelanggan ORDER BY id_pelanggan DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_pelanggan"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("no_wa"),
                    rs.getString("alamat"),
                    "Aksi"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
    }

    private void editPelanggan(int modelRow) {
        Object id = model.getValueAt(modelRow, 0);
        Object nama = model.getValueAt(modelRow, 1);
        JOptionPane.showMessageDialog(this, "Edit data untuk: " + nama + " (ID: " + id + ")");
        // Nanti panggil Form Edit di sini
    }

    private void hapusPelanggan(int modelRow) {
        Object id = model.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus data pelanggan ID " + id + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM pelanggan WHERE id_pelanggan = ?");
                pstmt.setObject(1, id);
                pstmt.executeUpdate();
                loadData();
            } catch (SQLException e) { 
                JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage()); 
            }
        }
    }

    // ========== CLASS RENDERER & EDITOR UNTUK TOMBOL ==========

    class ActionRenderer extends JButton implements TableCellRenderer {
        public ActionRenderer() {
            setText("Aksi");
            setFont(new Font("Inter", Font.BOLD, 12));
            setBackground(new Color(245, 245, 245));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                int viewRow = table.getSelectedRow();
                if (viewRow != -1) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    
                    // Opsi hanya Edit dan Hapus sesuai permintaan (Tanpa WA)
                    String[] options = {"Edit Data", "Hapus Pelanggan", "Batal"};
                    int choice = JOptionPane.showOptionDialog(
                        button, 
                        "Pilih tindakan untuk baris ini:", 
                        "Menu Kelola", 
                        JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.PLAIN_MESSAGE, 
                        null, options, options[0]
                    );
                    
                    if (choice == 0) editPelanggan(modelRow);
                    else if (choice == 1) hapusPelanggan(modelRow);
                }
                fireEditingStopped();
            });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) { return button; }
        @Override public Object getCellEditorValue() { return "Aksi"; }
    }
}