package model;

public class Collectible {

    public static final int SIZE = 16;

    private double x;
    private double y;

    private boolean collected = false;

    public Collectible(int row, int col) {
        // Center inside the tile
        this.x = col * 32 + (32 - SIZE) / 2.0;
        this.y = row * 32 + (32 - SIZE) / 2.0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return SIZE; }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }
}
