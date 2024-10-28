import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel {
    private final Maze maze;
    public static int panelWidth;
    public static int panelHeight;
    public static boolean playable = true;
    public int calculatedTileSize;
    private static List<Enemy> enemies;
    private Pathfinder pathfinder;
    private final int DELAY = 16;
    private int xOffset;
    private int yOffset;
    private Round currentRound;
    public static int currentRoundNumber;
    private Timer spawnTimer;
    private int enemiesSpawned;
    private List<Tower> towers = new ArrayList<>();
    private boolean roundComplete = false;
    public static int nextRound;

    private BufferedImage stoneWallTexture;
    private BufferedImage floorTileTexture;

    private Clock gameClock;
    private final GameState gameState;

    private int confirmX, confirmY, confirmCost;
    private boolean showConfirm = false;

    private static final int PADDING = 10;
    private static final int ITEM_HEIGHT = 30;
    private static final int ITEM_SPACING = 10;

    private List<Projectile> allProjectiles = new ArrayList<>();

    // Constructor
    public GamePanel() {
        this.setBackground(Color.DARK_GRAY);
        
        // Initialize final fields first
        this.maze = new Maze();
        this.gameState = new GameState();
        int alarmTime = 60;
        gameClock = new Clock(alarmTime);
        gameClock.start(); // 60 seconds alarm time

        loadTextures();

        // Now call reset for other initializations
        resetGame();
        setupEventListeners();
    }

    private void loadTextures() {
        try {
            // Use relative paths since images are in the same package
            stoneWallTexture = ImageIO.read(Objects.requireNonNull(getClass().getResource("stonewall.png")));
            floorTileTexture = ImageIO.read(Objects.requireNonNull(getClass().getResource("grass_01.png")));
            System.out.println("Textures loaded successfully.");
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            // Fallback to solid colors if textures fail to load
            stoneWallTexture = null;
            floorTileTexture = null;
        }
    }

    public void resetGame() {

        GamePanel.playable = true;
        GamePanel.enemies = new ArrayList<>();
        towers = new ArrayList<>();

        GamePanel.currentRoundNumber = 1;
        nextRound = currentRoundNumber;
        this.enemiesSpawned = 0;
        if (this.spawnTimer != null && this.spawnTimer.isRunning()) {
            this.spawnTimer.stop();
        }

        this.gameState.reset();
        this.gameClock.reset();
        this.maze.regenerateMaze();

        this.showConfirm = false;
        this.confirmX = 0;
        this.confirmY = 0;
        this.confirmCost = 0;

        boolean[][] visited = new boolean[Maze.ROWS][Maze.COLS];
        for (int row = 0; row < Maze.ROWS - 1; row++) {
            for (int col = 0; col < Maze.COLS - 1; col++) {
                // Skip if this tile is already part of a processed building
                if (visited[row][col]) continue;

                if (isBuilding(row, col)) {
                    // Add a new Tower at the top-left corner of the building
                    Tower newTower = new Tower(this, row, col);
                    towers.add(newTower);

                    // Mark the 2x2 building area as visited
                    visited[row][col] = true;
                    visited[row + 1][col] = true;
                    visited[row][col + 1] = true;
                    visited[row + 1][col + 1] = true;
                }
            }
        }

        updateSize();
        
        // Start new round
        startNewRound(currentRoundNumber);
    }
    
    private void setupEventListeners() {
        Timer gameTimer = new Timer(DELAY, e -> {
            updateGameState();
        });
        gameTimer.start();

        enemies = new ArrayList<>();
        pathfinder = new Pathfinder(maze);

        updateSize();

        currentRoundNumber = 1;
        nextRound = currentRoundNumber;
        startNewRound(currentRoundNumber);
        spawnTimer = new Timer(0, null);

        // Add mouse listener for tower clicks
        Tower tower = new Tower(this, -1, -1);
        this.addMouseListener(tower.new BuildingClicked());

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameClock.timeOver) {
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

        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (roundComplete) {
                        // Start the next round
                        roundComplete = false;
                        currentRoundNumber++;
                        startNewRound(currentRoundNumber);
                        gameClock.reset();
                        gameClock.start();
                    } else if (gameClock.timeOver) {
                        // Existing functionality if needed
                        gameClock.reset();
                        gameClock.start();
                    }
                }
            }
        });
    }

    // Method to spawn an enemy
    public static void spawnEnemy(int[][] path, EnemyTypes type) {
        Enemy enemy = new Enemy(path, type);
        enemies.add(enemy);
    }

    public Tower getTower(int row, int col) {
        for (Tower tower : towers) {
            if (tower.getRow() == row && tower.getCol() == col) {
                return tower;
            }
        }
        return null;
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

    public static List<Enemy> getEnemies() {
        return enemies;
    }
    
    public int getXOffset() {
        return xOffset;
    }
    
    public int getYOffset() {
        return yOffset;
    }

    public void addProjectile(Projectile p) {
        allProjectiles.add(p);
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
                BufferedImage texture = null;
                if (Maze.maze[row][col] >= 5) {
                    Tower tower = getTower(row, col);
                    if (tower != null) {
                        tower.draw(g);
                    }
                }
                switch (cell) {
                    case Maze.WALL:
                        if (stoneWallTexture != null) {
                            texture = stoneWallTexture;
                        }
                        break;
                    case Maze.PATH:
                        if (floorTileTexture != null) {
                            texture = floorTileTexture;
                        }
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
                                g2d.setColor(Color.ORANGE);
                                break;
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

                if (texture != null) {
                    // Draw the texture scaled to the tile size
                    g2d.drawImage(texture,
                            xOffset + col * calculatedTileSize,
                            yOffset + row * calculatedTileSize,
                            calculatedTileSize,
                            calculatedTileSize,
                            this);
                }else {
                    // Fill the rectangle with color if no texture
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
                            // Set color based on other cell types if necessary
                            break;
                    }
                    g.fillRect(xOffset + col * calculatedTileSize,
                            yOffset + row * calculatedTileSize,
                            calculatedTileSize,
                            calculatedTileSize);
                }
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

        //draw projectiles
        for (Projectile p : allProjectiles) {
            p.draw(g);
        }

        // Draw Towers
        for (Tower tower : towers) {
            tower.draw(g);
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
                GameState.money += enemy.getMoney() * 5;
                iterator.remove();
            } else if (enemy.hasReachedEnd()) {
                System.out.println("Enemy reached the end!");
                gameState.lives -= 1;
                iterator.remove();

                if (gameState.lives <= 0) {
                    gameOver();
                    return;
                }
            }
        }

        for (Tower tower : towers) {
            tower.update();
        }

        // Update Projectiles
        Iterator<Projectile> projectileIterator = allProjectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile p = projectileIterator.next();
            p.move();

            if (p.isExpired()) {
                projectileIterator.remove();
                continue;
            }

            // Check collision with enemies
            for (Enemy enemy : enemies) {
                if (isHitting(p, enemy)) {
                    enemy.takeDamage(p.damage());
                    projectileIterator.remove();
                    break;
                }
            }
        }

        repaint();  // Trigger a repaint to reflect changes
    }

    private boolean isHitting(Projectile p, Enemy e) {
        double[] pos = e.getPosition(calculatedTileSize, xOffset, yOffset);
        double dx = p.x - pos[0];
        double dy = p.y - pos[1];
        return (dx * dx + dy * dy) < (20 * 20);
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

    public void addTower(Tower tower) {
        towers.add(tower);
    }

    public List<Tower> getTowers() {
        return towers;
    }

    private boolean isRoundActive() {
        return spawnTimer != null && spawnTimer.isRunning();
    }

    private void checkRoundCompletion() {
        // Check if all enemies are defeated
        if (!isRoundActive() && enemies.isEmpty()&& !roundComplete) {
            // Start the next round after a short delay (e.g., 3 seconds)
            roundComplete = true;
            System.out.println("Round " + currentRoundNumber + " completed. Press SPACE to start the next round.");
            GameState.money += currentRoundNumber * 100;

            gameClock.setPrompt("New Wave: Press Space");
            // Optionally, you can display a message on the UI
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

    public boolean isBuilding(int row, int col) {
        return row >= 0 && col >= 0 &&
                row < Maze.ROWS - 1 && col < Maze.COLS - 1 &&
                Maze.maze[row][col] >= 4 &&
                Maze.maze[row][col] == Maze.maze[row + 1][col] &&
                Maze.maze[row][col] == Maze.maze[row][col + 1] &&
                Maze.maze[row + 1][col + 1] == Maze.maze[row][col];
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
