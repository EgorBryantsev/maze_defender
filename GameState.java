import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class GameState {
    private static final int INITIAL_LIVES = 10;  // Amount of lives before game over
    private static final int INITIAL_MONEY = 500; // Amount of money player starts with

    private static final int PADDING = 10;
    private static final int ITEM_HEIGHT = 30;
    private static final int ITEM_SPACING = 10;
    private static final Font FONT = new Font(Font.SERIF, Font.BOLD, 18);
    private final int numberOfItems = 2;

    public int lives;
    public static int money;
    
    public void reset() {
        lives = INITIAL_LIVES;
        money = INITIAL_MONEY;

    }

    public void draw(Graphics2D g){
        g.setFont(FONT);
        
        // Calculate the width of the money text
        String moneyText = "Money: " + money;
        FontMetrics fm = g.getFontMetrics();

        int x = PADDING;
        int y = PADDING;

        drawInfoBox(g, "Money: " + money, x, y, fm);
        y += ITEM_HEIGHT + ITEM_SPACING;

        drawInfoBox(g, "Lives: " + lives, x, y, fm);
        y += ITEM_HEIGHT + ITEM_SPACING;
    }

    private void drawInfoBox(Graphics2D g, String text, int x, int y, FontMetrics fm) {
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(x, y, textWidth + 20, ITEM_HEIGHT, 15, 15); // Rounded corners for aesthetics

        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, textWidth + 20, ITEM_HEIGHT, 15, 15);

        g.setColor(Color.WHITE);
        int textX = x + 10;
        int textY = y + ((ITEM_HEIGHT - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, textX, textY);
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }
}
