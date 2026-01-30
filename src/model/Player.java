package model;

/**
 * Represents the player in the maze.
 * The player moves in pixel space and is smaller than a tile,
 * allowing smooth cornering and sliding along walls.
 */
public class Player {

    // Player size (smaller than a tile)
    public static final int SIZE = 18;   // tile is 32px, so this is smaller

    // Position in pixels
    private double x;
    private double y;

    // Movement speed in pixels per update
    private double speed = 3.0;

    private Maze maze;

    public Player(int startRow, int startCol, Maze maze) {
        this.maze = maze;

        // Convert tile coordinates to pixel coordinates
        this.x = startCol * 32 + (32 - SIZE) / 2.0;
        this.y = startRow * 32 + (32 - SIZE) / 2.0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return SIZE; }

    /**
     * Attempts to move the player by dx, dy pixels.
     * Performs collision checks against the maze walls.
     */
    public void move(double dx, double dy) {
        double newX = x + dx;
        double newY = y + dy;

        // Check horizontal movement
        if (!collidesWithWall(newX, y)) {
            x = newX;
        }

        // Check vertical movement
        if (!collidesWithWall(x, newY)) {
            y = newY;
        }
    }

    /**
     * Checks if the player's bounding box intersects a wall tile.
     */
    private boolean collidesWithWall(double px, double py) {
        int tileSize = 32;

        // Player bounding box
        int left   = (int) px;
        int right  = (int) (px + SIZE);
        int top    = (int) py;
        int bottom = (int) (py + SIZE);

        // Convert bounding box edges to tile coordinates
        int leftCol   = left   / tileSize;
        int rightCol  = right  / tileSize;
        int topRow    = top    / tileSize;
        int bottomRow = bottom / tileSize;

        // Check all tiles the player overlaps
        return !maze.isWalkable(topRow, leftCol) ||
               !maze.isWalkable(topRow, rightCol) ||
               !maze.isWalkable(bottomRow, leftCol) ||
               !maze.isWalkable(bottomRow, rightCol);
    }

    public double getSpeed() {
        return speed;
    }
}
