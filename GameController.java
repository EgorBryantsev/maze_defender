import javax.swing.Timer;

public class GameController {
    private Timer gameTimer;
    private final int DELAY = 16; // Approximately 60 FPS

    public GameController() {
        gameTimer = new Timer(DELAY, e -> gameUpdate());
        gameTimer.start();
    }

    private void gameUpdate() {
    }
}