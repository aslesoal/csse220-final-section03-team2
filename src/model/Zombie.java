package model;

import java.util.Random;

/**
 * A zombie that moves in a straight line until it hits a wall.
 * When blocked, it chooses a new random direction and continues.
 * Slightly larger than the player but smaller than a tile.
 */
public class Zombie {

    public static final int SIZE = 24; // Player is 20, tile is 32

    private double x;
    private double y;

    private double dx;
    private double dy;

    private double speed = 1.6;

    private Maze maze;
    private Random rand = new Random();

    public Zombie(int startRow, int startCol, Maze maze) {
        this.maze = maze;

        // Convert tile coords to pixel coords
        this.x = startCol * 32 + (32 - SIZE) / 2.0;
        this.y = startRow * 32 + (32 - SIZE) / 2.0;

        // Safety check: ensure zombie is not spawning inside a wall
        if (collidesWithWall(x, y)) {
            throw new IllegalArgumentException(
                "Zombie spawned inside a wall at row=" + startRow + ", col=" + startCol
            );
        }

        chooseNewDirection();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return SIZE; }

    /**
     * Moves the zombie. If the next step hits a wall,
     * it chooses a new random direction.
     */
    public void update() {
        double newX = x + dx;
        double newY = y + dy;

        // If blocked horizontally or vertically, pick a new direction
        if (collidesWithWall(newX, y) || collidesWithWall(x, newY)) {
            chooseNewDirection();
            return;
        }

        // Otherwise move normally
        x = newX;
        y = newY;
    }

    /**
     * Picks a random cardinal direction (up, down, left, right)
     * that is NOT blocked by a wall.
     */
    private void chooseNewDirection() {
        // Try up to 10 random directions
        for (int i = 0; i < 10; i++) {
            int dir = rand.nextInt(4);

            double testDx = 0;
            double testDy = 0;

            switch (dir) {
                case 0: testDx = speed; break;   // right
                case 1: testDx = -speed; break;  // left
                case 2: testDy = speed; break;   // down
                case 3: testDy = -speed; break;  // up
            }

            // Check if this direction is valid
            if (!collidesWithWall(x + testDx, y + testDy)) {
                dx = testDx;
                dy = testDy;
                return;
            }
        }

        // If all directions fail (rare), stop temporarily
        dx = 0;
        dy = 0;
    }

    /**
     * Checks if the zombie's bounding box intersects a wall tile.
     */
    private boolean collidesWithWall(double px, double py) {
        int tileSize = 32;

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
