import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

// Node class representing a vertex in the graph
class Node {
    private int id;
    private double x, y;
    private static final int RADIUS = 25;
    private Color color = new Color(70, 130, 180);
    private int distance = Integer.MAX_VALUE;
    private Node parent = null;
    
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
    public void setColor(Color color) { this.color = color; }
    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }
    public Node getParent() { return parent; }
    public void setParent(Node parent) { this.parent = parent; }
    
    public void reset() {
        this.color = new Color(70, 130, 180);
        this.distance = Integer.MAX_VALUE;
        this.parent = null;
    }
    
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
    private boolean isInPath = false;
    
    public Edge(Node source, Node target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }
    
    public Node getSource() { return source; }
    public Node getTarget() { return target; }
    public int getWeight() { return weight; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public boolean isInPath() { return isInPath; }
    public void setInPath(boolean inPath) { this.isInPath = inPath; }
    
    public void reset() {
        this.color = new Color(100, 100, 100);
        this.isInPath = false;
    }
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
    
    public void dijkstra(Node startNode) {
        // Reset all nodes
        for (Node node : nodes) {
            node.reset();
        }
        for (Edge edge : edges) {
            edge.reset();
        }
        
        // Initialize
        startNode.setDistance(0);
        startNode.setColor(new Color(50, 205, 50)); // Green for start
        
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(Node::getDistance));
        Set<Node> visited = new HashSet<>();
        pq.add(startNode);
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            
            if (visited.contains(current)) continue;
            visited.add(current);
            
            // Color visited nodes (except start)
            if (current != startNode) {
                current.setColor(new Color(255, 215, 0)); // Gold for visited
            }
            
            // Check all edges from current node
            for (Edge edge : edges) {
                if (edge.getSource() == current) {
                    Node neighbor = edge.getTarget();
                    int newDist = current.getDistance() + edge.getWeight();
                    
                    if (newDist < neighbor.getDistance()) {
                        neighbor.setDistance(newDist);
                        neighbor.setParent(current);
                        pq.add(neighbor);
                    }
                }
            }
        }
        
        // Highlight shortest paths
        for (Node node : nodes) {
            if (node.getParent() != null) {
                for (Edge edge : edges) {
                    if (edge.getSource() == node.getParent() && edge.getTarget() == node) {
                        edge.setInPath(true);
                        edge.setColor(new Color(255, 69, 0)); // Red-orange for path
                    }
                }
            }
        }
    }
    
    public void reset() {
        for (Node node : nodes) {
            node.reset();
        }
        for (Edge edge : edges) {
            edge.reset();
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
    private Node startNode = null;
    
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
    
    public void setStartNode(Node node) {
        this.startNode = node;
        if (node != null) {
            graph.dijkstra(node);
        }
        repaint();
    }
    
    public void resetGraph() {
        graph.reset();
        startNode = null;
        repaint();
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
        
        // Draw distances
        if (startNode != null) {
            drawDistances(g2d);
        }
    }
    
    private void drawEdge(Graphics2D g2d, Edge edge) {
        Node source = edge.getSource();
        Node target = edge.getTarget();
        
        g2d.setColor(edge.getColor());
        g2d.setStroke(edge.isInPath() ? new BasicStroke(4) : new BasicStroke(2));
        
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
    
    private void drawDistances(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        int y = 20;
        g2d.drawString("Jarak Terpendek dari Node " + startNode.getId() + ":", 10, y);
        y += 20;
        
        for (Node node : graph.getNodes()) {
            String distText = "Node " + node.getId() + ": ";
            if (node.getDistance() == Integer.MAX_VALUE) {
                distText += "∞ (tidak terjangkau)";
            } else {
                distText += node.getDistance();
            }
            g2d.drawString(distText, 10, y);
            y += 18;
        }
    }
}

// Main application class
public class GraphVisualizer extends JFrame {
    private Graph graph;
    private GraphPanel panel;
    
    public GraphVisualizer(int[][] adjacencyMatrix) {
        setTitle("Graph Visualizer - Dijkstra Shortest Path");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        graph = new Graph(adjacencyMatrix);
        panel = new GraphPanel(graph);
        
        // Create control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        
        JLabel label = new JLabel("Pilih Start Node: ");
        controlPanel.add(label);
        
        // Create buttons for each node
        for (Node node : graph.getNodes()) {
            JButton button = new JButton("Node " + node.getId());
            button.addActionListener(e -> panel.setStartNode(node));
            controlPanel.add(button);
        }
        
        // Reset button
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> panel.resetGraph());
        controlPanel.add(resetButton);
        
        // Layout
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
}