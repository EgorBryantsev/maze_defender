import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GamePanel extends JPanel {
    private final int tileSize = 25; // Adjust tile size as needed
    private Maze maze;
    public int panelWidth;
    public int panelHeight;
    public int calculatedTileSize;

    // Constructor
    public GamePanel() {
        this.setBackground(Color.DARK_GRAY);
        maze = new Maze(); // Initialize the maze

        // Add mouse listener for tower clicks
        Tower tower = new Tower(this);
        this.addMouseListener(tower.new BuildingClicked());
    }

    private void updateSize() {
        panelWidth = (int) (getWidth() * 0.9);  // 90% of window width
        panelHeight = (int) (getHeight() * 0.9);  // 90% of window height  
        calculatedTileSize = Math.min(panelWidth / Maze.COLS, panelHeight / Maze.ROWS);  // Adjust tile size
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateSize();

        int xOffset = (getWidth() - panelWidth) / 2;
        int yOffset = (getHeight() - panelHeight) / 2;

        for (int row = 0; row < Maze.ROWS; row++) {
            for (int col = 0; col < Maze.COLS; col++) {
                int cell = maze.getCell(row, col);
                switch (cell) {
                    case Maze.WALL:
                        g.setColor(Color.BLACK);
                        break;
                    case Maze.PATH:
                        g.setColor(Color.WHITE);
                        break;
                    case Maze.START:
                        g.setColor(Color.GREEN);
                        break;
                    case Maze.END:
                        g.setColor(Color.YELLOW);
                        break;
                    case Maze.BUILDING:
                        g.setColor(Color.GRAY);
                        break;
                    default:
                    switch (Maze.maze[row][col]) {
                        case 5:
                            g.setColor(Color.RED);
                            break;
                        case 6:
                            g.setColor(Color.ORANGE);
                            break;
                        case 7:
                            g.setColor(Color.CYAN);
                            break;
                        default:
                            break;
                    }
                    break;

                }
                g.fillRect(xOffset + col * calculatedTileSize, yOffset + row * calculatedTileSize, calculatedTileSize, calculatedTileSize);
                g.setColor(Color.LIGHT_GRAY);  // Grid lines
                g.drawRect(xOffset + col * calculatedTileSize, yOffset + row * calculatedTileSize, calculatedTileSize, calculatedTileSize);
            }
        }
    }
}
