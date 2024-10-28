import java.awt.Color;
import java.awt.Graphics;

public class Enemy {
    private int currentPathIndex;
    private double progress;
    private int hp;
    private final int maxHp;
    private final double speed;
    private final int points;
    private final int money;
    private final int[][] path;
    private final EnemyTypes type;

    // Get position, for aimed bullets
    public double[] getPosition(int calculatedTileSize, int xOffset, int yOffset) {
        if (currentPathIndex >= path.length) {
            return new double[]{0, 0};
        }
    
        // Current and next cells
        int[] currentCell = path[currentPathIndex];
        int[] nextCell = (currentPathIndex < path.length - 1) ? path[currentPathIndex + 1] : currentCell;
    
        // Interpolated position based on progress
        double interpRow = currentCell[0] + (nextCell[0] - currentCell[0]) * progress;
        double interpCol = currentCell[1] + (nextCell[1] - currentCell[1]) * progress;
    
        // Convert to pixel coordinates
        double pixelX = xOffset + ((interpCol + 0.5) * calculatedTileSize);
        double pixelY = yOffset + ((interpRow + 0.5) * calculatedTileSize);
    
        return new double[]{pixelX, pixelY};
    }

    // Constructor Enemy
    public Enemy(int[][] path, EnemyTypes type) {
        this.path = path;
        this.type = type;
        this.speed = type.speed();
        this.hp = type.hp();
        this.maxHp = type.hp();
        this.points = type.points();
        this.money = type.money();
        this.currentPathIndex = 0;
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
            return;
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

        double sizeRatio = type.sizeRatio();
        int enemySize = (int) (sizeRatio * tileSize);
        enemySize = Math.max(enemySize, 5);

        g.setColor(type.color());
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
    
    public int getPoints() { return points; }
    public int getMoney() { return money; }
}
