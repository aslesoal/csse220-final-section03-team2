package ui;

import java.awt.*;
import model.Player;

/*
 * Handles the output of the HUD
 */
public class HUD {

    // Constants for layout 
    private static final int BAR_HEIGHT = 60;
    private static final int INSTRUCTION_Y = 22;
    private static final int STATS_Y = 50;
    private static final int DANGER_BORDER_THICKNESS = 6;

    public void render(Graphics2D g2, Player player, boolean danger, int width, int height) {

        // Background bar
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, width, BAR_HEIGHT);

        g2.setColor(Color.WHITE);

        // Instructions
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("R: Restart   P: Pause   L: Leaderboard   N: Night Mode   H: Rules",
                10, INSTRUCTION_Y);

        // Lives + Score
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Lives: " + player.getLives(), 10, STATS_Y);
        g2.drawString("Score: " + player.getScore(), 150, STATS_Y);

        // Danger border
        if (danger) {
            g2.setColor(new Color(1f, 0f, 0f, 0.4f));
            int t = DANGER_BORDER_THICKNESS;

            g2.fillRect(0, 0, width, t);
            g2.fillRect(0, height - t, width, t);
            g2.fillRect(0, 0, t, height);
            g2.fillRect(width - t, 0, t, height);
        }
    }
}
