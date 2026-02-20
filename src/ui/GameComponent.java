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

/**
 * The GameComponent class represents the main gameplay panel.
 *
 * This class acts as the central controller for the game.
 */
public class GameComponent extends JPanel implements KeyListener {

    // Core systems used throughout the game
    final GameStateManager gsm = new GameStateManager();
    final Camera camera = new Camera();
    final DangerDetector dangerDetector = new DangerDetector();
    final Renderer renderer = new Renderer();
    final CollisionSystem collisionSystem = new CollisionSystem();

    // Game world objects
    Maze maze;
    Player player;
    List<Zombie> zombies;
    List<Collectible> collectibles;
    Spawner spawner;

    //Getter added for encapsulation and UML alignment
    public Maze getMaze() {
        return maze;
    }

    // Movement flags
    boolean up, down, left, right;

    // Level progression
    boolean exitUnlocked = false;
    int currentLevel = 1;
    final int maxLevel = 2;
    int carryoverScore = 0;

    // Win / Game Over prompt timing
    boolean pendingWinPrompt = false;
    boolean pendingGameOverPrompt = false;
    long winFadeCompleteTime = 0L;
    long gameOverFadeCompleteTime = 0L;

    // Transition state
    boolean inTransition = false;
    long transitionStartTime = 0L;
    final long transitionDuration = 3000;

    // Modes and power-ups
    boolean nightMode = false;
    boolean freezeActive = false;
    long freezeStartTime = 0L;
    boolean doublePointsActive = false;
    long doublePointsStartTime = 0L;

    // Rules screen
    private List<String> rulesLines = new ArrayList<>();
    private GameMode rulesReturnMode = GameMode.TITLE;

    /**
     * Constructs the GameComponent, initializes listeners, loads rules,
     * loads the first level, and starts the game loop timer.
     */
    public GameComponent() {
        setFocusable(true);
        addKeyListener(this);

        setPreferredSize(new Dimension(650, 650));

        loadRulesFile();
        loadLevel(currentLevel);

        // 60 FPS game loop
        Timer timer = new Timer(16, e -> gameLoop());
        timer.start();
    }

    /**
     * Loads the rules text file and passes the lines to the renderer.
     */
    private void loadRulesFile() {
        try {
            File file = new File("bin/levels/rules.txt");
            rulesLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            rulesLines = List.of("RULES FILE NOT FOUND");
        }
        renderer.setRulesText(rulesLines);
    }

    /**
     * Loads a level from file, initializes the maze and spawner,
     * and resets the game state.
     *
     * @param level the level number to load
     */
    void loadLevel(int level) {
        String path = "bin/levels/level" + level + ".txt";
        maze = new Maze(new File(path));
        spawner = new Spawner(maze);

        // Level-specific zombie counts
        if (level == 1) {
            spawner.setZombieCount(8);
        } else if (level == 2) {
            spawner.setZombieCount(10);
        }

        resetGameState();
    }

    /**
     * Resets all gameplay state for a newly loaded level.
     * Spawns player, zombies, collectibles, and resets flags.
     */
    private void resetGameState() {
        player = spawner.spawnPlayer();
        zombies = spawner.spawnZombies(player);
        collectibles = spawner.spawnCollectibles(zombies);
        exitUnlocked = false;

        // Carry score across levels
        player.addScore(carryoverScore);

        // Reset movement
        up = down = left = right = false;

        // Reset prompts
        pendingWinPrompt = false;
        pendingGameOverPrompt = false;
        winFadeCompleteTime = 0L;
        gameOverFadeCompleteTime = 0L;

        // Reset power-ups
        freezeActive = false;
        freezeStartTime = 0L;
        doublePointsActive = false;
        doublePointsStartTime = 0L;
    }

    /**
     * Fully restarts the game back to level 1 and clears all state.
     */
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

    /**
     * Main game loop called ~60 times per second.
     * Handles transitions, updates logic, and triggers repainting.
     */
    private void gameLoop() {
        gsm.updateFades();
        camera.update();

        // Handle level transition countdown
        if (gsm.getMode() == GameMode.TRANSITION) {
            long elapsed = System.currentTimeMillis() - transitionStartTime;

            if (elapsed >= transitionDuration) {
                inTransition = false;
                gsm.setMode(GameMode.PLAYING);
            }

            repaint();
            return;
        }

        // Update gameplay only when in PLAYING mode
        if (gsm.isPlaying()) {
            updateGameLogic();
        }

        handleDelayedPrompts();
        repaint();
    }

