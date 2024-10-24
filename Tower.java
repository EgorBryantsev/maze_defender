import java.awt.event.*;

public class Tower {
    private GamePanel gamePanel;
    public int speed;
    public int damage;
    public int range;
    public static int towerLevel = 0;

    // Constructor to receive GamePanel reference
    public Tower(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public class BuildingClicked extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // Calculate the column and row based on click position
            int col = (e.getX() - (gamePanel.getWidth() - gamePanel.panelWidth) / 2) / gamePanel.calculatedTileSize;
            int row = (e.getY() - (gamePanel.getHeight() - gamePanel.panelHeight) / 2) / gamePanel.calculatedTileSize;
            
            if (isBuilding(row, col)) {
                buildingNewLevel(row, col);
                gamePanel.repaint();  // Repaint after updating the building state
            }
        }
    }

    public void upgradeTower(int towerLevel) {
        speed = towerLevel*towerLevel;
        range = 5*towerLevel;
        damage = towerLevel*towerLevel/2;
    }

    private boolean isBuilding(int row, int col) {
        return row >= 0 && col >= 0 &&
                row < Maze.ROWS - 1 && col < Maze.COLS - 1 &&
                Maze.maze[row][col] >= 4 && Maze.maze[row][col] == Maze.maze[row + 1][col] &&
                Maze.maze[row][col] == Maze.maze[row][col + 1] && Maze.maze[row + 1][col + 1] == Maze.maze[row][col];
    }

    private void buildingNewLevel(int row, int col) {
        int newState = Maze.maze[row][col] + 1;
        if (newState > 7) newState = 8;

        Maze.maze[row][col] = newState;
        Maze.maze[row + 1][col] = newState;
        Maze.maze[row][col + 1] = newState;
        Maze.maze[row + 1][col + 1] = newState;
    }
}
