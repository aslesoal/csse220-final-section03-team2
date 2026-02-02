package ui;

import javax.swing.JFrame;

/**
 * GameWindow creates the main application window and
 * embeds the GameComponent inside it.
 *
 * This class contains no game logic; it only manages the UI frame.
 */
public class GameWindow {

    /**
     * Creates and displays the game window.
     */
	public static void show() {
	    System.out.println("Creating JFrame...");
	    JFrame frame = new JFrame("Maze Game");

	    System.out.println("Creating GameComponent...");
	    GameComponent comp = new GameComponent();

	    System.out.println("Adding component...");
	    frame.add(comp);

	    System.out.println("Setting size...");
	    frame.setSize(600, 600);

	    System.out.println("Showing window...");
	    frame.setVisible(true);

	    System.out.println("Window should now be visible.");
	}

    
}
