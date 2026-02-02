package model;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 * Represents the player character.
 * The player moves smoothly in pixel space and collides with walls.
 */
public class Player {

    /** Rendered size of the player sprite. */
    public static final int SIZE = 18;

    private double x;
    private double y;
    private double speed = 3.0;

    private Maze maze;
    private Image sprite;

    /**
     * Creates a player at the given maze row/column.
     */
    public Player(int startRow, int startCol, Maze maze) {
        this.maze = maze;

        // Center the player inside the tile
        this.x = startCol * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;
        this.y = startRow * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;

        // Load sprite using ImageIO with safe fallback
        try {
            sprite = ImageIO.read(getClass().getResource(GameConstant.PLAYER_SPRITE));
        } catch (Exception e) {
            sprite = null;  // fallback mode
            System.err.println("Player sprite not found — using fallback.");
        }
    }

    public Image getSprite() { return sprite; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return SIZE; }
    public double getSpeed() { return speed; }

    /** Sets the player's position directly (used for respawning). */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Attempts to move the player by dx, dy, blocking movement into walls. */
    public void move(double dx, double dy) {
        double newX = x + dx;
        double newY = y + dy;

        if (!collidesWithWall(newX, y)) x = newX;
        if (!collidesWithWall(x, newY)) y = newY;
    }

    /** Checks whether the player's bounding box intersects a wall tile. */
    private boolean collidesWithWall(double px, double py) {
        int tileSize = GameConstant.TILE_SIZE;

        int left   = (int) px;
        int right  = (int) (px + SIZE);
        int top    = (int) py;
        int bottom = (int) (py + SIZE);

        int leftCol   = left   / tileSize;
        int rightCol  = right  / tileSize;
        int topRow    = top    / tileSize;
        int bottomRow = bottom / tileSize;

        return !maze.isWalkable(topRow, leftCol) ||
               !maze.isWalkable(topRow, rightCol) ||
               !maze.isWalkable(bottomRow, leftCol) ||
               !maze.isWalkable(bottomRow, rightCol);
    }
}
