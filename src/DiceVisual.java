import javax.swing.*;
import java.awt.*;

public class DiceVisual extends JPanel {
    private int number = 1;
    private Color diceColor = new Color(255, 255, 255, 250); 

    public DiceVisual() { 
        setPreferredSize(new Dimension(80, 80)); 
        setOpaque(false); 
        setAlignmentX(Component.CENTER_ALIGNMENT); 
    }

    public void setDiceColor(Color c) {
        this.diceColor = c;
        repaint();
    }

    public void setNumber(int n) { 
        this.number = n; 
        repaint(); 
    }

    public void roll(Runnable callback) {
        setDiceColor(new Color(255, 255, 255, 250));
        javax.swing.Timer t = new javax.swing.Timer(50, null);
        final int[] tick = {0};
        t.addActionListener(e -> {
            number = (int)(Math.random()*6)+1; 
            repaint();
            if(tick[0]++ > 15) { 
                t.stop(); 
                callback.run(); 
            }
        });
        t.start();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        GradientPaint diceGrad = new GradientPaint(
            0, 0, Color.WHITE, 
            getWidth(), getHeight(), diceColor
        );
        
        g2.setPaint(diceGrad);
        g2.fillRoundRect(10, 10, 60, 60, 15, 15);
        
        g2.setColor(Color.BLACK); 
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g2.getFontMetrics();
        String s = String.valueOf(number);
        g2.drawString(s, 40 - fm.stringWidth(s)/2, 40 + fm.getAscent()/2 - 5);
    }
}