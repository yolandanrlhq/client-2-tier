package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.net.URI; // Import untuk WhatsApp
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

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyResponsiveness();
            }
        });
    }

    private void initializeUI() {
        mainLayout = new MigLayout("fill, insets 30", "[grow]", "[]10[]20[grow]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        title = new JLabel("Daftar Pelanggan");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setForeground(new Color(33, 37, 41));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });

        add(title, "split 2, growx");
        add(btnRefresh, "right, wrap");

        JPanel searchPanel = new JPanel(new MigLayout("insets 0", "[grow]"));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari nama pelanggan atau alamat...");
        
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { searchData(); }
            @Override public void removeUpdate(DocumentEvent e) { searchData(); }
            @Override public void changedUpdate(DocumentEvent e) { searchData(); }
        });

        searchPanel.add(new JLabel("Pencarian: "), "split 2");
        searchPanel.add(txtSearch, "growx, h 35");
        add(searchPanel, "growx, wrap");

        String[] columns = {"ID", "Nama Pelanggan", "No WhatsApp", "Alamat", "Aksi"};
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(230, 245, 238));
        
        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 14));
        header.setBackground(new Color(245, 247, 250));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

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
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 3));
        }
    }

    private void applyResponsiveness() {
        int w = this.getWidth();
        TableColumnModel tcm = table.getColumnModel();
        if (tcm.getColumnCount() < 4) return;
        
        if (w < 600) {
            title.setFont(new Font("Inter", Font.BOLD, 20));
            tcm.getColumn(3).setMinWidth(0);
            tcm.getColumn(3).setMaxWidth(0);
        } else {
            title.setFont(new Font("Inter", Font.BOLD, 28));
            tcm.getColumn(3).setMinWidth(100);
            tcm.getColumn(3).setMaxWidth(5000);
            tcm.getColumn(3).setPreferredWidth(200);
        }
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
                    "Aksi"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat: " + e.getMessage());
        }
    }

    private void hubungiWA(int modelRow) {
        String noWa = model.getValueAt(modelRow, 2).toString();
        noWa = noWa.replaceAll("[^0-9]", "");
        if (noWa.startsWith("0")) noWa = "62" + noWa.substring(1);

        try {
            String url = "https://wa.me/" + noWa + "?text=Halo%20Kak,%20kami%20dari%20Costume%20Rental...";
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal membuka WhatsApp: " + e.getMessage());
        }
    }

    private void editPelanggan(int modelRow) {
        Object id = model.getValueAt(modelRow, 0);
        JOptionPane.showMessageDialog(this, "Fitur Edit Pelanggan ID: " + id);
    }

    private void hapusPelanggan(int modelRow) {
        Object id = model.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus ID " + id + "?", "Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM pelanggan WHERE id_pelanggan = ?");
                pstmt.setObject(1, id);
                pstmt.executeUpdate();
                loadData();
            } catch (SQLException e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
        }
    }

    class ActionRenderer extends JButton implements TableCellRenderer {
        public ActionRenderer() {
            setText("Aksi");
            setFont(new Font("Inter", Font.PLAIN, 12));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
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
            button.addActionListener(e -> {
                int viewRow = table.getSelectedRow();
                if (viewRow != -1) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    String[] options = {"Hubungi WA", "Edit", "Hapus", "Batal"};
                    int choice = JOptionPane.showOptionDialog(button, "Pilih aksi:", "Menu", 0, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (choice == 0) hubungiWA(modelRow);
                    else if (choice == 1) editPelanggan(modelRow);
                    else if (choice == 2) hapusPelanggan(modelRow);
                }
                fireEditingStopped();
            });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) { return button; }
        @Override public Object getCellEditorValue() { return "Aksi"; }
    }
}