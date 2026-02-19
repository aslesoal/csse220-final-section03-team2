package model;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Maze {

    private Tile[][] tiles;
    private int rows;
    private int cols;

    // Spawn points
    private Point playerSpawn = null;
    private final ArrayList<Point> zombieSpawns = new ArrayList<>();

    public Point getPlayerSpawn() { return playerSpawn; }
    public ArrayList<Point> getZombieSpawns() { return zombieSpawns; }

    public Maze(File inputFile) {
        try {
            Scanner fileReader = new Scanner(inputFile);
            ArrayList<char[]> fileContents = new ArrayList<>();

            while (fileReader.hasNext()) {
                String line = fileReader.nextLine();

                // Skip comment lines
                if (line.startsWith("#")) continue;

                // Skip blank lines
                if (line.trim().isEmpty()) continue;

                fileContents.add(line.toCharArray());
            }
            fileReader.close();

            rows = fileContents.size();
            cols = fileContents.get(0).length;
            tiles = new Tile[rows][cols];

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {

                    char ch = fileContents.get(r)[c];

                    switch (ch) {

                        case '█' -> tiles[r][c] = new Tile(TileType.WALL);
                        case ' ' -> tiles[r][c] = new Tile(TileType.FLOOR);

                        // EXIT (uppercase or lowercase)
                        case 'X', 'x' -> tiles[r][c] = new Tile(TileType.EXIT);

                        // PLAYER SPAWN (uppercase or lowercase)
                        case 'P', 'p' -> {
                            playerSpawn = new Point(r, c);
                            tiles[r][c] = new Tile(TileType.FLOOR);
                        }

                        // ZOMBIE SPAWN (uppercase or lowercase)
                        case 'Z', 'z' -> {
                            zombieSpawns.add(new Point(r, c));
                            tiles[r][c] = new Tile(TileType.FLOOR);
                        }

                        default -> tiles[r][c] = new Tile(TileType.FLOOR);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Level load failed, using fallback.");

            rows = 15;
            cols = 15;
            tiles = new Tile[rows][cols];

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    tiles[r][c] = new Tile(MazeLayout.MAZE[r][c]);
                }
            }
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean isWalkable(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return false;
        return tiles[row][col].isWalkable();
    }

    public boolean isExit(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return false;
        return tiles[row][col].isExit();
    }

    public void draw(Graphics2D g2) {
        int tileSize = GameConstant.TILE_SIZE;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c].draw(g2, r, c, tileSize);
            }
        }
    }
}
