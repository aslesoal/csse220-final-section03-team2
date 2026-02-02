package model;

import java.awt.Image;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Represents a zombie enemy that wanders the maze.
 * Zombies move in straight lines until they hit a wall,
 * then choose a new direction.
 */
public class Zombie {

    /** Rendered size of the zombie sprite. */
    public static final int SIZE = 24;

    private double x;
    private double y;
    private double dx;
    private double dy;
    private double speed = 2.5;

    private Maze maze;
    private Random rand = new Random();
    private Image sprite;

    private int collisionCooldown = 0;
    private int wanderTimer = 0;

    /**
     * Creates a zombie at the given maze row/column.
     */
    public Zombie(int startRow, int startCol, Maze maze) {
        this.maze = maze;

        // Center zombie in tile
        this.x = startCol * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;
        this.y = startRow * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;

        // Prevent spawning inside walls
        if (collidesWithWallInternal(x, y)) {
            throw new IllegalArgumentException("Zombie spawned inside a wall at row=" + startRow + ", col=" + startCol);
        }

        // Load sprite with safe fallback
        try {
            sprite = ImageIO.read(getClass().getResource(GameConstant.ZOMBIE_SPRITE));
        } catch (Exception e) {
            sprite = null;
            System.err.println("Zombie sprite not found — using fallback.");
        }

        chooseNewDirection();
    }

    public Image getSprite() { return sprite; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return SIZE; }

    public boolean isInCollisionCooldown() { return collisionCooldown > 0; }
    public void triggerCollisionCooldown() { collisionCooldown = 20; }
    private void tickCooldown() { if (collisionCooldown > 0) collisionCooldown--; }

    /** Updates zombie movement and direction changes. */
    public void update() {
        tickCooldown();

        // Occasional upward bias when in bottom half
        wanderTimer++;
        if (wanderTimer > 180) {
            int row = (int)(y / GameConstant.TILE_SIZE);
            int mid = maze.getRows() / 2;

            if (row > mid && rand.nextInt(100) < 35) {
                dy = -speed;
                dx = 0;
            }

            wanderTimer = 0;
        }

        double newX = x + dx;
        double newY = y + dy;

        if (collidesWithWallInternal(newX, y) || collidesWithWallInternal(x, newY)) {
            chooseNewDirection();
            return;
        }

        x = newX;
        y = newY;
    }

    /** Chooses a new direction based on current movement. */
    public void chooseNewDirection() {
        boolean movingHoriz = Math.abs(dx) > 0;
        boolean movingVert  = Math.abs(dy) > 0;

        for (int i = 0; i < 10; i++) {
            double testDx = 0;
            double testDy = 0;

            if (movingVert) {
                testDx = rand.nextBoolean() ? speed : -speed;
            } else if (movingHoriz) {
                testDy = rand.nextBoolean() ? speed : -speed;
            } else {
                int dir = rand.nextInt(4);
                switch (dir) {
                    case 0: testDx = speed; break;
                    case 1: testDx = -speed; break;
                    case 2: testDy = speed; break;
                    case 3: testDy = -speed; break;
                }
            }

            if (!collidesWithWallInternal(x + testDx, y + testDy)) {
                dx = testDx;
                dy = testDy;
                return;
            }
        }

        dx = 0;
        dy = 0;
    }

    /** Checks collision with walls using zombie's bounding box. */
    private boolean collidesWithWallInternal(double px, double py) {
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
