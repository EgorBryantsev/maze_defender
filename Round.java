import javax.lang.model.type.ArrayType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Round {
    private final int totalPoints;
    public final int spawnFrequency; // in milliseconds
    private final List<EnemyTypes> enemiesToSpawn;

    public Round(int totalPoints, int spawnFrequency) {
        this.totalPoints = totalPoints;
        this.spawnFrequency = spawnFrequency;
        this.enemiesToSpawn = generateEnemies(totalPoints);
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getSpawnFrequency() {
        return spawnFrequency;
    }

    public List<EnemyTypes> getEnemiesToSpawn() {
        return enemiesToSpawn;
    }

    private List<EnemyTypes> generateEnemies(int points) {
        List<EnemyTypes> enemyList = new ArrayList<>();
        int remainingPoints = points;
        Random rand = new Random();

        while (remainingPoints > 0) {
            List<EnemyTypes> possibleTypes = new ArrayList<>();

            if (remainingPoints >= 10) {
                possibleTypes.add(new EnemyTypes(2.0, 100, 10));
            }

            if (remainingPoints >= 20) {
                possibleTypes.add(new EnemyTypes(1.5, 150, 20));
            }

            if (remainingPoints >= 30) {
                possibleTypes.add(new EnemyTypes(1.0, 200, 30));
            }

            if (possibleTypes.isEmpty()) {
                break;
            }

            EnemyTypes selectedType = possibleTypes.get(rand.nextInt(possibleTypes.size()));
            enemyList.add(selectedType);
            remainingPoints -= selectedType.getPoints();
        }

        return enemyList;

    }
}
