import java.awt.Color;
import java.awt.Graphics;

public class Projectile {
    private int x, y; // Position of the bullet
    private int speed; // Speed of the bullet
    private int damage; // Damage of the bullet
    private int targetX, targetY; // Coordinates of the enemy target

    public Projectile(int x, int y, int targetX, int targetY, int speed, int damage) {
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;
        this.damage = damage;
    }

    public void move() {
        // Calculate direction towards target
        int dx = targetX - x;
        int dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Normalize and move towards the target
        x += (int) (speed * dx / distance);
        y += (int) (speed * dy / distance);
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, 5, 5); // Draw bullet as a small yellow circle
    }

    public boolean hitTarget() {
        // Check if the bullet has reached the target (simple proximity check)
        return Math.abs(x - targetX) < 5 && Math.abs(y - targetY) < 5;
    }

    public int getDamage() {
        return damage;
    }
}
