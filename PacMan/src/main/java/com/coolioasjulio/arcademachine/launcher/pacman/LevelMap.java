package com.coolioasjulio.arcademachine.launcher.pacman;

import com.coolioasjulio.arcademachine.launcher.pacman.engine.BoxCollider;
import com.coolioasjulio.arcademachine.launcher.pacman.engine.Coord;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class LevelMap {
    /**
     * Load a LevelMap object from an image. Black pixels are walls, white are passages, red is the spawn point, and green are the powerups.
     * Pellets are placed at all tiles without a spawn point or powerup.
     *
     * @param image The image to load the level map from.
     * @return The LevelMap object loaded from the image.
     */
    public static LevelMap loadFromImg(BufferedImage image) {
        boolean[][] map = new boolean[image.getHeight()][image.getWidth()];
        int spawnX = -1, spawnY = -1;
        List<Coord> powerups = new ArrayList<>();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                int rgb = image.getRGB(col, row);
                map[row][col] = rgb == Color.BLACK.getRGB();
                if (rgb == Color.RED.getRGB()) {
                    spawnX = col;
                    spawnY = row;
                } else if (rgb == Color.GREEN.getRGB()) {
                    powerups.add(new Coord(col, row));
                }
            }
        }
        if (spawnX == -1) {
            throw new IllegalArgumentException("The supplied image doesn't have a spawn point! The spawn point must be a pixel with values (255,0,0)");
        }
        return new LevelMap(map, spawnX, spawnY, powerups.toArray(new Coord[0]));
    }

    private boolean[][] walls;
    private int spawnX, spawnY;
    private Coord[] powerups;

    public LevelMap(boolean[][] walls, int spawnX, int spawnY, Coord... powerups) {
        int width = walls[0].length;
        for (boolean[] row : walls) {
            if (row.length != width) {
                throw new IllegalArgumentException("Cannot use jagged array!");
            }
        }
        this.walls = walls;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.powerups = powerups;
    }

    /**
     * Get the width of the level.
     *
     * @return The width in tiles.
     */
    public int getWidth() {
        return walls[0].length;
    }

    /**
     * Get the height of the level.
     *
     * @return The height in tiles.
     */
    public int getHeight() {
        return walls.length;
    }

    /**
     * Checks if the tile is a wall or not.
     *
     * @param x X coordinate, in tiles. Positive is to the right.
     * @param y Y coordinate, in tiles. Positive is down.
     * @return True if the tile is occupied by a wall, false otherwise.
     */
    public boolean isWall(int x, int y) {
        return walls[y][x];
    }

    /**
     * Checks if the supplied tile is navigable. (not a wall and within the bounds of the level)
     *
     * @param x X coordinate, in tiles. Positive is to the right.
     * @param y Y coordinate, in tiles. Positive is down.
     * @return True if there is no wall and the supplied coordinate is an open passageway.
     */
    public boolean isOpen(int x, int y) {
        return Utils.inRange(x, 0, getWidth()) && Utils.inRange(y, 0, getHeight()) && !isWall(x, y);
    }

    /**
     * Gets the coordinate locations of all the powerups.
     *
     * @return A {@link Coord} array of all powerups in the level. (Not just the unconsumed ones) The position is in tiles.
     */
    public Coord[] getPowerupsCoords() {
        return powerups;
    }

    /**
     * Gets the x coordinate of the spawn location.
     *
     * @return The x coordinate of the spawn location.
     */
    public int spawnX() {
        return spawnX;
    }

    /**
     * Gets the y coordinate of the spawn location.
     *
     * @return The y coordinate of the spawn location.
     */
    public int spawnY() {
        return spawnY;
    }

    /**
     * Check if the supplied {@link BoxCollider} collides any of the walls in the level.
     *
     * @param collider The collider to check against.
     * @param size     The side length of a tile, in pixels.
     * @return True if the collider collides any wall. False otherwise.
     */
    public boolean collides(BoxCollider collider, int size) {
        for (int row = 0; row < walls.length; row++) {
            for (int col = 0; col < walls[row].length; col++) {
                // Create a size x size collider to represent the wall block, and check against the collider
                if (walls[row][col] && new BoxCollider(col * size, row * size, size, size).intersects(collider)) {
                    return true;
                }
            }
        }
        return false;
    }
}
