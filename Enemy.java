import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

public class Enemy {
    private double x, y; //location
    private int hp; //health
    private double speed;
    private int points;
    private List<int[]> path; // path
    private int currentPathIndex; // path target

    public Enemy(List<int[]> path, double speed, int hp, int points, int tileSize) {
        this.path = path;
        this.speed = speed;
        this.hp = hp;
        this.points = points;
        this.currentPathIndex = 1; // Start moving towards the first step

        // start at the start
        this.x = path.get(0)[1] * tileSize + tileSize / 2;
        this.y = path.get(0)[0] * tileSize + tileSize / 2;
    }

    public void move(double deltaTime, int tileSize) {
        //reaching the end
        if (currentPathIndex >= path.size()) {
            return;
        }

        int targetRow = path.get(currentPathIndex)[0];
        int targetCol = path.get(currentPathIndex)[1];

        // grid to pixel coords
        double targetX = targetCol * tileSize + tileSize / 2;
        double targetY = targetRow * tileSize + tileSize / 2;

        // distance to target
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < speed * deltaTime) {
            // Move directly to the target position
            x = targetX;
            y = targetY;
            // Proceed to the next cell in the path
            currentPathIndex++;
        } else {
            /*
             * Move towards the target.
             * - (dx / distance) and (dy / distance) normalize the direction vector.
             * - Multiplying by speed * deltaTime scales the movement based on speed and elapsed time.
             */
            x += (dx / distance) * speed * deltaTime;
            y += (dy / distance) * speed * deltaTime;
        }
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public boolean hasReachedEnd() {
        return currentPathIndex >= path.size();
    }

    public void draw(Graphics g, int tileSize) {
        // Draw the enemy as a red circle
        g.setColor(Color.RED);
        g.fillOval((int)x - tileSize/4, (int)y - tileSize/4, tileSize/2, tileSize/2);

        // Draw health bar
        g.setColor(Color.GREEN);
        int barWidth = tileSize / 2;
        int barHeight = 5;
        g.fillRect((int)x - tileSize/4, (int)y - tileSize/4 - 10, (int)((hp / 100.0) * barWidth), barHeight);
        g.setColor(Color.BLACK);
        g.drawRect((int)x - tileSize/4, (int)y - tileSize/4 - 10, barWidth, barHeight);
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public int getPoints() { return points; }
}
