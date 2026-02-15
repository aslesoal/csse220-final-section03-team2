package model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

public class Renderer {

    private void drawCenteredString(Graphics2D g2, String text, int y, int width) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (width - textWidth) / 2;
        g2.drawString(text, x, y);
    }

    private void renderLeaderboardInline(Graphics2D g2, int width) {
        java.util.List<String[]> scores = ScoreManager.loadScores();

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        drawCenteredString(g2, "Leaderboard", 360, width);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));

        int limit = Math.min(10, scores.size());
        int y = 390;

        for (int i = 0; i < limit; i++) {
            String[] s = scores.get(i);
            String line = (i + 1) + ". " + s[0] + " — " + s[1];
            drawCenteredString(g2, line, y, width);
            y += 22;
        }

        if (limit == 0) {
            drawCenteredString(g2, "No scores yet.", y, width);
        }
    }

    public void renderWorld(Graphics2D g2, Maze maze, Player player,
                            List<Zombie> zombies,
                            List<Collectible> collectibles,
                            int width, int height) {

        int mazeSize = GameConstant.TILE_SIZE * maze.getRows();
        int offsetX = (width - mazeSize) / 2;
        int offsetY = (height - mazeSize) / 2;

        g2.translate(offsetX, offsetY);

        maze.draw(g2);

        g2.setColor(Color.YELLOW);
        for (Collectible c : collectibles) {
            if (!c.isCollected()) {
                g2.fillOval((int) c.getX(), (int) c.getY(),
                        Collectible.SIZE, Collectible.SIZE);
            }
        }

        for (Zombie z : zombies) {
            if (z.getSprite() != null) {
                drawRotatedSprite(g2, z.getSprite(), z.getX(), z.getY(),
                        Zombie.SIZE, Zombie.SIZE, z.getFacingAngle());
            } else {
                g2.setColor(Color.RED);
                g2.fillOval((int) z.getX(), (int) z.getY(),
                        Zombie.SIZE, Zombie.SIZE);
            }
        }

        if (player.getSprite() != null) {
            drawRotatedSprite(g2, player.getSprite(), player.getX(), player.getY(),
                    Player.SIZE, Player.SIZE, player.getFacingAngle());
        } else {
            g2.setColor(Color.BLUE);
            g2.fillOval((int) player.getX(), (int) player.getY(),
                    Player.SIZE, Player.SIZE);
        }

        g2.translate(-offsetX, -offsetY);
    }

    private void drawRotatedSprite(Graphics2D g2, Image sprite,
                                   double x, double y, int width, int height,
                                   double angle) {

        int cx = (int) x + width / 2;
        int cy = (int) y + height / 2;

        AffineTransform old = g2.getTransform();
        g2.rotate(angle, cx, cy);
        g2.drawImage(sprite, (int) x, (int) y, width, height, null);
        g2.setTransform(old);
    }

    // ⭐ UPDATED HUD ⭐
    public void renderHUD(Graphics2D g2, Player player, boolean danger, int width, int height) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, width, 50);

        g2.setColor(Color.WHITE);

        // TOP LINE — Instructions (bold + spaced out)
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("R: Restart", 10, 20);
        g2.drawString("P: Pause", 140, 20);
        g2.drawString("L: Leaderboard", 260, 20);

        // SECOND LINE — Lives + Score (slightly larger + bold)
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Lives: " + player.getLives(), 10, 40);
        g2.drawString("Score: " + player.getScore(), 150, 40);

        if (danger) {
            g2.setColor(new Color(1f, 0f, 0f, 0.4f));
            int t = 6;
            g2.fillRect(0, 0, width, t);
            g2.fillRect(0, height - t, width, t);
            g2.fillRect(0, 0, t, height);
            g2.fillRect(width - t, 0, t, height);
        }
    }

    public void renderFlash(Graphics2D g2, Player player, int width, int height) {
        if (player.isFlashing()) {
            g2.setColor(new Color(1f, 0f, 0f, 0.35f));
            g2.fillRect(0, 0, width, height);
        }
    }

    public void renderOverlays(Graphics2D g2, GameStateManager gsm, Player player, int width, int height) {

        if (gsm.isTitle() || gsm.getTitleAlpha() > 0f) {
            float a = gsm.getTitleAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.7f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(1f, 1f, 1f, a));
            drawCenteredString(g2, "Zombie Maze", 250, width);

            g2.setFont(new Font("Arial", Font.BOLD, 24));
            drawCenteredString(g2, "Press ENTER to Start", 310, width);
        }

        if (gsm.isPaused()) {
            float a = gsm.getPauseAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.6f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(1f, 1f, 0f, a));
            drawCenteredString(g2, "PAUSED", 260, width);

            g2.setFont(new Font("Arial", Font.BOLD, 24));
            drawCenteredString(g2, "Press P to Resume", 310, width);
        }

        if (gsm.isWin()) {
            float a = gsm.getWinAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.6f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(0f, 1f, 0f, a));
            drawCenteredString(g2, "YOU WIN!", 240, width);

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            drawCenteredString(g2, "Final Score: " + player.getScore(), 300, width);

            if (gsm.isNewHighScore()) {
                float hs = gsm.getHighScoreAlpha();
                g2.setFont(new Font("Arial", Font.BOLD, 36));
                g2.setColor(new Color(1f, 0.84f, 0f, hs));
                drawCenteredString(g2, "NEW HIGH SCORE!", 180, width);
            }

            renderLeaderboardInline(g2, width);
        }

        if (gsm.isGameOver()) {
            float a = gsm.getGameOverAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.6f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(1f, 0f, 0f, a));
            drawCenteredString(g2, "GAME OVER", 240, width);

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            drawCenteredString(g2, "Final Score: " + player.getScore(), 300, width);

            if (gsm.isNewHighScore()) {
                float hs = gsm.getHighScoreAlpha();
                g2.setFont(new Font("Arial", Font.BOLD, 36));
                g2.setColor(new Color(1f, 0.84f, 0f, hs));
                drawCenteredString(g2, "NEW HIGH SCORE!", 180, width);
            }

            g2.setFont(new Font("Arial", Font.BOLD, 24));
            drawCenteredString(g2, "Press R to Restart  |  L: Leaderboard", 330, width);

            renderLeaderboardInline(g2, width);
        }
    }
}
