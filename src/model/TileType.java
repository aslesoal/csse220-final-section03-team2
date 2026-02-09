package model;

/**
 * Tile types with embedded behavior flags.
 */
public enum TileType {
    WALL(false, false),
    FLOOR(true, false),
    EXIT(true, true);

    private final boolean walkable;
    private final boolean exit;

    TileType(boolean walkable, boolean exit) {
        this.walkable = walkable;
        this.exit = exit;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public boolean isExit() {
        return exit;
    }
}
