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

        g.setColor(Color.WHITE);
        g.drawRect(0, 0, game.getWidth()*blockSize, game.getHeight()*blockSize);

        Coord pellet = game.getPellet();
        g.setColor(PELLET_COLOR);
        g.fillRect(blocksToPixels(pellet.getX()) + blockSize / 4, blocksToPixels(pellet.getY()) + blockSize / 4,
                blockSize / 2, blockSize / 2);

        g.setColor(PLAYER_COLOR);
        Snake snake = game.getSnake();
        Coord head = snake.getHead().getCoord();
        g.fillOval(blocksToPixels(head.getX()), blocksToPixels(head.getY()), blockSize, blockSize);
        List<Block> blocks = snake.getBlocks();
        for (int i = 1; i < blocks.size(); i++) {
            Coord c = blocks.get(i).getCoord();
            g.fillRect(blocksToPixels(c.getX()), blocksToPixels(c.getY()), blockSize, blockSize);
        }
    }

    private int blocksToPixels(int blocks) {
        return blocks * blockSize;
    }
}
