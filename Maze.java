import java.util.Random;

public class Maze {
    public static final int ROWS = 20;
    public static final int COLS = 40;
    public static final int WALL = 1;
    public static final int PATH = 0;
    public static final int START = 2;
    public static final int END = 3;
    public static final int BUILDING = 4;
    public static int[][] maze;
    public int pathStarts;

    public void regenerateMaze() {
        maze = new int[ROWS][COLS];
        generateMaze();
        placeGraySquares();
        Pathfinder pathChecker = new Pathfinder(this);
        if (pathChecker.findPath() == null) {
            System.out.println("Debug: Invalid maze generated, regenerating...");
            regenerateMaze();  // Recursively try again
        }
    }

    // Constructor
    public Maze() {
        maze = new int[ROWS][COLS];
        generateMaze();
        placeGraySquares();  // These are the spots Towers will be built on
        
    }

    // Generate a maze where initially the entire grid is walls (maze[][] = 1)
    // Carve a random path from the first column to the last column.
    private void generateMaze() {
        // Initialize all tiles to walls
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                maze[row][col] = WALL;
            }
        }

        // Randomly choose a starting point in the first column
        Random rand = new Random();
        int startRow = rand.nextInt(ROWS);
        pathStarts = 4;
        createPath(startRow);
    }

    private void createPath(int startRow) {
        Random rand = new Random();
        for (int x = 1; x <= pathStarts + 1; x ++) {
            if (x > 1) {
                startRow = rand.nextInt(ROWS - 5) + 5;
            }
            int currentRow = startRow;

            // Path to the last column
            for (int col = 1; col < COLS - 1; col++) {
                maze[currentRow][col] = PATH; 
                // Randomly decide to move up, down, or stay in the same row
                int move = rand.nextInt(3);  // 0: up, 1: same, 2: down
                if (move == 0 && currentRow > 0) {
                    currentRow --;  // Move up 1
                    if (maze[currentRow][col - 1] == WALL) {
                        makePath(currentRow, col);
                        if (currentRow > 1){
                            currentRow --;
                            makePath(currentRow, col);
                        }
                    } else if (maze[currentRow][col - 1] == PATH) {
                        currentRow += 1;
                    }
                } else if (move == 2 && currentRow < ROWS - 1) {
                    currentRow ++;  // Move down 1
                    if (maze[currentRow][col - 1] == WALL) {
                        makePath(currentRow, col);
                        if (currentRow < ROWS - 2) {
                            currentRow ++;
                            makePath(currentRow, col);
                        }
                    } else if (maze[currentRow][col - 1] == PATH) {
                        currentRow -= 1;
                    }
                } else if (move == 1) {
                    makePath(currentRow, col);
                }

                // Backwards path with a small probability
                int back = rand.nextInt(10);
                int stepsBack = rand.nextInt(3) + 1;
                if (back == 0 && col > stepsBack + 1) {
                    for (int i = 0; i <= stepsBack; i ++) {
                        col --;
                        makePath(currentRow, col);
                    }
                }
            }
        }

        // clean maze
        for (int x = 0; x < COLS; x ++) {
            for (int y = 0; y < ROWS; y ++) {
                checkSquare(x, y);
            }
        }

        // Set the start and end points
        setStartAndEnd();
    }

    private void setStartAndEnd() {
        Random rand = new Random();
        int k = 0;
        while (k < 1) {
            int m = rand.nextInt(ROWS);
            if (maze[m][1] == PATH) {
                maze[m][0] = START;
                k++;
            }
        }

        k = 0;
        while (k < 1) {
            int m = rand.nextInt(ROWS);
            if (maze[m][COLS - 2] == PATH) {
                maze[m][COLS - 1] = END;
                k++;
            }
        }
    }

    private void makePath(int randomRow, int randomCol) {
        maze[randomRow][randomCol] = PATH;
    }

    // If there is a square (or larger) of just path tiles, it will remove some paths
    private void checkSquare(int x, int y) {
        boolean up = (y > 0) && (maze[y - 1][x] == PATH);
        boolean down = (y < ROWS - 1) && (maze[y + 1][x] == PATH);
        boolean left = (x > 0) && (maze[y][x - 1] == PATH);
        boolean right = (x < COLS - 1) && (maze[y][x + 1] == PATH);

        boolean upLeft = (y > 0 && x > 0) && (maze[y - 1][x - 1] == PATH);
        boolean downLeft = (y < ROWS - 1 && x > 0) && (maze[y + 1][x - 1] == PATH);
        boolean upRight = (y > 0 && x < COLS - 1) && (maze[y - 1][x + 1] == PATH);
        boolean downRight = (y < ROWS - 1 && x < COLS - 1) && (maze[y + 1][x + 1] == PATH);

        if (maze[y][x] == PATH) {
            if ((up && down) && (left && right) && (upLeft && downLeft) && (upRight && downRight)) {
                maze[y][x] = WALL;
            } else if ((down) && left && right && downLeft && downRight) {
                maze[y][x] = WALL;
            } else if (up && down && right && upRight && downRight) {
                maze[y][x] = WALL;
            }
        }
    }

    // Ensure 2x2 gray squares next to the path
    private void placeGraySquares() {
        Random rand = new Random();
        int squaresPlaced = 0;
        int maxSquares = 10;

        while (squaresPlaced < maxSquares) {
            int row = rand.nextInt(ROWS - 1);
            int col = rand.nextInt(COLS - 1);

            // Check if the 2x2 area is all walls and has a neighboring path
            if (maze[row][col] == 1 && maze[row + 1][col] == 1 &&
                    maze[row][col + 1] == 1 && maze[row + 1][col + 1] == 1 &&
                    connectToPath(row, col)) {


                // Place a 2x2 square of gray tiles (maze[][] = 4)
                maze[row][col] = BUILDING;
                maze[row + 1][col] = BUILDING;
                maze[row][col + 1] = BUILDING;
                maze[row + 1][col + 1] = BUILDING;

                squaresPlaced++;
            }
        } //CREATE FUNCTION TO ENSURE 10 BUILDINGS FIT, OTHERWISE RE-GENERATE GRID!!!
    }

    private boolean connectToPath(int row, int col) {
        for (int r = row - 1; r <= row + 2; r++) {
            for (int c = col - 1; c <= col + 2; c++) {
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS && maze[r][c] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    // Getters for maze data
    public int getCell(int row, int col) {
        return maze[row][col];
    }
    
    public int[] getStartPosition() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (maze[row][col] == START) {
                    return new int[] { row, col };
                }
            }
        }
        return null;
    }

    public int[] getEndPosition() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (maze[row][col] == END) {
                    return new int[] { row, col };
                }
            }
        }
        return null;
    }

    public boolean isWalkable(int row, int col) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            return maze[row][col] == PATH || maze[row][col] == START || maze[row][col] == END;
        }
        return false; // Return false if the indices are out of bounds
    }

}
