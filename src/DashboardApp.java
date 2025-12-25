import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.FrameUtama;

/**
 * Main Class untuk menjalankan aplikasi Penyewaan Kostum
 */
public class DashboardApp {

    public static void main(String[] args) {
        
        try {
            // 1. Kustomisasi Look and Feel sebelum Frame dibuat
            UIManager.put("defaultFont", new java.awt.Font("Inter", java.awt.Font.PLAIN, 13));
            
            // Mengaktifkan tema FlatLaf Mac Light
            FlatMacLightLaf.setup();
            
            // Kustomisasi warna aksen agar hijau (Soft Green sesuai Dashboard kamu)
            UIManager.put("Component.accentColor", new java.awt.Color(131, 188, 160));
            UIManager.put("Button.arc", 10); // Tambahan: Membuat sudut tombol sedikit melengkung (modern)
            
        } catch (Exception ex) {
            System.err.println("Gagal menginisialisasi FlatLaf");
        }

        // 2. Menjalankan Frame Utama di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            FrameUtama frame = new FrameUtama();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}