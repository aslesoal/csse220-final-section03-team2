package model;

import java.awt.Graphics2D;
import java.util.Random;

/**
 * Handles camera effects like screen shake.
 */
public class Camera {

    private int shakeTimer = 0;
    private final Random random = new Random();

    private int offsetX = 0;
    private int offsetY = 0;

    public void triggerShake() {
        shakeTimer = GameConstant.SHAKE_FRAMES;
    }

    public void update() {
        if (shakeTimer > 0) {
            shakeTimer--;
            offsetX = random.nextInt(GameConstant.SHAKE_STRENGTH * 2 + 1) - GameConstant.SHAKE_STRENGTH;
            offsetY = random.nextInt(GameConstant.SHAKE_STRENGTH * 2 + 1) - GameConstant.SHAKE_STRENGTH;
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
