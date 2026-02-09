package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Responsible for spawning zombies and collectibles.
 */
public class Spawner {

    private final Maze maze;
    private final Random random = new Random();

    public Spawner(Maze maze) {
        this.maze = maze;
    }

    private int[] getRandomFloorTile(int rowMin, int rowMax) {
        int row, col;
        do {
            row = random.nextInt(rowMax - rowMin + 1) + rowMin;
            col = random.nextInt(maze.getCols());
        } while (!maze.isWalkable(row, col));
        return new int[]{row, col};
    }

    private boolean tooClose(double x1, double y1, double x2, double y2, double minDist) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy) < minDist;
    }

    public List<Zombie> spawnZombies(Player player) {
        List<Zombie> zombies = new ArrayList<>();

        int rows = maze.getRows();
        int mid = rows / 2;

        int zombiesTop = GameConstant.ZOMBIE_COUNT / 2;
        int zombiesBottom = GameConstant.ZOMBIE_COUNT - zombiesTop;

        double playerX = player.getX();
        double playerY = player.getY();
        double minDistFromPlayer = 4 * GameConstant.TILE_SIZE;
        double minDistBetweenZombies = 40;

        for (int i = 0; i < zombiesTop; i++) {
            int[] pos;
            double x, y;

            while (true) {
                pos = getRandomFloorTile(1, mid - 1);
                x = pos[1] * GameConstant.TILE_SIZE + 4;
                y = pos[0] * GameConstant.TILE_SIZE + 4;

                boolean ok = true;

                for (Zombie z : zombies) {
                    if (tooClose(x, y, z.getX(), z.getY(), minDistBetweenZombies)) {
                        ok = false;
                        break;
                    }
                }

                if (ok && tooClose(x, y, playerX, playerY, minDistFromPlayer)) {
                    ok = false;
                }

                if (ok) break;
            }

            zombies.add(new Zombie(pos[0], pos[1], maze));
        }

        for (int i = 0; i < zombiesBottom; i++) {
            int[] pos;
            double x, y;

            while (true) {
                pos = getRandomFloorTile(mid + 1, rows - 2);
                x = pos[1] * GameConstant.TILE_SIZE + 4;
                y = pos[0] * GameConstant.TILE_SIZE + 4;

                boolean ok = true;

                for (Zombie z : zombies) {
                    if (tooClose(x, y, z.getX(), z.getY(), minDistBetweenZombies)) {
                        ok = false;
                        break;
                    }
                }

                if (ok && tooClose(x, y, playerX, playerY, minDistFromPlayer)) {
                    ok = false;
                }

                if (ok) break;
            }

            zombies.add(new Zombie(pos[0], pos[1], maze));
        }

        return zombies;
    }

    public List<Collectible> spawnCollectibles(List<Zombie> zombies) {
        List<Collectible> collectibles = new ArrayList<>();

        int total = GameConstant.COLLECTIBLE_COUNT;
        double minDistCollectibles = 32;
        double minDistToZombie = 40;

        for (int i = 0; i < total; i++) {
            int[] pos;
            double x, y;

            while (true) {
                pos = getRandomFloorTile(1, maze.getRows() - 2);

                x = pos[1] * GameConstant.TILE_SIZE + 8;
                y = pos[0] * GameConstant.TILE_SIZE + 8;

                boolean ok = true;

                for (Collectible c : collectibles) {
                    if (tooClose(x, y, c.getX(), c.getY(), minDistCollectibles)) {
                        ok = false;
                        break;
                    }
                }

                if (ok) {
                    for (Zombie z : zombies) {
                        if (tooClose(x, y, z.getX(), z.getY(), minDistToZombie)) {
                            ok = false;
                            break;
                        }
                    }
                }

                if (ok) break;
            }

            collectibles.add(new Collectible(pos[0], pos[1]));
        }

        return collectibles;
    }
}
