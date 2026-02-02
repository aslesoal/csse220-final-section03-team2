package ui;

import javax.swing.JPanel;
import javax.swing.Timer;

import model.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * Main game panel responsible for rendering and updating the game.
 * Handles player movement, zombie AI, collectible logic, and drawing.
 */
public class GameComponent extends JPanel implements KeyListener {

    private Maze maze;
    private Player player;
    private ArrayList<Zombie> zombies = new ArrayList<>();
    private ArrayList<Collectible> collectibles = new ArrayList<>();

    // Movement flags
    private boolean up, down, left, right;

    /**
     * Constructs the game component, loads the maze, player,
     * zombies, collectibles, and starts the update loop.
     */
    public GameComponent() {
        setFocusable(true);
        addKeyListener(this);

        maze = new Maze(MazeLayout.MAZE);
        player = new Player(1, 1, maze);

        spawnZombies();        // 6 zombies, safe placement
        spawnCollectibles();   // 8 collectibles, safe placement

        Timer timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    // ---------------------------------------------------------
    // Helper Methods for Spawning
    // ---------------------------------------------------------

    /**
     * Finds a random walkable tile within a given row range.
     */
    private int[] getRandomFloorTile(int rowMin, int rowMax) {
        Random rand = new Random();
        int row, col;

        do {
            row = rand.nextInt(rowMax - rowMin + 1) + rowMin;
            col = rand.nextInt(maze.getCols());
        } while (!maze.isWalkable(row, col));

        return new int[]{row, col};
    }

    /**
     * Ensures zombies do not spawn too close together.
     */
    private boolean tooClose(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy) < 40;
    }

    /**
     * Ensures collectibles do not overlap each other.
     */
    private boolean collectibleTooClose(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy) < 32;
    }

    /**
     * Ensures collectibles do not spawn too close to zombies.
     */
    private boolean tooCloseToZombie(double x, double y) {
        for (Zombie z : zombies) {
            double dx = x - z.getX();
            double dy = y - z.getY();
            if (Math.sqrt(dx * dx + dy * dy) < 40) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------
    // Zombie Spawning (4 total)
    // ---------------------------------------------------------

    /**
     * Spawns 4 zombies:
     * 2 in the top half of the maze,
     * 2 in the bottom half.
     * Ensures valid tiles and no overlapping.
     */
    private void spawnZombies() {
        zombies.clear();

        int rows = maze.getRows();
        int mid = rows / 2;

        int zombiesTop = 3;
        int zombiesBottom = 3;

        // Top half zombies
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
                if (ok) break;
            }

            zombies.add(new Zombie(pos[0], pos[1], maze));
        }

        // Bottom half zombies
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
                if (ok) break;
            }

            zombies.add(new Zombie(pos[0], pos[1], maze));
        }
    }

    // ---------------------------------------------------------
    // Collectible Spawning (8 total)
    // ---------------------------------------------------------

    /**
     * Spawns 8 collectibles on valid floor tiles.
     * Ensures no overlap with walls, zombies, or other collectibles.
     */
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

                // Check against other collectibles
                for (Collectible c : collectibles) {
                    if (collectibleTooClose(x, y, c.getX(), c.getY())) {
                        ok = false;
                        break;
                    }
                }

                // Check against zombies
                if (ok && tooCloseToZombie(x, y)) {
                    ok = false;
                }

                if (ok) break;
            }

            collectibles.add(new Collectible(pos[0], pos[1]));
        }
    }

    // ---------------------------------------------------------
    // Game Update Loop
    // ---------------------------------------------------------

    private void updateGame() {
        double speed = player.getSpeed();
        double dx = 0, dy = 0;

        if (up) dy -= speed;
        if (down) dy += speed;
        if (left) dx -= speed;
        if (right) dx += speed;

        if (dx != 0 || dy != 0) player.move(dx, dy);

        // Update zombies and check collisions
        for (Zombie z : zombies) {
            z.update();

            if (!z.isInCollisionCooldown()) {
                if (overlaps(player.getX(), player.getY(), Player.SIZE,
                             z.getX(), z.getY(), Zombie.SIZE)) {

                    z.triggerCollisionCooldown();
                    player.setPosition(
                        1 * GameConstant.TILE_SIZE + 7,
                        1 * GameConstant.TILE_SIZE + 7
                    );
                }
            }
        }

        // Collectible pickups
        for (Collectible c : collectibles) {
            if (!c.isCollected()) {
                if (overlaps(player.getX(), player.getY(), Player.SIZE,
                             c.getX(), c.getY(), Collectible.SIZE)) {
                    c.collect();
                }
            }
        }

        // Win detection
        int row = (int)(player.getY() / GameConstant.TILE_SIZE);
        int col = (int)(player.getX() / GameConstant.TILE_SIZE);
        if (maze.isExit(row, col)) {
            System.out.println("You win!");
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
    }

    // ---------------------------------------------------------
    // Keyboard Input
    // ---------------------------------------------------------

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: up = true; break;
            case KeyEvent.VK_S: down = true; break;
            case KeyEvent.VK_A: left = true; break;
            case KeyEvent.VK_D: right = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
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
