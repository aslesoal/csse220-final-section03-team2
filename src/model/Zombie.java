package model;

import java.util.Random;

public class Zombie {

    public static final int SIZE = 24;

    private double x;
    private double y;

    private double dx;
    private double dy;

    private double speed = 2.5;

    private Maze maze;
    private Random rand = new Random();

    // Collision cooldown (grace period)
    private int collisionCooldown = 0;

    // Wander-zone timer
    private int wanderTimer = 0;

    public Zombie(int startRow, int startCol, Maze maze) {
        this.maze = maze;

        this.x = startCol * 32 + (32 - SIZE) / 2.0;
        this.y = startRow * 32 + (32 - SIZE) / 2.0;

        if (collidesWithWallInternal(x, y)) {
            throw new IllegalArgumentException(
                "Zombie spawned inside a wall at row=" + startRow + ", col=" + startCol
            );
        }

        chooseNewDirection();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return SIZE; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Cooldown helpers
    public boolean isInCollisionCooldown() {
        return collisionCooldown > 0;
    }

    public void triggerCollisionCooldown() {
        collisionCooldown = 20; // ~0.33 seconds
    }

    private void tickCooldown() {
        if (collisionCooldown > 0) collisionCooldown--;
    }

    public void update() {
        tickCooldown();

        // Wander-zone Option C1 (light upward bias)
        wanderTimer++;
        if (wanderTimer > 180) { // every ~3 seconds
            int row = (int)(y / 32);
            int mid = maze.getRows() / 2;

            if (row > mid) {
                // 35% chance to bias upward
                if (rand.nextInt(100) < 35) {
                    dy = -speed;
                    dx = 0;
                }
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

    public void chooseNewDirection() {
        boolean movingHoriz = Math.abs(dx) > 0;
        boolean movingVert  = Math.abs(dy) > 0;

        for (int i = 0; i < 10; i++) {
            double testDx = 0;
            double testDy = 0;

            if (movingVert) {
                if (rand.nextBoolean()) testDx = speed;
                else testDx = -speed;
            } else if (movingHoriz) {
                if (rand.nextBoolean()) testDy = speed;
                else testDy = -speed;
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

    public boolean collidesWithWall(double px, double py) {
        return collidesWithWallInternal(px, py);
    }

    private boolean collidesWithWallInternal(double px, double py) {
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
