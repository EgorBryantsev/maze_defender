import javax.swing.*;
import java.awt.*;

public class MazeDefenderGame {
    public static void main(String[] args) {
        JFrame window = new JFrame("Tower Defense Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(1000, 600); // Increased width for HUD

        GamePanel gamePanel = new GamePanel();

        window.setLayout(new BorderLayout());
        window.add(gamePanel, BorderLayout.CENTER);

        window.setVisible(true);
    }
}
