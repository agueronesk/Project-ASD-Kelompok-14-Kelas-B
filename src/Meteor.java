import java.awt.*;
import java.awt.geom.Point2D;

public class Meteor {
    double x, y, tx, ty; 
    Runnable onFinish; 
    public boolean finished = false;
    
    public Meteor(Point start, Point end, Runnable cb) {
        this.x = start.x; this.y = start.y; this.tx = end.x; this.ty = end.y; this.onFinish = cb;
    }
    
    public void update() {
        double dx = tx - x; double dy = ty - y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        if(dist < 10) finished = true;
        x += dx * 0.12; y += dy * 0.12;
    }
    
    public void draw(Graphics2D g2) {
        RadialGradientPaint head = new RadialGradientPaint(new Point2D.Double(x,y), 8, new float[]{0f, 1f}, new Color[]{Color.WHITE, GalaxyRefined.COL_ACCENT_CYAN});
        g2.setPaint(head);
        g2.fillOval((int)x-5, (int)y-5, 10, 10);

        GradientPaint tail = new GradientPaint((float)x, (float)y, new Color(GalaxyRefined.COL_ACCENT_CYAN.getRed(), GalaxyRefined.COL_ACCENT_CYAN.getGreen(), GalaxyRefined.COL_ACCENT_CYAN.getBlue(), 180),
                (float)(x-(tx-x)*0.3), (float)(y-(ty-y)*0.3), new Color(0,0,0,0));
        g2.setPaint(tail);
        g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine((int)x, (int)y, (int)(x-(tx-x)*0.3), (int)(y-(ty-y)*0.3));
    }
}