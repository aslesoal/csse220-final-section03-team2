package model;

import java.awt.Graphics2D;
import java.util.Random;

/**
 * Handles camera effects like screen shake.
 */
public class Camera {

    // Shaking constants
    public static final int SHAKE_FRAMES = 8;
    public static final int SHAKE_STRENGTH = 3;

    private int shakeTimer = 0;
    private final Random random = new Random();

    private int offsetX = 0;
    private int offsetY = 0;

    public void triggerShake() {
        shakeTimer = SHAKE_FRAMES;
    }

    public void update() {
        if (shakeTimer > 0) {
            shakeTimer--;
            offsetX = random.nextInt(SHAKE_STRENGTH * 2 + 1) - SHAKE_STRENGTH;
            offsetY = random.nextInt(SHAKE_STRENGTH * 2 + 1) - SHAKE_STRENGTH;
        } else {
            offsetX = 0;
            offsetY = 0;
        }
    }

    public void apply(Graphics2D g2) {
        g2.translate(offsetX, offsetY);
    }

    public void reset(Graphics2D g2) {
        g2.translate(-offsetX, -offsetY);
    }
}
