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

/**
 * GameComponent handles:
 *  - Drawing the maze
 *  - Drawing the player
 *  - Smooth continuous movement
 *  - Diagonal movement
 *  - Key state tracking
 *  - A 60 FPS update loop
 *
 * It contains no game logic — that belongs in model classes.
 */
public class GameComponent extends JComponent {

    public static final int TILE_SIZE = 32;

    private Maze maze;
    private Player player;

    // Key state booleans for smooth movement
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // 60 FPS timer
    private Timer timer;

    public GameComponent() {
        this.maze = new Maze(MazeLayout.MAZE);

        // Start player at a walkable tile
        this.player = new Player(1, 1, maze);

        setFocusable(true);
        setupKeyBindings();

        // 60 FPS update loop
        timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    public Maze getMaze() {
        return maze;
    }

    /**
     * Sets up WASD key bindings using key-pressed and key-released events.
     * This allows smooth continuous movement and diagonal input.
     */
    private void setupKeyBindings() {

        // W pressed
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed W"), "pressUp");
        getActionMap().put("pressUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upPressed = true;
            }
        });

        // W released
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released W"), "releaseUp");
        getActionMap().put("releaseUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upPressed = false;
            }
        });

        // S pressed
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed S"), "pressDown");
        getActionMap().put("pressDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downPressed = true;
            }
        });

        // S released
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "releaseDown");
        getActionMap().put("releaseDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downPressed = false;
            }
        });

        // A pressed
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed A"), "pressLeft");
        getActionMap().put("pressLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftPressed = true;
            }
        });

        // A released
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released A"), "releaseLeft");
        getActionMap().put("releaseLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftPressed = false;
            }
        });

        // D pressed
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed D"), "pressRight");
        getActionMap().put("pressRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightPressed = true;
            }
        });

        // D released
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released D"), "releaseRight");
        getActionMap().put("releaseRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightPressed = false;
            }
        });
    }

    /**
     * Runs every frame (60 FPS).
     * Computes movement based on key states and moves the player.
     */
    private void updateGame() {

        double dx = 0;
        double dy = 0;

        if (upPressed)    dy -= player.getSpeed();
        if (downPressed)  dy += player.getSpeed();
        if (leftPressed)  dx -= player.getSpeed();
        if (rightPressed) dx += player.getSpeed();

        // Normalize diagonal movement
        if (dx != 0 && dy != 0) {
            dx *= 0.707;
            dy *= 0.707;
        }

        player.move(dx, dy);

        repaint();
    }

    /**
     * Draws the maze and the player.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw maze tiles
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                maze.getTile(r, c).draw(g2, TILE_SIZE);
            }
        }

        // Draw player (smaller than a tile)
        g2.setColor(Color.BLUE);
        g2.fillOval(
            (int) player.getX(),
            (int) player.getY(),
            player.getSize(),
            player.getSize()
        );
    }
}
