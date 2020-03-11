package com.coolioasjulio.pacman;

import com.coolioasjulio.arcademachine.gameutils.InputManager;
import com.coolioasjulio.pacman.engine.BoxCollider;
import com.coolioasjulio.pacman.engine.Coord;
import com.coolioasjulio.pacman.engine.Drawer;
import com.coolioasjulio.pacman.engine.GameObject;
import com.coolioasjulio.pacman.engine.Sprite;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.awt.Color;

public class PacMan extends GameObject {

    private static final Set<Integer> movementKeyCodes = new HashSet<>(Arrays.asList(KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN));

    private Direction direction = Direction.EAST;
    private int size;
    private int speed;
    private Coord prevTile;

    public PacMan(int x, int y, int size, Color color, Color bgColor) {
        super(x, y, new BoxCollider(0, 0, size, size), new PacManSprite(size, color, bgColor));
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

    public void update(LevelMap levelMap, double dt) {
        Coord currTile = getTile();
        updateDirection();
        Utils.moveGameObject(this, levelMap, direction, Utils.round(speed * dt), size);
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
        if (InputManager.keyPressed(KeyEvent.VK_UP)) newDir = Direction.NORTH;
        else if (InputManager.keyPressed(KeyEvent.VK_RIGHT)) newDir = Direction.EAST;
        else if (InputManager.keyPressed(KeyEvent.VK_DOWN)) newDir = Direction.SOUTH;
        else if (InputManager.keyPressed(KeyEvent.VK_LEFT)) newDir = Direction.WEST;
        else return;

        int threshold = 3 * size / 8;
        if (newDir != direction && newDir != Utils.opposite(direction)) {
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
    }

    private static class PacManSprite extends Sprite {
        private int size;
        private final Color color;
        private final Color bgColor;
        private Supplier<Direction> directionSupplier;

        public PacManSprite(int size, Color color, Color bgColor) {
            super(0, 0, 10);
            this.size = size;
            this.color = color;
            this.bgColor = bgColor;
        }

        private void setDirectionSupplier(Supplier<Direction> directionSupplier) {
            this.directionSupplier = directionSupplier;
        }

        public void drawActiveImage(Drawer d) {
            d.setColor(color);
            d.fillOval(getX(), getY(), size, size);
            d.setColor(bgColor);
            int[] x = new int[]{size / 4, size / 2, 3 * size / 4};
            int[] y = new int[]{0, 3 * size / 5, 0};
            int[] temp = y;
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
            if (getActiveImageIndex() != 0) d.fillPolygon(x, y);
        }

        public int numImages() {
            return 2;
        }
    }
}
