package model;

/**
 * Represents a collectible item placed in the maze.
 * Collectibles disappear once picked up by the player.
 */
public class Collectible {

    /** Rendered size of the collectible. */
    public static final int SIZE = 16;

    private double x;
    private double y;
    private boolean collected = false;

    /**
     * Creates a collectible centered in the given maze tile.
     */
    public Collectible(int row, int col) {
        this.x = col * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;
        this.y = row * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return SIZE; }

    /** Returns true if the collectible has already been picked up. */
    public boolean isCollected() {
        return collected;
    }

    /** Marks this collectible as collected. */
    public void collect() {
        collected = true;
    }
}
