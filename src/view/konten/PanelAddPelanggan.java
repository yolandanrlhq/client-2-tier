package view.konten;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import config.DBConfig;

public class PanelAddPelanggan extends JPanel {

    private JTextField txtNama, txtWa;
    private JTextArea txtAlamat;

    public PanelAddPelanggan() {
        setLayout(new MigLayout(
            "insets 40",
            "[right]20[grow, fill]",
            "[]30[]15[]15[]30[]"
        ));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Tambah Pelanggan");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "span 2, center, wrap");

        add(new JLabel("Nama Pelanggan:"));
        txtNama = new JTextField();
        add(txtNama, "wrap");

        add(new JLabel("No WhatsApp:"));
        txtWa = new JTextField();
        add(txtWa, "wrap");

        add(new JLabel("Alamat:"));
        txtAlamat = new JTextArea(3, 20);
        add(new JScrollPane(txtAlamat), "wrap");

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.addActionListener(e -> simpan());
        add(btnSimpan, "span 2, center, w 160!");
    }

    private void simpan() {
        if (txtNama.getText().isEmpty() || txtWa.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama & No WA wajib diisi!");
            return;
        }

        try {
            Connection conn = DBConfig.getConnection();
            String sql = "INSERT INTO pelanggan (nama_pelanggan, no_wa, alamat) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, txtNama.getText());
            pst.setString(2, txtWa.getText());
            pst.setString(3, txtAlamat.getText());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Pelanggan berhasil ditambahkan!");
            txtNama.setText("");
            txtWa.setText("");
            txtAlamat.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
        }
    }
}
