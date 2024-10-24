import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wave {
    private int waveNumber;
    private int totalPoints;
    private List<Enemy> enemiesToSpawn;
    private int enemiesSpawned;
    private long lastSpawnTime;
    private long spawnInterval; // in nanoseconds
    private Pathfinder pathfinder;

    public Wave(int waveNumber, int basePoints, Pathfinder pathfinder) {
        this.waveNumber = waveNumber;
        this.pathfinder = pathfinder;
        this.totalPoints = (int) Math.pow(1.5, waveNumber) * basePoints;
        this.enemiesToSpawn = generateEnemies(totalPoints);
        this.enemiesSpawned = 0;
        this.lastSpawnTime = System.nanoTime();
        this.spawnInterval = 1_000_000_000; // 1 second
    }

    private List<Enemy> generateEnemies(int totalPoints) {
        List<Enemy> enemies = new ArrayList<>();
        Random rand = new Random();
        while (totalPoints > 0) {
            int enemyType = rand.nextInt(3);
            int points = 1;
            int hp = 50;
            double speed = 2;
            switch (enemyType) {
                case 0:
                    points = 1;
                    hp = 50;
                    speed = 2;
                    break;
                case 1:
                    points = 2;
                    hp = 100;
                    speed = 2;
                    break;
                case 2:
                    points = 3;
                    hp = 150;
                    speed = 2;
                    break;
            }
            if (totalPoints - points < 0) break;
            enemies.add(new Enemy(null, speed, hp, points)); // Path is set later
            totalPoints -= points;
        }
        return enemies;
    }

    public Enemy spawnEnemyIfReady(long currentTime) {
        if (enemiesSpawned < enemiesToSpawn.size() && currentTime - lastSpawnTime >= spawnInterval) {
            Enemy enemy = enemiesToSpawn.get(enemiesSpawned);
            int[][] path = pathfinder.findPath();
            if (path == null) {
                System.out.println("No path found for enemy.");
                return null;
            }
            enemy.setPath(path);
            enemiesSpawned++;
            lastSpawnTime = currentTime;
            return enemy;
        }
        return null;
    }

    public boolean isWaveComplete() {
        return enemiesSpawned >= enemiesToSpawn.size();
    }
}
