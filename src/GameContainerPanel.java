import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GameContainerPanel extends JPanel {
    private GalaxyRefined mainFrame;
    private GamePanel gamePanel;
    private Sidebar sidebarPanel;

    public List<Player> activePlayers = new ArrayList<>();
    public int currentPlayerIndex = 0;
    public Map<Integer, Point> nodeCoordinates = new HashMap<>();
    public Map<Integer, Integer> warps = new HashMap<>();
    public Set<Integer> scoreNodes = new HashSet<>();
    private boolean isAnimating = false;

    public GameContainerPanel(GalaxyRefined frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        
        gamePanel = new GamePanel(this);
        sidebarPanel = new Sidebar(this);

        add(gamePanel, BorderLayout.CENTER);
        add(sidebarPanel, BorderLayout.EAST);
    }

    public void stopAnimation() { gamePanel.stopLoop(); }

    public void setupNewGame(int numPlayers, List<String> names) {
        activePlayers.clear();
        Color[] cols = {
            GalaxyRefined.COL_ACCENT_MAGENTA,
            GalaxyRefined.COL_ACCENT_CYAN,
            GalaxyRefined.COL_ACCENT_GOLD,
            new Color(0x39FF14) 
        };
        for(int i=0; i<numPlayers; i++) {
            String n = names.get(i);
            PlayerData data = mainFrame.globalPlayerData.computeIfAbsent(n, PlayerData::new);
            activePlayers.add(new Player(n, cols[i], data));
        }
        currentPlayerIndex = 0;
        generateBoard();

        sidebarPanel.reset();
        updateUIState();

        gamePanel.startLoop();
        isAnimating = false;
        sidebarPanel.setRollEnabled(true);
    }

    private void generateBoard() {
        nodeCoordinates.clear(); warps.clear(); scoreNodes.clear();
        int rows = 6, cols = 8;
        int xGap = (GalaxyRefined.GAME_WIDTH - 100)/cols, yGap = (GalaxyRefined.FRAME_HEIGHT - 120)/rows;
        int startX = 60, startY = GalaxyRefined.FRAME_HEIGHT - 100;

        for (int i=0; i<GalaxyRefined.MAX_NODES; i++) {
            int r = i/cols, c = i%cols;
            int x = (r%2==0) ? startX + c*xGap : startX + (cols-1-c)*xGap;
            int y = startY - r*yGap;
            x += (Math.random()*20)-10; y += (Math.random()*20)-10;
            nodeCoordinates.put(i+1, new Point(x,y));
        }

        Random rand = new Random();
        int count=0;
        while(count<5) {
            int s = rand.nextInt(GalaxyRefined.MAX_NODES-10)+2;
            int e = s + rand.nextInt(15)+5;
            if(e<=GalaxyRefined.MAX_NODES && !warps.containsKey(s) && !warps.containsValue(s)) {
                warps.put(s, e); count++;
            }
        }
        for(int i=0; i<12; i++) scoreNodes.add(rand.nextInt(GalaxyRefined.MAX_NODES-2)+2);
    }

    public void executeTurn() {
        if(isAnimating) return;
        setAnimating(true);
        Player p = activePlayers.get(currentPlayerIndex);
        sidebarPanel.log("Turn: " + p.name.toUpperCase());

        sidebarPanel.dicePanel.roll(() -> {
            int percentage = (int)(Math.random() * 100) + 1;
            int steps = (int)(Math.random() * 6) + 1;
            int move = (percentage <= 80) ? steps : -steps;
            String status = (move > 0) ? "THRUSTERS ON" : "GRAVITY PULL";

            MidiSynthSystem.playSFX("roll");

            if (move > 0) {
                sidebarPanel.dicePanel.setDiceColor(new Color(0x39FF14)); 
            } else {
                sidebarPanel.dicePanel.setDiceColor(new Color(0xFF2A2A)); 
            }

            sidebarPanel.log("Rolled: " + Math.abs(move) + " (" + status + ")");
            sidebarPanel.dicePanel.setNumber(Math.abs(move));
            animateMove(p, move);
        });
    }

    private void animateMove(Player p, int steps) {
        int dir = Integer.signum(steps);
        int total = Math.abs(steps);
        final int[] taken = {0};

        javax.swing.Timer t = new javax.swing.Timer(300, null);
        t.addActionListener(e -> {
            if(taken[0] < total) {
                int next = p.position + dir;
                if(next < 1) next = 1;
                if(next > GalaxyRefined.MAX_NODES) next = GalaxyRefined.MAX_NODES;
                p.position = next;
                taken[0]++;
                
                MidiSynthSystem.playSFX("move");
                
                gamePanel.repaint();

                if(p.position==GalaxyRefined.MAX_NODES || (p.position==1 && dir<0)) {
                    t.stop(); finishTurnLogic(p);
                }
            } else {
                t.stop(); finishTurnLogic(p);
            }
        });
        t.start();
    }

    private void finishTurnLogic(Player p) {
        boolean extraTurn = false;
        Point loc = nodeCoordinates.get(p.position);

        if(loc != null && scoreNodes.contains(p.position)) {
            p.currentScore+=15;
            sidebarPanel.log("Data Cache Found: +15 XP");
            gamePanel.explode(loc.x, loc.y, Color.GREEN);
            MidiSynthSystem.playSFX("score");
        }

        if(p.position%5==0 && p.position!=GalaxyRefined.MAX_NODES && p.position!=0) {
            extraTurn=true;
            sidebarPanel.log("Starlight Boost: EXTRA TURN!");
            if(loc != null) gamePanel.explode(loc.x, loc.y, GalaxyRefined.COL_ACCENT_GOLD);
        }

        if(warps.containsKey(p.position)) {
            if(p.hasPrimePower) {
                int dest = warps.get(p.position);
                Point destLoc = nodeCoordinates.get(dest);

                if(loc != null && destLoc != null) {
                    sidebarPanel.log("WARP GATE OPENING...");
                    boolean finalExtraTurn = extraTurn;

                    gamePanel.launchMeteor(loc, destLoc, () -> {
                        MidiSynthSystem.playSFX("warp");
                        
                        p.position = dest;
                        sidebarPanel.log(">>> WARP SUCCESS TO " + dest);
                        gamePanel.explode(destLoc.x, destLoc.y, GalaxyRefined.COL_ACCENT_CYAN);
                        finalizeTurn(p, finalExtraTurn);
                    });
                    return;
                }
            } else {
                sidebarPanel.log("Warp Gate Locked (Need Prime Charge)");
            }
        }

        finalizeTurn(p, extraTurn);
    }

    private void finalizeTurn(Player p, boolean extraTurn) {
        p.hasPrimePower = GalaxyRefined.isPrime(p.position);
        if(p.position == GalaxyRefined.MAX_NODES) { win(p); return; }
        if(!extraTurn) currentPlayerIndex = (currentPlayerIndex+1)%activePlayers.size();
        setAnimating(false);
        updateUIState();
    }

    private void win(Player p) {
        gamePanel.stopLoop(); 
        mainFrame.updateGlobalData(p, true);
        for(Player other : activePlayers) {
            if(other != p) mainFrame.updateGlobalData(other, false);
        }
        
        MidiSynthSystem.stopBGM();
        MidiSynthSystem.playSFX("win");

        SwingUtilities.invokeLater(() -> {
            new WinDialog(mainFrame, p, () -> mainFrame.showIntro()).setVisible(true);
        });
    }

    private void setAnimating(boolean b) {
        isAnimating=b;
        sidebarPanel.setRollEnabled(!b);
    }

    private void updateUIState() {
        if(!activePlayers.isEmpty()) {
            sidebarPanel.updateInfo(activePlayers.get(currentPlayerIndex));
            sidebarPanel.updateLeaderboard(activePlayers);
        }
    }
}