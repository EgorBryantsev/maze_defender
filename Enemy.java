import java.awt.*;
import java.util.List;

public class Enemy {
    private int currentPathIndex;
    private double progress;
    private int hp;
    private double speed;
    private int points;
    private int[][] path;

    public Enemy(int[][] path, double speed, int hp, int points) {
        this.path = path;
        this.speed = speed;
        this.hp = hp;
        this.points = points;
        this.currentPathIndex = 0;
        this.progress = 0.0;
    }

    public void setPath(int[][] path) {
        this.path = path;
    }

    public void move(double deltaTime) {
        if (currentPathIndex >= path.length - 1) {
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
            return;
        }

        int[] currentCell = path[currentPathIndex];
        int[] nextCell = (currentPathIndex < path.length - 1) ? path[currentPathIndex + 1] : currentCell;

        double interpRow = currentCell[0] + (nextCell[0] - currentCell[0]) * progress;
        double interpCol = currentCell[1] + (nextCell[1] - currentCell[1]) * progress;

        int pixelX = xOffset + (int) ((interpCol + 0.5) * tileSize);
        int pixelY = yOffset + (int) ((interpRow + 0.5) * tileSize);

        g.setColor(Color.RED);
        int enemySize = tileSize / 2;
        g.fillOval(pixelX - enemySize / 2, pixelY - enemySize / 2, enemySize, enemySize);

        // Draw health bar
        g.setColor(Color.GREEN);
        int barWidth = enemySize;
        int barHeight = 5;
        int barX = pixelX - enemySize / 2;
        int barY = pixelY - enemySize / 2 - 10;
        g.fillRect(barX, barY, (int) ((hp / 100.0) * barWidth), barHeight);
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    public int getPoints() {
        return points;
    }

    public int getX() {
        return currentPathIndex < path.length ? path[currentPathIndex][1] : 0;
    }

    public int getY() {
        return currentPathIndex < path.length ? path[currentPathIndex][0] : 0;
    }
}
