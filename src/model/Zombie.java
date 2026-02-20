package model;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.util.Random;

/*
 * Handles zombie movement and collisions
 */
public class Zombie extends Entity {

    public static final int SIZE = 22;

    // Constants
    public static final int COLLISION_COOLDOWN_FRAMES = 30;

    // sprite folder
    public static final String ZOMBIE_SPRITE = "/images/zombie.png";

    private double speed = 2.6;

    private final Maze maze;
    private Image sprite;

    private int collisionCooldown = 0;

    private final Random random = new Random();

    private double dirX = 1;
    private double dirY = 0;

    private double facingAngle = 0.0;

    //Spawning zombies in the middle of their tile and loading the sprite image
    public Zombie(int startRow, int startCol, Maze maze) {
        this.maze = maze;

        this.size = SIZE;

        this.x = startCol * Maze.TILE_SIZE + (Maze.TILE_SIZE - SIZE) / 2.0;
        this.y = startRow * Maze.TILE_SIZE + (Maze.TILE_SIZE - SIZE) / 2.0;

        try {
            sprite = ImageIO.read(Zombie.class.getResource(ZOMBIE_SPRITE));
        } catch (Exception e) {
            sprite = null;
            System.err.println("Zombie sprite not found: " + e);
        }

        randomizeDirection();
    }

    public Image getSprite() { return sprite; }

    //collision management
    public boolean isInCollisionCooldown() { return collisionCooldown > 0; }
    public void triggerCollisionCooldown() { collisionCooldown = COLLISION_COOLDOWN_FRAMES; }
    public void tickCollisionCooldown() { if (collisionCooldown > 0) collisionCooldown--; }

    public double getFacingAngle() { return facingAngle; }

    //movement mechanics
    private void randomizeDirection() {
        int dir = random.nextInt(4);

        switch (dir) {
            case 0 -> { dirX = 1;  dirY = 0; }
            case 1 -> { dirX = -1; dirY = 0; }
            case 2 -> { dirX = 0;  dirY = 1; }
            case 3 -> { dirX = 0;  dirY = -1; }
        }
    }


    //cannot move through walls
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

    //collides with wall and picks a random direction to move int
    private boolean collidesWithWall(double px, double py) {
        int tileSize = Maze.TILE_SIZE;

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
