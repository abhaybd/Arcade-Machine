package com.coolioasjulio.pacman;

import com.coolioasjulio.pacman.engine.BoxCollider;
import com.coolioasjulio.pacman.engine.Coord;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LevelMap {
    public static LevelMap loadFromImg(BufferedImage image) {
        boolean[][] map = new boolean[image.getHeight()][image.getWidth()];
        int spawnX = 0, spawnY = 0;
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

    public int getWidth() {
        return walls[0].length;
    }

    public int getHeight() {
        return walls.length;
    }

    public boolean isWall(int x, int y) {
        return walls[y][x];
    }

    public boolean isOpen(int x, int y) {
        return Utils.inRange(x, 0, getWidth()) && Utils.inRange(y, 0, getHeight()) && !isWall(x, y);
    }

    public Coord[] getPowerupsCoords() {
        return powerups;
    }

    public int spawnX() {
        return spawnX;
    }

    public int spawnY() {
        return spawnY;
    }

    public boolean collides(BoxCollider collider, int size) {
        for (int row = 0; row < walls.length; row++) {
            for (int col = 0; col < walls[row].length; col++) {
                if (walls[row][col] && new BoxCollider(col * size, row * size, size, size).intersects(collider)) {
                    return true;
                }
            }
        }
        return false;
    }
}
