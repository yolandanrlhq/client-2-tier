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
        // 1. Setup Font Inter (Sesuai dengan kode awal kamu)
        UIManager.put("defaultFont", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
        
        // 2. Setup Look and Feel (Tampilan Modern ala Mac)
        try {
            // Mengatur font default aplikasi ke Inter
            UIManager.put("defaultFont", new java.awt.Font("Inter", java.awt.Font.PLAIN, 13));
            
            // Mengaktifkan tema FlatLaf Mac Light
            FlatMacLightLaf.setup();
            
            // Kustomisasi warna aksen agar hijau (sesuai tema penyewaan kamu)
            UIManager.put("Component.accentColor", new java.awt.Color(131, 188, 160));
            
        } catch (Exception ex) {
            System.err.println("Gagal menginisialisasi FlatLaf");
        }

        // 3. Menjalankan Frame Utama
        SwingUtilities.invokeLater(() -> {
            FrameUtama frame = new FrameUtama();
            frame.pack();
            frame.setLocationRelativeTo(null); // Agar muncul di tengah layar
            frame.setVisible(true);
        });
    }
}