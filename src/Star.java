import java.awt.Color;
import java.awt.Graphics2D;

public class Star extends SpaceObject {
    double speed; int size; float alpha;
    
    public Star() { reset(true); }
    
    void reset(boolean randomY) {
        x = Math.random() * GalaxyRefined.FRAME_WIDTH;
        y = randomY ? Math.random() * GalaxyRefined.FRAME_HEIGHT : 0;
        speed = Math.random() * 0.3 + 0.05; 
        size = (Math.random() > 0.9) ? 2 : 1;
        alpha = (float)Math.random();
    }
    
    public void update() {
        y += speed;
        if(y > GalaxyRefined.FRAME_HEIGHT) reset(false);
        alpha += (Math.random()*0.05 - 0.025);
        if(alpha<0.1) alpha=0.1f; if(alpha>0.8) alpha=0.8f;
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(1f, 1f, 1f, alpha));
        g2.fillOval((int)x, (int)y, size, size);
    }
}