package model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

import ui.HUD;
import ui.OverlayRenderer;

/*
 * Renders all of the graphics for the game
 */
public class Renderer {

    // Freeze border state
    private long freezeStartTime = 0;
    private boolean freezeActive = false;

    // Double Points border state
    private boolean doublePointsActive = false;
    private long doublePointsStartTime = 0L;

    // Rules Text
    private List<String> rulesText;

    // Night mode status
    private boolean nightMode = false;

    // Helper classes
    private HUD hud = new HUD();
    private OverlayRenderer overlayRenderer = new OverlayRenderer();

    public void setNightMode(boolean value) {
        this.nightMode = value;
    }

    public void setRulesText(List<String> lines) {
        this.rulesText = lines;
    }

    //Enabling powerup systems
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

        // maze centering
        int mazeWidth  = Maze.TILE_SIZE * maze.getCols();
        int mazeHeight = Maze.TILE_SIZE * maze.getRows();

        int offsetX = (width  - mazeWidth)  / 2;
        int offsetY = (height - mazeHeight) / 2;

        g2.translate(offsetX, offsetY);

        maze.draw(g2);

        g2.setColor(Color.YELLOW);
        
        //Drawing collectibles
        for (Collectible c : collectibles) {
            if (!c.isCollected()) {
                g2.fillOval((int) c.getX(), (int) c.getY(),
                        Collectible.SIZE, Collectible.SIZE);
            }
        }

        //Drawing zombies with sprite image or red circle
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
        
        //Drawing player with sprite image or blue circle
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

    //rotating sprite images
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

        // Maze centering
        int mazeWidth  = Maze.TILE_SIZE * maze.getCols();
        int mazeHeight = Maze.TILE_SIZE * maze.getRows();

        int offsetX = (width  - mazeWidth)  / 2;
        int offsetY = (height - mazeHeight) / 2;

        int px = (int) player.getX() + Player.SIZE / 2 + offsetX;
        int py = (int) player.getY() + Player.SIZE / 2 + offsetY;

        gDark.fillOval(px - radius, py - radius, radius * 2, radius * 2);

        gDark.dispose();

        g2.drawImage(darkness, 0, 0, null);
    }

    // ---------------------------------------------------------
    // FLASH EFFECT
    // ---------------------------------------------------------
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
    // OVERLAYS
    // ---------------------------------------------------------
    public void renderOverlays(Graphics2D g2, GameStateManager gsm, Player player, int width, int height) {
        overlayRenderer.render(g2, gsm, player, rulesText, nightMode, width, height);
    }

    // ---------------------------------------------------------
    // HUD
    // ---------------------------------------------------------
    public void renderHUD(Graphics2D g2, Player player, boolean danger, int width, int height) {
        hud.render(g2, player, danger, width, height);
    }
}
