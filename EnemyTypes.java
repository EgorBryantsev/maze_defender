import java.awt.Color;

public record EnemyTypes(double speed, int hp, int points, Color color, double sizeRatio) {

    public static final EnemyTypes BASIC = new EnemyTypes(2.0, 100, 10, Color.RED, 0.8);
    public static final EnemyTypes FAST = new EnemyTypes(3.5, 50, 15, Color.ORANGE, 0.6);
    public static final EnemyTypes TANK = new EnemyTypes(1.5, 300, 30, Color.YELLOW, 1.0);
    public static final EnemyTypes FLYER = new EnemyTypes(4.0, 75, 20, Color.GREEN, 0.4);

}
