package model;

public class Collectible {

    public static final int SIZE = 16;

    private double x;
    private double y;
    private boolean collected = false;

    private int maxValue = 1000;
    private int currentValue = 1000;
    private int decayRate = 2;

    public Collectible(double x, double y) {
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

    // NEW: Reset remaining collectibles to 1000
    public void resetValue() {
        currentValue = maxValue;
    }

    public boolean isCollected() { return collected; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getValue() { return currentValue; }
}
