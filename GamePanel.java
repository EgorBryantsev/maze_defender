import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel {
    private final int tileSize = 25; // Adjust tile size as needed
    private final Maze maze;
    public static int panelWidth;
    public static int panelHeight;
    public int calculatedTileSize;
    private final List<Enemy> enemies; // List to manage active enemies
    private final Pathfinder pathfinder; // To get the path
    private final int DELAY = 16;
    private int xOffset;
    private int yOffset;
    private boolean initialEnemiesSpawned = false;

    // Add Clock instance
    private final Clock gameClock;
    private final GameState gameState;

    // Constructor
    public GamePanel() {
        this.setBackground(Color.DARK_GRAY);
        maze = new Maze(); // Initialize the maze

        gameState = new GameState();

        // Update the game state and clock
        Timer gameTimer = new Timer(DELAY, e -> {
            updateGameState();  // Update the game state and clock
        });

        // Start the timer
        gameTimer.start();

        // Initialize the clock at position (10, 10), width 100, height 40, and alarm time of 30 seconds
        gameClock = new Clock(10, 10, 100, 40, 10);
        gameClock.start();

        //enemies
        enemies = new ArrayList<>();
        pathfinder = new Pathfinder(maze);

        updateSize();

        // Add mouse listener for tower clicks
        Tower tower = new Tower(this);
        this.addMouseListener(tower.new BuildingClicked());

        // Add mouse listener for clock clicks (keep this if you want both click and spacebar reset)
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameClock.tijdIsOm) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    int[] bounds = gameClock.getBounds();
                    if (mouseX >= bounds[0] && mouseX <= (bounds[0] + bounds[2]) &&
                        mouseY >= bounds[1] && mouseY <= (bounds[1] + bounds[3])) {
                        gameClock.reset();
                        gameClock.start();
                    }
                }
            }
        });

        // Add key listener for spacebar to start a new wave
        this.setFocusable(true); // Make sure the panel can receive key events
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && gameClock.tijdIsOm) {
                    // Reset and start a new wave when spacebar is pressed
                    gameClock.reset();
                    gameClock.start();
                }
            }
        });

        Image buidlingUpgrade = new ImageIcon("upgrade-svgrepo-com.png").getImage();
    }

    // Method to spawn an enemy
    public void spawnEnemy(List<int[]> path) {
        double speed = 2.0;
        int hp = 100;
        int points = 3;
        Enemy enemy = new Enemy(path, speed, hp, points);
        enemies.add(enemy);
    }

    /**
     * Update the panel size based on the current window size.
     */
    private void updateSize() {
        panelWidth = (int) (getWidth() * 0.9);  // 90% of window width
        panelHeight = (int) (getHeight() * 0.8);  // 90% of window height  
        calculatedTileSize = Math.min(panelWidth / Maze.COLS, panelHeight / Maze.ROWS);  // Adjust tile size

        this.xOffset = (getWidth() - panelWidth) / 2;
        this.yOffset = (getHeight() - panelHeight) / 2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateSize();

        // Render the maze
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
                        g.setColor(Color.YELLOW);
                        break;
                    case Maze.BUILDING:
                        g.setColor(Color.GRAY);
                        break;
                    default:
                        switch (Maze.maze[row][col]) {
                            case 5:
                                g.setColor(Color.CYAN);
                                Tower.towerLevel++;
                                break;
                            case 6:
                                g.setColor(Color.YELLOW);
                                Tower.towerLevel++;
                                break;
                            case 9:
                                Maze.maze[row][col] = 8;
                            case 7:
                                g.setColor(Color.ORANGE);
                                Tower.towerLevel++;
                                break;
                            case 8:
                                g.setColor(Color.RED);
                                Tower.towerLevel++;
                                break;
                            default:
                                break;
                        }
                        break;
                }
                g.fillRect(xOffset + col * calculatedTileSize, yOffset + row * calculatedTileSize, calculatedTileSize, calculatedTileSize);
                g.setColor(Color.LIGHT_GRAY);  // Grid lines
                g.drawRect(xOffset + col * calculatedTileSize, yOffset + row * calculatedTileSize, calculatedTileSize, calculatedTileSize);
            }
        }

        // Draw Enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g, calculatedTileSize, xOffset, yOffset);
        }

        // Draw the clock
        Graphics2D g2d = (Graphics2D) g;
        gameClock.teken(g2d);  // Call the Clock's teken method to draw it
        //gameState.teken(g2d);

    }

    /**
     * This method should be called repeatedly, perhaps in the game loop.
     * It updates the game state and repaints the panel.
     */
    public void updateGameState() {
        updateSize();
        gameClock.beweeg(1.0f);  // Update the clock's state
        double deltaTime = DELAY / 1000.0; // Convert milliseconds to seconds

        if (!initialEnemiesSpawned && getWidth() > 0 && getHeight() > 0) {
            // Now we can safely spawn the enemies
            int[][] pathArray = pathfinder.findPath();
            if (pathArray != null) {
                // Convert pathArray to List<int[]>
                List<int[]> path = new ArrayList<>(Arrays.asList(pathArray));
                // Spawn an enemy for demonstration
                spawnEnemy(path);
                initialEnemiesSpawned = true;
            }
        }

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.move(deltaTime);

            if (!enemy.isAlive()) {
                // Enemy is dead, award points
                gameState.money += enemy.getPoints();
                iterator.remove();
            } else if (enemy.hasReachedEnd()) {
                // Enemy reached the end, handle accordingly
                // For example, reduce player lives or money
                System.out.println("Enemy reached the end!");
                iterator.remove();
            }
        }

        repaint();  // Trigger a repaint to reflect changes
    }
}
