package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spawner {

    private final Maze maze;
    private final Random random = new Random();

    public Spawner(Maze maze) {
        this.maze = maze;
    }

    public List<Zombie> spawnZombies(Player player) {
        List<Zombie> zombies = new ArrayList<>();

        int count = GameConstant.ZOMBIE_COUNT;

        while (zombies.size() < count) {
            int row = random.nextInt(maze.getRows());
            int col = random.nextInt(maze.getCols());

            if (!maze.isWalkable(row, col)) continue;
            if (distance(row, col, player) < 3) continue;

            zombies.add(new Zombie(row, col, maze));
        }

        return zombies;
    }

    public List<Collectible> spawnCollectibles(List<Zombie> zombies) {
        List<Collectible> collectibles = new ArrayList<>();

        int count = GameConstant.COLLECTIBLE_COUNT;

        while (collectibles.size() < count) {
            int row = random.nextInt(maze.getRows());
            int col = random.nextInt(maze.getCols());

            if (!maze.isWalkable(row, col)) continue;

            boolean tooClose = false;

            for (Zombie z : zombies) {
                if (distance(row, col, z) < 3) {
                    tooClose = true;
                    break;
                }
            }
            if (tooClose) continue;

            for (Collectible c : collectibles) {
                int cRow = (int) (c.getY() / GameConstant.TILE_SIZE);
                int cCol = (int) (c.getX() / GameConstant.TILE_SIZE);
                if (Math.abs(cRow - row) < 2 && Math.abs(cCol - col) < 2) {
                    tooClose = true;
                    break;
                }
            }
            if (tooClose) continue;

            double x = col * GameConstant.TILE_SIZE +
                       (GameConstant.TILE_SIZE - Collectible.SIZE) / 2.0;

            double y = row * GameConstant.TILE_SIZE +
                       (GameConstant.TILE_SIZE - Collectible.SIZE) / 2.0;

            collectibles.add(new Collectible(x, y));
        }

        return collectibles;
    }

    private double distance(int row, int col, Player p) {
        int pRow = (int) (p.getY() / GameConstant.TILE_SIZE);
        int pCol = (int) (p.getX() / GameConstant.TILE_SIZE);
        return Math.hypot(row - pRow, col - pCol);
    }

    private double distance(int row, int col, Zombie z) {
        int zRow = (int) (z.getY() / GameConstant.TILE_SIZE);
        int zCol = (int) (z.getX() / GameConstant.TILE_SIZE);
        return Math.hypot(row - zRow, col - zCol);
    }
}
