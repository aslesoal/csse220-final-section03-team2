package model;

import java.util.Random;

public class Zombie {

    public static final int SIZE = 24;

    private double x;
    private double y;

    private double dx;
    private double dy;

    private double speed = 1.6;

    private Maze maze;
    private Random rand = new Random();

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

    public void update() {
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
