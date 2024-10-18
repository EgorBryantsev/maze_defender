// GamePanel.java
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements MouseListener {
    private final int tileSize = 25; // Adjust tile size as needed
    private Maze maze;

    // Constructor
    public GamePanel() {
        this.setBackground(Color.DARK_GRAY);
        this.addMouseListener(this);
        maze = new Maze(); // Initialize the maze
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int calculatedTileSize = Math.min(panelWidth / Maze.COLS, panelHeight / Maze.ROWS);
        int xOffset = (panelWidth - (calculatedTileSize * Maze.COLS)) / 2;
        int yOffset = (panelHeight - (calculatedTileSize * Maze.ROWS)) / 2;

        for (int row = 0; row < Maze.ROWS; row++) {
            for (int col = 0; col < Maze.COLS; col++) {
                int cell = maze.getCell(row, col);
                switch (cell) {
                    case Maze.WALL:
                        g.setColor(Color.BLACK);  // Walls are black
                        break;
                    case Maze.PATH:
                        g.setColor(Color.WHITE);  // Paths are white
                        break;
                    case Maze.START:
                        g.setColor(Color.GREEN);  // Start is green
                        break;
                    case Maze.END:
                        g.setColor(Color.YELLOW);  // End is yellow
                        break;
                    case Maze.BUILDING:
                        g.setColor(Color.GRAY);  // Building is gray
                        break;
                    default:
                        g.setColor(Color.RED); // Unknown cell type
                        break;
                }
                g.fillRect(xOffset + col * calculatedTileSize, yOffset + row * calculatedTileSize, calculatedTileSize, calculatedTileSize);
                g.setColor(Color.LIGHT_GRAY);  // Grid lines
                g.drawRect(xOffset + col * calculatedTileSize, yOffset + row * calculatedTileSize, calculatedTileSize, calculatedTileSize);
            }
        }

        // draw towers, enemies, etc.
    }

    // mouse start
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    // mouse todo
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
