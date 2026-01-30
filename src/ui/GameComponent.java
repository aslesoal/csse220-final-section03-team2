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

    // Key state booleans
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private Timer timer;

    public GameComponent() {
        this.maze = new Maze(MazeLayout.MAZE);
        this.player = new Player(1, 1, maze);

        // Spawn 6 zombies on valid tiles
        for (int i = 0; i < 6; i++) {
            int[] pos = getRandomFloorTile();
            zombies.add(new Zombie(pos[0], pos[1], maze));
        }

        setFocusable(true);
        setupKeyBindings();

        timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    private int[] getRandomFloorTile() {
        Random rand = new Random();
        int row, col;

        do {
            row = rand.nextInt(maze.getRows());
            col = rand.nextInt(maze.getCols());
        } while (!maze.isWalkable(row, col));

        return new int[]{row, col};
    }

    private void setupKeyBindings() {
        // W
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed W"), "pressUp");
        getActionMap().put("pressUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { upPressed = true; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released W"), "releaseUp");
        getActionMap().put("releaseUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { upPressed = false; }
        });

        // S
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed S"), "pressDown");
        getActionMap().put("pressDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { downPressed = true; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "releaseDown");
        getActionMap().put("releaseDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { downPressed = false; }
        });

        // A
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed A"), "pressLeft");
        getActionMap().put("pressLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { leftPressed = true; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released A"), "releaseLeft");
        getActionMap().put("releaseLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { leftPressed = false; }
        });

        // D
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed D"), "pressRight");
        getActionMap().put("pressRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { rightPressed = true; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released D"), "releaseRight");
        getActionMap().put("releaseRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { rightPressed = false; }
        });
    }

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

        for (Zombie z : zombies) {
            z.update();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw maze
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                maze.getTile(r, c).draw(g2, TILE_SIZE);
            }
        }

        // Draw player
        g2.setColor(Color.BLUE);
        g2.fillOval(
            (int) player.getX(),
            (int) player.getY(),
            player.getSize(),
            player.getSize()
        );

        // Draw zombies
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
