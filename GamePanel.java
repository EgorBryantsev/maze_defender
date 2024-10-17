import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {
    private final int gridSize = 10;
    private final int tileSize = 50;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawString("Game Screen", 350, 300);
    }
}