    /**
     * Handles delayed win/game-over prompts that must wait
     * until fade animations complete.
     */
    private void handleDelayedPrompts() {

        // WIN prompt logic
        if (pendingWinPrompt) {

            if (!gsm.isWin()) {
                pendingWinPrompt = false;
                winFadeCompleteTime = 0L;
                return;
            }

            // Wait until fade reaches full opacity
            if (gsm.getWinAlpha() >= 1f && winFadeCompleteTime == 0L) {
                winFadeCompleteTime = System.currentTimeMillis();
            }

            // Delay before showing name prompt
            if (winFadeCompleteTime > 0L) {
                long elapsed = System.currentTimeMillis() - winFadeCompleteTime;
                if (elapsed >= 500) {
                    showNamePromptAndSaveScore();
                    pendingWinPrompt = false;
                    winFadeCompleteTime = 0L;
                }
            }
        }

        // GAME OVER prompt logic
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

    /**
     * Updates all gameplay logic: movement, zombies, collectibles,
     * power-ups, collisions, and danger detection.
     */
    private void updateGameLogic() {
        player.tickInvincibility();
        player.tickFlash();

        // Player movement
        player.move(up, down, left, right);

        // Zombie movement (disabled during freeze)
        for (Zombie z : zombies) {
            if (!freezeActive) {
                z.update();
            }
        }

        // Update collectible animations
        for (Collectible c : collectibles) {
            c.updateValue();
        }

        // Freeze power-up expiration
        if (freezeActive) {
            if (System.currentTimeMillis() - freezeStartTime >= 3000) {
                freezeActive = false;
                renderer.deactivateFreeze();
            }
        }

        // Double points expiration
        if (doublePointsActive) {
            if (System.currentTimeMillis() - doublePointsStartTime >= 5000) {
                doublePointsActive = false;
                renderer.deactivateDoublePoints();
            }
        }

        // Collision handling
        collisionSystem.update(this, player, zombies, collectibles, maze);

        // Danger detection for HUD
        dangerDetector.update(player, zombies);
    }

    /**
     * Handles switching to WIN or GAME OVER mode and prepares prompts.
     *
     * @param mode the end-game mode to switch to
     */
    void handleGameEnd(GameMode mode) {
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

    /**
     * Prompts the player for their name and saves the score.
     */
    private void showNamePromptAndSaveScore() {
        String name = JOptionPane.showInputDialog(this, "Enter your name:");
        if (name != null && !name.isBlank()) {
            ScoreManager.saveScore(name, player.getScore());
        }
    }

    /**
     * Simple AABB collision check between two square objects.
     */
    boolean overlap(double x1, double y1, int size1,
                    double x2, double y2, int size2) {
        return x1 < x2 + size2 &&
                x1 + size1 > x2 &&
                y1 < y2 + size2 &&
                y1 + size1 > y2;
    }

    /**
     * Renders the entire game world, HUD, overlays, transitions,
     * and power-up borders.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        // Apply camera offset for world rendering
        camera.apply(g2);
        renderer.renderWorld(g2, maze, player, zombies, collectibles, width, height);
        camera.reset(g2);

        // Night mode spotlight
        if (nightMode && gsm.isPlaying()) {
            renderer.renderNightMode(g2, player, maze, width, height);
        }

        // HUD + flash effect
        renderer.renderHUD(g2, player, dangerDetector.isInDanger(), width, height);
        renderer.renderFlash(g2, player, width, height);

        renderer.setNightMode(nightMode);

        // Overlays (title, win, game over, rules)
        renderer.renderOverlays(g2, gsm, player, width, height);

        // Level transition countdown
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

        // Power-up borders
        renderer.renderFreezeBorder(g2, width, height);
        renderer.renderDoublePointsBorder(g2, width, height);

        g2.dispose();
    }

    /**
     * Handles all key press events for movement, menus, rules,
     * leaderboard, pausing, and restarting.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // TITLE SCREEN CONTROLS
        if (gsm.isTitle()) {
            if (code == KeyEvent.VK_ENTER) {
                gsm.setMode(GameMode.PLAYING);
            }
            if (code == KeyEvent.VK_N) {
                nightMode = !nightMode;
            }
            if (code == KeyEvent.VK_H) {
                rulesReturnMode = GameMode.TITLE;
                gsm.setMode(GameMode.RULES);
            }
            return;
        }

        // RULES SCREEN CONTROLS
        if (gsm.isRules()) {
            if (code == KeyEvent.VK_H) {

                if (rulesReturnMode == GameMode.TITLE) {
                    fullRestart();
                    gsm.setMode(GameMode.TITLE);
                    return;
                }

                gsm.setMode(rulesReturnMode);
                return;
            }
            return;
        }

        // LEADERBOARD
        if (code == KeyEvent.VK_L) {

            GameMode current = gsm.getMode();
            GameMode returnMode = current;

            if (current == GameMode.PLAYING) {
                gsm.setMode(GameMode.PAUSED);
                returnMode = GameMode.PAUSED;
            }

            LeaderboardPanel.showLeaderboard(this);

            gsm.setMode(returnMode);
            return;
        }

        // RULES (in-game)
        if (code == KeyEvent.VK_H) {

            GameMode current = gsm.getMode();
            rulesReturnMode = current;

            if (current == GameMode.PLAYING) {
                gsm.setMode(GameMode.PAUSED);
                rulesReturnMode = GameMode.PAUSED;
            }

            gsm.setMode(GameMode.RULES);
            return;
        }

        // WIN / GAME OVER screen restart
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

        // Ignore movement if not playing
        if (!gsm.isPlaying()) return;

        // MOVEMENT
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = true;

        // Restart key
        if (code == KeyEvent.VK_R) fullRestart();
    }

    /**
     * Handles key release events for movement.
     */
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
