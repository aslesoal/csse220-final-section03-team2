package model;

import java.awt.Image;
import javax.imageio.ImageIO;

public class Player extends Entity {

	// Player constants
    public static final int SIZE = 18;
    public static final int INITIAL_LIVES = 4;
    public static final int INVINCIBILITY_FRAMES = 60;
    public static final int FLASH_FRAMES = 20;

    // Sprite Folder
    public static final String PLAYER_SPRITE = "/images/player.png";

    private double speed = 3.0;

    private final Maze maze;
    private Image sprite;

    private int lives = INITIAL_LIVES;
    private int score = 0;

    private int invincibleTimer = 0;
    private int flashTimer = 0;

    private double facingAngle = 0.0;

    //Starts the player in the middle of a tile and loads sprite
    public Player(int startRow, int startCol, Maze maze) {
        this.maze = maze;

        this.size = SIZE;

        this.x = startCol * Maze.TILE_SIZE + (Maze.TILE_SIZE - SIZE) / 2.0;
        this.y = startRow * Maze.TILE_SIZE + (Maze.TILE_SIZE - SIZE) / 2.0;

        try {
            sprite = ImageIO.read(Player.class.getResource(PLAYER_SPRITE));
        } catch (Exception e) {
            sprite = null;
            System.err.println("Player sprite not found: " + e);
        }
    }

    public Image getSprite() { return sprite; }

    //for HUD
    public int getLives() { return lives; }
    public int getScore() { return score; }

    public void addScore(int amount) { score += amount; }
    public void setScore(int value) { score = value; }

    //handles losing lives and dieing
    public void loseLife() { lives--; }
    public boolean isDead() { return lives <= 0; }

    //Invincibility frames
    public boolean isInvincible() { return invincibleTimer > 0; }
    public void triggerInvincibility() { invincibleTimer = INVINCIBILITY_FRAMES; }
    public void tickInvincibility() { if (invincibleTimer > 0) invincibleTimer--; }

    //Flash frames after losing a life
    public boolean isFlashing() { return flashTimer > 0; }
    public void triggerFlash() { flashTimer = FLASH_FRAMES; }
    public void tickFlash() { if (flashTimer > 0) flashTimer--; }

    public double getFacingAngle() { return facingAngle; }

    //player movement
    public void move(boolean up, boolean down, boolean left, boolean right) {

        double dx = 0;
        double dy = 0;

        if (up)    dy -= speed;
        if (down)  dy += speed;
        if (left)  dx -= speed;
        if (right) dx += speed;

        if (dx != 0 && dy != 0) {
            dx *= 0.707;
            dy *= 0.707;
        }

        if (dx != 0 || dy != 0) {
            facingAngle = Math.atan2(dy, dx);
        }

        double newX = x + dx;
        double newY = y + dy;

        if (!collidesWithWall(newX, y)) x = newX;
        if (!collidesWithWall(x, newY)) y = newY;
    }

    //player stops at the wall
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

    //handles player reset
    public void reset() {
        lives = INITIAL_LIVES;
        score = 0;
    }
}
