package model;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 * A single tile in the maze grid.
 * Tiles do not move and do not update.
 * @author Aiden
 */
public class Tile {

    private final TileType type;
    private final int row;
    private final int col;

    public Tile(TileType type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
    }

    public TileType getType() {
        return type;
    }

    public boolean isWalkable() {
        return type != TileType.WALL;
    }

    public void draw(Graphics2D g, int tileSize) {
        switch (type) {
            case WALL:
                g.setColor(Color.DARK_GRAY);
                break;
            case FLOOR:
                g.setColor(Color.LIGHT_GRAY);
                break;
            case EXIT:
                g.setColor(Color.GREEN);
                break;
        }

        g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
    }
}
