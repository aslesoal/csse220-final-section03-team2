package ui;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.Timer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.ActionEvent;

import model.Maze;
import model.MazeLayout;
import model.Player;
import model.Zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameComponent extends JComponent {

    public static final int TILE_SIZE = 32;

    private Maze maze;
    private Player player;
    private List<Zombie> zombies = new ArrayList<>();

    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private Timer timer;

    public GameComponent() {
        this.maze = new Maze(MazeLayout.MAZE);
        this.player = new Player(1, 1, maze);

        spawnZombiesHalfAndHalf();

        setFocusable(true);
        setupKeyBindings();

        timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    // ------------------------------------------------------------
    // SPAWNING LOGIC
    // ------------------------------------------------------------

    private void spawnZombiesHalfAndHalf() {
        int rows = maze.getRows();
        int mid = rows / 2;

        for (int i = 0; i < 4; i++) {
            int[] pos;
            do {
                pos = getRandomFloorTileInRange(0, mid - 1);
            } while (tooCloseToPlayer(pos[0], pos[1]) ||
                     tooCloseToOtherZombies(pos[0], pos[1]));

            zombies.add(new Zombie(pos[0], pos[1], maze));
        }

        for (int i = 0; i < 4; i++) {
            int[] pos;
            do {
                pos = getRandomFloorTileInRange(mid, rows - 1);
            } while (tooCloseToPlayer(pos[0], pos[1]) ||
                     tooCloseToOtherZombies(pos[0], pos[1]));

            zombies.add(new Zombie(pos[0], pos[1], maze));
        }
    }

    private int[] getRandomFloorTileInRange(int rowMin, int rowMax) {
        Random rand = new Random();
        int row, col;

        do {
            row = rand.nextInt(rowMax - rowMin + 1) + rowMin;
            col = rand.nextInt(maze.getCols());
        } while (!maze.isWalkable(row, col));

        return new int[]{row, col};
    }

    private int tileDistance(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    private boolean tooCloseToPlayer(int row, int col) {
        int pr = (int)(player.getY() / TILE_SIZE);
        int pc = (int)(player.getX() / TILE_SIZE);

        return tileDistance(row, col, pr, pc) < 4;
    }

    private boolean tooCloseToOtherZombies(int row, int col) {
        for (Zombie z : zombies) {
            int zr = (int)(z.getY() / TILE_SIZE);
            int zc = (int)(z.getX() / TILE_SIZE);

            if (tileDistance(row, col, zr, zc) < 4) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------
    // INPUT HANDLING
    // ------------------------------------------------------------

    private void setupKeyBindings() {

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed W"), "pressUp");
        getActionMap().put("pressUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { upPressed = true; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released W"), "releaseUp");
        getActionMap().put("releaseUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { upPressed = false; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed S"), "pressDown");
        getActionMap().put("pressDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { downPressed = true; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "releaseDown");
        getActionMap().put("releaseDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { downPressed = false; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed A"), "pressLeft");
        getActionMap().put("pressLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { leftPressed = true; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released A"), "releaseLeft");
        getActionMap().put("releaseLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { leftPressed = false; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed D"), "pressRight");
        getActionMap().put("pressRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { rightPressed = true; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released D"), "releaseRight");
        getActionMap().put("releaseRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { rightPressed = false; }
        });
    }

    // ------------------------------------------------------------
    // GAME LOOP
    // ------------------------------------------------------------

    private void updateGame() {
        double dx = 0;
        double dy = 0;

        if (upPressed)    dy -= player.getSpeed();
        if (downPressed)  dy += player.getSpeed();
        if (leftPressed)  dx -= player.getSpeed();
        if (rightPressed) dx += player.getSpeed();

        if (dx != 0 && dy != 0) {
            dx *= 0.707;
            dy *= 0.707;
        }

        player.move(dx, dy);

        for (Zombie z : zombies) {
            z.update();
        }

        handleZombieCollisions();

        repaint();
    }

    // ------------------------------------------------------------
    // ZOMBIE–ZOMBIE COLLISION HANDLING (SAFE)
    // ------------------------------------------------------------

    private void handleZombieCollisions() {
        for (int i = 0; i < zombies.size(); i++) {
            Zombie a = zombies.get(i);

            for (int j = i + 1; j < zombies.size(); j++) {
                Zombie b = zombies.get(j);

                double dx = b.getX() - a.getX();
                double dy = b.getY() - a.getY();

                double dist = Math.sqrt(dx * dx + dy * dy);
                double minDist = Zombie.SIZE;

                if (dist < minDist) {

                    b.chooseNewDirection();

                    double overlap = minDist - dist;

                    if (dist == 0) {
                        dx = 1;
                        dy = 0;
                        dist = 1;
                    }

                    double pushX = (dx / dist) * (overlap / 2);
                    double pushY = (dy / dist) * (overlap / 2);

                    double aNewX = a.getX() - pushX;
                    double aNewY = a.getY() - pushY;

                    double bNewX = b.getX() + pushX;
                    double bNewY = b.getY() + pushY;

                    boolean aSafe = !a.collidesWithWall(aNewX, aNewY);
                    boolean bSafe = !b.collidesWithWall(bNewX, bNewY);

                    if (aSafe) a.setPosition(aNewX, aNewY);
                    if (bSafe) b.setPosition(bNewX, bNewY);
                }
            }
        }
    }

    // ------------------------------------------------------------
    // DRAWING
    // ------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                maze.getTile(r, c).draw(g2, TILE_SIZE);
            }
        }

        g2.setColor(Color.BLUE);
        g2.fillOval(
            (int) player.getX(),
            (int) player.getY(),
            player.getSize(),
            player.getSize()
        );

        g2.setColor(Color.RED);
        for (Zombie z : zombies) {
            g2.fillOval(
                (int) z.getX(),
                (int) z.getY(),
                z.getSize(),
                z.getSize()
            );
        }
    }
}
