package com.coolioasjulio.pacman;

import com.coolioasjulio.pacman.engine.BoxCollider;
import com.coolioasjulio.pacman.engine.GameObject;

public class Utils {
    public static boolean inRange(int num, int low, int high) {
        return num >= low && num < high;
    }

    public static int getDeltaX(Direction direction) {
        switch (direction) {
            case EAST:
                return 1;

            case WEST:
                return -1;

            default:
            case NORTH:
            case SOUTH:
                return 0;
        }
    }

    public static int getDeltaY(Direction direction) {
        switch (direction) {
            case NORTH:
                return -1;

            case SOUTH:
                return 1;

            default:
            case EAST:
            case WEST:
                return 0;
        }
    }

    public static int round(double d) {
        return (int) Math.round(d);
    }

    public static <T> T randomChoice(T... arr) {
        return arr[(int) (Math.random() * arr.length)];
    }

    public static Direction opposite(Direction dir) {
        if (dir == null) return null;
        switch (dir) {
            case WEST:
                return Direction.EAST;

            case EAST:
                return Direction.WEST;

            case NORTH:
                return Direction.SOUTH;

            default:
            case SOUTH:
                return Direction.NORTH;
        }
    }

    public static void warpEdges(GameObject obj, LevelMap levelMap, int size) {
        if (obj.getX() / size >= levelMap.getWidth()) {
            obj.setLocalPosition(0, obj.getY());
        } else if (obj.getX() / size < 0) {
            obj.setLocalPosition((levelMap.getWidth() - 1) * size, obj.getY());
        }
        if (obj.getY() / size >= levelMap.getHeight()) {
            obj.setLocalPosition(obj.getX(), 0);
        } else if (obj.getY() / size < 0) {
            obj.setLocalPosition(obj.getX(), (levelMap.getHeight() - 1) * size);
        }
    }

    public static void snapGameObject(GameObject obj, int size, int threshold) {
        int x = obj.getX();
        if (Math.abs(obj.getTileX(size) * size - obj.getX()) <= threshold) {
            x = obj.getTileX(size) * size;
        }
        int y = obj.getY();
        if (Math.abs(obj.getTileY(size) * size - obj.getY()) <= threshold) {
            y = obj.getTileY(size) * size;
        }
        obj.setLocalPosition(x, y);
    }

    public static void moveGameObject(GameObject obj, LevelMap levelMap, Direction direction, int distance, int size) {
        int dx = Utils.getDeltaX(direction);
        int dy = Utils.getDeltaY(direction);
        int newXPixel = Utils.round(obj.getX() + dx * distance);
        int newYPixel = Utils.round(obj.getY() + dy * distance);
        if (levelMap.collides(new BoxCollider(newXPixel, newYPixel, size, size), size)) {
            int x = obj.getX(), y = obj.getY();
            switch (direction) {
                case EAST:
                    x = size * (int) Math.ceil(((double) obj.getX()) / size);
                    break;

                case WEST:
                    x = size * (int) Math.floor(((double) obj.getX()) / size);
                    break;

                case SOUTH:
                    y = size * (int) Math.ceil(((double) obj.getY()) / size);
                    break;

                case NORTH:
                    y = size * (int) Math.floor(((double) obj.getY()) / size);
                    break;
            }
            obj.setLocalPosition(x, y);
        } else {
            obj.setLocalPosition(newXPixel, newYPixel);
        }
    }
}
