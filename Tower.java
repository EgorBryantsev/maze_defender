import java.awt.Color;
import java.awt.Image;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Tower {
    private GamePanel gamePanel;
    public int speed;
    public int damage;
    public int range;
    public static int towerLevel = 0;
    private boolean confirmationPending = false;  // New flag to track if confirmation is needed
    private int upgradeCost = 0;  // Cost of the upgrade    
    private double costMultiplier = 0;
    private double cost = 0;

    public static final Image upgradeIcon = new ImageIcon("upgrade-svgrepo-com.png").getImage();


    public Tower(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public class BuildingClicked extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            costMultiplier = 2.5 * towerLevel;
            int col = (e.getX() - (gamePanel.getWidth() - gamePanel.panelWidth) / 2) / gamePanel.calculatedTileSize;
            int row = (e.getY() - (gamePanel.getHeight() - gamePanel.panelHeight) / 2) / gamePanel.calculatedTileSize;

            if (isBuilding(row, col)) {
                cost = 100 + 100 * costMultiplier; // Use the tower's individual level for cost

                // Show a confirmation dialog with the upgrade cost
                int confirm = JOptionPane.showConfirmDialog(
                    gamePanel,
                    "Upgrade cost: " + cost + "\nProceed with upgrade?",
                    "Upgrade Confirmation",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    buildingNewLevel(row, col);
                    gamePanel.repaint();
                }
            }
        }
    }

    public void upgradeTower() {
        speed = towerLevel*towerLevel;
        range = 5*towerLevel;
        damage = towerLevel*towerLevel/2;
    }

    public Color getLevelColor() {
        switch (towerLevel) {
            case 1: return Color.CYAN;
            case 2: return Color.YELLOW;
            case 3: return Color.ORANGE;
            case 4: return Color.RED;
            default: return Color.GRAY;
        }
    }

    private boolean isBuilding(int row, int col) {
        return row >= 0 && col >= 0 &&
                row < Maze.ROWS - 1 && col < Maze.COLS - 1 &&
                Maze.maze[row][col] >= 4 && Maze.maze[row][col] == Maze.maze[row + 1][col] &&
                Maze.maze[row][col] == Maze.maze[row][col + 1] && Maze.maze[row + 1][col + 1] == Maze.maze[row][col];
    }

    private void buildingNewLevel(int row, int col) {
        int newState = Maze.maze[row][col] + 1;
        cost = 100 + 100 * costMultiplier;
        if (GameState.money >= cost) {
            GameState.money -= cost;
            towerLevel ++;
            upgradeTower();
            if (newState > 7) newState = 8;
            Maze.maze[row][col] = newState;
            Maze.maze[row + 1][col] = newState;
            Maze.maze[row][col + 1] = newState;
            Maze.maze[row + 1][col + 1] = newState;
        } else {

        }
    }
}
