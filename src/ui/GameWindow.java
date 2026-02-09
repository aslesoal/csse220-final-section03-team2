package ui;

import javax.swing.JFrame;

/**
 * GameWindow is required by MainApp. It simply creates the JFrame
 * and attaches the GameComponent. All game logic lives elsewhere.
 */
public class GameWindow {

    public static void show() {
        JFrame frame = new JFrame("Zombie Maze");

        GameComponent game = new GameComponent();
        frame.add(game);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(600, 600); // Adjust if needed
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.requestFocusInWindow();
    }
}
