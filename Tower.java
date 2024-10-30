import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JOptionPane;

public class Tower {
    public GamePanel gamePanel;
    public int speed;
    public int damage;
    public int range;
    private static int baseTowerLevel = 0;  // Changed to start at level 1
    public int towerLevel;  // Instance-specific tower level
    public String towerType;
    private boolean confirmationPending = false;
    private double costMultiplier = 0;
    double cost = 0;
    int row;  // Row position in the maze
    int col;  // Column position in the maze
    public int ovalX;
    public int ovalY;
    public String costMessage;
    public static int towerLevel2;

    private int burstShotsFired = 0;
    private int burstCooldown = 0;
    private static final int BURST_SIZE = 3;       // n burst shots
    private static final int BURST_INTERVAL = 10;  // frames between burst shots
    private static final int BURST_COOLDOWN_TIME = 5; // frames between burst


    public int shootTimer = 0;
    ArrayList<Projectile> projectiles;
    private int spinAngle = 0;
    
    public Tower(GamePanel gamePanel, int row, int col) {
        this.gamePanel = gamePanel;
        this.projectiles = new ArrayList<>();  // Initialize instance tower level
        this.row = row;
        this.col = col;
        this.towerLevel = baseTowerLevel;
        this.towerLevel2 = towerLevel;
        this.towerType = "Basic";
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

        if (shootTimer >= 60 / speed) {  // Assuming 60 frames per second
            shootTimer = 0;  // Reset timer
            shoot();
        }

        updateProjectiles();
    }

