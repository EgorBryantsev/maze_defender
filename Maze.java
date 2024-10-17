import java.util.Random;

public class Maze {
    private static final int ROWS = 20;
    private static final int COLS = 40;
    private int[][] maze;
    private int panelWidth;
    private int panelHeight;
    private int tileSize;
    private int pathStarts;

    // Constructor
    public Maze() {
        maze = new int[ROWS][COLS];
        generateMaze();
        placeGraySquares();  // Place 2x2 gray squares after generating the maze
    }

    // Generate a maze where initially the entire grid is walls (1)
    // and then carve a random path from the first column to the last column.
    private void generateMaze() {
        // Initialize all tiles to walls
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                maze[row][col] = 1;  // 1 represents a wall
            }
        }

        // Randomly choose a starting point in the first column
        Random rand = new Random();
        int startRow = rand.nextInt(ROWS);
        pathStarts = 4;
        createPath(startRow, 1);
    }

    private void createPath(int startRow, int startCol) {
        Random rand = new Random();
        for (int x = 1; x <= pathStarts + 1; x ++) {
            if (x > 1) {
                startCol = rand.nextInt(COLS);
                startRow = rand.nextInt(ROWS - 5) + 5;
            }
            int currentRow = startRow;

            // Random walk to carve a path to the last column
            for (int col = 1; col < COLS - 1; col++) {
                maze[currentRow][col] = 0;  // Carve a path
                // Randomly decide to move up, down, or stay in the same row
                int move = rand.nextInt(3);  // 0: up, 1: same, 2: down
                if (move == 0 && currentRow > 0) {
                    currentRow --;  // Move up 1
                    if (maze[currentRow][col - 1] == 1) {
                        checkBorders(currentRow, col);
                        if (currentRow > 1){
                            currentRow --;
                            checkBorders(currentRow, col);
                        }
                    } else if (maze[currentRow][col - 1] == 0) {
                        currentRow += 1;
                    }
                } else if (move == 2 && currentRow < ROWS - 1) {
                    currentRow ++;  // Move down 1
                    if (maze[currentRow][col - 1] == 1) {
                        checkBorders(currentRow, col);
                        if (currentRow < ROWS - 2) {
                            currentRow ++;
                            checkBorders(currentRow, col);
                        }
                    } else if (maze[currentRow][col - 1] == 0) {
                        currentRow -= 1;
                    }
                } else if (move == 1 && col < COLS-1) {
                    checkBorders(currentRow, col);
                }
                int back = rand.nextInt(10);
                int stepsBack = rand.nextInt(3) + 1;
                if (back == 0 && col > stepsBack + 1) {
                    for (int i = 0; i <= stepsBack; i ++) {
                        col --;
                        checkBorders(currentRow, col);
                    }

                }
            }
        }
        for (int x = 0; x < COLS; x ++) {
            for (int y = 0; y < ROWS; y ++) {
                checkSquare(x, y);
            }
        }
        int k = 0;
        while (k < 1) {
            int m = rand.nextInt(ROWS);
            if (maze[m][1] == 0) {
                maze[m][0] = 2;
                k ++;
            }
        }
        k = 0;
        while (k < 1) {
            int m = rand.nextInt(ROWS);
            if (maze[m][COLS - 2] == 0) {
                maze[m][COLS - 1] = 3;
                k ++;
            }
        }
    }

    private void checkBorders(int randomRow, int randomCol) {
        maze[randomRow][randomCol] = 0;
    }

    private void checkSquare(int x, int y) {
        boolean up = (y > 0) && (maze[y - 1][x] == 0);
        boolean down = (y < ROWS - 1) && (maze[y + 1][x] == 0);
        boolean left = (x > 0) && (maze[y][x - 1] == 0);
        boolean right = (x < COLS - 1) && (maze[y][x + 1] == 0);

        boolean upLeft = (y > 0 && x > 0) && (maze[y - 1][x - 1] == 0);
        boolean downLeft = (y < ROWS - 1 && x > 0) && (maze[y + 1][x - 1] == 0);
        boolean upRight = (y > 0 && x < COLS - 1) && (maze[y - 1][x + 1] == 0);
        boolean downRight = (y < ROWS - 1 && x < COLS - 1) && (maze[y + 1][x + 1] == 0);

        if (maze[y][x] == 0) {
            if ((up && down) && (left && right) && (upLeft && downLeft) && (upRight && downRight)) {
                maze[y][x] = 1;
            } else if ((down) && left && right && downLeft && downRight) {
                maze[y][x] = 1;
            } else if (up && down && right && upRight && downRight) {
                maze[y][x] = 1;
            }
        }
    }

    // Method to place five 2x2 gray squares next to the path
    private void placeGraySquares() {
        Random rand = new Random();
        int squaresPlaced = 0;

        while (squaresPlaced < 10) {
            int row = rand.nextInt(ROWS - 1);
            int col = rand.nextInt(COLS - 1);

            // Check if the 2x2 area is all walls and has a neighboring path
            if (maze[row][col] == 1 && maze[row + 1][col] == 1 &&
                    maze[row][col + 1] == 1 && maze[row + 1][col + 1] == 1 &&
                    connectToPath(row, col)) {

                // Place a 2x2 square of gray tiles (4)
                maze[row][col] = 4;
                maze[row + 1][col] = 4;
                maze[row][col + 1] = 4;
                maze[row + 1][col + 1] = 4;

                squaresPlaced++;
            }
        } //CREATE FUNCTION TO ENSURE 10 BUILDINGS FIT, OTHERWISE RE-GENERATE GRID!!!
    }

    private boolean connectToPath(int row, int col) {
        for (int r = row - 1; r <= row + 2; r++) {
            for (int c = col - 1; c <= col + 2; c++) {
                if ((r >= 0 && r < ROWS && c >= 0 && c < COLS && maze[r][c] == 0) && !(r >= 0 && r < ROWS && c >= 0 && c < COLS && maze[r][c] == 4)) {
                    return true;
                }
            }
        }
        return false;
    }
}
