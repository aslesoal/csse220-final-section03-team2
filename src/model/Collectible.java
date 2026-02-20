package model;

/* 
 * Handles the values of the collectibles with their maxValue and decay over time
 * 
 * Holds the collectible size 
 */
public class Collectible extends Entity {

    public static final int SIZE = 16;

    private boolean collected = false;

    //collectible score values
    private final int maxValue = 1000;
    private int currentValue = 1000;
    private final int decayRate = 2;

    public Collectible(double x, double y) {
        this.size = SIZE;

        this.x = x;
        this.y = y;

        this.currentValue = maxValue;
    }

    //collectible value decay
    public void updateValue() {
        if (!collected && currentValue > 0) {
            currentValue -= decayRate;
            if (currentValue < 0) currentValue = 0;
        }
    }
    
    //scoring value
    public int collect() {
        collected = true;
        return currentValue;
    }

    //reset all collectibles to maxValue to restart the timer
    public void resetValue() {
        currentValue = maxValue;
    }

    public boolean isCollected() { return collected; }
    public int getValue() { return currentValue; }
}
