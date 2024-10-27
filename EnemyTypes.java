import java.awt.Color;

public record EnemyTypes(double speed, int hp, int points, Color color, int size) {

    public static final EnemyTypes BASIC = new EnemyTypes(1.0, 100, 10, Color.RED, 20);
    public static final EnemyTypes FAST = new EnemyTypes(2.0, 50, 15, Color.BLUE, 15);
    public static final EnemyTypes TANK = new EnemyTypes(0.5, 300, 30, Color.DARK_GRAY, 30);
    public static final EnemyTypes FLYER = new EnemyTypes(3.0, 75, 20, Color.MAGENTA, 18);

}
