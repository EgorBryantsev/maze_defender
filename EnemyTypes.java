import java.awt.Color;

// The different kind of enemies with different stats
public record EnemyTypes(double speed, int hp, int points, double money, int fromRound, Color color, double sizeRatio) {

    public static final EnemyTypes BASIC = new EnemyTypes(2.0, 100, 10, 5, 0, Color.RED, 0.8);
    public static final EnemyTypes FAST = new EnemyTypes(3.5, 50, 15, 5, 0, Color.ORANGE, 0.6);
    public static final EnemyTypes TANK = new EnemyTypes(1.5, 300, 30, 7.5, 5, Color.YELLOW, 1.0);
    public static final EnemyTypes RUNNER = new EnemyTypes(5.0, 75, 20, 10, 4, Color.GREEN, 0.4);
    public static final EnemyTypes FLYER = new EnemyTypes(7.0, 20, 25, 7.5, 6, Color.PINK, 0.2);
    public static final EnemyTypes BOSS = new EnemyTypes(1.0, 5000, 250, 100, 11, Color.BLACK, 1.2);
    public static final EnemyTypes WEAK = new EnemyTypes(2.75, 15, 5, 1, 0, Color.BLUE, 0.5);
    public static final EnemyTypes ROCKET = new EnemyTypes(10, 100, 30, 10, 8, Color.LIGHT_GRAY, 0.2);
    public static final EnemyTypes MONSTER = new EnemyTypes(8, 500, 30, 15, 9, Color.DARK_GRAY, 0.4);
    public static final EnemyTypes BULLET = new EnemyTypes(17.5,1000, 150, 20, 13, Color.CYAN, 0.3);
 
}