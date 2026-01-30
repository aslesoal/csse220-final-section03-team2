package ui;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;

import model.Maze;
import model.MazeLayout;

public class GameComponent extends JComponent {

    public static final int TILE_SIZE = 32;

    private Maze maze;

    public GameComponent() {
        this.maze = new Maze(MazeLayout.MAZE);
        setFocusable(true);
    }

    public Maze getMaze() {
        return maze;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw the maze
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                maze.getTile(r, c).draw(g2, TILE_SIZE);
            }
        }
    }
}
