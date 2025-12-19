import java.awt.*;
import java.awt.geom.Point2D;

public class Planet extends SpaceObject {
    Color c1, c2; int r; boolean hasRing;
    
    public Planet(boolean big) {
        x = Math.random() * GalaxyRefined.FRAME_WIDTH;
        y = Math.random() * GalaxyRefined.FRAME_HEIGHT;
        r = big ? (int)(Math.random()*50)+30 : (int)(Math.random()*20)+5;
        c1 = (Math.random()>0.5) ? GalaxyRefined.COL_ACCENT_ELECTRO : GalaxyRefined.COL_ACCENT_MAGENTA;
        c2 = GalaxyRefined.COL_BG_DEEP_INDIGO;
        hasRing = Math.random() > 0.7;
    }
    
    public void update() { x -= 0.05; if(x < -100) x = GalaxyRefined.FRAME_WIDTH + 100; }
    
    public void draw(Graphics2D g2) {
        RadialGradientPaint planetGrad = new RadialGradientPaint(
                new Point2D.Double(x-r/2, y-r/2), r*1.5f,
                new float[]{0.0f, 1.0f},
                new Color[]{c1, c2}
        );
        g2.setPaint(planetGrad);
        g2.fillOval((int)x-r, (int)y-r, r*2, r*2);
        if(hasRing) {
            g2.setColor(new Color(255,255,255,30));
            g2.setStroke(new BasicStroke(r/5f));
            g2.drawOval((int)x-r-15, (int)y-r/2, r*2+30, r);
        }
    }
}