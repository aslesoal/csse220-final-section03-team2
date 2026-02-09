package model;

import java.awt.Image;
import javax.imageio.ImageIO;

public class Player {

    public static final int SIZE = 18;

    private double x;
    private double y;
    private double speed = 3.0;

    private final Maze maze;
    private Image sprite;

    private int lives = GameConstant.INITIAL_LIVES;
    private int score = 0;

    private int invincibleTimer = 0;
    private int flashTimer = 0;

    private double facingAngle = 0.0;

    public Player(int startRow, int startCol, Maze maze) {
        this.maze = maze;

        this.x = startCol * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;
        this.y = startRow * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;

        try {
            sprite = ImageIO.read(Player.class.getResource(GameConstant.PLAYER_SPRITE));
            System.out.println("Loaded player sprite from: " + Player.class.getResource(GameConstant.PLAYER_SPRITE));
        } catch (Exception e) {
            sprite = null;
            System.err.println("Player sprite not found: " + e);
        }
    }

    public Image getSprite() { return sprite; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return SIZE; }

    public int getLives() { return lives; }
    public int getScore() { return score; }

    public void addScore(int amount) { score += amount; }
    public void loseLife() { lives--; }
    public boolean isDead() { return lives <= 0; }

    public boolean isInvincible() { return invincibleTimer > 0; }
    public void triggerInvincibility() { invincibleTimer = GameConstant.INVINCIBILITY_FRAMES; }
    public void tickInvincibility() { if (invincibleTimer > 0) invincibleTimer--; }

    public boolean isFlashing() { return flashTimer > 0; }
    public void triggerFlash() { flashTimer = GameConstant.FLASH_FRAMES; }
    public void tickFlash() { if (flashTimer > 0) flashTimer--; }

    public double getFacingAngle() { return facingAngle; }

    public void move(boolean up, boolean down, boolean left, boolean right) {

        double dx = 0;
        double dy = 0;

        if (up)    dy -= speed;
        if (down)  dy += speed;
        if (left)  dx -= speed;
        if (right) dx += speed;

        // Normalize diagonal movement
        if (dx != 0 && dy != 0) {
            dx *= 0.707;
            dy *= 0.707;
        }

        // Update facing angle
        if (dx != 0 || dy != 0) {
            facingAngle = Math.atan2(dy, dx);
        }

        double newX = x + dx;
        double newY = y + dy;

        if (!collidesWithWall(newX, y)) x = newX;
        if (!collidesWithWall(x, newY)) y = newY;
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
