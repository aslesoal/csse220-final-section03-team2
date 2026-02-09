package model;

import java.util.List;

/**
 * Computes whether the player is in danger based on nearby zombies.
 */
public class DangerDetector {

    private boolean inDanger = false;

    public boolean isInDanger() {
        return inDanger;
    }

    public void update(Player player, List<Zombie> zombies) {
        double dangerDistancePixels = GameConstant.DANGER_DISTANCE_TILES * GameConstant.TILE_SIZE;
        double px = player.getX();
        double py = player.getY();

        inDanger = false;

        for (Zombie z : zombies) {
            double dx = px - z.getX();
            double dy = py - z.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < dangerDistancePixels) {
                inDanger = true;
                return;
            }
        }
    }
}
