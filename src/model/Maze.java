package model;

/**
 * Stores the maze layout and provides helper methods
 * for checking walkability and tile types.
 * @author Aiden
 */
public class Maze {

    private final Tile[][] grid;
    private final int rows;
    private final int cols;

    public Maze(TileType[][] layout) {
        this.rows = layout.length;
        this.cols = layout[0].length;
        this.grid = new Tile[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Tile(layout[r][c], r, c);
            }
        }
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean isWalkable(int row, int col) {
        return inBounds(row, col) && grid[row][col].isWalkable();
    }

    public boolean isExit(int row, int col) {
        return inBounds(row, col) && grid[row][col].getType() == TileType.EXIT;
    }

    public Tile getTile(int row, int col) {
        return grid[row][col];
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
}
