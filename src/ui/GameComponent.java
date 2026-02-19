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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

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

    private boolean pendingWinPrompt = false;
    private boolean pendingGameOverPrompt = false;

    private long winFadeCompleteTime = 0L;
    private long gameOverFadeCompleteTime = 0L;

    private int currentLevel = 1;
    private final int maxLevel = 2;

    private int carryoverScore = 0;

    private boolean inTransition = false;
    private long transitionStartTime = 0L;
    private final long transitionDuration = 3000;

    // NIGHT MODE
    private boolean nightMode = false;

    // FREEZE
    private boolean freezeActive = false;
    private long freezeStartTime = 0L;

    // DOUBLE POINTS
    private boolean doublePointsActive = false;
    private long doublePointsStartTime = 0L;

    // RULES TEXT
    private List<String> rulesLines = new ArrayList<>();

    public GameComponent() {
        setFocusable(true);
        addKeyListener(this);

        setPreferredSize(new Dimension(650, 650));

        loadRulesFile();
        loadLevel(currentLevel);

        Timer timer = new Timer(16, e -> gameLoop());
        timer.start();
    }

    private void loadRulesFile() {
        try {
            File file = new File("bin/levels/rules.txt");
            rulesLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            rulesLines = List.of("RULES FILE NOT FOUND");
        }
        renderer.setRulesText(rulesLines);
    }

    private void loadLevel(int level) {
        String path = "bin/levels/level" + level + ".txt";
        maze = new Maze(new File(path));
        spawner = new Spawner(maze);

        if (level == 1) {
            spawner.setZombieCount(8);
        } else if (level == 2) {
            spawner.setZombieCount(10);
        }

        resetGameState();
    }

    private void resetGameState() {
        player = spawner.spawnPlayer();
        zombies = spawner.spawnZombies(player);
        collectibles = spawner.spawnCollectibles(zombies);
        exitUnlocked = false;

        player.addScore(carryoverScore);

        up = down = left = right = false;

        pendingWinPrompt = false;
        pendingGameOverPrompt = false;

        winFadeCompleteTime = 0L;
        gameOverFadeCompleteTime = 0L;

        freezeActive = false;
        freezeStartTime = 0L;

        doublePointsActive = false;
        doublePointsStartTime = 0L;
    }

    private void fullRestart() {
        currentLevel = 1;
        carryoverScore = 0;
        gsm.reset();
        inTransition = false;
        transitionStartTime = 0L;

        freezeActive = false;
        freezeStartTime = 0L;

        doublePointsActive = false;
        doublePointsStartTime = 0L;

        loadLevel(currentLevel);
    }

    private void gameLoop() {
        gsm.updateFades();
        camera.update();

        if (gsm.getMode() == GameMode.TRANSITION) {
            long elapsed = System.currentTimeMillis() - transitionStartTime;

            if (elapsed >= transitionDuration) {
                inTransition = false;
                gsm.setMode(GameMode.PLAYING);
            }

            repaint();
            return;
        }

        if (gsm.isPlaying()) {
            updateGameLogic();
        }

        handleDelayedPrompts();

        repaint();
    }

    private void handleDelayedPrompts() {

        // WIN delayed prompt
        if (pendingWinPrompt) {

            if (!gsm.isWin()) {
                pendingWinPrompt = false;
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
                    pendingWinPrompt = false;
                    winFadeCompleteTime = 0L;
                }
            }
        }

        // GAME OVER delayed prompt
        if (pendingGameOverPrompt) {

            if (!gsm.isGameOver()) {
                pendingGameOverPrompt = false;
                gameOverFadeCompleteTime = 0L;
                return;
            }

            if (gsm.getGameOverAlpha() >= 1f && gameOverFadeCompleteTime == 0L) {
                gameOverFadeCompleteTime = System.currentTimeMillis();
            }

            if (gameOverFadeCompleteTime > 0L) {
                long elapsed = System.currentTimeMillis() - gameOverFadeCompleteTime;
                if (elapsed >= 500) {
                    showNamePromptAndSaveScore();
                    pendingGameOverPrompt = false;
                    gameOverFadeCompleteTime = 0L;
                }
            }
        }
    }

    private void updateGameLogic() {
        player.tickInvincibility();
        player.tickFlash();

        player.move(up, down, left, right);

        for (Zombie z : zombies) {
            if (!freezeActive) {
                z.update();
            }
        }

        for (Collectible c : collectibles) {
            c.updateValue();
        }

        // Freeze timer
        if (freezeActive) {
            if (System.currentTimeMillis() - freezeStartTime >= 3000) {
                freezeActive = false;
                renderer.deactivateFreeze();
            }
        }

        // Double Points timer
        if (doublePointsActive) {
            if (System.currentTimeMillis() - doublePointsStartTime >= 5000) {
                doublePointsActive = false;
                renderer.deactivateDoublePoints();
            }
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

                // Apply double points
                if (doublePointsActive) {
                    earned *= 2;
                }

                player.addScore(earned);

                // 15% chance to instantly activate freeze
                if (Math.random() < 0.15) {
                    freezeActive = true;
                    freezeStartTime = System.currentTimeMillis();
                    renderer.activateFreeze();
                }

                // 15% chance to activate double points
                if (Math.random() < 0.15) {
                    doublePointsActive = true;
                    doublePointsStartTime = System.currentTimeMillis();
                    renderer.activateDoublePoints();
                }

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

            if (currentLevel < maxLevel) {

                carryoverScore = player.getScore();

                currentLevel++;
                loadLevel(currentLevel);

                inTransition = true;
                transitionStartTime = System.currentTimeMillis();
                gsm.setMode(GameMode.TRANSITION);
                return;
            }

            handleGameEnd(GameMode.WIN);
        }
    }

    private void handleGameEnd(GameMode mode) {
        inTransition = false;
        gsm.setMode(mode);

        boolean newHigh = ScoreManager.isNewHighScore(player.getScore());
        gsm.setNewHighScore(newHigh);

        if (mode == GameMode.WIN) {
            pendingWinPrompt = true;
            winFadeCompleteTime = 0L;
        } else {
            pendingGameOverPrompt = true;
            gameOverFadeCompleteTime = 0L;
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

        // WORLD
        camera.apply(g2);
        renderer.renderWorld(g2, maze, player, zombies, collectibles, width, height);
        camera.reset(g2);

        // NIGHT MODE
        if (nightMode && gsm.isPlaying()) {
            renderer.renderNightMode(g2, player, maze, width, height);
        }

        // HUD
        renderer.renderHUD(g2, player, dangerDetector.isInDanger(), width, height);

        // Flash effect
        renderer.renderFlash(g2, player, width, height);

        // UPDATE NIGHT MODE BEFORE DRAWING TITLE SCREEN
        renderer.setNightMode(nightMode);

        // Overlays
        renderer.renderOverlays(g2, gsm, player, width, height);

        // Transition overlay
        if (inTransition && gsm.getMode() == GameMode.TRANSITION) {

            long elapsed = System.currentTimeMillis() - transitionStartTime;
            float progress = Math.min(1f, elapsed / (float) transitionDuration);

            int alpha = (int)((1f - progress) * 180);

            g2.setColor(new Color(0, 0, 0, alpha));
            g2.fillRect(0, 0, width, height);

            int secondsLeft = 3 - (int) (elapsed / 1000);
            if (secondsLeft < 1) secondsLeft = 1;

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 72));
            String text = String.valueOf(secondsLeft);
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            g2.drawString(text, (width - tw) / 2, height / 2);

            g2.setFont(new Font("Arial", Font.PLAIN, 32));
            String lvl = "LEVEL " + currentLevel;
            fm = g2.getFontMetrics();
            int lw = fm.stringWidth(lvl);
            g2.drawString(lvl, (width - lw) / 2, height / 2 - 80);
        }

        // Freeze border
        renderer.renderFreezeBorder(g2, width, height);

        // Double Points border
        renderer.renderDoublePointsBorder(g2, width, height);

        g2.dispose();
    }

    // ---------------------------------------------------------
    // KEY LISTENER METHODS
    // ---------------------------------------------------------

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // TITLE SCREEN
        if (gsm.isTitle()) {
            if (code == KeyEvent.VK_ENTER) {
                gsm.setMode(GameMode.PLAYING);
            }
            if (code == KeyEvent.VK_N) {
                nightMode = !nightMode;
            }
            if (code == KeyEvent.VK_H) {
                gsm.setMode(GameMode.RULES);
            }
            return;
        }

        // RULES SCREEN
        if (gsm.isRules()) {
            if (code == KeyEvent.VK_H) {
                fullRestart();              
                gsm.setMode(GameMode.TITLE);
            }
            return;
        }

        // GLOBAL RULES ACCESS (except during gameplay)
        if (!gsm.isPlaying() && code == KeyEvent.VK_H) {
            gsm.setMode(GameMode.RULES);
            return;
        }

        // LEADERBOARD ACCESS
        if (code == KeyEvent.VK_L) {
            GameMode returnMode = gsm.getMode();
            LeaderboardPanel.showLeaderboard(this);
            gsm.setMode(returnMode);
            return;
        }

        // WIN / GAME OVER
        if (gsm.isWin() || gsm.isGameOver()) {
            if (code == KeyEvent.VK_R) {
                fullRestart();
            }
            return;
        }

        // PAUSE
        if (code == KeyEvent.VK_P) {
            if (gsm.isPlaying()) gsm.setMode(GameMode.PAUSED);
            else if (gsm.isPaused()) gsm.setMode(GameMode.PLAYING);
            return;
        }

        // GAMEPLAY CONTROLS
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
