import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Round {
    private final int totalPoints;
    public final int spawnFrequency;
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

            if (remainingPoints >= EnemyTypes.TANK.points() && GamePanel.currentRoundNumber >= EnemyTypes.TANK.fromRound()) {
                possibleTypes.add(EnemyTypes.TANK);
            }
            if (remainingPoints >= EnemyTypes.FAST.points() && GamePanel.currentRoundNumber >= EnemyTypes.FAST.fromRound()) {
                possibleTypes.add(EnemyTypes.FAST);
            }
            if (remainingPoints >= EnemyTypes.BASIC.points() && GamePanel.currentRoundNumber >= EnemyTypes.BASIC.fromRound()) {
                possibleTypes.add(EnemyTypes.BASIC);
            }

            if (remainingPoints >= EnemyTypes.FLYER.points() && GamePanel.currentRoundNumber >= EnemyTypes.FLYER.fromRound()) {
                possibleTypes.add(EnemyTypes.FLYER);
            }

            if (remainingPoints >= EnemyTypes.ROCKET.points() && GamePanel.currentRoundNumber >= EnemyTypes.ROCKET.fromRound()) {
                possibleTypes.add(EnemyTypes.ROCKET);
            }

            if (remainingPoints >= EnemyTypes.BOSS.points() && GamePanel.currentRoundNumber >= EnemyTypes.BOSS.fromRound()) {
                possibleTypes.add(EnemyTypes.BOSS);
            }

            if (remainingPoints >= EnemyTypes.WEAK.points() && GamePanel.currentRoundNumber >= EnemyTypes.WEAK.fromRound()) {
                possibleTypes.add(EnemyTypes.WEAK);
            }

            if (remainingPoints >= EnemyTypes.RUNNER.points() && GamePanel.currentRoundNumber >= EnemyTypes.RUNNER.fromRound()) {
                possibleTypes.add(EnemyTypes.RUNNER);
            }

            if (remainingPoints >= EnemyTypes.MONSTER.points() && GamePanel.currentRoundNumber >= EnemyTypes.MONSTER.fromRound()) {
                possibleTypes.add(EnemyTypes.MONSTER);
            }

            if (remainingPoints >= EnemyTypes.BULLET.points() && GamePanel.currentRoundNumber >= EnemyTypes.BULLET.fromRound()) {
                possibleTypes.add(EnemyTypes.BULLET);
            }

            if (possibleTypes.isEmpty()) {
                break; 
            }

            EnemyTypes selectedType = possibleTypes.get(rand.nextInt(possibleTypes.size()));
            enemyList.add(selectedType);
            remainingPoints -= selectedType.points();
        }

        return enemyList;

    }
}
