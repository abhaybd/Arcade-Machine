package com.coolioasjulio.arcademachine.snake;

import java.awt.*;
import java.util.List;
import javax.swing.*;

public class SnakePanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color PLAYER_COLOR = Color.BLUE;
    private static final Color PELLET_COLOR = Color.YELLOW;

    private Game game;
    private int blockSize;
    private Coord origin;

    public SnakePanel(Game game) {
        this.game = game;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int gameWidth = game.getWidth();
        int gameHeight = game.getHeight();
        blockSize = Math.min(getWidth() / gameWidth, getHeight() / gameHeight);
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        origin = new Coord(getWidth()/2 - gameWidth*blockSize/2, 0);

        g.setColor(Color.WHITE);
        g.drawRect(origin.getX(), origin.getY(), game.getWidth()*blockSize, game.getHeight()*blockSize);

        Coord pellet = game.getPellet();
        g.setColor(PELLET_COLOR);
        g.fillRect(toPixelsX(pellet.getX()) + blockSize / 4, toPixelsY(pellet.getY()) + blockSize / 4,
                blockSize / 2, blockSize / 2);

        g.setColor(PLAYER_COLOR);
        Snake snake = game.getSnake();
        Coord head = snake.getHead().getCoord();
        g.fillOval(toPixelsX(head.getX()), toPixelsY(head.getY()), blockSize, blockSize);
        List<Block> blocks = snake.getBlocks();
        for (int i = 1; i < blocks.size(); i++) {
            Coord c = blocks.get(i).getCoord();
            g.fillRect(toPixelsX(c.getX()), toPixelsY(c.getY()), blockSize, blockSize);
        }
    }

    private int toPixelsX(int blocks) {
        return blocks * blockSize + origin.getX();
    }

    private int toPixelsY(int blocks) {
        return blocks * blockSize + origin.getY();
    }
}
