import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel {
    private GameContainerPanel parent;
    private CopyOnWriteArrayList<SpaceObject> bgObjects = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();
    private Meteor activeMeteor = null;
    private javax.swing.Timer loop;

    GamePanel(GameContainerPanel p) {
        this.parent = p;
        for(int i=0; i<5; i++) bgObjects.add(new Planet(false));
        for(int i=0; i<100; i++) bgObjects.add(new Star());
    }

    void startLoop() {
        stopLoop();
        loop = new javax.swing.Timer(16, e -> {
            if(!isShowing()) return;

            for(SpaceObject so : bgObjects) so.update();
            for(Particle p : particles) { p.update(); if(!p.alive) particles.remove(p); }

            if(activeMeteor != null) {
                activeMeteor.update();
                if(activeMeteor.finished) {
                    Runnable cb = activeMeteor.onFinish;
                    activeMeteor = null;
                    if(cb != null) cb.run();
                }
            }
            repaint();
        });
        loop.start();
    }

    void stopLoop() {
        if(loop!=null && loop.isRunning()) loop.stop();
    }

    void explode(int x, int y, Color c) {
        for(int i=0; i<25; i++) particles.add(new Particle(x, y, c));
    }

    void launchMeteor(Point start, Point end, Runnable onFinish) {
        activeMeteor = new Meteor(start, end, onFinish);
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GalaxyRefined.paintCosmicBackground(g, getWidth(), getHeight(), bgObjects);

        if(parent.nodeCoordinates == null || parent.nodeCoordinates.isEmpty()) return;

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(1.2f));
        g2.setColor(new Color(GalaxyRefined.COL_ACCENT_ELECTRO.getRed(), GalaxyRefined.COL_ACCENT_ELECTRO.getGreen(), GalaxyRefined.COL_ACCENT_ELECTRO.getBlue(), 60));
        for(int i=1; i<GalaxyRefined.MAX_NODES; i++) {
            Point p1 = parent.nodeCoordinates.get(i);
            Point p2 = parent.nodeCoordinates.get(i+1);
            if(p1!=null && p2!=null) g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5,5}, 0));
        if(parent.warps != null) {
            parent.warps.forEach((s,e) -> {
                Point p1 = parent.nodeCoordinates.get(s);
                Point p2 = parent.nodeCoordinates.get(e);
                if(p1!=null && p2!=null) {
                    g2.setColor(new Color(GalaxyRefined.COL_ACCENT_CYAN.getRed(), GalaxyRefined.COL_ACCENT_CYAN.getGreen(), GalaxyRefined.COL_ACCENT_CYAN.getBlue(), 120));
                    QuadCurve2D q = new QuadCurve2D.Float(p1.x, p1.y, (p1.x+p2.x)/2 + 50, (p1.y+p2.y)/2, p2.x, p2.y);
                    g2.draw(q);
                }
            });
        }

        for(int i=1; i<=GalaxyRefined.MAX_NODES; i++) {
            Point p = parent.nodeCoordinates.get(i);
            if(p != null) drawNode(g2, p.x, p.y, i);
        }

        if(parent.activePlayers != null) {
            for(int i=0; i<parent.activePlayers.size(); i++) {
                Player pl = parent.activePlayers.get(i);
                if(pl.position<=0) continue;
                Point p = parent.nodeCoordinates.get(pl.position);
                if(p != null) {
                    int off = (i%2==0?-8:8);
                    RadialGradientPaint playerGlow = new RadialGradientPaint(new Point2D.Double(p.x+off, p.y+(i*3)), 15, new float[]{0f, 1f}, new Color[]{pl.color, new Color(0,0,0,0)});
                    g2.setPaint(playerGlow);
                    g2.fillOval(p.x+off-15, p.y+(i*3)-15, 30, 30);

                    g2.setColor(pl.color);
                    g2.fillOval(p.x+off-6, p.y+(i*3)-6, 12, 12);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(p.x+off-6, p.y+(i*3)-6, 12, 12);
                    if(pl.hasPrimePower) {
                        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
                        g2.drawString("⚡", p.x+off+5, p.y-10);
                    }
                }
            }
        }

        if(activeMeteor != null) activeMeteor.draw(g2);
        for(Particle p : particles) p.draw(g2);
    }

    private void drawNode(Graphics2D g2, int x, int y, int index) {
        Color c = GalaxyRefined.COL_ACCENT_ELECTRO; 
        Shape shape;

        if (index == GalaxyRefined.MAX_NODES) { // Finish
            c = GalaxyRefined.COL_ACCENT_GOLD;
            shape = new Ellipse2D.Double(x-25, y-25, 50, 50);
        }
        else if (index % 5 == 0) { // Star
            c = GalaxyRefined.COL_ACCENT_GOLD;
            shape = GalaxyRefined.createStarShape(x, y, 14, 7);
        }
        else if (GalaxyRefined.isPrime(index)) { // Diamond
            c = GalaxyRefined.COL_ACCENT_MAGENTA;
            Path2D d = new Path2D.Double();
            d.moveTo(x, y-14); d.lineTo(x+12, y); d.lineTo(x, y+14); d.lineTo(x-12, y); d.closePath();
            shape = d;
        }
        else if (parent.scoreNodes.contains(index)) { // Score
            c = new Color(0x39FF14); 
            shape = new Rectangle2D.Double(x-10, y-10, 20, 20);
        }
        else { // Normal
            shape = new Ellipse2D.Double(x-8, y-8, 16, 16);
        }

        RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Double(x, y), (index == GalaxyRefined.MAX_NODES ? 40 : 25),
                new float[]{0.0f, 1.0f},
                new Color[]{new Color(c.getRed(), c.getGreen(), c.getBlue(), 150), new Color(0,0,0,0)}
        );
        g2.setPaint(glow);
        if (index == GalaxyRefined.MAX_NODES) g2.fillOval(x-40, y-40, 80, 80);
        else g2.fillOval(x-25, y-25, 50, 50);

        g2.setColor(c);
        g2.fill(shape);

        g2.setColor(new Color(255,255,255,200));
        if(!(shape instanceof Ellipse2D)) { g2.setStroke(new BasicStroke(1.5f)); g2.draw(shape); }

        g2.setColor(GalaxyRefined.COL_TEXT_WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        String s = ""+index;
        g2.drawString(s, x - fm.stringWidth(s)/2, y + fm.getAscent()/2 - 2);
    }
}