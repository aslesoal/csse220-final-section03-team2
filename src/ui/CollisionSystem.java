package ui;

import model.*;
import java.util.List;

public class CollisionSystem {

    public void update(GameComponent gc,
                       Player player,
                       List<Zombie> zombies,
                       List<Collectible> collectibles,
                       Maze maze) {

        // -------------------------------
        // PLAYER–ZOMBIE COLLISIONS
        // -------------------------------
        for (Zombie z : zombies) {
            if (z.isInCollisionCooldown()) continue;

            if (gc.overlap(player.getX(), player.getY(), Player.SIZE,
                    z.getX(), z.getY(), Zombie.SIZE)) {

                z.triggerCollisionCooldown();

                if (!player.isInvincible()) {
                    player.loseLife();
                    player.triggerInvincibility();
                    player.triggerFlash();
                    gc.camera.triggerShake();

                    if (player.isDead()) {
                        gc.handleGameEnd(GameMode.GAME_OVER);
                        return;
                    }
                }
            }
        }

        // -------------------------------
        // PLAYER–COLLECTIBLE COLLISIONS
        // -------------------------------
        int collectedCount = 0;

        for (Collectible c : collectibles) {
            if (!c.isCollected() &&
                    gc.overlap(player.getX(), player.getY(), Player.SIZE,
                            c.getX(), c.getY(), Collectible.SIZE)) {

                int earned = c.collect();

                // Double points
                if (gc.doublePointsActive) {
                    earned *= 2;
                }

                player.addScore(earned);

                // 15% chance: freeze
                if (Math.random() < 0.15) {
                    gc.freezeActive = true;
                    gc.freezeStartTime = System.currentTimeMillis();
                    gc.renderer.activateFreeze();
                }

                // 15% chance: double points
                if (Math.random() < 0.15) {
                    gc.doublePointsActive = true;
                    gc.doublePointsStartTime = System.currentTimeMillis();
                    gc.renderer.activateDoublePoints();
                }

                // Reset remaining collectibles
                for (Collectible other : collectibles) {
                    if (!other.isCollected()) {
                        other.resetValue();
                    }
                }
            }

            if (c.isCollected()) collectedCount++;
        }

        // Unlock exit
        if (collectedCount == collectibles.size()) {
            gc.exitUnlocked = true;
        }

        // -------------------------------
        // EXIT TILE CHECK
        // -------------------------------
        int tileSize = Maze.TILE_SIZE;
        int row = (int) ((player.getY() + Player.SIZE / 2) / tileSize);
        int col = (int) ((player.getX() + Player.SIZE / 2) / tileSize);

        if (gc.exitUnlocked && maze.isExit(row, col)) {

            if (gc.currentLevel < gc.maxLevel) {

                gc.carryoverScore = player.getScore();

                gc.currentLevel++;
                gc.loadLevel(gc.currentLevel);

                gc.inTransition = true;
                gc.transitionStartTime = System.currentTimeMillis();
                gc.gsm.setMode(GameMode.TRANSITION);
                return;
            }

            gc.handleGameEnd(GameMode.WIN);
        }
    }
}
