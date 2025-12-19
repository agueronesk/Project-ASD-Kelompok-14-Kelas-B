import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class ModernButton extends JButton {
    public ModernButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setForeground(GalaxyRefined.COL_TEXT_WHITE);
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { setForeground(GalaxyRefined.COL_ACCENT_CYAN); }
            public void mouseExited(MouseEvent e) { setForeground(GalaxyRefined.COL_TEXT_WHITE); }
        });
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c1 = GalaxyRefined.COL_ACCENT_ELECTRO;
        Color c2 = GalaxyRefined.COL_ACCENT_MAGENTA;
        if(getModel().isPressed()) { c1 = c1.darker(); c2 = c2.darker(); }

        GradientPaint btnGrad = new GradientPaint(0,0, c1, getWidth(), getHeight(), c2);
        g2.setPaint(btnGrad);
        g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
        super.paintComponent(g);
    }
}