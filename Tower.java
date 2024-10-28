import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JOptionPane;

public class Tower {
    private GamePanel gamePanel;
    public int speed;
    public int damage;
    public int range;
    private static int baseTowerLevel = 0;  // Changed to start at level 1
    public int towerLevel;  // Instance-specific tower level
    public static int towerLevel2; 
    private boolean confirmationPending = false;
    private double costMultiplier = 0;
    private double cost = 0;
    private int row;  // Row position in the maze
    private int col;  // Column position in the maze
    public int ovalX;
    public int ovalY;
    public String costMessage;

    private int shootTimer = 0;
    private ArrayList<Projectile> projectiles;
    private int spinAngle = 0;
    
    public Tower(GamePanel gamePanel, int row, int col) {
        this.gamePanel = gamePanel;
        this.projectiles = new ArrayList<>();  // Initialize instance tower level
        this.row = row;
        this.col = col;
        this.towerLevel = baseTowerLevel;
        this.towerLevel2 = towerLevel;
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

            if (p.isExpired()) {
                projectiles.remove(i);
                continue;
            }

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
        for (Projectile p : projectiles) {
            p.draw(g);
        }

        g.setColor(getLevelColor());
        int x = gamePanel.getXOffset() + (col * gamePanel.calculatedTileSize);
        int y = gamePanel.getYOffset() + (row * gamePanel.calculatedTileSize);
        int size = gamePanel.calculatedTileSize * 2; // Because towers are 2x2 tiles
        g.fillRect(x, y, size, size);

        if (towerLevel >= 1) {
            g.setColor(new Color(255, towerLevel * 5, towerLevel*5, 15));  // Semi-transparent red
            int circleSize = range * 2 * gamePanel.calculatedTileSize;
            int centerX = x + size / 2;
            int centerY = y + size / 2;
            ovalX = centerX - circleSize / 2;
            ovalY = centerY - circleSize / 2;
            g.fillOval(ovalX, ovalY, circleSize, circleSize);
        }
    }
    
    // Update tower stats when upgraded
    public void upgradeTower() {
        if (towerLevel <= 5) {
            speed = towerLevel * 2;  // Shots per second
        }
        if (towerLevel <= 4) {
            range = towerLevel * 2;  // Range in tiles
        } else if (towerLevel >= 8) {
            range += 0.25; 
        }
        if (towerLevel <= 6) {
            damage = towerLevel * 5;  // Damage per shot
        }
        if (towerLevel == 10) {
            damage += towerLevel * 5;  // Damage per shot
        }
        cost = 100 + 100 * costMultiplier;
    }

    private void buildingNewLevel(int row, int col) {
        int newState = Maze.maze[row][col] + 1;
        if (GameState.money >= cost) {
            GameState.money -= cost;
            towerLevel++;
            towerLevel2 = towerLevel;
            costMultiplier += 3;
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

    public class BuildingClicked extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = (e.getX() - (gamePanel.getWidth() - gamePanel.panelWidth) / 2) / gamePanel.calculatedTileSize;
            int row = (e.getY() - (gamePanel.getHeight() - gamePanel.panelHeight) / 2) / gamePanel.calculatedTileSize;

            if (gamePanel.isBuilding(row, col) || gamePanel.isBuilding(row - 1, col) || gamePanel.isBuilding(row, col - 1) || gamePanel.isBuilding(row - 1, col - 1)) {

                // Show a confirmation dialog with the upgrade cost
                int confirm = JOptionPane.showConfirmDialog(
                    gamePanel,
                    "Upgrade cost: " + (100 + 100 * costMultiplier) + "\nProceed with upgrade?",
                    "Upgrade Confirmation",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {

                    if (gamePanel.isBuilding(row, col)) {
                        Tower existingTower = findTowerAt(row, col);
                        if (existingTower != null) {
                            existingTower.buildingNewLevel(row, col);
                        } else {

                            addNewTower(row, col);
                        }
                        gamePanel.repaint();
                    } else if (gamePanel.isBuilding(row - 1, col)) {
                        Tower existingTower = findTowerAt(row - 1, col);
                        if (existingTower != null) {
                            existingTower.buildingNewLevel(row - 1, col);
                        } else {

                            addNewTower(row - 1, col);
                        }
                        gamePanel.repaint();
                    } else if (gamePanel.isBuilding(row, col - 1)) {
                        Tower existingTower = findTowerAt(row, col - 1);
                        if (existingTower != null) {
                            existingTower.buildingNewLevel(row, col - 1);
                        } else {

                            addNewTower(row, col - 1);
                        }
                        gamePanel.repaint();
                    } else if (gamePanel.isBuilding(row - 1, col - 1)) {
                        Tower existingTower = findTowerAt(row - 1, col - 1);
                        if (existingTower != null) {
                            existingTower.buildingNewLevel(row - 1, col - 1);
                        } else {

                            addNewTower(row - 1, col - 1);
                        }
                        gamePanel.repaint();
                    }
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
            default: 
            if (towerLevel >= 5 && towerLevel < 10) {
                return Color.DARK_GRAY;
            } else if (towerLevel >= 10) {
                return Color.BLACK;
            } else {
                return Color.GRAY;
            }
        }
    }

}
