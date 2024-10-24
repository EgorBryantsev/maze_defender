import java.util.List;

public class Enemy {
    private int x, y; //location
    private int hp; //health
    private double speed;
    private List<Node> path; //for A*
    private int currentPathIndex;
    private int points;

    public Enemy(int startX, int startY, int hp, double speed, int points) {
        this.x = startX;
        this.y = startY;
        this.hp = hp;
        this.speed = speed;
        this.points = points;
        this.currentPathIndex = 0;
    }

    public void setPath(List<Node> path) {
        this.path = path;
    }

    public void move() {
        if (path == null  || currentPathIndex >= path.size()) {
            return;
        }

        Node targetNode =
    }

    public void takeDamage() {
        //enemy kil
    }

    public boolean isAlive() {
        return hp > 0;
    }

    //gettrs

    //settrs
}
