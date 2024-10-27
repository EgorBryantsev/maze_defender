import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JOptionPane;

public class Tower {
    private GamePanel gamePanel;
    public int speed;
    public int damage;
    public int range;
    private static int baseTowerLevel = 1;  // Changed to start at level 1
    public int towerLevel;  // Instance-specific tower level
    private boolean confirmationPending = false;
    private int upgradeCost = 0;
    private double costMultiplier = 3;
    private double cost = 0;
    private int row;  // Row position in the maze
    private int col;  // Column position in the maze
    public int ovalX;
    public int ovalY;

    private int shootTimer = 0;
    private ArrayList<Projectile> projectiles;
    private int spinAngle = 0;
    
    public Tower(GamePanel gamePanel, int row, int col) {
        this.gamePanel = gamePanel;
        this.projectiles = new ArrayList<>();
        this.towerLevel = baseTowerLevel;  // Initialize instance tower level
        this.row = row;
        this.col = col;
        upgradeTower();  // Set initial stats
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void update() {
        // Skip if tower is inactive
        if (towerLevel == 0) return;

        // Increment the shoot timer
        shootTimer++;

        // Check if it's time to shoot based on tower speed
        // speed represents shots per second
        if (shootTimer >= 60 / speed) {  // Assuming 60 frames per second
            shootTimer = 0;  // Reset timer

            // Determine shooting behavior based on tower level
            switch (towerLevel) {
                default:
                    shootSpinningAttack();
                    break;
            }
        }

        // Update all projectiles
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.move();

            // Check if projectile hit any enemy
            for (Enemy enemy : GamePanel.getEnemies()) {
                if (isHitting(p, enemy)) {
                    enemy.takeDamage(damage);
                    projectiles.remove(i);
                    break;
                }
            }

            // Remove projectiles that went too far
            if (isProjectileOutOfRange(p)) {
                projectiles.remove(i);
            }
        }
    }

    private void shootSpinningAttack() {
        // Center coordinates for bullets based on the actual middle of the range oval
        int originX = ovalX + (range * gamePanel.calculatedTileSize);
        int originY = ovalY + (range * gamePanel.calculatedTileSize);
        int projectileSpeed = 10; // Adjust as needed
    
        // Number of projectiles (e.g., 8 for every 45 degrees)
        int projectilesCount = 8;
        double angleIncrement = 360.0 / projectilesCount;
    
        for (int i = 0; i < projectilesCount; i++) {
            double angle = Math.toRadians(i * angleIncrement);
            int targetX = originX + (int)(Math.cos(angle) * range * gamePanel.calculatedTileSize);
            int targetY = originY + (int)(Math.sin(angle) * range * gamePanel.calculatedTileSize);
    
            // Create projectile originating from the center of the oval
            Projectile p = new Projectile(
                    originX,
                    originY,
                    targetX,
                    targetY,
                    projectileSpeed,
                    damage
            );
            projectiles.add(p);
        }
    }
    
    
    
    private Enemy findClosestEnemy(ArrayList<Enemy> enemies) {
        Enemy closest = null;
        int closestDistance = range * gamePanel.calculatedTileSize;  // Maximum range
        
        for (Enemy enemy : enemies) {
            int distance = getDistanceToEnemy(enemy);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = enemy;
            }
        }
        
