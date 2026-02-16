package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spawner {

    private final Maze maze;
    private final Random random = new Random();

    // ⭐ NEW: level-specific zombie count (defaults to constant)
    private int zombieCount = GameConstant.ZOMBIE_COUNT;

    public Spawner(Maze maze) {
        this.maze = maze;
    }

    // ⭐ NEW: allow GameComponent to override zombie count per level
    public void setZombieCount(int count) {
        this.zombieCount = count;
    }

    // PLAYER SPAWN
    public Player spawnPlayer() {
        Point p = maze.getPlayerSpawn();
        if (p != null) {
            return new Player(p.x, p.y, maze);
        }
        return new Player(1, 1, maze); // fallback
    }

    // Manhattan distance helper
    private int dist(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    // ZOMBIE SPAWN
    public List<Zombie> spawnZombies(Player player) {
        List<Zombie> zombies = new ArrayList<>();

        ArrayList<Point> spawnTiles = maze.getZombieSpawns();

        // If map contains 'Z/z' tiles → spawn exactly there
        if (!spawnTiles.isEmpty()) {
            for (Point p : spawnTiles) {
                zombies.add(new Zombie(p.x, p.y, maze));
            }
            return zombies;
        }

        // fallback: random spawning with spacing rules
        int count = zombieCount; // ⭐ USE LEVEL-SPECIFIC COUNT

        // Player tile position
        int pr = (int) ((player.getY() + Player.SIZE / 2) / GameConstant.TILE_SIZE);
        int pc = (int) ((player.getX() + Player.SIZE / 2) / GameConstant.TILE_SIZE);

        while (zombies.size() < count) {

            int row = random.nextInt(maze.getRows());
            int col = random.nextInt(maze.getCols());

            // Must be walkable
            if (!maze.isWalkable(row, col)) continue;

            // Must be at least 4 tiles from player
            if (dist(row, col, pr, pc) < 4) continue;

            // Must be at least 2 tiles from all existing zombies
            boolean tooClose = false;
            for (Zombie z : zombies) {
                int zr = (int) (z.getY() / GameConstant.TILE_SIZE);
                int zc = (int) (z.getX() / GameConstant.TILE_SIZE);

                if (dist(row, col, zr, zc) < 2) {
                    tooClose = true;
                    break;
                }
            }
            if (tooClose) continue;

            // Valid spawn
            zombies.add(new Zombie(row, col, maze));
        }

        return zombies;
    }

    // COLLECTIBLES ALWAYS RANDOM
    public List<Collectible> spawnCollectibles(List<Zombie> zombies) {
        List<Collectible> collectibles = new ArrayList<>();

        int count = GameConstant.COLLECTIBLE_COUNT;

        while (collectibles.size() < count) {
            int row = random.nextInt(maze.getRows());
            int col = random.nextInt(maze.getCols());

            if (!maze.isWalkable(row, col)) continue;

            double x = col * GameConstant.TILE_SIZE +
                       (GameConstant.TILE_SIZE - Collectible.SIZE) / 2.0;

            double y = row * GameConstant.TILE_SIZE +
                       (GameConstant.TILE_SIZE - Collectible.SIZE) / 2.0;

            collectibles.add(new Collectible(x, y));
        }

        return collectibles;
    }
}
