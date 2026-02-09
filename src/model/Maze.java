package model;

import java.awt.Graphics2D;

/**
 * Represents the maze grid and provides tile-level queries.
 */
public class Maze {

    private final Tile[][] tiles;
    private final int rows;
    private final int cols;

    public Maze(TileType[][] layout) {
        rows = layout.length;
        cols = layout[0].length;
        tiles = new Tile[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c] = new Tile(layout[r][c]);
            }
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean isWalkable(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return false;
        return tiles[row][col].isWalkable();
    }

    public boolean isExit(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return false;
        return tiles[row][col].isExit();
    }

    public void draw(Graphics2D g2) {
        int tileSize = GameConstant.TILE_SIZE;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c].draw(g2, r, c, tileSize);
            }
        }
    }
}
