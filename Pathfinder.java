import java.util.*;

public class Pathfinder {
    private final Maze maze;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right

    public Pathfinder(Maze maze) {
        this.maze = maze;
    }

    public int[][] findPath() {
        // Get start and end positions
        int[] start = maze.getStartPosition();
        int[] end = maze.getEndPosition();
        
        if (start == null || end == null) {
            System.out.println("Debug: Start or end position not found");
            System.out.println("Start: " + (start != null ? Arrays.toString(start) : "null"));
            System.out.println("End: " + (end != null ? Arrays.toString(end) : "null"));
            return null;
        }

        // Initialize data structures for BFS
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[Maze.ROWS][Maze.COLS];
        int[][][] parent = new int[Maze.ROWS][Maze.COLS][2];
        
        // Start BFS
        queue.offer(start);
        visited[start[0]][start[1]] = true;
        
        boolean foundPath = false;
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            
            // Check if we reached the end
            if (current[0] == end[0] && current[1] == end[1]) {
                foundPath = true;
                break;
            }
            
            // Try all directions
            for (int[] dir : DIRECTIONS) {
                int newRow = current[0] + dir[0];
                int newCol = current[1] + dir[1];
                
                if (maze.isWalkable(newRow, newCol) && !visited[newRow][newCol]) {
                    queue.offer(new int[]{newRow, newCol});
                    visited[newRow][newCol] = true;
                    parent[newRow][newCol] = current;
                }
            }
        }
        
        if (!foundPath) {
            System.out.println("Debug: No path found between start and end");
            return null;
        }
        
        // Reconstruct path
        List<int[]> path = new ArrayList<>();
        int[] current = end;
        while (current[0] != start[0] || current[1] != start[1]) {
            path.add(current);
            current = parent[current[0]][current[1]];
        }
        path.add(start);
        Collections.reverse(path);
        
        // Convert to 2D array
        int[][] pathArray = new int[path.size()][2];
        for (int i = 0; i < path.size(); i++) {
            pathArray[i] = path.get(i);
        }
        
        System.out.println("Debug: Path found with length " + pathArray.length);
        return pathArray;
    }
}