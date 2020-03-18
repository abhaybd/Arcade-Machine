package com.coolioasjulio.arcademachine.launcher.pacman;

import com.coolioasjulio.arcademachine.launcher.pacman.engine.BoxCollider;
import com.coolioasjulio.arcademachine.launcher.pacman.engine.GameObject;

public class Utils {
    public static boolean inRange(int num, int low, int high) {
        return num >= low && num < high;
    }

    /**
     * Get the direction of travel in the x axis for the given direction.
     *
     * @param direction The direction to get the x delta for.
     * @return 1 if East, -1 if West, 0 otherwise.
     */
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

    /**
     * Get the direction of travel in the y axis for the given direction.
     *
     * @param direction The direction to get the y delta for.
     * @return 1 if South, -1 if North, 0 otherwise. North is negative due to +y being downwards.
     */
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

    /**
     * Return the direction opposite of the given direction.
     *
     * @param dir The direction of which to find the opposite.
     * @return North if South, South if North, East if West, West if East.
     */
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


    /**
     * Warp an object around the edges of the level map. This is used to teleport through tunnels.
     *
     * @param obj      The game object to warp.
     * @param levelMap The active level map.
     * @param size     The side length of a tile, in pixels.
     */
    public static void warpEdges(GameObject obj, LevelMap levelMap, int size) {
        if (obj.getParent() != null) {
            throw new IllegalArgumentException("Only objects that aren't children can be warped!");
        }
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

    /**
     * Snap a game object to the center of its block if it's close enough to the center.
     *
     * @param obj       The GameObject to snap.
     * @param size      The side length of a tile, in pixels.
     * @param threshold The distance threshold under which snapping is allowed, in pixels.
     */
    public static void snapGameObject(GameObject obj, int size, int threshold) {
        if (obj.getParent() != null) {
            throw new IllegalArgumentException("Only objects that aren't children can be snapped!");
        }
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

    /**
     * Move the supplied game object in the supplied level map in the supplied direction by at most the supplied distance.
     * It will try to move by the full distance, but if there is a wall in the way, it will move as much as possible instead.
     *
     * @param obj The object to move.
     * @param levelMap The active level map.
     * @param direction The direction to move in.
     * @param distance The distance to move, in pixels. This cannot exceed the size of a tile.
     * @param size The side length of a tile, in pixels.
     */
    public static void moveGameObject(GameObject obj, LevelMap levelMap, Direction direction, int distance, int size) {
        if (distance >= size)
        {
            throw new IllegalArgumentException("Distance to move cannot be more than the size of a tile!");
        }
        int dx = Utils.getDeltaX(direction);
        int dy = Utils.getDeltaY(direction);
        int newXPixel = Utils.round(obj.getX() + dx * distance);
        int newYPixel = Utils.round(obj.getY() + dy * distance);
        // If moving the full distance results in a collision with a wall, then snap backwards to the closest open tile
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
            // Otherwise, just move there
            obj.setLocalPosition(newXPixel, newYPixel);
        }
    }
}
