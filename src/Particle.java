import java.awt.Color;
import java.awt.Graphics2D;

public class Particle {
    double x,y,vx,vy,a=255; 
    Color c; 
    public boolean alive=true;
    
    public Particle(int x, int y, Color c){
        this.x=x;this.y=y;this.c=c;
        vx=Math.random()*5-2.5;
        vy=Math.random()*5-2.5;
    }
    
    public void update(){
        x+=vx;y+=vy;a-=8; 
        if(a<0){a=0;alive=false;}
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)a));
        g2.fillOval((int)x, (int)y, 4, 4);
    }
}