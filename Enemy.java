import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

public class Enemy {
    private int currentPathIndex; //location
    private double progress;
    private int hp; //health
    private double speed;
    private int points;
    private List<int[]> path; // path

    public Enemy(List<int[]> path, double speed, int hp, int points) {
        this.path = path;
        this.speed = speed;
        this.hp = hp;
        this.points = points;
        this.currentPathIndex = 0; // Start moving towards the first step
        this.progress = 0.0;
    }

    public void move(double deltaTime) {
        //reaching the end
        if (currentPathIndex >= path.size()) {
            return;
        }

        progress += speed * deltaTime;

        while (progress >= 1.0 && currentPathIndex < path.size() - 1) {
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
        return currentPathIndex >= path.size() - 1 && progress >= 1.0;
    }

    public void draw(Graphics g, int tileSize, int xOffset, int yOffset) {
        if (currentPathIndex >= path.size()) {
            return; // Nothing to draw
        }

        // Current and next cells
        int[] currentCell = path.get(currentPathIndex);
        int[] nextCell = (currentPathIndex < path.size() - 1) ? path.get(currentPathIndex + 1) : currentCell;

        // Interpolated position based on progress
        double interpRow = currentCell[0] + (nextCell[0] - currentCell[0]) * progress;
        double interpCol = currentCell[1] + (nextCell[1] - currentCell[1]) * progress;

        // Convert grid position to pixel coordinates
        int pixelX = xOffset + (int)((interpCol + 0.5) * tileSize);
        int pixelY = yOffset + (int)((interpRow + 0.5) * tileSize);
        // Draw the enemy as a red circle
        g.setColor(Color.RED);
        int enemySize = tileSize / 2;
        g.fillOval(pixelX - enemySize / 2, pixelY - enemySize / 2, enemySize, enemySize);

        // Draw health bar
        g.setColor(Color.GREEN);
        int barWidth = enemySize * 2;
        int barHeight = 5;
        int barX = pixelX - enemySize;
        int barY = pixelY - enemySize / 2 - 10;
        g.fillRect(barX, barY, (int)((hp / 100.0) * barWidth), barHeight);
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    // Getters
    public int getPoints() { return points; }
}
