import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    Color bg;
    public RoundedPanel(Color bg) { this.bg=bg; setOpaque(false); }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),25,25);
        GradientPaint border = new GradientPaint(0,0, new Color(255,255,255,50), getWidth(), getHeight(), new Color(255,255,255,10));
        g2.setPaint(border);
        g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,25,25);
        super.paintComponent(g);
    }
}