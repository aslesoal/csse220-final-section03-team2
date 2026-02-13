package model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

public class Renderer {

    // Centered text helper
    private void drawCenteredString(Graphics2D g2, String text, int y, int width) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (width - textWidth) / 2;
        g2.drawString(text, x, y);
    }

    // WORLD RENDERING
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

    // HUD
    public void renderHUD(Graphics2D g2, Player player, boolean danger, int width, int height) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, width, 40);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Lives: " + player.getLives(), 10, 25);
        g2.drawString("Score: " + player.getScore(), 150, 25);
        g2.drawString("R: Restart  |  P: Pause", 300, 25);

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

    // OVERLAYS (WIN/LOSS SHOW SCORE)
    public void renderOverlays(Graphics2D g2, GameStateManager gsm, Player player, int width, int height) {

        // TITLE SCREEN
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

        // PAUSE SCREEN
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

        // WIN SCREEN (SHOW SCORE)
        if (gsm.isWin()) {
            float a = gsm.getWinAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.6f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(0f, 1f, 0f, a));
            drawCenteredString(g2, "YOU WIN!", 240, width);

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            drawCenteredString(g2, "Final Score: " + player.getScore(), 300, width);
        }

        // GAME OVER SCREEN (SHOW SCORE)
        if (gsm.isGameOver()) {
            float a = gsm.getGameOverAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.6f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(1f, 0f, 0f, a));
            drawCenteredString(g2, "GAME OVER", 240, width);

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            drawCenteredString(g2, "Final Score: " + player.getScore(), 300, width);

            g2.setFont(new Font("Arial", Font.BOLD, 24));
            drawCenteredString(g2, "Press R to Restart", 350, width);
        }
    }
}
