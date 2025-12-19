import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IntroPanel extends JPanel {
    private GalaxyRefined mainFrame;
    private CopyOnWriteArrayList<SpaceObject> spaceObjects = new CopyOnWriteArrayList<>();
    private JPanel nameInputPanel;
    private List<ModernTextField> nameFields = new ArrayList<>();
    private int selectedPlayers = 2;
    private JTextArea rankArea;
    private javax.swing.Timer animTimer;

    public IntroPanel(GalaxyRefined frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        for(int i=0; i<3; i++) spaceObjects.add(new Planet(true));
        for(int i=0; i<60; i++) spaceObjects.add(new Star());

        animTimer = new javax.swing.Timer(30, e -> {
            if(!isShowing()) return;
            for(SpaceObject so : spaceObjects) so.update();
            repaint();
        });
        animTimer.start();

        initUI();
    }

    public void startAnimation() { if(!animTimer.isRunning()) animTimer.start(); }
    public void stopAnimation() { if(animTimer.isRunning()) animTimer.stop(); }

    private void initUI() {
        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setOpaque(false);
        centerBox.setBorder(new EmptyBorder(50, 0, 0, 0));

        JLabel title = new JLabel("GALAXY EXPLORER");
        title.setFont(new Font("Verdana", Font.BOLD, 55));
        title.setForeground(GalaxyRefined.COL_ACCENT_CYAN); 
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("ELECTRO NEBULA");
        sub.setFont(new Font("Verdana", Font.ITALIC, 22));
        sub.setForeground(GalaxyRefined.COL_ACCENT_ELECTRO); 
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel controlPanel = new RoundedPanel(GalaxyRefined.COL_GLASS_PANEL);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setMaximumSize(new Dimension(400, 450));
        controlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] opts = {"2 Explorers", "3 Explorers", "4 Explorers"};
        JComboBox<String> combo = new JComboBox<>(opts);
        combo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        combo.addActionListener(e -> { selectedPlayers = combo.getSelectedIndex()+2; updateInputs(controlPanel); });

        JPanel pCombo = new JPanel(); pCombo.setOpaque(false);
        pCombo.add(new JLabel("Mission Crew: ") {{ setForeground(GalaxyRefined.COL_TEXT_LAVENDER); }});
        pCombo.add(combo);

        nameInputPanel = new JPanel();
        nameInputPanel.setLayout(new BoxLayout(nameInputPanel, BoxLayout.Y_AXIS));
        nameInputPanel.setOpaque(false);

        ModernButton startBtn = new ModernButton("LAUNCH MISSION");
        startBtn.addActionListener(e -> {
            List<String> n = new ArrayList<>();
            for(int i=0; i<selectedPlayers; i++) {
                String txt = nameFields.get(i).getText().trim();
                n.add(txt.isEmpty() ? "Cadet " + (i+1) : txt);
            }
            mainFrame.startGame(selectedPlayers, n);
        });

        controlPanel.add(pCombo);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(nameInputPanel);
        controlPanel.add(Box.createVerticalStrut(20));
        controlPanel.add(startBtn);

        updateInputs(controlPanel);

        JPanel rankPanel = new RoundedPanel(GalaxyRefined.COL_GLASS_PANEL);
        rankPanel.setPreferredSize(new Dimension(300, 200));
        rankPanel.setLayout(new BorderLayout());
        rankPanel.setBorder(new EmptyBorder(10,10,10,10));

        JLabel rankTitle = new JLabel("HALL OF FAME");
        rankTitle.setForeground(GalaxyRefined.COL_ACCENT_GOLD);
        rankTitle.setHorizontalAlignment(SwingConstants.CENTER);
        rankTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rankPanel.add(rankTitle, BorderLayout.NORTH);

        rankArea = new JTextArea();
        rankArea.setOpaque(false);
        rankArea.setForeground(GalaxyRefined.COL_TEXT_LAVENDER);
        rankArea.setEditable(false);
        rankArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        rankPanel.add(new JScrollPane(rankArea) {{setOpaque(false); getViewport().setOpaque(false); setBorder(null);}}, BorderLayout.CENTER);

        centerBox.add(title);
        centerBox.add(sub);
        centerBox.add(Box.createVerticalStrut(40));
        centerBox.add(controlPanel);

        add(centerBox, BorderLayout.CENTER);
        add(rankPanel, BorderLayout.EAST);
    }

    private void updateInputs(JPanel parent) {
        nameInputPanel.removeAll();
        nameFields.clear();
        for(int i=0; i<selectedPlayers; i++) {
            JPanel p = new JPanel(); p.setOpaque(false);
            ModernTextField tf = new ModernTextField(15);
            nameFields.add(tf);
            p.add(new JLabel("Pilot "+(i+1)+": ") {{ setForeground(GalaxyRefined.COL_TEXT_LAVENDER); }});
            p.add(tf);
            nameInputPanel.add(p);
        }
        parent.revalidate(); parent.repaint();
    }

    public void refreshLeaderboard() {
        StringBuilder sb = new StringBuilder();
        List<PlayerData> list = new ArrayList<>(mainFrame.globalPlayerData.values());
        list.sort((a,b) -> b.totalScore - a.totalScore);

        for(int i=0; i<list.size(); i++) {
            PlayerData d = list.get(i);
            sb.append(String.format("%d. %-10s [W:%d] %d pts\n", (i+1), d.name, d.wins, d.totalScore));
            if(i>=15) break;
        }
        if(list.isEmpty()) sb.append("No records yet.\nBe the first!");
        rankArea.setText(sb.toString());
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GalaxyRefined.paintCosmicBackground(g, getWidth(), getHeight(), spaceObjects);
    }
}