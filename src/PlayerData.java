import java.io.Serializable;

public class PlayerData implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    public int totalScore;
    public int wins;

    public PlayerData(String n) {
        this.name = n;
    }
}