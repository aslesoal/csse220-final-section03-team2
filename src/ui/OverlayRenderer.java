package ui;

import java.awt.*;
import model.GameStateManager;
import model.Player;

/*
 * Makes the different screens with their information
 * Generates the border during the danger, freeze, and double points
 */
public class OverlayRenderer {

    // Layout constants
    private static final int TITLE_Y_MAIN = 250;
    private static final int TITLE_Y_START = 310;
    private static final int TITLE_Y_NIGHT = 350;
    private static final int TITLE_Y_STATUS = 380;
    private static final int TITLE_Y_RULES = 420;

    private static final int PAUSE_Y_MAIN = 260;
    private static final int PAUSE_Y_SUB = 310;

    private static final int WIN_Y_MAIN = 240;
    private static final int WIN_Y_SCORE = 300;
    private static final int WIN_Y_PROMPT = 350;

    private static final int GAMEOVER_Y_MAIN = 240;
    private static final int GAMEOVER_Y_SCORE = 300;
    private static final int GAMEOVER_Y_PROMPT = 350;

    private static final int RULES_TITLE_Y = 80;
    private static final int RULES_TEXT_START_Y = 100;
    private static final int RULES_LINE_SPACING = 30;
    private static final int RULES_RETURN_Y_OFFSET = 80;

    private void drawCenteredString(Graphics2D g2, String text, int y, int width) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (width - textWidth) / 2;
        g2.drawString(text, x, y);
    }

    public void render(Graphics2D g2, GameStateManager gsm, Player player,
                       java.util.List<String> rulesText,
                       boolean nightMode,
                       int width, int height) {

        // TITLE SCREEN
        if (gsm.isTitle() || gsm.getTitleAlpha() > 0f) {
            float a = gsm.getTitleAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.7f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(1f, 1f, 1f, a));
            drawCenteredString(g2, "Zombie Maze", TITLE_Y_MAIN, width);

            g2.setFont(new Font("Arial", Font.BOLD, 24));
            drawCenteredString(g2, "Press ENTER to Start", TITLE_Y_START, width);
            drawCenteredString(g2, "Press N to Toggle Night Mode", TITLE_Y_NIGHT, width);

            String nightStatus = nightMode ? "Night Mode: ON" : "Night Mode: OFF";
            drawCenteredString(g2, nightStatus, TITLE_Y_STATUS, width);

            drawCenteredString(g2, "Press H for Rules", TITLE_Y_RULES, width);
        }

        // RULES SCREEN
        if (gsm.isRules()) {
            g2.setColor(new Color(0f, 0f, 0f, 0.85f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 36));
            g2.setColor(Color.WHITE);
            drawCenteredString(g2, "GAME RULES", RULES_TITLE_Y, width);

            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            int y = RULES_TEXT_START_Y;

            for (String line : rulesText) {
                g2.drawString(line, 20, y);
                y += RULES_LINE_SPACING;
            }

            g2.setFont(new Font("Arial", Font.BOLD, 22));
            drawCenteredString(g2, "Press H to return", height - RULES_RETURN_Y_OFFSET, width);
        }

        // PAUSE
        if (gsm.isPaused()) {
            float a = gsm.getPauseAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.6f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(1f, 1f, 0f, a));
            drawCenteredString(g2, "PAUSED", PAUSE_Y_MAIN, width);

            g2.setFont(new Font("Arial", Font.BOLD, 24));
            drawCenteredString(g2, "Press P to Resume", PAUSE_Y_SUB, width);
        }

        // WIN
        if (gsm.isWin()) {
            float a = gsm.getWinAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.6f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(0f, 1f, 0f, a));
            drawCenteredString(g2, "YOU WIN!", WIN_Y_MAIN, width);

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            drawCenteredString(g2, "Final Score: " + player.getScore(), WIN_Y_SCORE, width);

            g2.setFont(new Font("Arial", Font.BOLD, 24));
            drawCenteredString(g2, "Press L to view Leaderboard", WIN_Y_PROMPT, width);
        }

        // GAME OVER
        if (gsm.isGameOver()) {
            float a = gsm.getGameOverAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.6f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(1f, 0f, 0f, a));
            drawCenteredString(g2, "GAME OVER", GAMEOVER_Y_MAIN, width);

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            drawCenteredString(g2, "Final Score: " + player.getScore(), GAMEOVER_Y_SCORE, width);

            g2.setFont(new Font("Arial", Font.BOLD, 24));
            drawCenteredString(g2, "Press L to view Leaderboard", GAMEOVER_Y_PROMPT, width);
        }
    }
}
