import java.awt.*;

public class SniperTower extends Tower {

    private int upgradeCost;

    public SniperTower(GamePanel gamePanel, int row, int col) {
        super(gamePanel, row, col);
        this.towerType = "SniperTower";
        this.towerLevel = 1;
        this.speed = 1;
        this.damage = 50;
        this.range = Integer.MAX_VALUE;
        this.cost = 200;
        this.upgradeCost = 150;
    }

    @Override
    public void update() {
        if (towerLevel == 0) return;

        // increment the shoot timer
        shootTimer++;

        // check if it's time to shoot based on tower speed
        if (shootTimer >= 60 / speed) {  // Assuming 60 frames per second
            shootTimer = 0;  // Reset timer
            shootAtAnyEnemy();
        }

        updateProjectiles();
    }

    private void shootAtAnyEnemy() {
        Enemy targetEnemy = findStrongestEnemy();

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
                    80,     // Projectile speed
                    damage  // Damage of the projectile
            );

            projectiles.add(p);
            gamePanel.addProjectile(p);
        }
    }

    private Enemy findStrongestEnemy() {
        Enemy strongestEnemy = null;
        int highestHealth = -1;

        for (Enemy enemy : GamePanel.getEnemies()) {
            if (enemy.getHealth() > highestHealth) {
                highestHealth = enemy.getHealth();
                strongestEnemy = enemy;
            }
        }

        return strongestEnemy;
    }

    @Override
    public void draw(Graphics g) {
        // draw the tower base
        g.setColor(Color.DARK_GRAY);
        int x = gamePanel.getXOffset() + (col * gamePanel.calculatedTileSize);
        int y = gamePanel.getYOffset() + (row * gamePanel.calculatedTileSize);
        int size = gamePanel.calculatedTileSize * 2; // Towers are 2x2 tiles
        g.fillRect(x, y, size, size);

        // draw the sniper tower specifics
        g.setColor(Color.BLUE);
        g.fillRect(x + size / 4, y + size / 4, size / 2, size / 2);

        // draw projectiles
        for (Projectile p : projectiles) {
            p.draw(g);
        }
    }

    @Override
    public void upgradeTower() {
        // Initialize or upgrade tower stats based on towerLevel
        if (towerLevel == 1) {
            // Building the tower
            speed = 0.5;
            damage = 30;
            range = Integer.MAX_VALUE;
        } else {
            // Upgrading the tower
            damage += 10;   // Increase damage
            speed += 0.2;   // Slightly increase firing rate
        }
        // Update cost for next upgrade
        upgradeCost += 100 * towerLevel;
    }

    @Override
    protected double getUpgradeCost() {
        return upgradeCost;
    }
}
