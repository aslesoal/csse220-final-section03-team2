package model;

import java.awt.Graphics2D;
import java.awt.Color;

public class Tile {

    private TileType type;
    private int row;
    private int col;

    public Tile(TileType type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
    }

    public TileType getType() {
        return type;
    }

    public boolean isWalkable() {
        return type == TileType.FLOOR || type == TileType.EXIT;
    }

    public boolean isExit() {
        return type == TileType.EXIT;
    }

    public void draw(Graphics2D g2, int tileSize) {
        int x = col * tileSize;
        int y = row * tileSize;

        if (type == TileType.WALL) {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(x, y, tileSize, tileSize);
        } else if (type == TileType.FLOOR) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(x, y, tileSize, tileSize);
        } else if (type == TileType.EXIT) {
            g2.setColor(Color.GREEN);
            g2.fillRect(x, y, tileSize, tileSize);
        }
    }
}
