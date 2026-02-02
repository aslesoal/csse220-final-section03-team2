package model;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Represents a single tile in the maze grid.
 * Each tile has a type and knows how to draw itself.
 */
public class Tile {

    private TileType type;
    private int row;
    private int col;

    /**
     * Creates a tile with the given type and grid position.
     */
    public Tile(TileType type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
    }

    public TileType getType() {
        return type;
    }

    /** Returns true if the tile can be walked on. */
    public boolean isWalkable() {
        return type == TileType.FLOOR || type == TileType.EXIT;
    }

    /** Returns true if this tile is the maze exit. */
    public boolean isExit() {
        return type == TileType.EXIT;
    }

    /**
     * Draws the tile using simple colored rectangles.
     * (Your UI can later replace this with sprite-based tiles.)
     */
    public void draw(Graphics2D g2, int tileSize) {
        int x = col * tileSize;
        int y = row * tileSize;

        switch (type) {
            case WALL:
                g2.setColor(Color.DARK_GRAY);
                break;
            case FLOOR:
                g2.setColor(Color.LIGHT_GRAY);
                break;
            case EXIT:
                g2.setColor(Color.GREEN);
                break;
        }

        g2.fillRect(x, y, tileSize, tileSize);
    }
}
