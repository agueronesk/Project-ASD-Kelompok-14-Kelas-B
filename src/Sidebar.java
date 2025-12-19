import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Sidebar extends JPanel {
    private GameContainerPanel parent;
    private JLabel lblTurnName, lblTurnScore, lblStatus;
    public DiceVisual dicePanel;
    private ModernButton btnRoll;
    private JTextArea txtLog;
    private JPanel leaderboardListPanel;

    public Sidebar(GameContainerPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(320, GalaxyRefined.FRAME_HEIGHT));
        setOpaque(false); 
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JPanel infoCard = new RoundedPanel(GalaxyRefined.COL_GLASS_PANEL);
        infoCard.setLayout(new GridLayout(3,1));
        infoCard.setMaximumSize(new Dimension(300, 100));
        
        lblTurnName = new JLabel("PLAYER");
        lblTurnName.setFont(new Font("Segoe UI", Font.BOLD, 22)); 
        lblTurnName.setForeground(GalaxyRefined.COL_TEXT_WHITE); 
        lblTurnName.setHorizontalAlignment(0);
        
        lblTurnScore = new JLabel("0 XP");
        lblTurnScore.setFont(new Font("Segoe UI", Font.PLAIN, 16)); 
        lblTurnScore.setForeground(GalaxyRefined.COL_ACCENT_CYAN); 
        lblTurnScore.setHorizontalAlignment(0);
        
        lblStatus = new JLabel("NORMAL");
        lblStatus.setFont(new Font("Monospaced", Font.BOLD, 14)); 
        lblStatus.setForeground(GalaxyRefined.COL_TEXT_LAVENDER); 
        lblStatus.setHorizontalAlignment(0);
        
        infoCard.add(lblTurnName); infoCard.add(lblTurnScore); infoCard.add(lblStatus);

        JPanel controlCard = new RoundedPanel(GalaxyRefined.COL_GLASS_PANEL);
        controlCard.setLayout(new BoxLayout(controlCard, BoxLayout.Y_AXIS));
        dicePanel = new DiceVisual();
        btnRoll = new ModernButton("ENGAGE JUMP");
        btnRoll.addActionListener(e -> parent.executeTurn());
        controlCard.add(Box.createVerticalStrut(10));
        controlCard.add(dicePanel);
        controlCard.add(Box.createVerticalStrut(15));
        controlCard.add(btnRoll);
        controlCard.add(Box.createVerticalStrut(10));

        JPanel leaderboardCard = new RoundedPanel(GalaxyRefined.COL_GLASS_PANEL);
        leaderboardCard.setLayout(new BorderLayout());
        leaderboardCard.setMaximumSize(new Dimension(300, 200));

        JLabel lbTitle = new JLabel("FLEET RANKING");
        lbTitle.setForeground(GalaxyRefined.COL_ACCENT_GOLD);
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lbTitle.setBorder(new EmptyBorder(5,0,5,0));

        leaderboardListPanel = new JPanel();
        leaderboardListPanel.setLayout(new BoxLayout(leaderboardListPanel, BoxLayout.Y_AXIS));
        leaderboardListPanel.setOpaque(false);

        leaderboardCard.add(lbTitle, BorderLayout.NORTH);
        leaderboardCard.add(leaderboardListPanel, BorderLayout.CENTER);

        RoundedPanel logCard = new RoundedPanel(GalaxyRefined.COL_GLASS_PANEL);
        logCard.setLayout(new BorderLayout());
        txtLog = new JTextArea(10, 20); 
        txtLog.setEditable(false); 
        txtLog.setOpaque(false); 
        txtLog.setForeground(new Color(0xADF8FF)); 
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        logCard.add(new JLabel(" MISSION LOG") {{setForeground(GalaxyRefined.COL_TEXT_LAVENDER);}}, BorderLayout.NORTH);
        logCard.add(new JScrollPane(txtLog) {{setOpaque(false); getViewport().setOpaque(false); setBorder(null);}}, BorderLayout.CENTER);

        content.add(infoCard); content.add(Box.createVerticalStrut(10));
        content.add(controlCard); content.add(Box.createVerticalStrut(10));
        content.add(leaderboardCard); content.add(Box.createVerticalStrut(10));
        content.add(logCard);

        add(content, BorderLayout.CENTER);
    }

    public void reset() { txtLog.setText(""); }
    public void log(String s) { txtLog.append("> " + s + "\n"); txtLog.setCaretPosition(txtLog.getDocument().getLength()); }
    public void setRollEnabled(boolean b) { btnRoll.setEnabled(b); }

    public void updateInfo(Player p) {
        lblTurnName.setText(p.name); lblTurnName.setForeground(p.color);
        lblTurnScore.setText("XP: " + p.currentScore);
        lblStatus.setText(p.hasPrimePower ? "⚡ PRIME CHARGED" : "SYSTEM NORMAL");
        lblStatus.setForeground(p.hasPrimePower ? GalaxyRefined.COL_ACCENT_GOLD : GalaxyRefined.COL_TEXT_LAVENDER);
    }

    public void updateLeaderboard(List<Player> allPlayers) {
        leaderboardListPanel.removeAll();
        List<Player> sorted = new ArrayList<>(allPlayers);
        sorted.sort((p1, p2) -> {
            if(p2.position != p1.position) return p2.position - p1.position;
            return p2.currentScore - p1.currentScore;
        });

        for(int i=0; i<sorted.size(); i++) {
            Player p = sorted.get(i);
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(2, 5, 2, 5));
            
            JLabel nameLbl = new JLabel((i+1) + ". " + p.name);
            nameLbl.setForeground(p.color);
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            JLabel posLbl = new JLabel("Node " + p.position + " (" + p.currentScore + "xp)");
            posLbl.setForeground(GalaxyRefined.COL_TEXT_LAVENDER);
            posLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            row.add(nameLbl, BorderLayout.WEST);
            row.add(posLbl, BorderLayout.EAST);
            leaderboardListPanel.add(row);
        }
        leaderboardListPanel.revalidate();
        leaderboardListPanel.repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(10,5,20, 240), 0, getHeight(), new Color(20, 10, 40, 240));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}