    protected void updateProjectiles() {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.move();

            boolean projectileRemoved = false;

            if (p.isExpired()) {
                projectiles.remove(i);
                continue;
            }

            // Check if projectile hit any enemy
            for (Enemy enemy : GamePanel.getEnemies()) {
                if (isHitting(p, enemy)) {
                    enemy.takeDamage(damage);
                    projectiles.remove(i);
                    projectileRemoved = true;
                    break;
                }
            }

            if (projectileRemoved) {
                continue;
            }

            // Remove projectiles that went too far
            if (isProjectileOutOfRange(p)) {
                projectiles.remove(i);
                continue;
            }
        }
    }

    protected void shoot() {
        switch (towerLevel) {
            case 1:
            case 2:
                shootAtNearestEnemy();
                break;
            case 3:
            case 4:
                shootBurstAtNearestEnemy();
                break;
            default:
                shootSpinningAttack();
                break;
        }
    }

    private void shootBurstAtNearestEnemy() {
        if (burstCooldown > 0) {
            burstCooldown--;
            return;
        }

        if (burstShotsFired < BURST_SIZE) {
            Enemy targetEnemy = findNearestEnemy();

            if (targetEnemy != null) {
                double originX = getTowerX();
                double originY = getTowerY();

                double[] enemyPos = targetEnemy.getPosition(
                        gamePanel.calculatedTileSize,
                        gamePanel.getXOffset(),
                        gamePanel.getYOffset()
                );

                Projectile p = new Projectile(
                        originX,
                        originY,
                        enemyPos[0],
                        enemyPos[1],
                        12,
                        damage
                );

                projectiles.add(p);
                gamePanel.addProjectile(p);
            }

            burstShotsFired++;
        } else {
            burstShotsFired = 0;
            burstCooldown = BURST_COOLDOWN_TIME;
        }
    }


    private void shootAtNearestEnemy() {
        Enemy targetEnemy = findNearestEnemy();

        if (targetEnemy != null) {
            double originX = getTowerX();
            double originY = getTowerY();

            double[] enemyPos = targetEnemy.getPosition(
                    gamePanel.calculatedTileSize,
                    gamePanel.getXOffset(),
                    gamePanel.getYOffset()
            );

            Projectile p = new Projectile(
                    originX,
                    originY,
                    enemyPos[0],
                    enemyPos[1],
                    10,
                    damage //
            );

            projectiles.add(p);
            gamePanel.addProjectile(p);
        }
    }

    private Enemy findNearestEnemy() {
        Enemy nearestEnemy = null;
        double nearestDistanceSq = Double.MAX_VALUE;

        for (Enemy enemy : GamePanel.getEnemies()) {
            double[] enemyPos = enemy.getPosition(
                    gamePanel.calculatedTileSize,
                    gamePanel.getXOffset(),
                    gamePanel.getYOffset()
            );

            double dx = enemyPos[0] - getTowerX();
            double dy = enemyPos[1] - getTowerY();
            double distanceSq = dx * dx + dy * dy;

            if (distanceSq <= (range * gamePanel.calculatedTileSize) * (range * gamePanel.calculatedTileSize)) {
                if (distanceSq < nearestDistanceSq) {
                    nearestDistanceSq = distanceSq;
                    nearestEnemy = enemy;
                }
            }
        }

        return nearestEnemy;
    }

    private void shootSpinningAttack() {
        // Center coordinates for bullets based on the actual middle of the range oval
        double originX = getTowerX();
        double originY = getTowerY();
        int projectileSpeed = 5; // Adjust as needed

        // Number of projectiles
        int projectilesCount = 8;
        double angleIncrement = 360.0 / projectilesCount;

        for (int i = 0; i < projectilesCount; i++) {
            double angle = Math.toRadians(i * angleIncrement);
            double targetX = originX + Math.cos(angle) * range * gamePanel.calculatedTileSize;
            double targetY = originY + Math.sin(angle) * range * gamePanel.calculatedTileSize;

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
        double dx = p.x - pos[0];
        double dy = p.y - pos[1];
        return (dx * dx + dy * dy) < (20 * 20);
    }

    private boolean isProjectileOutOfRange(Projectile p) {
        double dx = p.x - getTowerX();
        double dy = p.y - getTowerY();
        return Math.sqrt(dx * dx + dy * dy) > range * gamePanel.calculatedTileSize;
    }

    // Get tower position (center of the tower)
    double getTowerX() {
        return gamePanel.getXOffset() + (col * gamePanel.calculatedTileSize) + gamePanel.calculatedTileSize; // Center X of the tower
    }

    double getTowerY() {
        return gamePanel.getYOffset() + (row * gamePanel.calculatedTileSize) + gamePanel.calculatedTileSize; // Center Y of the tower
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
            g.setColor(new Color(62, 55, 55, 50));
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
        if (towerLevel <= 2) {
            speed = towerLevel * 2;  // Shots per second
        }
        if (towerLevel <= 4) {
            range = towerLevel * 2;  // Range in tiles
        } else if (towerLevel >= 8) {
            range += 0.25;
        }
        if (towerLevel <= 6) {
            damage = towerLevel * 2;  // Damage per shot
        }
        if (towerLevel == 10) {
            damage += towerLevel * 5;  // Damage per shot
        }
        // Update cost for next upgrade
        cost = 100 + 100 * costMultiplier;
    }


    protected void buildingNewLevel(int row, int col) {
        double upgradeCost = getUpgradeCost();

        if (GameState.money >= upgradeCost) {
            GameState.money -= upgradeCost;

            if (towerLevel == 0) {
                // Building a new tower
                towerLevel = 1;
            } else {
                // Upgrading existing tower
                towerLevel++;
            }

            towerLevel2 = towerLevel;
            costMultiplier += 1;
            upgradeTower();  // Update tower attributes based on new level

            // Update maze state if needed
            int newState = Maze.maze[row][col] + 1;
            if (newState > 7) newState = 8;
            Maze.maze[row][col] = newState;
            Maze.maze[row + 1][col] = newState;
            Maze.maze[row][col + 1] = newState;
            Maze.maze[row + 1][col + 1] = newState;
        } else {
            JOptionPane.showMessageDialog(gamePanel, "Not enough money!", "Action Failed", JOptionPane.WARNING_MESSAGE);
        }
    }


    public class BuildingClicked extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = (e.getX() - (gamePanel.getWidth() - gamePanel.panelWidth) / 2) / gamePanel.calculatedTileSize;
            int row = (e.getY() - (gamePanel.getHeight() - gamePanel.panelHeight) / 2) / gamePanel.calculatedTileSize;

            if (gamePanel.isBuilding(row, col)) {
                Tower existingTower = findExistingTower(row, col);
                if (existingTower != null) {
                    if (existingTower.towerLevel > 0) {
                        // Tower is already built, show upgrade confirmation
                        double upgradeCost = existingTower.getUpgradeCost();
                        int result = JOptionPane.showConfirmDialog(
                                gamePanel,
                                "Upgrade tower for $" + upgradeCost + "?",
                                "Upgrade Tower",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (result == JOptionPane.YES_OPTION) {
                            existingTower.buildingNewLevel(existingTower.getRow(), existingTower.getCol());
                            gamePanel.repaint();
                        }
                    } else {
                        // Tower has towerLevel == 0, it's unbuilt
                        // Show the "choose tower to build" dialog
                        showBuildTowerDialog(row, col, existingTower);
                    }
                }
            }
        }

        private void showBuildTowerDialog(int row, int col, Tower existingTower) {
            String[] options = {"Basic Tower", "Sniper Tower"};
            int choice = JOptionPane.showOptionDialog(
                    gamePanel,
                    "Choose a tower to build:",
                    "Build Tower",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (choice == 0) {
                if (existingTower != null) {
                    existingTower.buildingNewLevel(existingTower.getRow(), existingTower.getCol());
                } else {
                    addNewTower(row, col);
                }
            } else if (choice == 1) {
                if (existingTower != null) {
                    if (existingTower.towerLevel == 0) {
                        // Replace the unbuilt tower with a Sniper Tower
                        gamePanel.getTowers().remove(existingTower);
                        addNewSniperTower(existingTower.getRow(), existingTower.getCol());
                    } else {
                        JOptionPane.showMessageDialog(gamePanel, "Cannot change tower type after placement.", "Action Denied", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    addNewSniperTower(row, col);
                }
            }
            gamePanel.repaint();
        }

        private Tower findExistingTower(int row, int col) {
            Tower existingTower = findTowerAt(row, col);
            if (existingTower != null) {
                return existingTower;
            }
            existingTower = findTowerAt(row - 1, col);
            if (existingTower != null) {
                return existingTower;
            }
            existingTower = findTowerAt(row, col - 1);
            if (existingTower != null) {
                return existingTower;
            }
            existingTower = findTowerAt(row - 1, col - 1);
            return existingTower;
        }

        private void addNewSniperTower(int row, int col) {
            Tower newTower = new SniperTower(gamePanel, row, col);
            if (GameState.money >= newTower.cost) {
                GameState.money -= newTower.cost;
                gamePanel.addTower(newTower);
            } else {
                JOptionPane.showMessageDialog(gamePanel, "Not enough money to build Sniper Tower!", "Build Failed", JOptionPane.WARNING_MESSAGE);
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

    protected double getUpgradeCost() {
        return cost;
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
