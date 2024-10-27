import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

public class GamePanel extends JPanel {
    private final Maze maze;
    public static int panelWidth;
    public static int panelHeight;
    public static boolean playable = true;
    public int calculatedTileSize;
    private static List<Enemy> enemies; // List to manage active enemies
    private final Pathfinder pathfinder; // To get the path
    private final int DELAY = 16;
    private int xOffset;
    private int yOffset;
    private Round currentRound;
    private int currentRoundNumber;
    private Timer spawnTimer;
    private int enemiesSpawned;

    // Add Clock instance
    private final Clock gameClock;
    private final GameState gameState;

    private int confirmX, confirmY, confirmCost;
    private boolean showConfirm = false; // Track if the confirmation message is visible

    private static final int PADDING = 10;
    private static final int ITEM_HEIGHT = 30;
    private static final int ITEM_SPACING = 10;

    // Constructor
    public GamePanel() {
        this.setBackground(Color.DARK_GRAY);

        maze = new Maze(); // Initialize the maze

        currentRoundNumber = 1;
        startNewRound(currentRoundNumber);

        gameState = new GameState();

        Timer gameTimer = new Timer(DELAY, e -> {
            updateGameState();
        });
        gameTimer.start();

        // Initialize the clock at position (10, 10), width 100, height 40, and alarm time of 30 seconds
        int alarmTime = 60;
        gameClock = new Clock(alarmTime);
        gameClock.start();

        enemies = new ArrayList<>();
        pathfinder = new Pathfinder(maze);

        updateSize();

        currentRoundNumber = 1;
        startNewRound(currentRoundNumber);
        spawnTimer = new Timer(0, null);

        // Add mouse listener for tower clicks
        Tower tower = new Tower(this);
        this.addMouseListener(tower.new BuildingClicked());

        // Add mouse listener for clock clicks (keep this if you want both click and spacebar reset)
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameClock.timeOver) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    int[] bounds = gameClock.getBounds(PADDING, ITEM_HEIGHT * 2 + ITEM_SPACING * 2);
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
                if (e.getKeyCode() == KeyEvent.VK_SPACE && gameClock.timeOver) {
                    // Reset and start a new wave when spacebar is pressed
                    gameClock.reset();
                    gameClock.start();
                }
            }
        });

        Image buidlingUpgrade = new ImageIcon("upgrade-svgrepo-com.png").getImage();
    }

    // Method to spawn an enemy
    public static void spawnEnemy(int[][] path, EnemyTypes type) {
        Enemy enemy = new Enemy(path, type);
        enemies.add(enemy);
    }

    public Tower getTower(int row, int col) {
        for (int x = 0; x < Maze.ROWS; x ++) {
            for (int y = 0; y < Maze.COLS; y++) {
                if (Maze.maze[x][y] >= 5) {
                    row = x;
                    col = y;
                }
            }
        }
        return null; // Placeholder, replace with actual fetching logic
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

        Graphics2D g2d = (Graphics2D) g;

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
                                break;
                            case 6:
                                g.setColor(Color.YELLOW);
                                break;
                            case 9:
                                Maze.maze[row][col] = 8;
                            case 7:
                                g.setColor(Color.ORANGE);
                                break;
                            case 8:
                                g.setColor(Color.RED);
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

        if (showConfirm) {
            g.setColor(Color.BLACK);
            g.fillRect(confirmX, confirmY - 20, 100, 20); // Background for message
            g.setColor(Color.RED);
            g.drawString("Upgrade cost: $" + confirmCost, confirmX + 5, confirmY - 5);
        }

        // Draw Enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g, calculatedTileSize, xOffset, yOffset);
        }

        // Draw the GameState items (Money and Lives)
        gameState.draw(g2d);

        // clock
        int clockX = PADDING;
        int clockY = PADDING + (ITEM_HEIGHT + ITEM_SPACING) * gameState.getNumberOfItems();
        gameClock.draw(g2d, clockX, clockY);

    }

    public void confirmUpgrade() {
        showConfirm = false;
    }

    /**
     * This method should be called repeatedly, perhaps in the game loop.
     * It updates the game state and repaints the panel.
     */
    public void updateGameState() {
        updateSize();
        gameClock.move(1.0f);  // Update the clock's state
        double deltaTime = DELAY / 1000.0; // Convert milliseconds to seconds

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.move(deltaTime);

            if (!enemy.isAlive()) {
                // Enemy is dead, award points
                GameState.money += enemy.getPoints();
                iterator.remove();
            } else if (enemy.hasReachedEnd()) {
                // Enemy reached the end, handle accordingly
                // For example, reduce player lives or money
                System.out.println("Enemy reached the end!");
                gameState.lives -= 1;
                iterator.remove();

                if (gameState.lives <= 0) {
                    gameOver();
                    return; // Exit early to prevent further updates
                }
            }
        }

        repaint();  // Trigger a repaint to reflect changes
    }

    private void startNewRound(int roundNumber) {
        // Define how totalPoints and spawnFrequency scale with roundNumber
        int totalPoints = (int) (100 * Math.pow(1.5, roundNumber - 1));
        currentRound = new Round(totalPoints, 2000);
        enemiesSpawned = 0;
        boolean isRoundActive = true;

        System.out.println("Round " + roundNumber + " started with " + totalPoints + " points.");

        spawnTimer = new Timer(currentRound.getSpawnFrequency(), e -> spawnNextEnemy());
        spawnTimer.start();
    }

    private void spawnNextEnemy() {
        if (enemiesSpawned < currentRound.getEnemiesToSpawn().size()) {
            EnemyTypes enemyType = currentRound.getEnemiesToSpawn().get(enemiesSpawned);
            int[][] path = pathfinder.findPath(); // Correctly call the instance method

            if (path != null) {
                spawnEnemy(path, enemyType);
                enemiesSpawned++;
            } else {
                System.out.println("No path found for enemy.");
                playable = false;
            }
        } else {
            // All enemies for this round have been spawned
            spawnTimer.stop();
            checkRoundCompletion();
        }
    }

    private boolean isRoundActive() {
        return spawnTimer != null && spawnTimer.isRunning();
    }

    private void checkRoundCompletion() {
        // Check if all enemies are defeated
        if (!isRoundActive() && enemies.isEmpty()) {
            // Start the next round after a short delay (e.g., 3 seconds)
            Timer delayTimer = new Timer(3000, e -> {
                currentRoundNumber++;
                startNewRound(currentRoundNumber);
                ((Timer) e.getSource()).stop();
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }

    private void gameOver() {
        // Stop the spawn timer
        if (spawnTimer != null && spawnTimer.isRunning()) {
            spawnTimer.stop();
        }

        // Show game over message
        /* JOptionPane.showMessageDialog(this, "Game Over! You have lost all your lives.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0); // Exit the game */
        GamePanel.playable = false;
    }

    public void setConfirmX(int confirmX) {
        this.confirmX = confirmX;
    }

    public void setConfirmY(int confirmY) {
        this.confirmY = confirmY;
    }

    public void setConfirmCost(int confirmCost) {
        this.confirmCost = confirmCost;
    }
}
