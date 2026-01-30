package model;

/**
 * Defines the maze layout for the game.
 * You can later load this from a file.
 */

public class MazeLayout {

    public static final TileType[][] MAZE = {

        // 15 columns per row
        // 15 rows total

        {TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.WALL,  TileType.FLOOR, TileType.WALL, TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL, TileType.WALL, TileType.WALL,  TileType.FLOOR, TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.WALL,  TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL,  TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL,  TileType.FLOOR, TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR,  TileType.WALL,  TileType.FLOOR, TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL,  TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL,  TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL},

        {TileType.WALL, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR,  TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL,  TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL,  TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL},

        {TileType.WALL, TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR,  TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.WALL, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL,  TileType.WALL,  TileType.WALL,  TileType.FLOOR, TileType.WALL},

        {TileType.WALL, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.FLOOR, TileType.EXIT, TileType.WALL},

        {TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL, TileType.WALL}
    };
}

