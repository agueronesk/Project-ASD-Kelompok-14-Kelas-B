import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class GalaxyRefined extends JFrame {

    // --- Konfigurasi Game (PUBLIC STATIC agar bisa diakses file lain) ---
    public static final int MAX_NODES = 50;
    public static final int FRAME_WIDTH = 1280;
    public static final int FRAME_HEIGHT = 800;
    public static final int GAME_WIDTH = 900;
    private static final String SAVE_FILE = "galaxy_leaderboard.dat";

    // --- PALETTE WARNA (PUBLIC STATIC) ---
    public static final Color COL_BG_DEEP_INDIGO = new Color(0x1F1250); 
    public static final Color COL_BG_DARK_VIOLET = new Color(0x2C1E5C); 
    public static final Color COL_BG_BLACKEST     = new Color(0x05020a); 

    public static final Color COL_ACCENT_ELECTRO = new Color(0x7D28D4); 
    public static final Color COL_ACCENT_CYAN    = new Color(0x00F0FF); 
    public static final Color COL_ACCENT_MAGENTA = new Color(0xC71585); 
    public static final Color COL_ACCENT_GOLD    = new Color(0xFFD700); 

    public static final Color COL_TEXT_LAVENDER  = new Color(0xEED3FA); 
    public static final Color COL_TEXT_WHITE     = new Color(0xFFFFFF); 
    public static final Color COL_GLASS_PANEL    = new Color(31, 18, 80, 200); 

    // Data Global
    public static Map<String, PlayerData> globalPlayerData = new HashMap<>();

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private IntroPanel introPanel;
    private GameContainerPanel gameContainerPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
                UIManager.put("Label.foreground", COL_TEXT_LAVENDER);
            } catch (Exception e) {}
            
            // 1. INISIALISASI AUDIO ENGINE (MIDI)
            MidiSynthSystem.init();
            
            GalaxyRefined game = new GalaxyRefined();
            game.setVisible(true);
            
            // 2. MULAI MUSIK AMBIENT
            MidiSynthSystem.startBGM();
        });
    }

    public GalaxyRefined() {
        loadLeaderboard();

        setTitle("Galaxy Explorer: Electro Nebula Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        introPanel = new IntroPanel(this);
        gameContainerPanel = new GameContainerPanel(this);

        mainPanel.add(introPanel, "INTRO");
        mainPanel.add(gameContainerPanel, "GAME");

        add(mainPanel);
        cardLayout.show(mainPanel, "INTRO");
    }

    public void startGame(int numPlayers, List<String> playerNames) {
        introPanel.stopAnimation();
        gameContainerPanel.setupNewGame(numPlayers, playerNames);
        cardLayout.show(mainPanel, "GAME");
    }

    public void showIntro() {
        gameContainerPanel.stopAnimation();
        introPanel.startAnimation();
        introPanel.refreshLeaderboard();
        cardLayout.show(mainPanel, "INTRO");
        
        // Pastikan BGM nyala lagi kalau habis menang
        MidiSynthSystem.startBGM();
    }

    // --- SAVE/LOAD SYSTEM ---
    public void updateGlobalData(Player p, boolean won) {
        PlayerData pd = globalPlayerData.computeIfAbsent(p.name, PlayerData::new);
        pd.totalScore += p.currentScore;
        if(won) pd.wins++;
        saveLeaderboard();
    }

    private void saveLeaderboard() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(globalPlayerData);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan leaderboard: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadLeaderboard() {
        File f = new File(SAVE_FILE);
        if(!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if(obj instanceof Map) globalPlayerData = (Map<String, PlayerData>) obj;
        } catch (Exception e) {
            System.err.println("Leaderboard baru/corrupt, membuat baru.");
        }
    }

    // Helper static methods accessible globally
    public static void paintCosmicBackground(Graphics g, int w, int h, List<SpaceObject> objects) {
        Graphics2D g2 = (Graphics2D)g;

        RadialGradientPaint baseGradient = new RadialGradientPaint(
                new Point2D.Double(w / 2.0, h / 2.0), Math.max(w, h),
                new float[]{0.0f, 0.6f, 1.0f},
                new Color[]{COL_BG_DEEP_INDIGO, COL_BG_DARK_VIOLET, COL_BG_BLACKEST}
        );
        g2.setPaint(baseGradient);
        g2.fillRect(0,0,w,h);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        RadialGradientPaint nebula1 = new RadialGradientPaint(
                new Point(w/4, h/4), w/1.5f, new float[]{0f, 1f},
                new Color[]{COL_ACCENT_ELECTRO, new Color(0,0,0,0)});
        g2.setPaint(nebula1);
        g2.fillOval(0, 0, w, h);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
        RadialGradientPaint nebula2 = new RadialGradientPaint(
                new Point(w - w/4, h - h/3), w/1.5f, new float[]{0f, 1f},
                new Color[]{COL_ACCENT_MAGENTA, new Color(0,0,0,0)});
        g2.setPaint(nebula2);
        g2.fillOval(0, 0, w, h);

        g2.setComposite(AlphaComposite.SrcOver); 

        for(SpaceObject so : objects) so.draw(g2);
    }

    public static Shape createStarShape(double centerX, double centerY, double outerRadius, double innerRadius) {
        Path2D path = new Path2D.Double();
        double deltaAngle = Math.PI / 5;
        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            double angle = (i * deltaAngle) - Math.PI / 2;
            double x = centerX + Math.cos(angle) * r;
            double y = centerY + Math.sin(angle) * r;
            if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
        }
        path.closePath();
        return path;
    }

    public static boolean isPrime(int n) {
        if(n<2) return false;
        for(int i=2; i<=Math.sqrt(n); i++) if(n%i==0) return false;
        return true;
    }
}