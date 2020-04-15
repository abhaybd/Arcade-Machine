package com.coolioasjulio.arcademachine.snake;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class Game {

    /**
     * This value is completely arbitrary. It affects the slope/curvature of the delay curve.
     * Increasing this value makes the delay drop faster.
     */
    private static final double DIFFICULTY = 0.2;
    /**
     * The delay, in ms, at level 0
     */
    private static final double LEVEL_0_DELAY = 300;
    
    private int width, height;
    private int level;
    private Snake snake;
    private Coord pellet;
    
    public Game(int width, int height) {
        this.width = width;
        this.height = height;
        
        reset();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Snake getSnake() {
        return snake;
    }

    public Coord getPellet() {
        return pellet;
    }

    public int getLevel() {
        return level;
    }

    public void turnTo(Snake.Direction direction) {
        snake.turnTo(direction);
    }

    public boolean update() {
        snake.forward();
        eatPellet();

        if (isDead()) {
            if (onDeath()) {
                reset();
            } else {
                return true;
            }
        }
        return false;
    }

    public void onLevelUp() {
        // Override me!
    }

    /**
     * Called when the player has died.
     *
     * @return If true, start a new game. Otherwise, quit.
     */
    public boolean onDeath() {
        // Override me!
        return false;
    }

    public long getDelay() {
        double a = LEVEL_0_DELAY / Math.log(2.0); // solve for a where f(0) = LEVEL_0_DELAY
        return Math.round(a * Math.log(1.0 + Math.exp(-DIFFICULTY * level)));
    }

    public boolean isDead() {
        Coord head = snake.getHead().getCoord();
        if (!Utils.inRange(head.getX(), 0, width) || !Utils.inRange(head.getY(), 0, height)) {
            return true;
        }

        List<Block> blocks = snake.getBlocks();
        return blocks.stream().skip(1).map(Block::getCoord).anyMatch(Predicate.isEqual(head));
    }

    public void eatPellet() {
        if (snake.getHead().getCoord().equals(pellet)) {
            level++;
            onLevelUp();
            snake.grow();
            placePellet();
        }
    }

    public void reset() {
        snake = new Snake(new Coord(width/2, height/2), new Coord(width/2, height/2+1));
        level = 0;
        placePellet();
        onLevelUp();
    }

    private void placePellet() {
        Random r = ThreadLocalRandom.current();
        List<Block> blocks = snake.getBlocks();
        do {
            pellet = new Coord(r.nextInt(width), r.nextInt(height));
        } while (blocks.stream().anyMatch(Predicate.isEqual(pellet)));
    }
}
