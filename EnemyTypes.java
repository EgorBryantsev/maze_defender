public class EnemyTypes {
    private final double speed;
    private final int hp;
    private final int points;

    public EnemyTypes(double speed, int hp, int points) {
        this.speed = speed;
        this.hp = hp;
        this.points = points;
    }

    public double getSpeed() {
        return speed;
    }

    public int getHp() {
        return hp;
    }

    public int getPoints() {
        return points;
    }
}
