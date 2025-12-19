import javax.swing.SwingUtilities;

public class Main {
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