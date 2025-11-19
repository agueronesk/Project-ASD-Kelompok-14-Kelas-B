import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Node class representing a vertex in the graph
class Node {
    private int id;
    private double x, y;
    private static final int RADIUS = 25;
    private Color color = new Color(70, 130, 180);
    
    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
    
    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public int getRadius() { return RADIUS; }
    public Color getColor() { return color; }
    
    public boolean contains(Point point) {
        double dx = point.x - x;
        double dy = point.y - y;
        return dx * dx + dy * dy <= RADIUS * RADIUS;
    }
}

// Edge class representing a connection between nodes
class Edge {
    private Node source;
    private Node target;
    private int weight;
    private Color color = new Color(100, 100, 100);
    
    public Edge(Node source, Node target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }
    
    public Node getSource() { return source; }
    public Node getTarget() { return target; }
    public int getWeight() { return weight; }
    public Color getColor() { return color; }
}

// Graph class managing nodes and edges
class Graph {
    private List<Node> nodes;
    private List<Edge> edges;
    private int[][] adjacencyMatrix;
    
    public Graph(int[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        buildGraph();
    }
    
    private void buildGraph() {
        int n = adjacencyMatrix.length;
        
        // Create nodes in circular layout
        double centerX = 400;
        double centerY = 300;
        double radius = 200;
        
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            nodes.add(new Node(i, x, y));
        }
        
        // Create edges from adjacency matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    edges.add(new Edge(nodes.get(i), nodes.get(j), adjacencyMatrix[i][j]));
                }
            }
        }
    }
    
    public List<Node> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }
}

// Canvas panel for drawing the graph
class GraphPanel extends JPanel {
    private Graph graph;
    private Node selectedNode = null;
    private Point mouseOffset;
    
    public GraphPanel(Graph graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        
        // Mouse listener for dragging nodes
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                for (Node node : graph.getNodes()) {
                    if (node.contains(e.getPoint())) {
                        selectedNode = node;
                        mouseOffset = new Point(
                            (int)(e.getX() - node.getX()),
                            (int)(e.getY() - node.getY())
                        );
                        break;
                    }
                }
            }
            
            public void mouseReleased(java.awt.event.MouseEvent e) {
                selectedNode = null;
            }
        });
        
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (selectedNode != null) {
                    selectedNode.setX(e.getX() - mouseOffset.x);
                    selectedNode.setY(e.getY() - mouseOffset.y);
                    repaint();
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw edges
        for (Edge edge : graph.getEdges()) {
            drawEdge(g2d, edge);
        }
        
        // Draw nodes
        for (Node node : graph.getNodes()) {
            drawNode(g2d, node);
        }
    }
    
    private void drawEdge(Graphics2D g2d, Edge edge) {
        Node source = edge.getSource();
        Node target = edge.getTarget();
        
        g2d.setColor(edge.getColor());
        g2d.setStroke(new BasicStroke(2));
        
        // Draw line
        g2d.drawLine((int)source.getX(), (int)source.getY(), 
                     (int)target.getX(), (int)target.getY());
        
        // Draw arrow
        drawArrow(g2d, source.getX(), source.getY(), target.getX(), target.getY());
        
        // Draw weight
        int midX = (int)((source.getX() + target.getX()) / 2);
        int midY = (int)((source.getY() + target.getY()) / 2);
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(String.valueOf(edge.getWeight()), midX + 5, midY - 5);
    }
    
    private void drawArrow(Graphics2D g2d, double x1, double y1, double x2, double y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 10;
        
        // Calculate arrow tip position (at edge of target node)
        double tipX = x2 - 25 * Math.cos(angle);
        double tipY = y2 - 25 * Math.sin(angle);
        
        // Arrow wings
        double angle1 = angle + Math.PI * 3 / 4;
        double angle2 = angle - Math.PI * 3 / 4;
        
        int[] xPoints = {
            (int)tipX,
            (int)(tipX + arrowSize * Math.cos(angle1)),
            (int)(tipX + arrowSize * Math.cos(angle2))
        };
        int[] yPoints = {
            (int)tipY,
            (int)(tipY + arrowSize * Math.sin(angle1)),
            (int)(tipY + arrowSize * Math.sin(angle2))
        };
        
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void drawNode(Graphics2D g2d, Node node) {
        int x = (int)node.getX();
        int y = (int)node.getY();
        int r = node.getRadius();
        
        // Draw circle
        g2d.setColor(node.getColor());
        g2d.fillOval(x - r, y - r, 2 * r, 2 * r);
        
        // Draw border
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - r, y - r, 2 * r, 2 * r);
        
        // Draw label
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String label = String.valueOf(node.getId());
        FontMetrics fm = g2d.getFontMetrics();
        int labelX = x - fm.stringWidth(label) / 2;
        int labelY = y + fm.getAscent() / 2 - 2;
        g2d.drawString(label, labelX, labelY);
    }
}

// Main application class
public class GraphVisualizer extends JFrame {
    
    public GraphVisualizer(int[][] adjacencyMatrix) {
        setTitle("Graph Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Graph graph = new Graph(adjacencyMatrix);
        GraphPanel panel = new GraphPanel(graph);
        
        add(panel);
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        // Example adjacency matrix (directed weighted graph)
        int[][] adjacencyMatrix = {
            {0, 5, 3, 0, 0},
            {0, 0, 2, 6, 0},
            {0, 0, 0, 7, 4},
            {0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0}
        };
        
        SwingUtilities.invokeLater(() -> {
            GraphVisualizer visualizer = new GraphVisualizer(adjacencyMatrix);
            visualizer.setVisible(true);
        });
    }
}