        return closest;
    }
    
    private void shootAt(Enemy enemy) {
        double[] pos = enemy.getPosition(gamePanel.calculatedTileSize, gamePanel.getXOffset(), gamePanel.getYOffset());
        Projectile p = new Projectile(
            getTowerX(),
            getTowerY(),
            (int)pos[0],
            (int)pos[1],
            10,  // Speed
            damage
        );
        projectiles.add(p);
    }
    
    private int getDistanceToEnemy(Enemy enemy) {
        double[] pos = enemy.getPosition(gamePanel.calculatedTileSize, gamePanel.getXOffset(), gamePanel.getYOffset());
        int dx = getTowerX() - (int)pos[0];
        int dy = getTowerY() - (int)pos[1];
        return (int)Math.sqrt(dx * dx + dy * dy);
    }
    
    private boolean isHitting(Projectile p, Enemy e) {
        double[] pos = e.getPosition(gamePanel.calculatedTileSize, gamePanel.getXOffset(), gamePanel.getYOffset());
        int dx = p.x - (int)pos[0];
        int dy = p.y - (int)pos[1];
        return Math.sqrt(dx * dx + dy * dy) < 20;  // 20 pixels hit radius
    }
    
    private boolean isProjectileOutOfRange(Projectile p) {
        int dx = p.x - getTowerX();
        int dy = p.y - getTowerY();
        return Math.sqrt(dx * dx + dy * dy) > range * gamePanel.calculatedTileSize;
    }

    // Get tower position (center of the tower)
    private int getTowerX() {
        return gamePanel.getXOffset() + (col * gamePanel.calculatedTileSize) + (gamePanel.calculatedTileSize / 2);
    }

    private int getTowerY() {
        return gamePanel.getYOffset() + (row * gamePanel.calculatedTileSize) + (gamePanel.calculatedTileSize / 2);
    }
    
    public void draw(Graphics g) {
        // Draw all projectiles
        for (Projectile p : projectiles) {
            p.draw(g);
        }
        
        // Draw range circle for max level towers
        if (towerLevel >= 1) {
            g.setColor(new Color(255, 0, 0, 50));  // Semi-transparent red
            int size = range * 2 * gamePanel.calculatedTileSize;
            int bottomRightX = getTowerX() + gamePanel.calculatedTileSize / 2;
            int bottomRightY = getTowerY() + gamePanel.calculatedTileSize / 2;
            ovalX = bottomRightX - size / 2;
            ovalY = bottomRightY - size / 2;
            g.fillOval(ovalX, ovalY, size, size);
        }
    }
    
    // Update tower stats when upgraded
    public void upgradeTower() {
        speed = towerLevel * 2;  // Shots per second
        range = towerLevel * 2;  // Range in tiles
        damage = towerLevel * 10;  // Damage per shot
    }

    public class BuildingClicked extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            costMultiplier = 2.5 * towerLevel;
            int col = (e.getX() - (gamePanel.getWidth() - gamePanel.panelWidth) / 2) / gamePanel.calculatedTileSize;
            int row = (e.getY() - (gamePanel.getHeight() - gamePanel.panelHeight) / 2) / gamePanel.calculatedTileSize;

            if (isBuilding(row, col) || isBuilding(row - 1, col) || isBuilding(row, col - 1) || isBuilding(row - 1, col - 1)) {
                cost = 100 + 100 * costMultiplier; // Use the tower's individual level for cost

                // Show a confirmation dialog with the upgrade cost
                int confirm = JOptionPane.showConfirmDialog(
                    gamePanel,
                    "Upgrade cost: " + cost + "\nProceed with upgrade?",
                    "Upgrade Confirmation",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    // Determine the top-left tile of the tower
                    int towerRow = isBuilding(row, col) ? row : (isBuilding(row - 1, col) ? row - 1 :
                            (isBuilding(row, col - 1) ? row : row - 1));
                    int towerCol = isBuilding(row, col) ? col : (isBuilding(row - 1, col) ? col :
                            (isBuilding(row, col - 1) ? col - 1 : col - 1));


                    Tower existingTower = findTowerAt(towerRow, towerCol);
                    if (existingTower != null) {
                        existingTower.buildingNewLevel(towerRow, towerCol);
                    } else {

                        addNewTower(towerRow, towerCol);
                    }
                    gamePanel.repaint();
                }
            }
        }

        private void addNewTower(int row, int col) {
            Tower newTower = new Tower(gamePanel, row, col);
            gamePanel.addTower(newTower);
        }

        // Helper method to find a tower at a specific position
        private Tower findTowerAt(int row, int col) {
            for (Tower tower : gamePanel.getTowers()) {
                if (tower.getRow() == row && tower.getCol() == col) {
                    return tower;
                }
            }
            return null;
        }
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
                Maze.maze[row][col] >= 4 &&
                Maze.maze[row][col] == Maze.maze[row + 1][col] &&
                Maze.maze[row][col] == Maze.maze[row][col + 1] &&
                Maze.maze[row + 1][col + 1] == Maze.maze[row][col];
    }

    private void buildingNewLevel(int row, int col) {
        int newState = Maze.maze[row][col] + 1;
        cost = 100 + 100 * costMultiplier * towerLevel;
        if (GameState.money >= cost) {
            GameState.money -= cost;
            towerLevel++;
            upgradeTower();  // Update tower attributes based on new level
            if (newState > 7) newState = 8;
            Maze.maze[row][col] = newState;
            Maze.maze[row + 1][col] = newState;
            Maze.maze[row][col + 1] = newState;
            Maze.maze[row + 1][col + 1] = newState;
        } else {
            // Optionally, notify the player about insufficient funds
            JOptionPane.showMessageDialog(gamePanel, "Not enough money to upgrade!", "Upgrade Failed", JOptionPane.WARNING_MESSAGE);
        }
    }
}
