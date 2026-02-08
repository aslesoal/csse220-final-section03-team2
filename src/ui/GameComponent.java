package ui;

import javax.swing.JPanel;
import javax.swing.Timer;

import model.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class GameComponent extends JPanel implements KeyListener {

    private Maze maze;
    private Player player;
    private ArrayList<Zombie> zombies = new ArrayList<>();
    private ArrayList<Collectible> collectibles = new ArrayList<>();

    private boolean up, down, left, right;

    private boolean exitUnlocked = false;

    // Game end states
    private boolean win = false;
    private boolean gameOver = false;
    private boolean paused = false;

    // Fade animation
    private float winAlpha = 0f;
    private float gameOverAlpha = 0f;
    private float pauseAlpha = 0f;

    public GameComponent() {
        setFocusable(true);
        addKeyListener(this);

        maze = new Maze(MazeLayout.MAZE);
        player = new Player(1, 1, maze);

        spawnZombies();
        spawnCollectibles();

        Timer timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    // ---------------------------------------------------------
    // Restart System
    // ---------------------------------------------------------

    private void restartGame() {
        win = false;
        gameOver = false;
        paused = false;

        winAlpha = 0f;
        gameOverAlpha = 0f;
        pauseAlpha = 0f;

        player = new Player(1, 1, maze);

        spawnZombies();
        spawnCollectibles();

        up = down = left = right = false;

        repaint();
    }

    // ---------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------

    private int[] getRandomFloorTile(int rowMin, int rowMax) {
        Random rand = new Random();
        int row, col;

        do {
            row = rand.nextInt(rowMax - rowMin + 1) + rowMin;
            col = rand.nextInt(maze.getCols());
        } while (!maze.isWalkable(row, col));

        return new int[]{row, col};
    }

    private boolean tooClose(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy) < 40;
    }

    private boolean collectibleTooClose(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy) < 32;
    }

    private boolean tooCloseToZombie(double x, double y) {
        for (Zombie z : zombies) {
            double dx = x - z.getX();
            double dy = y - z.getY();
            if (Math.sqrt(dx * dx + dy * dy) < 40) return true;
        }
        return false;
    }

    // ---------------------------------------------------------
    // Spawning
    // ---------------------------------------------------------

    private void spawnZombies() {
        zombies.clear();

        int rows = maze.getRows();
        int mid = rows / 2;

        int zombiesTop = 4;
        int zombiesBottom = 4;

        double playerX = player.getX();
        double playerY = player.getY();
        double minDist = 4 * GameConstant.TILE_SIZE;

        for (int i = 0; i < zombiesTop; i++) {
            int[] pos;
            double x, y;

            while (true) {
                pos = getRandomFloorTile(1, mid - 1);
                x = pos[1] * GameConstant.TILE_SIZE + 4;
                y = pos[0] * GameConstant.TILE_SIZE + 4;

                boolean ok = true;

                for (Zombie z : zombies) {
                    if (tooClose(x, y, z.getX(), z.getY())) ok = false;
                }

                if (Math.sqrt(Math.pow(x - playerX, 2) + Math.pow(y - playerY, 2)) < minDist)
                    ok = false;

                if (ok) break;
            }

            zombies.add(new Zombie(pos[0], pos[1], maze));
        }

        for (int i = 0; i < zombiesBottom; i++) {
            int[] pos;
            double x, y;

            while (true) {
                pos = getRandomFloorTile(mid + 1, rows - 2);
                x = pos[1] * GameConstant.TILE_SIZE + 4;
                y = pos[0] * GameConstant.TILE_SIZE + 4;

                boolean ok = true;

                for (Zombie z : zombies) {
                    if (tooClose(x, y, z.getX(), z.getY())) ok = false;
                }

                if (Math.sqrt(Math.pow(x - playerX, 2) + Math.pow(y - playerY, 2)) < minDist)
                    ok = false;

                if (ok) break;
            }

            zombies.add(new Zombie(pos[0], pos[1], maze));
        }
    }

    private void spawnCollectibles() {
        collectibles.clear();

        int total = 8;

        for (int i = 0; i < total; i++) {
            int[] pos;
            double x, y;

            while (true) {
                pos = getRandomFloorTile(1, maze.getRows() - 2);

                x = pos[1] * GameConstant.TILE_SIZE + 8;
                y = pos[0] * GameConstant.TILE_SIZE + 8;

                boolean ok = true;

                for (Collectible c : collectibles) {
                    if (collectibleTooClose(x, y, c.getX(), c.getY())) ok = false;
                }

                if (ok && tooCloseToZombie(x, y)) ok = false;

                if (ok) break;
            }

            collectibles.add(new Collectible(pos[0], pos[1]));
        }
    }

    // ---------------------------------------------------------
    // Game Update Loop
    // ---------------------------------------------------------

    private void updateGame() {
        if (paused || gameOver || win) {
            repaint();
            return;
        }

        player.tickInvincibility();
        player.tickFlash();

        double speed = player.getSpeed();
        double dx = 0, dy = 0;

        if (up) dy -= speed;
        if (down) dy += speed;
        if (left) dx -= speed;
        if (right) dx += speed;

        if (dx != 0 || dy != 0) player.move(dx, dy);

        // Zombie collisions → lose life (no teleport)
        for (Zombie z : zombies) {
            z.update();

            if (!z.isInCollisionCooldown()) {
                if (!player.isInvincible() &&
                    overlaps(player.getX(), player.getY(), Player.SIZE,
                             z.getX(), z.getY(), Zombie.SIZE)) {

                    z.triggerCollisionCooldown();
                    player.loseLife();
                    player.triggerInvincibility();
                    player.triggerFlash();

                    if (player.isDead()) {
                        gameOver = true;
                        up = down = left = right = false;
                    }
                }
            }
        }

        // Collectibles → score
        for (Collectible c : collectibles) {
            if (!c.isCollected()) {
                if (overlaps(player.getX(), player.getY(), Player.SIZE,
                             c.getX(), c.getY(), Collectible.SIZE)) {
                    c.collect();
                    player.addScore(10);
                }
            }
        }

        // Unlock exit
        boolean allCollected = true;
        for (Collectible c : collectibles) {
            if (!c.isCollected()) allCollected = false;
        }
        if (allCollected) exitUnlocked = true;

        // Win condition
        int row = (int)(player.getY() / GameConstant.TILE_SIZE);
        int col = (int)(player.getX() / GameConstant.TILE_SIZE);

        if (exitUnlocked && maze.isExit(row, col)) {
            win = true;
            up = down = left = right = false;
        }

        repaint();
    }

    private boolean overlaps(double x1, double y1, int s1,
                             double x2, double y2, int s2) {
        return x1 < x2 + s2 &&
               x1 + s1 > x2 &&
               y1 < y2 + s2 &&
               y1 + s1 > y2;
    }

    // ---------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int tileSize = GameConstant.TILE_SIZE;

        // Draw maze
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                maze.getTile(r, c).draw(g2, tileSize);
            }
        }

        // Draw player
        if (player.getSprite() != null) {
            g2.drawImage(player.getSprite(),
                         (int) player.getX(),
                         (int) player.getY(),
                         Player.SIZE, Player.SIZE, null);
        } else {
            g2.setColor(Color.BLUE);
            g2.fillOval((int) player.getX(), (int) player.getY(),
                        Player.SIZE, Player.SIZE);
        }

        // Draw zombies
        for (Zombie z : zombies) {
            if (z.getSprite() != null) {
                g2.drawImage(z.getSprite(),
                             (int) z.getX(),
                             (int) z.getY(),
                             Zombie.SIZE, Zombie.SIZE, null);
            } else {
                g2.setColor(Color.RED);
                g2.fillOval((int) z.getX(), (int) z.getY(),
                            Zombie.SIZE, Zombie.SIZE);
            }
        }

        // Draw collectibles
        g2.setColor(Color.YELLOW);
        for (Collectible c : collectibles) {
            if (!c.isCollected()) {
                g2.fillOval((int) c.getX(), (int) c.getY(),
                            Collectible.SIZE, Collectible.SIZE);
            }
        }

        // ---------------------------------------------------------
        // HUD BAR (never covers maze)
        // ---------------------------------------------------------
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, getWidth(), 40);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Lives: " + player.getLives(), 10, 25);
        g2.drawString("Score: " + player.getScore(), 150, 25);
        g2.drawString("Press R to Restart", 300, 25);

        // ---------------------------------------------------------
        // Damage flash (short red overlay)
        // ---------------------------------------------------------
        if (player.isFlashing()) {
            g2.setColor(new Color(1f, 0f, 0f, 0.35f));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // ---------------------------------------------------------
        // GAME OVER FADE-IN
        // ---------------------------------------------------------
        if (gameOver) {
            if (gameOverAlpha < 1f) {
                gameOverAlpha += 0.01f;
                if (gameOverAlpha > 1f) gameOverAlpha = 1f;
                repaint();
            }

            g2.setColor(new Color(0f, 0f, 0f, gameOverAlpha * 0.6f));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(1f, 0f, 0f, gameOverAlpha));
            g2.drawString("GAME OVER", 150, 300);
        }

        // ---------------------------------------------------------
        // WIN SCREEN FADE-IN
        // ---------------------------------------------------------
        if (win) {
            if (winAlpha < 1f) {
                winAlpha += 0.01f;
                if (winAlpha > 1f) winAlpha = 1f;
                repaint();
            }

            g2.setColor(new Color(0f, 0f, 0f, winAlpha * 0.6f));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(0f, 1f, 0f, winAlpha));
            g2.drawString("YOU WIN!", 150, 300);
        }

        // ---------------------------------------------------------
        // PAUSE SCREEN FADE-IN
        // ---------------------------------------------------------
        if (paused) {
            if (pauseAlpha < 1f) {
                pauseAlpha += 0.05f;
                if (pauseAlpha > 1f) pauseAlpha = 1f;
                repaint();
            }

            g2.setColor(new Color(0f, 0f, 0f, pauseAlpha * 0.6f));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(1f, 1f, 0f, pauseAlpha));
            g2.drawString("PAUSED", 180, 300);

            g2.setFont(new Font("Arial", Font.BOLD, 24));
            g2.drawString("Press P to Resume", 180, 350);
        } else {
            pauseAlpha = 0f;
        }
    }

    // ---------------------------------------------------------
    // Keyboard Input
    // ---------------------------------------------------------

    @Override
    public void keyPressed(KeyEvent e) {

        // Restart key works anytime
        if (e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
            return;
        }

        // Pause toggle
        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (!win && !gameOver) {
                paused = !paused;
            }
            return;
        }

        if (paused || gameOver || win) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: up = true; break;
            case KeyEvent.VK_S: down = true; break;
            case KeyEvent.VK_A: left = true; break;
            case KeyEvent.VK_D: right = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (paused || gameOver || win) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: up = false; break;
            case KeyEvent.VK_S: down = false; break;
            case KeyEvent.VK_A: left = false; break;
            case KeyEvent.VK_D: right = false; break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
