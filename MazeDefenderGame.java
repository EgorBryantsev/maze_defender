import javax.swing.*;

public class MazeDefenderGame {
    private static JFrame window;
    private static GamePanel gamePanel;

    public static void main(String[] args) {
        // Set up the main window
        window = new JFrame("Tower Defense Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);

        startNewGame();

        // Timer to check if the game should be restarted
        Timer restartTimer = new Timer(1000, e -> {
            if (!GamePanel.playable) {
                restartGame();
            }
        });
        restartTimer.start();
    }

    public static void startNewGame() {
        if (gamePanel != null) {
            gamePanel.resetGame();
        } else {
            gamePanel = new GamePanel();
        }
        
        window.getContentPane().removeAll();
        window.add(gamePanel);
        window.revalidate();
        window.repaint();
        window.setVisible(true);
    }

    // Restart the game
    public static void restartGame() {
        int option = JOptionPane.showConfirmDialog(
            window,
            "Game Over! Do you want to play again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            startNewGame(); // Start a new game if the player chooses "Yes"
        } else {
            System.exit(0); // Close the application if they choose "No"
        }
    }
}
