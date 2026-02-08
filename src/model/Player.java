package model;

import java.awt.Image;
import javax.imageio.ImageIO;

public class Player {

    public static final int SIZE = 18;

    private double x;
    private double y;
    private double speed = 3.0;

    private Maze maze;
    private Image sprite;

    // Lives + Score
    private int lives = 4;
    private int score = 0;

    // Invincibility frames (grace period)
    private int invincibleTimer = 0;

    // Short red flash timer
    private int flashTimer = 0;

    public Player(int startRow, int startCol, Maze maze) {
        this.maze = maze;

        this.x = startCol * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;
        this.y = startRow * GameConstant.TILE_SIZE + (GameConstant.TILE_SIZE - SIZE) / 2.0;

        try {
            sprite = ImageIO.read(getClass().getResource(GameConstant.PLAYER_SPRITE));
        } catch (Exception e) {
            sprite = null;
            System.err.println("Player sprite not found — using fallback.");
        }
    }

    public Image getSprite() { return sprite; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return SIZE; }
    public double getSpeed() { return speed; }

    public int getLives() { return lives; }
    public int getScore() { return score; }

    public void addScore(int amount) { score += amount; }
    public void loseLife() { lives--; }
    public boolean isDead() { return lives <= 0; }

    // Invincibility
    public boolean isInvincible() { return invincibleTimer > 0; }
    public void triggerInvincibility() { invincibleTimer = 60; } // 1 second
    public void tickInvincibility() { if (invincibleTimer > 0) invincibleTimer--; }

    // Flash effect
    public void triggerFlash() { flashTimer = 10; } // short flash
    public boolean isFlashing() { return flashTimer > 0; }
    public void tickFlash() { if (flashTimer > 0) flashTimer--; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(double dx, double dy) {
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
