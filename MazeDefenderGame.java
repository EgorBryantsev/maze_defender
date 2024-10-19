    import java.awt.Color;
    import java.awt.Graphics;
    import javax.swing.*;

    public class MazeDefenderGame {
        public static void main(String[] args) {
            JFrame window = new JFrame("Tower Defense Game");

            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(800,600);

            GamePanel gamePanel = new GamePanel();
            window.add(gamePanel);

            window.setVisible(true);
            // gamePanel.startGame();
        }
    }
