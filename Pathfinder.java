import java.util.PriorityQueue;
import java.util.Comparator;

public class Pathfinder {
    private final int[] start;
    private final int[] end;
    private final int startRow;
    private final int startCol;
    private final int endRow;
    private final int endCol;
    private Maze maze;

    public Pathfinder(Maze maze) {
        this.maze = maze;
        this.start = maze.getStartPosition();
        this.end = maze.getEndPosition();
        if (start == null || end == null) {
            throw new IllegalArgumentException("Maze must have start and end positions.");
        }
        this.startRow = start[0];
        this.startCol = start[1];
        this.endRow = end[0];
        this.endCol = end[1];
    }

    public int[][] findPath() {
        // Initialize local variables for each pathfinding operation
        int[][] gScores = new int[Maze.ROWS][Maze.COLS];
        int[][] fScores = new int[Maze.ROWS][Maze.COLS];
        int[][][] cameFrom = new int[Maze.ROWS][Maze.COLS][2];
        boolean[][] closedList = new boolean[Maze.ROWS][Maze.COLS];
        boolean[][] openList = new boolean[Maze.ROWS][Maze.COLS];

        // Initialize scores
        for (int i = 0; i < Maze.ROWS; i++) {
            for (int j = 0; j < Maze.COLS; j++) {
                gScores[i][j] = Integer.MAX_VALUE;
                fScores[i][j] = Integer.MAX_VALUE;
                cameFrom[i][j][0] = -1;
                cameFrom[i][j][1] = -1;
            }
        }

        PriorityQueue<int[]> openSet = new PriorityQueue<>(Comparator.comparingInt(a -> fScores[a[0]][a[1]]));
        openSet.add(new int[]{startRow, startCol});
        openList[startRow][startCol] = true;
        gScores[startRow][startCol] = 0;
        fScores[startRow][startCol] = heuristic(startRow, startCol);

        while (!openSet.isEmpty()) {
            int[] current = openSet.poll();
            int currentRow = current[0];
            int currentCol = current[1];

            // Debug: Current node being evaluated
            // System.out.println("Evaluating node: (" + currentRow + ", " + currentCol + ")");

            if (currentRow == endRow && currentCol == endCol) {
                // System.out.println("Path found!");
                return reconstructPath(cameFrom);
            }

            closedList[currentRow][currentCol] = true;

            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : directions) {
                int neighborRow = currentRow + dir[0];
                int neighborCol = currentCol + dir[1];

                if (!maze.isWalkable(neighborRow, neighborCol) || closedList[neighborRow][neighborCol]) {
                    continue;
                }

                int tentativeGScore = gScores[currentRow][currentCol] + 1;

                if (tentativeGScore < gScores[neighborRow][neighborCol]) {
                    cameFrom[neighborRow][neighborCol][0] = currentRow;
                    cameFrom[neighborRow][neighborCol][1] = currentCol;
                    gScores[neighborRow][neighborCol] = tentativeGScore;
                    fScores[neighborRow][neighborCol] = tentativeGScore + heuristic(neighborRow, neighborCol);

                    if (!openList[neighborRow][neighborCol]) {
                        openSet.add(new int[]{neighborRow, neighborCol});
                        openList[neighborRow][neighborCol] = true;
                        // Debug: Adding node to open set
                        // System.out.println("Adding node to open set: (" + neighborRow + ", " + neighborCol + ")");
                    }
                }
            }
        }

        // No path found
        System.out.println("No path found for enemy.");
        return null;
    }

    private int[][] reconstructPath(int[][][] cameFrom) {
        int tempRow = endRow;
        int tempCol = endCol;

        int maxPathLength = Maze.ROWS * Maze.COLS;
        int[][] path = new int[maxPathLength][2];
        int pathIndex = 0;

        while (tempRow != startRow || tempCol != startCol) {
            path[pathIndex][0] = tempRow;
            path[pathIndex][1] = tempCol;
            pathIndex++;

            int parentRow = cameFrom[tempRow][tempCol][0];
            int parentCol = cameFrom[tempRow][tempCol][1];

            if (parentRow == -1 || parentCol == -1) {
                return null;
            }

            tempRow = parentRow;
            tempCol = parentCol;
        }

        path[pathIndex][0] = startRow;
        path[pathIndex][1] = startCol;
        pathIndex++;

        int[][] finalPath = new int[pathIndex][2];
        for (int i = 0; i < pathIndex; i++) {
            finalPath[i][0] = path[pathIndex - i - 1][0];
            finalPath[i][1] = path[pathIndex - i - 1][1];
        }

        return finalPath;
    }

    private int heuristic(int row, int col) {
        return Math.abs(row - endRow) + Math.abs(col - endCol);
    }
}
