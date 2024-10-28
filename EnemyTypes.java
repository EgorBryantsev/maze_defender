import java.awt.Color;

// The different kind of enemies with different stats
public record EnemyTypes(double speed, int hp, int points, int money, int fromRound, Color color, double sizeRatio) {

    public static final EnemyTypes BASIC = new EnemyTypes(2.0, 100, 10, 5, 0, Color.RED, 0.8);
    public static final EnemyTypes FAST = new EnemyTypes(3.5, 50, 15, 5, 0, Color.ORANGE, 0.6);
    public static final EnemyTypes TANK = new EnemyTypes(1.5, 300, 30, 10, 3, Color.YELLOW, 1.0);
    public static final EnemyTypes FLYER = new EnemyTypes(5.0, 75, 20, 15, 2, Color.GREEN, 0.4);
    public static final EnemyTypes ROCKET = new EnemyTypes(7.0, 5, 25, 10, 4, Color.PINK, 0.2);
    public static final EnemyTypes BOSS = new EnemyTypes(1.0, 2500, 150, 100, 8, Color.BLACK, 1.2);

}
