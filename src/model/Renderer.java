package model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

public class Renderer {

    // Freeze border state
    private long freezeStartTime = 0;
    private boolean freezeActive = false;

    // Double Points border state
    private boolean doublePointsActive = false;
    private long doublePointsStartTime = 0L;

    // RULES TEXT (loaded by GameComponent)
    private List<String> rulesText;

    public void setRulesText(List<String> lines) {
        this.rulesText = lines;
    }

    public void activateFreeze() {
        freezeActive = true;
        freezeStartTime = System.currentTimeMillis();
    }

    public void deactivateFreeze() {
        freezeActive = false;
    }

    public void activateDoublePoints() {
        doublePointsActive = true;
        doublePointsStartTime = System.currentTimeMillis();
    }

    public void deactivateDoublePoints() {
        doublePointsActive = false;
    }

    private void drawCenteredString(Graphics2D g2, String text, int y, int width) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (width - textWidth) / 2;
        g2.drawString(text, x, y);
    }

    // ---------------------------------------------------------
    // WORLD RENDERING
    // ---------------------------------------------------------
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

    // ---------------------------------------------------------
    // NIGHT MODE
    // ---------------------------------------------------------
    public void renderNightMode(Graphics2D g2, Player player, Maze maze, int width, int height) {

        BufferedImage darkness = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gDark = darkness.createGraphics();

        gDark.setColor(new Color(0, 0, 0, 245));
        gDark.fillRect(0, 0, width, height);

        gDark.setComposite(AlphaComposite.DstOut);

        int radius = 100;

        int mazeSize = GameConstant.TILE_SIZE * maze.getRows();
        int offsetX = (width - mazeSize) / 2;
        int offsetY = (height - mazeSize) / 2;

        int px = (int) player.getX() + Player.SIZE / 2 + offsetX;
        int py = (int) player.getY() + Player.SIZE / 2 + offsetY;

        gDark.fillOval(px - radius, py - radius, radius * 2, radius * 2);

        gDark.dispose();

        g2.drawImage(darkness, 0, 0, null);
    }

    // ---------------------------------------------------------
    // HUD
    // ---------------------------------------------------------
    public void renderHUD(Graphics2D g2, Player player, boolean danger, int width, int height) {

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, width, 60);

        g2.setColor(Color.WHITE);

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("R: Restart   P: Pause   L: Leaderboard   N: Night Mode   H: Rules", 10, 22);

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Lives: " + player.getLives(), 10, 50);
        g2.drawString("Score: " + player.getScore(), 150, 50);

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

    // ---------------------------------------------------------
    // FREEZE BORDER
    // ---------------------------------------------------------
    public void renderFreezeBorder(Graphics2D g2, int width, int height) {
        if (!freezeActive) return;

        long elapsed = System.currentTimeMillis() - freezeStartTime;
        if (elapsed > 3000) {
            freezeActive = false;
            return;
        }

        float pulse = (float) ((Math.sin(elapsed / 120.0) + 1) / 2);
        int alpha = (int) (pulse * 120 + 80);

        g2.setColor(new Color(100, 180, 255, alpha));
        int t = 8;

        g2.fillRect(0, 0, width, t);
        g2.fillRect(0, height - t, width, t);
        g2.fillRect(0, 0, t, height);
        g2.fillRect(width - t, 0, t, height);
    }

    // ---------------------------------------------------------
    // DOUBLE POINTS BORDER
    // ---------------------------------------------------------
    public void renderDoublePointsBorder(Graphics2D g2, int width, int height) {
        if (!doublePointsActive) return;

        long elapsed = System.currentTimeMillis() - doublePointsStartTime;
        if (elapsed > 5000) {
            doublePointsActive = false;
            return;
        }

        float pulse = (float) ((Math.sin(elapsed / 120.0) + 1) / 2);
        int alpha = (int) (pulse * 120 + 80);

        g2.setColor(new Color(255, 215, 0, alpha)); // gold
        int t = 8;

        g2.fillRect(0, 0, width, t);
        g2.fillRect(0, height - t, width, t);
        g2.fillRect(0, 0, t, height);
        g2.fillRect(width - t, 0, t, height);
    }

    // ---------------------------------------------------------
    // OVERLAYS (Title, Pause, Win, Game Over, Rules)
    // ---------------------------------------------------------
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
            drawCenteredString(g2, "Press N to Toggle Night Mode", 350, width);
            drawCenteredString(g2, "Press H for Rules", 390, width);
        }

        // RULES SCREEN
        if (gsm.isRules()) {
            g2.setColor(new Color(0f, 0f, 0f, 0.75f));
            g2.fillRect(0, 0, width, height);

            // Title centered
            g2.setFont(new Font("Arial", Font.BOLD, 42));
            g2.setColor(Color.WHITE);
            drawCenteredString(g2, "GAME RULES", 80, width);

            // Left aligned rules text
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            int y = 100;
            int leftX = 20;

            for (String line : rulesText) {
                g2.drawString(line, leftX, y);
                y += 28;
            }

            // Footer centered
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            drawCenteredString(g2, "Press H to return", height - 80, width);
        }

        // PAUSE
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

        // WIN
        if (gsm.isWin()) {
            float a = gsm.getWinAlpha();
            g2.setColor(new Color(0f, 0f, 0f, a * 0.6f));
            g2.fillRect(0, 0, width, height);

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(0f, 1f, 0f, a));
            drawCenteredString(g2, "YOU WIN!", 240, width);

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            drawCenteredString(g2, "Final Score: " + player.getScore(), 300, width);

            g2.setFont(new Font("Arial", Font.BOLD, 24));
            drawCenteredString(g2, "Press L to view Leaderboard", 350, width);
        }

        // GAME OVER
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
            drawCenteredString(g2, "Press L to view Leaderboard", 350, width);
        }
    }
}
