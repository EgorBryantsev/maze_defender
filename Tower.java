import java.awt.*;
import java.util.List;

public class Tower {
    public static final int BASE_COST = 100;
    private GamePanel gamePanel;
    private int row;
    private int col;
    private int damage;
    private int range;
    private int level;
    private long lastShotTime;
    private long shootInterval;

    public Tower(GamePanel gamePanel, int row, int col) {
        this.gamePanel = gamePanel;
        this.row = row;
        this.col = col;
        this.damage = 10;
        this.range = 100;
        this.level = 1;
        this.lastShotTime = 0;
        this.shootInterval = 1_000_000_000; // 1 second
    }

    public void update(long currentTime) {
        if (currentTime - lastShotTime >= shootInterval) {
            Enemy target = findTarget();
            if (target != null) {
                shoot(target);
                lastShotTime = currentTime;
            }
        }
    }

    private Enemy findTarget() {
        for (Enemy enemy : gamePanel.enemies) {
            double distance = calculateDistance(enemy);
            if (distance <= range) {
                return enemy;
            }
        }
        return null;
    }

    private double calculateDistance(Enemy enemy) {
        int towerX = col;
        int towerY = row;
        int enemyX = enemy.getX();
        int enemyY = enemy.getY();
        return Math.sqrt(Math.pow(towerX - enemyX, 2) + Math.pow(towerY - enemyY, 2)) * gamePanel.calculatedTileSize;
    }

    private void shoot(Enemy target) {
        int towerPixelX = gamePanel.xOffset + (col * gamePanel.calculatedTileSize) + gamePanel.calculatedTileSize / 2;
        int towerPixelY = gamePanel.yOffset + (row * gamePanel.calculatedTileSize) + gamePanel.calculatedTileSize / 2;
        int targetPixelX = gamePanel.xOffset + (target.getX() * gamePanel.calculatedTileSize) + gamePanel.calculatedTileSize / 2;
        int targetPixelY = gamePanel.yOffset + (target.getY() * gamePanel.calculatedTileSize) + gamePanel.calculatedTileSize / 2;
        Projectile projectile = new Projectile(towerPixelX, towerPixelY, targetPixelX, targetPixelY, 5, damage);
        gamePanel.projectiles.add(projectile);
    }

    public void draw(Graphics g, int tileSize, int xOffset, int yOffset) {
        int pixelX = xOffset + col * tileSize;
        int pixelY = yOffset + row * tileSize;

        g.setColor(Color.BLUE);
        g.fillRect(pixelX, pixelY, tileSize, tileSize);
    }
}
