package ui;

import javax.swing.JFrame;
import model.Maze;
import model.MazeLayout;

public class GameWindow {

    public static void show() {
        JFrame frame = new JFrame("Maze Game");

        GameComponent component = new GameComponent();
        frame.add(component);

        // Optional: size based on maze dimensions
        int tileSize = GameComponent.TILE_SIZE;
        Maze maze = new Maze(MazeLayout.MAZE);

        frame.setSize(
            maze.getCols() * tileSize + 16,
            maze.getRows() * tileSize + 39
        );

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        component.requestFocusInWindow();
    }
}
