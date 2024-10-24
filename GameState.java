import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class GameState {

    public int money = 1000;

    private GamePanel gamePanel;

   public void teken(Graphics2D g){
       // Draw the clock background
       g.setColor(Color.BLACK);
       g.fillRect(gamePanel.panelWidth, gamePanel.panelHeight, 1, 1);
       
       // Set font for the melding text
       g.setFont(new Font(Font.SERIF, Font.ITALIC, 18));
       
       // Draw the melding text
       g.setColor(Color.RED);
       g.drawString("" + money, gamePanel.panelWidth + 2, gamePanel.panelHeight + 3);
   }
}
