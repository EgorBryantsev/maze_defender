import java.awt.Color;
import java.awt.Graphics;

public class Projectile {
    public int x, y;
    private int targetX, targetY;
    private int speed;
    private int damage;
    private long startTime;  // Stores the creation time of the projectile in nanoseconds
    private static final long LIFETIME = 3_000_000_000L;  // 3 seconds in nanoseconds

    public Projectile(int startX, int startY, int targetX, int targetY, int speed, int damage) {
        this.x = startX;
        this.y = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;
        this.damage = damage;
        this.startTime = System.nanoTime();  // Initialize the start time when the projectile is created
    }
    
    public void move() {
        // Get direction to target
        int dx = targetX - x;
        int dy = targetY - y;
        
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
        // Draw projectile as a small yellow circle
        if (Tower.towerLevel2 >= 10) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.YELLOW);
        }
        g.fillOval(x - 3, y - 3, 6, 6);
    }
}
