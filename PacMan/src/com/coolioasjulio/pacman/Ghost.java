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

    public Ghost(int x, int y, int size, Color color, Color fleeColor) {
        super(x, y,
                new BoxCollider(size / 4, size / 4, size / 2, size / 2), new GhostSprite(size, color), new GhostSprite(size, fleeColor));
        this.size = size;
        setActiveSprite(0);
    }

    public void setBehavior(GhostBehavior behavior) {
        this.behavior = behavior;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setFleeing(boolean fleeing) {
        fleeBehavior.started = false;
        this.fleeing = fleeing;
        setActiveSprite(fleeing ? 1 : 0);
    }

    public boolean isFleeing() {
        return fleeing;
    }

    public void update(LevelMap levelMap, double dt) {
        GhostBehavior behavior = fleeing ? fleeBehavior : this.behavior;
        Direction newDir = behavior == null ? Direction.EAST : behavior.getDirection();
        if (newDir != null) {
            if (direction != null && newDir != direction && newDir != Utils.opposite(direction)) {
                int threshold = size / 6;
                if (Math.abs(getTileX(size) * size - getX()) <= threshold &&
                        Math.abs(getTileY(size) * size - getY()) <= threshold) {
                    int dx = Utils.getDeltaX(newDir);
                    int dy = Utils.getDeltaY(newDir);
                    if (PacManGame.getInstance().getLevelMap().isOpen(getTileX(size) + dx, getTileY(size) + dy)) {
                        Utils.snapGameObject(this, size, threshold);
                        direction = newDir;
                    }
                }
            } else {
                direction = newDir;
            }
        } else if (direction == null) {
            direction = Direction.EAST;
        }

        Coord currTile = getTile();
        Utils.moveGameObject(this, levelMap, direction, Utils.round(speed * dt), size);
        Utils.warpEdges(this, levelMap, size);
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

    public Coord getTile() {
        return new Coord(getTileX(size), getTileY(size));
    }

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
