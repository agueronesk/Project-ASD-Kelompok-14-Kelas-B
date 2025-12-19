import java.awt.Graphics2D;

public abstract class SpaceObject {
    double x, y;
    public abstract void update();
    public abstract void draw(Graphics2D g2);
}