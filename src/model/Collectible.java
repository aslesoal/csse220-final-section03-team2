package model;

/**
 * Simple collectible item that increases score when picked up.
 */
public class Collectible {

    public static final int SIZE = 12;

    private final double x;
    private final double y;
    private boolean collected = false;

    public Collectible(int row, int col) {
        this.x = col * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;
        this.y = row * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isCollected() { return collected; }
    public void collect() { collected = true; }
}
