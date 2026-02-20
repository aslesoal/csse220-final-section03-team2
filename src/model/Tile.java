package model;

import java.awt.Color;
import java.awt.Graphics2D;

public class Tile {

    private final TileType type;

    public Tile(TileType type) {
        this.type = type;
    }

    public TileType getType() {
        return type;
    }

    public boolean isWalkable() { 
        return type.isWalkable(); 
    }

    public boolean isExit() { 
        return type.isExit(); 
    }

    public void draw(Graphics2D g2, int row, int col, int tileSize) {
        int x = col * tileSize;
        int y = row * tileSize;

        if (!type.isWalkable()) {
            g2.setColor(Color.DARK_GRAY);   // WALL
        } else {
            g2.setColor(Color.LIGHT_GRAY);  // FLOOR
        }

        g2.fillRect(x, y, tileSize, tileSize);

        if (type.isExit()) {
            g2.setColor(Color.GREEN.darker());
            g2.fillRect(x + 8, y + 8, tileSize - 16, tileSize - 16);
        }
    }
}
