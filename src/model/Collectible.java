package model;

public class Collectible extends Entity {

    public static final int SIZE = 16;

    private boolean collected = false;

    private final int maxValue = 1000;
    private int currentValue = 1000;
    private final int decayRate = 2;

    public Collectible(double x, double y) {
        this.size = SIZE;

        this.x = x;
        this.y = y;

        this.currentValue = maxValue;
    }

    public void updateValue() {
        if (!collected && currentValue > 0) {
            currentValue -= decayRate;
            if (currentValue < 0) currentValue = 0;
        }
    }

    public int collect() {
        collected = true;
        return currentValue;
    }

    public void resetValue() {
        currentValue = maxValue;
    }

    public boolean isCollected() { return collected; }
    public int getValue() { return currentValue; }
}
