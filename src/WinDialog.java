import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WinDialog extends JDialog {
    public WinDialog(JFrame parent, Player winner, Runnable onConfirm) {
        super(parent, true);
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        setSize(500, 350);
        setLocationRelativeTo(parent);

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, GalaxyRefined.COL_BG_DEEP_INDIGO, 0, getHeight(), GalaxyRefined.COL_BG_BLACKEST);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                
                g2.setStroke(new BasicStroke(3));
                g2.setColor(GalaxyRefined.COL_ACCENT_CYAN);
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 40, 40);
            }
        };
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel lblTitle = new JLabel("MISSION ACCOMPLISHED");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 28));
        lblTitle.setForeground(GalaxyRefined.COL_ACCENT_GOLD);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel(winner.name.toUpperCase());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblName.setForeground(winner.color); 
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSub = new JLabel("Conquered the Galaxy!");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        lblSub.setForeground(GalaxyRefined.COL_TEXT_LAVENDER);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblScore = new JLabel("Final XP: " + winner.currentScore);
        lblScore.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblScore.setForeground(Color.WHITE);
        lblScore.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton btnHome = new ModernButton("RETURN TO BASE");
        btnHome.addActionListener(e -> {
            dispose();
            onConfirm.run();
        });
        btnHome.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(lblTitle);
        content.add(Box.createVerticalStrut(20));
        content.add(lblName);
        content.add(lblSub);
        content.add(Box.createVerticalStrut(20));
        content.add(lblScore);
        content.add(Box.createVerticalStrut(40));
        content.add(btnHome);

        add(content);
    }
}