import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Tower extends GamePanel {
    public class buildingClicked extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = (e.getX() - (getWidth() - panelWidth) / 2) / tileSize;
            int row = (e.getY() - (getHeight() - panelHeight) / 2) / tileSize;
            
            if (isBuilding(row, col)) {
                buildingNewLevel(row, col);
                repaint();
            }
        }
    }

    private boolean isBuilding(int row, int col) {
        return row < Maze.ROWS - 1 && col < Maze.COLS - 1 &&
        maze[row][col] >= 4 && maze[row][col] == maze[row + 1][col] &&
        maze[row][col] == maze[row][col + 1] && maze[row][col] == maze[row + 1][col + 1];
    }  

    private void buildingNewLevel(int row, int col) {
        int newState = maze[row][col] + 1;
        if (newState > 7) newState = 8;
        
        Maze.maze[row][col] = newState;
        Maze.maze[row + 1][col] = newState;
        Maze.maze[row][col + 1] = newState;
        Maze.maze[row + 1][col + 1] = newState;
    }

}
