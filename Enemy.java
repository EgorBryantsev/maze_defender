import java.awt.Color;
import java.awt.Graphics;

public class Enemy {
    private int currentPathIndex; // Location
    private double progress;
    private int hp; // Current health
    private final int maxHp; // Maximum health
    private final double speed;
    private final int points;
    private final int[][] path; // Path
    private final EnemyTypes type; // Enemy type

    public Enemy(int[][] path, EnemyTypes type) {
        this.path = path;
        this.type = type;
        this.speed = type.speed();
        this.hp = type.hp();
        this.maxHp = type.hp();
        this.points = type.points();
        this.currentPathIndex = 0; // Start moving towards the first step
        this.progress = 0.0;
    }

    public void move(double deltaTime) {
        //reaching the end
        if (currentPathIndex >= path.length) {
            return;
        }

        progress += speed * deltaTime;

        while (progress >= 1.0 && currentPathIndex < path.length - 1) {
            progress -= 1.0;
            currentPathIndex++;
        }
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public boolean hasReachedEnd() {
        return currentPathIndex >= path.length - 1 && progress >= 1.0;
    }

    public void draw(Graphics g, int tileSize, int xOffset, int yOffset) {
        if (currentPathIndex >= path.length) {
            return; // Nothing to draw
        }

        // Current and next cells
        int[] currentCell = path[currentPathIndex];
        int[] nextCell = (currentPathIndex < path.length - 1) ? path[currentPathIndex + 1] : currentCell;

        // Interpolated position based on progress
        double interpRow = currentCell[0] + (nextCell[0] - currentCell[0]) * progress;
        double interpCol = currentCell[1] + (nextCell[1] - currentCell[1]) * progress;

        // Convert grid position to pixel coordinates
        int pixelX = xOffset + (int)((interpCol + 0.5) * tileSize);
        int pixelY = yOffset + (int)((interpRow + 0.5) * tileSize);

        g.setColor(type.color());
        int enemySize = type.size();
        g.fillOval(pixelX - enemySize / 2, pixelY - enemySize / 2, enemySize, enemySize);

        // Draw health bar
        g.setColor(Color.GREEN);
        int barWidth = enemySize * 2;
        int barHeight = 5;
        int barX = pixelX - enemySize;
        int barY = pixelY - enemySize / 2 - 10;
        g.fillRect(barX, barY, (int)((hp / (double)maxHp) * barWidth), barHeight);
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    // Getters
    public int getPoints() { return points; }
}
