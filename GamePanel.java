import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer; // Correct Timer import

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final int DELAY = 16; // Approximately 60 FPS
    private Timer gameTimer;  // javax.swing.Timer
    private Maze maze;
    public Pathfinder pathfinder;
    public GameState gameState;
    public List<Enemy> enemies;
    private List<Tower> towers;
    public List<Projectile> projectiles;
    private int panelWidth;
    private int panelHeight;
    public static int calculatedTileSize;
    public static int xOffset;
    public static int yOffset;
    private GameStateEnum currentState;

    public GamePanel() {
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseListener(new MouseHandler());

        maze = new Maze();
        pathfinder = new Pathfinder(maze);
        gameState = new GameState();
        enemies = new ArrayList<>();
        towers = new ArrayList<>();
        projectiles = new ArrayList<>();
        currentState = GameStateEnum.MENU;

        gameTimer = new Timer(DELAY, this);  // Correct Timer usage
    }

    private void startGame() {
        currentState = GameStateEnum.PLAYING;
        gameTimer.start();
    }

    private void updateSize() {
        panelWidth = (int) (getWidth() * 0.8);
        panelHeight = (int) (getHeight() * 0.8);
        calculatedTileSize = Math.min(panelWidth / Maze.COLS, panelHeight / Maze.ROWS);

        xOffset = (getWidth() - (Maze.COLS * calculatedTileSize)) / 2;
        yOffset = (getHeight() - (Maze.ROWS * calculatedTileSize)) / 2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateSize();

        switch (currentState) {
            case MENU:
                drawMenu(g);
                break;
            case PLAYING:
                drawGame(g);
                break;
            case PAUSED:
                drawGame(g);
                drawPausedOverlay(g);
                break;
            case GAME_OVER:
                drawGameOver(g);
                break;
            case VICTORY:
                drawVictory(g);
                break;
        }
    }

    private void drawMenu(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Tower Defense Game", getWidth() / 2 - 200, getHeight() / 2 - 50);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Press ENTER to Start", getWidth() / 2 - 100, getHeight() / 2);
    }

    private void drawGame(Graphics g) {
        // Draw Maze
        for (int row = 0; row < Maze.ROWS; row++) {
            for (int col = 0; col < Maze.COLS; col++) {
                int cell = maze.getCell(row, col);
                switch (cell) {
                    case Maze.WALL:
                        g.setColor(Color.BLACK);
                        break;
                    case Maze.PATH:
                        g.setColor(Color.WHITE);
                        break;
                    case Maze.START:
                        g.setColor(Color.GREEN);
                        break;
                    case Maze.END:
                        g.setColor(Color.RED);
                        break;
                    case Maze.MAIN_BUILDING:
                        g.setColor(Color.BLUE);
                        break;
                    default:
                        g.setColor(Color.PINK);
                        break;
                }
                g.fillRect(xOffset + col * calculatedTileSize, yOffset + row * calculatedTileSize, calculatedTileSize, calculatedTileSize);
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(xOffset + col * calculatedTileSize, yOffset + row * calculatedTileSize, calculatedTileSize, calculatedTileSize);
            }
        }

        // Draw Towers
        for (Tower tower : towers) {
            tower.draw(g, calculatedTileSize, xOffset, yOffset);
        }

        // Draw Enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g, calculatedTileSize, xOffset, yOffset);
        }

        // Draw Projectiles
        for (Projectile projectile : projectiles) {
            projectile.draw(g);
        }

        // Draw HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Money: $" + gameState.money, 10, 20);
        g.drawString("Lives: " + gameState.lives, 10, 40);
        g.drawString("Wave: " + gameState.currentWave, 10, 60);
    }

    private void drawPausedOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Paused", getWidth() / 2 - 80, getHeight() / 2);
    }

    private void drawGameOver(Graphics g) {
        drawGame(g);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Game Over", getWidth() / 2 - 100, getHeight() / 2);
    }

    private void drawVictory(Graphics g) {
        drawGame(g);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("You Win!", getWidth() / 2 - 80, getHeight() / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState == GameStateEnum.PLAYING) {
            updateGameState();
            repaint();
        }
    }

    private void updateGameState() {
        double deltaTime = DELAY / 1000.0; // Convert milliseconds to seconds
        long currentTime = System.nanoTime();

        gameState.update(currentTime, this);

        // Update Towers
        for (Tower tower : towers) {
            tower.update(currentTime);
        }

        // Update Enemies
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.move(deltaTime);
            if (!enemy.isAlive()) {
                gameState.money += enemy.getPoints();
                enemyIterator.remove();
            } else if (enemy.hasReachedEnd()) {
                gameState.lives--;
                enemyIterator.remove();
                if (gameState.lives <= 0) {
                    currentState = GameStateEnum.GAME_OVER;
                    gameTimer.stop();
                }
            }
        }

        // Update Projectiles
        Iterator<Projectile> projIterator = projectiles.iterator();
        while (projIterator.hasNext()) {
            Projectile projectile = projIterator.next();
            projectile.move();
            boolean hit = false;
            for (Enemy enemy : enemies) {
                if (projectile.hitTarget(enemy)) {
                    enemy.takeDamage(projectile.getDamage());
                    hit = true;
                    break;
                }
            }
            if (hit) {
                projIterator.remove();
            }
        }

        // Check for victory condition
        if (gameState.isVictory()) {
            currentState = GameStateEnum.VICTORY;
            gameTimer.stop();
        }
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (currentState == GameStateEnum.PLAYING) {
                int col = (e.getX() - xOffset) / calculatedTileSize;
                int row = (e.getY() - yOffset) / calculatedTileSize;

                if (isValidPlacement(row, col)) {
                    if (gameState.money >= Tower.BASE_COST) {
                        Tower tower = new Tower(GamePanel.this, row, col);
                        towers.add(tower);
                        gameState.money -= Tower.BASE_COST;
                        maze.setCell(row, col, Maze.BUILDING);
                        repaint();
                    }
                }
            }
        }
    }

    private boolean isValidPlacement(int row, int col) {
        return row >= 0 && col >= 0 &&
                row < Maze.ROWS && col < Maze.COLS &&
                maze.getCell(row, col) == Maze.BUILDING;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (currentState == GameStateEnum.MENU && e.getKeyCode() == KeyEvent.VK_ENTER) {
            startGame();
        } else if (currentState == GameStateEnum.PLAYING && e.getKeyCode() == KeyEvent.VK_P) {
            currentState = GameStateEnum.PAUSED;
            gameTimer.stop();
        } else if (currentState == GameStateEnum.PAUSED && e.getKeyCode() == KeyEvent.VK_P) {
            currentState = GameStateEnum.PLAYING;
            gameTimer.start();
        } else if (currentState == GameStateEnum.GAME_OVER && e.getKeyCode() == KeyEvent.VK_ENTER) {
            // Restart the game
            maze = new Maze();
            pathfinder = new Pathfinder(maze);
            gameState = new GameState();
            enemies.clear();
            towers.clear();
            projectiles.clear();
            currentState = GameStateEnum.PLAYING;
            gameTimer.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public Pathfinder getPathfinder() {
        return pathfinder;
    }
}
