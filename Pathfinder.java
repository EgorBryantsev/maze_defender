import java.util.*;

public class Pathfinder {
    private final int[] start;
    private final int[] end;
    private final int startRow;
    private final int startCol;
    private final int endRow;
    private final int endCol;

    // Arrays to store scores
    private final int[][] gScores;
    private final int[][] fScores;
    private final int[][] cameFrom;
    // Open and Closed lists
    private final boolean[][] closedList;
    private final boolean[][] openList;

    private int heuristic(int row, int col) {
        return Math.abs(row - endRow) + Math.abs(col - endCol);
    }
    
    public Pathfinder(Maze maze) {
        this.start = maze.getStartPosition();
        this.end = maze.getEndPosition();
        this.startRow = start[0];
        this.startCol = start[1];
        this.endRow = end[0];
        this.endCol = end[1];

        gScores = new int[Maze.ROWS][Maze.COLS];
        fScores = new int[Maze.ROWS][Maze.COLS];
        cameFrom = new int[Maze.ROWS][Maze.COLS];
        closedList = new boolean[Maze.ROWS][Maze.COLS];
        openList = new boolean[Maze.ROWS][Maze.COLS];

        // Initialize scores to a large value
        for (int i = 0; i < Maze.ROWS; i++) {
            for (int j = 0; j < Maze.COLS; j++) {
                gScores[i][j] = Integer.MAX_VALUE;
                fScores[i][j] = Integer.MAX_VALUE;
                cameFrom[i][j] = -1; // -1 indicates no parent
            }
        }
    }

    public int[][] findPath() {
        gScores[startRow][startCol] = 0;
        fScores[startRow][startCol] = heuristic(startRow, startCol);
        openList[startRow][startCol] = true;
    }
}
