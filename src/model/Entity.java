package model;

public abstract class Entity {

    protected double x;
    protected double y;
    protected int size;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    // Shared behavior: safely update position
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
