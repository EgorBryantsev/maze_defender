import java.util.Random;

public class Maze {
    public static final int ROWS = 20;
    public static final int COLS = 40;
    public static final int WALL = 1;
    public static final int PATH = 0;
    public static final int START = 2;
    public static final int END = 3;
    public static final int BUILDING = 4;

    private int[][] grid;

    public Maze() {
        grid = new int[ROWS][COLS];
        generateMaze();
        placeGraySquares(); // Place buildings after generating the maze
    }

    private void generateMaze() {
        // Initialize all cells as walls
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = WALL;
            }
        }

        // Implement your maze generation logic here
        // For example, using Recursive Backtracking

        carvePath(1, 1);

        // Set Start and End points
        grid[1][0] = START; // Start on the left edge
        grid[ROWS - 2][COLS - 1] = END; // End on the right edge
    }

    private void carvePath(int row, int col) {
        grid[row][col] = PATH;

        int[][] directions = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} };
        shuffleArray(directions);

        for (int[] dir : directions) {
            int newRow = row + dir[0] * 2;
            int newCol = col + dir[1] * 2;

            if (isInBounds(newRow, newCol) && grid[newRow][newCol] == WALL) {
                grid[row + dir[0]][col + dir[1]] = PATH;
                carvePath(newRow, newCol);
            }
        }
    }

    private void shuffleArray(int[][] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private boolean isInBounds(int row, int col) {
        return row > 0 && row < ROWS - 1 && col > 0 && col < COLS - 1;
    }

    // Method to place 2x2 gray squares (buildings) next to the path
    private void placeGraySquares() {
        Random rand = new Random();
        int squaresPlaced = 0;
        int maxSquares = 10;

        while (squaresPlaced < maxSquares) {
            int row = rand.nextInt(ROWS - 1);
            int col = rand.nextInt(COLS - 1);

            // Check if the 2x2 area is all walls and adjacent to a path
            if (isValidBuildingSpot(row, col)) {
                grid[row][col] = BUILDING;
                grid[row + 1][col] = BUILDING;
                grid[row][col + 1] = BUILDING;
                grid[row + 1][col + 1] = BUILDING;
                squaresPlaced++;
            }
        }
    }

    private boolean isValidBuildingSpot(int row, int col) {
        // Check 2x2 area
        if (grid[row][col] != WALL || grid[row + 1][col] != WALL ||
                grid[row][col + 1] != WALL || grid[row + 1][col + 1] != WALL) {
            return false;
        }

        // Check adjacent cells for a path
        for (int r = row - 1; r <= row + 2; r++) {
            for (int c = col - 1; c <= col + 2; c++) {
                if (isWithinGrid(r, c) && grid[r][c] == PATH) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWithinGrid(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    public int getCell(int row, int col) {
        if (!isWithinGrid(row, col)) {
            return WALL; // Treat out-of-bounds as walls
        }
        return grid[row][col];
    }

    public void setCell(int row, int col, int value) {
        if (isWithinGrid(row, col)) {
            grid[row][col] = value;
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    // Additional methods as needed
}
