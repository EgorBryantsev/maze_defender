import java.awt.Color;
import java.awt.Graphics;

public class Projectile {
    public int x, y;
    private int targetX, targetY;
    private int speed;
    private int damage;
    
    public Projectile(int startX, int startY, int targetX, int targetY, int speed, int damage) {
        this.x = startX;
        this.y = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;
        this.damage = damage;
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
    
    public void draw(Graphics g) {
        // Draw projectile as a small yellow circle
        g.setColor(Color.YELLOW);
        g.fillOval(x - 3, y - 3, 6, 6);
    }
}