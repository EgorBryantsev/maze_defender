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
    private double costMultiplier = 0;
    private double cost = 0;

    private int shootTimer = 0;
    private ArrayList<Projectile> projectiles;
    private int spinAngle = 0;
    
    public Tower(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.projectiles = new ArrayList<>();
        this.towerLevel = baseTowerLevel;  // Initialize instance tower level
        //upgradeTower();  // Set initial stats
    }
    
    public void update() {
        // Increase timer
        if (towerLevel == 0) return;

        shootTimer++;
        
        // Check if it's time to shoot based on tower speed
        if (shootTimer >= 60 / speed) {  // 60 frames = 1 second
            shootTimer = 0;  // Reset timer
            
            // Get list of enemies from GamePanel
            ArrayList<Enemy> enemies = new ArrayList<>(GamePanel.getEnemies());
            
            if (towerLevel == 4) {
                // Max level: spinning attack
                shootSpinningAttack();
            } else if (!enemies.isEmpty()) {  // Only try to shoot if there are enemies
                // Normal levels: shoot closest enemy
                Enemy target = findClosestEnemy(enemies);
                if (target != null) {
                    shootAt(target);
                }
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
        // Create 8 projectiles in a circle
        for (int i = 0; i < 8; i++) {
            // Calculate positions in a circle
            double angle = spinAngle + (i * 45);  // 360 degrees / 8 = 45 degrees
            int targetX = getTowerX() + (int)(Math.cos(Math.toRadians(angle)) * 100);
            int targetY = getTowerY() + (int)(Math.sin(Math.toRadians(angle)) * 100);
            
            // Create projectile
            Projectile p = new Projectile(
                getTowerX(),
                getTowerY(),
                targetX,
                targetY,
                10,  // Speed
                damage
            );
            projectiles.add(p);
        }
        
        // Rotate the pattern
        spinAngle += 10;
        if (spinAngle >= 360) {
            spinAngle = 0;
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
        // Find tower in maze and return its X position
        for (int row = 0; row < Maze.ROWS; row++) {
            for (int col = 0; col < Maze.COLS; col++) {
                if (Maze.maze[row][col] >= 5) {
                    return gamePanel.getXOffset() + (col * gamePanel.calculatedTileSize) + 
                           (gamePanel.calculatedTileSize);
                }
            }
        }
        return 0;
    }
    
    private int getTowerY() {
        // Find tower in maze and return its Y position
        for (int row = 0; row < Maze.ROWS; row++) {
            for (int col = 0; col < Maze.COLS; col++) {
                if (Maze.maze[row][col] >= 5) {
                    return gamePanel.getYOffset() + (row * gamePanel.calculatedTileSize) + 
                           (gamePanel.calculatedTileSize);
                }
            }
        }
        return 0;
    }
    
    public void draw(Graphics g) {
        // Draw all projectiles
        for (Projectile p : projectiles) {
            p.draw(g);
        }
        
        // Draw range circle for max level towers
        if (towerLevel == 4) {
            g.setColor(new Color(255, 0, 0, 50));  // Semi-transparent red
            int size = range * 2 * gamePanel.calculatedTileSize;
            g.fillOval(getTowerX() - size/2, getTowerY() - size/2, size, size);
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

                if (isBuilding(row, col)) {
                    if (confirm == JOptionPane.YES_OPTION) {
                        buildingNewLevel(row, col);
                        gamePanel.repaint();
                    }
                } else if (isBuilding(row - 1, col)) {
                    if (confirm == JOptionPane.YES_OPTION) {
                        buildingNewLevel(row - 1, col);
                        gamePanel.repaint();
                    }
                } else if (isBuilding(row, col - 1)) {
                    if (confirm == JOptionPane.YES_OPTION) {
                        buildingNewLevel(row, col - 1);
                        gamePanel.repaint();
                    }
                } else if (isBuilding(row - 1, col - 1)) {
                    if (confirm == JOptionPane.YES_OPTION) {
                        buildingNewLevel(row - 1, col - 1);
                        gamePanel.repaint();
                    }
                }

            }
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
