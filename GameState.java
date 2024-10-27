import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class GameState {
    public static int money = 300;
    public int lives = 5;

    private static final int PADDING = 10;
    private static final int ITEM_HEIGHT = 30;
    private static final int ITEM_SPACING = 10;
    private static final Font FONT = new Font(Font.SERIF, Font.BOLD, 18);

    private int numberOfItems = 0;

    public void draw(Graphics2D g) {
        numberOfItems = 0;
        g.setFont(FONT);
        FontMetrics fm = g.getFontMetrics();

        int x = PADDING;
        int y = PADDING;

        drawInfoBox(g, "Money: " + money, x, y, fm);
        numberOfItems++;
        y += ITEM_HEIGHT + ITEM_SPACING;

        drawInfoBox(g, "Lives: " + lives, x, y, fm);
        numberOfItems++;
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
