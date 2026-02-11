package model;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents the maze grid and provides tile-level queries.
 */
public class Maze {
	
    private Tile[][] tiles;
    private int rows;
    private int cols;

    public Maze(TileType[][] layout) {
        rows = layout.length;
        cols = layout[0].length;
        tiles = new Tile[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c] = new Tile(layout[r][c]);
            }
        }
    }
    
    public Maze(File inputFile) {
    	try {
    		Scanner fileReader = new Scanner(inputFile);
    		ArrayList<char[]> fileContents = new ArrayList<>(); 
    		
    		while (fileReader.hasNext()) {
    			fileContents.add(fileReader.nextLine().toCharArray());
    		}
    		fileReader.close();
    		System.out.println("Loaded level from:         " + inputFile.toURI());
    		
    		rows = fileContents.size();
    		cols = fileContents.get(0).length;
    		tiles = new Tile[rows][cols];
    		
    		for (int r = 0; r < rows; r++) {
    			for (int c = 0; c < cols; c++) {
    				tiles[r][c] = switch (fileContents.get(r)[c]) {
    					case '█' -> new Tile(TileType.WALL);
    					case ' ' -> new Tile(TileType.FLOOR);
    					case 'X' -> new Tile(TileType.EXIT);
    					default  -> new Tile(TileType.FLOOR);
    				};
    			}
    		}
    		
    	} catch (FileNotFoundException e) {
    		System.err.println(e);
    		System.err.println("Loading default maze...");
    		
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
