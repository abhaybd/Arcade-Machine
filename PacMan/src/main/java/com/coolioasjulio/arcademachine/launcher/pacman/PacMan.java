package com.coolioasjulio.arcademachine.launcher.pacman;

import com.coolioasjulio.arcademachine.launcher.gameutils.InputManager;
import com.coolioasjulio.arcademachine.launcher.pacman.engine.BoxCollider;
import com.coolioasjulio.arcademachine.launcher.pacman.engine.Coord;
import com.coolioasjulio.arcademachine.launcher.pacman.engine.Drawer;
import com.coolioasjulio.arcademachine.launcher.pacman.engine.GameObject;
import com.coolioasjulio.arcademachine.launcher.pacman.engine.Sprite;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.function.Supplier;
import java.awt.Color;

public class PacMan extends GameObject {
    private Direction direction = Direction.EAST;
    private int size;
    private int speed;
    private Coord prevTile;

    public PacMan(int x, int y, int size, Color color, Color bgColor) {
        super(x, y, new BoxCollider(0, 0, size, size), new PacManSprite(size, color, bgColor));
        // Instead of using multiple sprites, we'll just use one and dynamically rotate it
        ((PacManSprite) getSprites()[0]).setDirectionSupplier(this::getDirection);
        this.size = size;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Update pacman by a timestep. The direction it moves in is determined by the player input.
     * This handles moving the player and wrapping around the edges.
     *
     * @param levelMap The map of the active level.
     * @param dt       The elapsed time in seconds since the last update.
     */
    public void update(LevelMap levelMap, double dt) {
        Coord currTile = getTile();
        // Get the new direction to move in
        updateDirection();
        // Move in the appropriate direction
        Utils.moveGameObject(this, levelMap, direction, Utils.round(speed * dt), size);
        // If required, wrap around the map
        Utils.warpEdges(this, levelMap, size);
        if (!currTile.equals(getTile())) {
            prevTile = currTile;
        }
    }

    public Coord getPrevTile() {
        return prevTile;
    }

    public Coord getTile() {
        return new Coord(getTileX(size), getTileY(size));
    }

    private void updateDirection() {
        Direction newDir;
        // If a key was pressed in the last tick, we're changing directions
        if (InputManager.keyPressed(KeyEvent.VK_UP)) newDir = Direction.NORTH;
        else if (InputManager.keyPressed(KeyEvent.VK_RIGHT)) newDir = Direction.EAST;
        else if (InputManager.keyPressed(KeyEvent.VK_DOWN)) newDir = Direction.SOUTH;
        else if (InputManager.keyPressed(KeyEvent.VK_LEFT)) newDir = Direction.WEST;
        else return;

        int threshold = 3 * size / 8; // the snap threshold is +-3/8 of the tile size
        if (newDir != direction && newDir != Utils.opposite(direction)) {
            // If we're close enough to the center of the block, snap to the middle
            // This is required so we don't collide with any walls.
            if (Math.abs(getTileX(size) * size - getX()) <= threshold &&
                    Math.abs(getTileY(size) * size - getY()) <= threshold) {
                int dx = Utils.getDeltaX(newDir);
                int dy = Utils.getDeltaY(newDir);
                // If the tile in the new direction is open, snap to the middle
                if (PacManGame.getInstance().getLevelMap().isOpen(getTileX(size) + dx, getTileY(size) + dy)) {
                    Utils.snapGameObject(this, size, threshold);
                    direction = newDir;
                }
            }
        } else {
            direction = newDir;
        }
    }

    /**
     * This is the sprite of the pac man, which handles the rendering
     */
    private static class PacManSprite extends Sprite {
        private int size;
        private final Color color;
        private final Color bgColor;
        private Supplier<Direction> directionSupplier;

        public PacManSprite(int size, Color color, Color bgColor) {
            super(0, 0, 10); // Render the animation at 10fps. (5 cycles per second)
            this.size = size;
            this.color = color;
            this.bgColor = bgColor;
        }

        private void setDirectionSupplier(Supplier<Direction> directionSupplier) {
            this.directionSupplier = directionSupplier;
        }

        public void drawActiveImage(Drawer d) {
            // Draw the circle
            d.setColor(color);
            d.fillOval(getX(), getY(), size, size);
            // Then draw the mouth. Depending on the direction of travel, the mouth should be rotated
            d.setColor(bgColor);
            // These are the x and y coordinates if we're moving north
            int[] x = new int[]{size / 4, size / 2, 3 * size / 4};
            int[] y = new int[]{0, 3 * size / 5, 0};
            int[] temp = y;
            // Depending on which direction we're actually moving, rotate the coordinates
            switch (directionSupplier.get()) {
                case NORTH:
                    break;

                case WEST:
                    y = x;
                    x = temp;
                    break;

                case EAST:
                    y = x;
                    x = Arrays.stream(temp).map(i -> size - i).toArray();
                    break;

                case SOUTH:
                    y = Arrays.stream(y).map(i -> size - i).toArray();
                    break;
            }
            x = Arrays.stream(x).map(i -> i + getX()).toArray();
            y = Arrays.stream(y).map(i -> i + getY()).toArray();
            // Render the mouth
            // Since we're being animated, we have two images (mouth open, mouth closed)
            // Only draw the mouth if the mouth is open (index is 1)
            if (getActiveImageIndex() != 0) d.fillPolygon(x, y);
        }

        public int numImages() {
            return 2;
        }
    }
}
