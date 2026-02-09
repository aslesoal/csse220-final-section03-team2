package model;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.util.Random;

public class Zombie {

    public static final int SIZE = 22;

    private double x;
    private double y;
    private double speed = 2.6;

    private final Maze maze;
    private Image sprite;

    private int collisionCooldown = 0;

    private final Random random = new Random();

    private double dirX = 1;
    private double dirY = 0;

    private double facingAngle = 0.0;

    public Zombie(int startRow, int startCol, Maze maze) {
        this.maze = maze;

        this.x = startCol * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;
        this.y = startRow * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;

        try {
            sprite = ImageIO.read(Zombie.class.getResource(GameConstant.ZOMBIE_SPRITE));
            System.out.println("Loaded zombie sprite from: " + Zombie.class.getResource(GameConstant.ZOMBIE_SPRITE));
        } catch (Exception e) {
            sprite = null;
            System.err.println("Zombie sprite not found: " + e);
        }

        randomizeDirection();
    }

    public Image getSprite() { return sprite; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return SIZE; }

    public boolean isInCollisionCooldown() { return collisionCooldown > 0; }
    public void triggerCollisionCooldown() { collisionCooldown = 30; }
    public void tickCollisionCooldown() { if (collisionCooldown > 0) collisionCooldown--; }

    public double getFacingAngle() { return facingAngle; }

    private void randomizeDirection() {
        int dir = random.nextInt(4);

        switch (dir) {
            case 0: dirX = 1;  dirY = 0;  break;
            case 1: dirX = -1; dirY = 0;  break;
            case 2: dirX = 0;  dirY = 1;  break;
            case 3: dirX = 0;  dirY = -1; break;
        }
    }

    public void update() {
        tickCollisionCooldown();

        double dx = dirX * speed;
        double dy = dirY * speed;

        boolean collided = false;

        if (!collidesWithWall(x + dx, y)) x += dx;
        else collided = true;

        if (!collidesWithWall(x, y + dy)) y += dy;
        else collided = true;

        if (collided) randomizeDirection();

        if (dx != 0 || dy != 0) {
            facingAngle = Math.atan2(dy, dx);
        }
    }

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
