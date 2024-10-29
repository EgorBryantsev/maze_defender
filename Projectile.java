import java.awt.Color;
import java.awt.Graphics;

public class Projectile {
    public double x;
    public double y;
    private double targetX;
    private double targetY;
    private int speed;
    private int damage;
    private long startTime;  // Stores the creation time of the projectile in nanoseconds
    private static final long LIFETIME = 3_000_000_000L;  // 3 seconds in nanoseconds

    public Projectile(double startX, double startY, double targetX, double targetY, int speed, int damage) {
        this.x = startX;
        this.y = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;
        this.damage = damage;
        this.startTime = System.nanoTime();
    }

    public int damage() {
        return damage;
    }

    public void move() {
        // Get direction to target
        double dx = targetX - x;
        double dy = targetY - y;
        
        // Calculate distance
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Move towards target
        if (distance > 0) {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        }
    }

    // Check if projectile has expired
    public boolean isExpired() {
        return System.nanoTime() - startTime >= LIFETIME;
    }
    
    public void draw(Graphics g) {
        // Draw projectile as a small yellow circle (or red)
        if (Tower.towerLevel2 >= 10) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.YELLOW);
        }
        g.fillOval((int) (x - 3), (int) (y - 3), 6, 6);
    }
}
