package com.coolioasjulio.pacman;

import com.coolioasjulio.pacman.behaviors.GhostAmbushBehavior;
import com.coolioasjulio.pacman.behaviors.GhostFleeBehavior;
import com.coolioasjulio.pacman.engine.BoxCollider;
import com.coolioasjulio.pacman.engine.Coord;
import com.coolioasjulio.pacman.engine.Drawer;
import com.coolioasjulio.pacman.engine.GameObject;
import com.coolioasjulio.pacman.engine.Sprite;

import java.awt.Color;
import java.util.function.Supplier;

public class Ghost extends GameObject {
    /**
     * Size in pixels.
     */
    private int size;
    /**
     * Movement speed in blocks per second.
     */
    private int speed;
    private Direction direction = null;
    private GhostBehavior behavior = null;
    private Coord prevTile;
    private boolean fleeing = false;
    private GhostFleeBehavior fleeBehavior = new GhostFleeBehavior(this);

    /**
     * Create a new Ghost object.
     *
     * @param x         The x coordinate in pixels.
     * @param y         The y coordinate in pixels.
     * @param size      The side length of a block in pixels.
     * @param color     The normal color of the ghost.
     * @param fleeColor The color of the ghost when in flee mode.
     */
    public Ghost(int x, int y, int size, Color color, Color fleeColor) {
        super(x, y,
                new BoxCollider(size / 4, size / 4, size / 2, size / 2), new GhostSprite(size, color), new GhostSprite(size, fleeColor));
        this.size = size;
        setActiveSprite(0);
    }

    /**
     * Set the behavior of the ghost.
     *
     * @param behavior The behavior to use during the game. This is the nominal behavior.
     */
    public void setBehavior(GhostBehavior behavior) {
        this.behavior = behavior;
    }

    /**
     * Get the speed.
     *
     * @return Speed, in pixels per second.
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Set the speed of the ghost.
     *
     * @param speed Speed to set, in pixels per second.
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * Enable/disable flee mode.
     *
     * @param fleeing If true, start fleeing from the player. Otherwise, return to normal behavior.
     */
    public void setFleeing(boolean fleeing) {
        fleeBehavior.started = false;
        this.fleeing = fleeing;
        // Activate the flee sprite
        setActiveSprite(fleeing ? 1 : 0);
    }

    public boolean isFleeing() {
        return fleeing;
    }

    /**
     * Update the ghost by a timestep. The direction it moves in is determined by the active behavior. (e.g. nominal behavior, and is fleeing)
     * This handles moving the ghost around, avoiding obstacles and navigating through the level.
     *
     * @param levelMap The map of the active level.
     * @param dt       The elapsed time in seconds since the last update.
     */
    public void update(LevelMap levelMap, double dt) {
        // Determine the active behavior
        GhostBehavior behavior = fleeing ? fleeBehavior : this.behavior;
        // If for any reason the behavior is null, just move east
        Direction newDir = behavior == null ? Direction.EAST : behavior.getDirection();
        // newDir may be null in edge cases (such as moving through the tunnel)
        // In these cases, don't change the direction, just keep moving
        // This handles direction changes
        if (newDir != null) {
            // Don't allow u-turns
            if (direction != null && newDir != direction && newDir != Utils.opposite(direction)) {
                // Now we know we're turning either right or left
                int threshold = size / 6;
                // Only allow the turn if we're close enough to the center of the current block
                // You can't turn from the edge of a block (since you have to navigate through narrow paths)
                if (Math.abs(getTileX(size) * size - getX()) <= threshold &&
                        Math.abs(getTileY(size) * size - getY()) <= threshold) {
                    int dx = Utils.getDeltaX(newDir);
                    int dy = Utils.getDeltaY(newDir);
                    // If the place you're turning to is open, snap to the nearest block center and change direction
                    if (levelMap.isOpen(getTileX(size) + dx, getTileY(size) + dy)) {
                        // Snapping to the center is necessary so you don't intersect walls once you've turned
                        Utils.snapGameObject(this, size, threshold);
                        direction = newDir;
                    }
                }
            } else {
                direction = newDir; // TODO: check if newDir is opposite of direction?
            }
        } else if (direction == null) {
            // If newDir is null, make sure direction isn't null
            direction = Direction.EAST;
        }

        Coord currTile = getTile();
        // Move the ghost in the appropriate direction by the appropriate amount
        Utils.moveGameObject(this, levelMap, direction, Utils.round(speed * dt), size);
        // If required, warp around the edges of the map
        Utils.warpEdges(this, levelMap, size);
        // If the new tile is different, set the previous tile. This is used for pathfinding.
        if (!currTile.equals(getTile())) {
            prevTile = currTile;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public Coord getPrevTile() {
        return prevTile;
    }

    /**
     * Get the current tile of the ghost. X is to the right, Y is down.
     *
     * @return The current tile of the ghost.
     */
    public Coord getTile() {
        return new Coord(getTileX(size), getTileY(size));
    }

    /**
     * Defines the template for ghost behaviors.
     */
    public abstract static class GhostBehavior {
        protected Ghost ghost;

        public GhostBehavior(Ghost ghost) {
            this.ghost = ghost;
        }

        /**
         * Give the movement direction for the ghost.
         *
         * @return The movement direction for this ghost.
         */
        public abstract Direction getDirection();
    }

    private static class GhostSprite extends Sprite {
        private Color color;
        private int size;

        public GhostSprite(int size, Color color) {
            super(0, 0, 0);
            this.color = color;
            this.size = size;
        }

        public void drawActiveImage(Drawer d) {
            d.setColor(color);
            d.fillOval(getX(), getY(), size, size);
        }

        public int numImages() {
            return 1;
        }
    }
}
