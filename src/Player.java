import java.awt.Color;
import java.io.Serializable;

public class Player implements Serializable {
    public String name;
    public Color color;
    public int position = 1;
    public int currentScore = 0;
    public boolean hasPrimePower = false;
    public PlayerData data;

    public Player(String n, Color c, PlayerData d) {
        this.name = n;
        this.color = c;
        this.data = d;
    }
}