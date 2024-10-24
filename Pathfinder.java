public class Pathfinder {
    private final int[] start;
    private final int[] end;
    private final int startRow;
    private final int startCol;
    private final int endRow;
    private final int endCol;
    private Maze maze;

    // Arrays to store scores
    private final int[][] gScores; //cost from start to each cell
    private final int[][] fScores; //total cost (gscore + hscore) for each cell
    private final int[][][] cameFrom; //parent cell identifier
    // Open and Closed lists
    private final boolean[][] closedList;
    private final boolean[][] openList;

    public Pathfinder(Maze maze) {
        this.maze = maze;
        this.start = maze.getStartPosition();
        this.end = maze.getEndPosition();
        this.startRow = start[0];
        this.startCol = start[1];
        this.endRow = end[0];
        this.endCol = end[1];

        gScores = new int[Maze.ROWS][Maze.COLS];
        fScores = new int[Maze.ROWS][Maze.COLS];
        cameFrom = new int[Maze.ROWS][Maze.COLS][2];
        closedList = new boolean[Maze.ROWS][Maze.COLS];
        openList = new boolean[Maze.ROWS][Maze.COLS];

        // Initialize scores to a large value
        for (int i = 0; i < Maze.ROWS; i++) {
            for (int j = 0; j < Maze.COLS; j++) {
                gScores[i][j] = Integer.MAX_VALUE;
                fScores[i][j] = Integer.MAX_VALUE;
                cameFrom[i][j][0] = -1; // -1 indicates no parent, column
                cameFrom[i][j][1] = -1; // -1 indicates no parent, row
            }
        }
    }

    public int[][] findPath() {
        // start pozzie
        gScores[startRow][startCol] = 0;
        fScores[startRow][startCol] = heuristic(startRow, startCol);
        openList[startRow][startCol] = true;

        while (hasOpenNodes()) {
            int[] current = getLowestF();
            if (current == null) {
                break;
            }

            int currentRow = current[0];
            int currentCol = current[1];

            if (currentRow == endRow && currentCol == endCol) {
                return reconstructPath();
            }

            // move current node from open to closed list
            openList[currentRow][currentCol] = false;
            closedList[currentRow][currentCol] = true;

            // check neighbors
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : directions) {
                int neighborRow = currentRow + dir[0]; //neighbor row index
                int neighborCol = currentCol + dir[1]; //neighbor column index

                // bad cells
                if (!maze.isWalkable(neighborRow, neighborCol) || closedList[neighborRow][neighborCol]) {
                    continue;
                }

                int moveCost = gScores[currentRow][currentCol] + 1; // cost to move is 1

                if (moveCost < gScores[neighborRow][neighborCol]) {
                    // found a faster path
                    cameFrom[neighborRow][neighborCol][0] = currentRow; //parent row
                    cameFrom[neighborRow][neighborCol][1] = currentCol; //parent col
                    gScores[neighborRow][neighborCol] = moveCost;
                    fScores[neighborRow][neighborCol] = moveCost + heuristic(neighborRow, neighborCol);

                    if (!openList[neighborRow][neighborCol]) {
                        openList[neighborRow][neighborCol] = true;
                    }
                }
            }
        }

        //no path found
        return null;
    }

    private int[][] reconstructPath() {
        // Use temporary variables to avoid modifying endRow and endCol
        int tempRow = endRow;
        int tempCol = endCol;

        // Estimate maximum path length
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
                // No path exists
                return null;
            }

            tempRow = parentRow;
            tempCol = parentCol;
        }

        // Add the start position
        path[pathIndex][0] = startRow;
        path[pathIndex][1] = startCol;
        pathIndex++;

        // Reverse the path to start -> end
        int[][] finalPath = new int[pathIndex][2];
        for (int i = 0; i < pathIndex; i++) {
            finalPath[i][0] = path[pathIndex - i - 1][0];
            finalPath[i][1] = path[pathIndex - i - 1][1];
        }

        return finalPath;
    }

    // manhattan distance heuristic
    private int heuristic(int row, int col) {
        return Math.abs(row - endRow) + Math.abs(col - endCol);
    }

    private boolean hasOpenNodes() {
        for (int i=0; i < Maze.ROWS; i++) {
            for (int j=0; j < Maze.COLS; j++) {
                if (openList[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    private int[] getLowestF() {
        int minF = Integer.MAX_VALUE;
        int minRow = -1;
        int minCol = -1;

        for (int i = 0; i < Maze.ROWS; i++) {
            for (int j = 0; j < Maze.COLS; j++) {
                if (openList[i][j] && fScores[i][j] < minF) {
                    minF = fScores[i][j];
                    minRow = i;
                    minCol = j;
                }
            }
        }

        if (minRow == -1 || minCol == -1) {
            return null;
        }

        return new int[] { minRow, minCol };
    }
}
