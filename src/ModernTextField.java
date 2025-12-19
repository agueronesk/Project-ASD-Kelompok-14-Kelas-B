import javax.swing.*;
import java.awt.*;

public class ModernTextField extends JTextField {
    public ModernTextField(int c) { 
        super(c); 
        setOpaque(false); 
        setForeground(GalaxyRefined.COL_TEXT_WHITE); 
        setCaretColor(GalaxyRefined.COL_TEXT_WHITE); 
        setBorder(BorderFactory.createMatteBorder(0,0,2,0,new Color(255,255,255,100))); 
    }
    @Override protected void paintComponent(Graphics g) { super.paintComponent(g); }
}
