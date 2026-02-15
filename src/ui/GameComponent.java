package ui;

import model.*;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.io.File;

public class GameComponent extends JPanel implements KeyListener {

    private final GameStateManager gsm = new GameStateManager();
    private final Camera camera = new Camera();
    private final DangerDetector dangerDetector = new DangerDetector();
    private final Renderer renderer = new Renderer();

    private Maze maze;
    private Player player;
    private List<Zombie> zombies;
    private List<Collectible> collectibles;
    private Spawner spawner;

    private boolean up, down, left, right;
    private boolean exitUnlocked = false;

    private GameMode previousMode = GameMode.TITLE;

    private boolean pendingNamePrompt = false;
    private long winFadeCompleteTime = 0L;

    public GameComponent() {
        setFocusable(true);
        addKeyListener(this);

        setPreferredSize(new Dimension(650, 650));

        maze = new Maze(new File("bin/levels/level1.txt"));
        spawner = new Spawner(maze);

        resetGameState();

        Timer timer = new Timer(16, e -> gameLoop());
        timer.start();
    }

    private void resetGameState() {
        player = spawner.spawnPlayer();
        zombies = spawner.spawnZombies(player);
        collectibles = spawner.spawnCollectibles(zombies);
        exitUnlocked = false;

        up = down = left = right = false;
        pendingNamePrompt = false;
        winFadeCompleteTime = 0L;
    }

    private void fullRestart() {
        gsm.reset();
        resetGameState();
    }

    private void gameLoop() {
        gsm.updateFades();
        camera.update();

        if (gsm.isPlaying()) {
            updateGameLogic();
        }

        handleDelayedNamePrompt();

        repaint();
    }

    private void handleDelayedNamePrompt() {
        if (!pendingNamePrompt) return;
        if (!gsm.isWin()) {
            pendingNamePrompt = false;
            winFadeCompleteTime = 0L;
            return;
        }

        if (gsm.getWinAlpha() >= 1f && winFadeCompleteTime == 0L) {
            winFadeCompleteTime = System.currentTimeMillis();
        }

        if (winFadeCompleteTime > 0L) {
            long elapsed = System.currentTimeMillis() - winFadeCompleteTime;
            if (elapsed >= 500) {
                showNamePromptAndSaveScore();
                pendingNamePrompt = false;
                winFadeCompleteTime = 0L;
            }
        }
    }

    private void updateGameLogic() {
        player.tickInvincibility();
        player.tickFlash();

        player.move(up, down, left, right);

        for (Zombie z : zombies) {
            z.update();
        }

        for (Collectible c : collectibles) {
            c.updateValue();
        }

        handleCollisions();
        dangerDetector.update(player, zombies);
    }

    private void handleCollisions() {

        for (Zombie z : zombies) {
            if (z.isInCollisionCooldown()) continue;

            if (overlap(player.getX(), player.getY(), Player.SIZE,
                        z.getX(), z.getY(), Zombie.SIZE)) {

                z.triggerCollisionCooldown();

                if (!player.isInvincible()) {
                    player.loseLife();
                    player.triggerInvincibility();
                    player.triggerFlash();
                    camera.triggerShake();

                    if (player.isDead()) {
                        handleGameEnd(GameMode.GAME_OVER);
                        return;
                    }
                }
            }
        }

        int collectedCount = 0;
        for (Collectible c : collectibles) {
            if (!c.isCollected() &&
                overlap(player.getX(), player.getY(), Player.SIZE,
                        c.getX(), c.getY(), Collectible.SIZE)) {

                int earned = c.collect();
                player.addScore(earned);

                for (Collectible other : collectibles) {
                    if (!other.isCollected()) {
                        other.resetValue();
                    }
                }
            }

            if (c.isCollected()) collectedCount++;
        }

        if (collectedCount == collectibles.size()) {
            exitUnlocked = true;
        }

        int tileSize = GameConstant.TILE_SIZE;
        int row = (int) ((player.getY() + Player.SIZE / 2) / tileSize);
        int col = (int) ((player.getX() + Player.SIZE / 2) / tileSize);

        if (exitUnlocked && maze.isExit(row, col)) {
            handleGameEnd(GameMode.WIN);
        }
    }

    private void handleGameEnd(GameMode mode) {
        gsm.setMode(mode);

        boolean newHigh = ScoreManager.isNewHighScore(player.getScore());
        gsm.setNewHighScore(newHigh);

        if (mode == GameMode.WIN) {
            pendingNamePrompt = true;
            winFadeCompleteTime = 0L;
        } else {
            showNamePromptAndSaveScore();
        }
    }

    private void showNamePromptAndSaveScore() {
        String name = JOptionPane.showInputDialog(this, "Enter your name:");
        if (name != null && !name.isBlank()) {
            ScoreManager.saveScore(name, player.getScore());
        }
    }

    private boolean overlap(double x1, double y1, int size1,
                            double x2, double y2, int size2) {
        return x1 < x2 + size2 &&
               x1 + size1 > x2 &&
               y1 < y2 + size2 &&
               y1 + size1 > y2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        camera.apply(g2);
        renderer.renderWorld(g2, maze, player, zombies, collectibles, width, height);
        camera.reset(g2);

        renderer.renderHUD(g2, player, dangerDetector.isInDanger(), width, height);
        renderer.renderFlash(g2, player, width, height);
        renderer.renderOverlays(g2, gsm, player, width, height);

        g2.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (gsm.isTitle()) {
            if (code == KeyEvent.VK_ENTER) {
                gsm.setMode(GameMode.PLAYING);
            }
            return;
        }

        if (code == KeyEvent.VK_L) {
            previousMode = gsm.getMode();
            gsm.setMode(GameMode.PAUSED);
            LeaderboardPanel.showLeaderboard(this);
            gsm.setMode(previousMode);
            return;
        }

        if (gsm.isWin() || gsm.isGameOver()) {
            if (code == KeyEvent.VK_R) {
                fullRestart();
            }
            return;
        }

        if (code == KeyEvent.VK_P) {
            if (gsm.isPlaying()) gsm.setMode(GameMode.PAUSED);
            else if (gsm.isPaused()) gsm.setMode(GameMode.PLAYING);
            return;
        }

        if (!gsm.isPlaying()) return;

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = true;

        if (code == KeyEvent.VK_R) fullRestart();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
