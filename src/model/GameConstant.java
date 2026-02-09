package model;

public final class GameConstant {

    private GameConstant() {}

    public static final int TILE_SIZE = 32;

    // Correct sprite paths based on your actual folder: src/images/
    public static final String PLAYER_SPRITE = "/images/player.png";
    public static final String ZOMBIE_SPRITE = "/images/zombie.png";

    public static final int INITIAL_LIVES = 4;
    public static final int ZOMBIE_COUNT = 8;
    public static final int COLLECTIBLE_COUNT = 8;

    public static final int INVINCIBILITY_FRAMES = 60;
    public static final int FLASH_FRAMES = 8;
    public static final int SHAKE_FRAMES = 8;
    public static final int SHAKE_STRENGTH = 3;

    public static final double DANGER_DISTANCE_TILES = 1.5;
}
