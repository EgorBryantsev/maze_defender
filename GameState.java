public class GameState {
    public int money = 500;
    public int lives = 20;
    public int currentWave = 1;
    private Wave currentWaveInstance;
    private int basePoints = 100;
    private boolean victory = false;

    public GameState() {
        // Initially, the PathFinder will be provided when starting the first wave
    }

    public void startNextWave(Pathfinder pathfinder) {
        currentWaveInstance = new Wave(currentWave, basePoints, pathfinder);
    }

    public void update(long currentTime, GamePanel gamePanel) {
        if (currentWaveInstance == null) {
            startNextWave(gamePanel.pathfinder);
        }

        Enemy newEnemy = currentWaveInstance.spawnEnemyIfReady(currentTime);
        if (newEnemy != null) {
            gamePanel.enemies.add(newEnemy);
        }

        if (currentWaveInstance.isWaveComplete() && gamePanel.enemies.isEmpty()) {
            if (currentWave >= 10) {
                victory = true;
            } else {
                currentWave++;
                startNextWave(gamePanel.pathfinder);
                money += 200;
            }
        }
    }

    public boolean isVictory() {
        return victory;
    }
}
