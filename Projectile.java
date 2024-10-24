import java.awt.*;

public class Projectile {
    private double x, y;
    private double speed;
    private int damage;
    private double targetX, targetY;

    public Projectile(double x, double y, double targetX, double targetY, double speed, int damage) {
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;
        this.damage = damage;
    }

    public void move() {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) return;
        x += (dx / distance) * speed;
        y += (dy / distance) * speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int) x, (int) y, 5, 5);
    }

    public boolean hitTarget(Enemy enemy) {
        double enemyPixelX = enemy.getX() * GamePanel.calculatedTileSize + GamePanel.xOffset + GamePanel.calculatedTileSize / 2;
        double enemyPixelY = enemy.getY() * GamePanel.calculatedTileSize + GamePanel.yOffset + GamePanel.calculatedTileSize / 2;
        double distance = Math.sqrt(Math.pow(x - enemyPixelX, 2) + Math.pow(y - enemyPixelY, 2));
        return distance < 5;
    }

    public int getDamage() {
        return damage;
    }
}
