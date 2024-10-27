import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class GameState {

    private static final int INITIAL_LIVES = 3;  // or whatever your starting value is
    private static final int INITIAL_MONEY = 300; // or whatever your starting value is
    
    public int lives;
    public static int money;
    
    public void reset() {
        lives = INITIAL_LIVES;
        money = INITIAL_MONEY;
    }
    public void draw(Graphics2D g){
        // Set font for the money text
        g.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        
        // Calculate the width of the money text
        String moneyText = "Money: " + money;
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(moneyText);

        // Set position in the top-right corner with dynamic width based on text
        int x = GamePanel.panelWidth - textWidth - 10; // Right alignment with padding
        int y = 10; // Top margin
        
        // Draw the money display background with dynamic width based on text
        g.setColor(Color.BLACK);
        g.fillRect(x, y, textWidth + 10, 40);  // Add padding to the text width
        
        // Draw the money text
        g.setColor(Color.GREEN);
        g.drawString(moneyText, x + 5, y + 30);

        // Draw Lives
        String livesText = "Lives: " + lives;
        textWidth = fm.stringWidth(livesText);
        x = GamePanel.panelWidth - textWidth - 10; // Right alignment with padding
        y = 50; // Position below the money display

        // Draw the lives display background
        g.setColor(Color.BLACK);
        g.fillRect(x, y, textWidth + 10, 40);  // Add padding

        // Draw the lives text
        g.setColor(Color.RED);
        g.drawString(livesText, x + 5, y + 30);
    }
}
