package com.coolioasjulio.arcademachine.snake;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.swing.*;

public class SnakePanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color PLAYER_COLOR = Color.BLUE;
    private static final Color PELLET_COLOR = Color.YELLOW;

    private Game game;
    private int blockSize;
    private Coord origin;
    private boolean dead;
    private Map<Integer, Font> pixelSizeToFont;

    public SnakePanel(Game game) {
        this.game = game;
        pixelSizeToFont = new HashMap<>();
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int gameWidth = game.getWidth();
        int gameHeight = game.getHeight();
        blockSize = Math.min(getWidth() / gameWidth, getHeight() / gameHeight);
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        origin = new Coord(getWidth() / 2 - gameWidth * blockSize / 2, 0);

        g.setColor(Color.WHITE);
        g.drawRect(origin.getX(), origin.getY(), game.getWidth() * blockSize, game.getHeight() * blockSize);

        Coord pellet = game.getPellet();
        g.setColor(PELLET_COLOR);
        g.fillRect(toPixelsX(pellet.getX()) + blockSize / 4, toPixelsY(pellet.getY()) + blockSize / 4,
                blockSize / 2, blockSize / 2);

        g.setColor(PLAYER_COLOR);
        Snake snake = game.getSnake();
        Coord head = snake.getHead();
        g.fillOval(toPixelsX(head.getX()), toPixelsY(head.getY()), blockSize, blockSize);
        List<Coord> coords = snake.getBody();
        for (ListIterator<Coord> iter = coords.listIterator(1); iter.hasNext(); ) {
            Coord c = iter.next();
            g.fillRect(toPixelsX(c.getX()), toPixelsY(c.getY()), blockSize, blockSize);
        }

        if (dead) {
            drawDeathScreen(g);
        }
    }

    private void drawDeathScreen(Graphics g) {
        int panelWidth = blockSize * 7 * game.getWidth() / 8;
        int panelHeight = blockSize * game.getHeight() / 2;

        int centerX = getWidth() / 2;

        int panelX = centerX - panelWidth / 2;
        int panelY = getHeight() / 4;

        int y = panelY;

        g.setColor(Color.WHITE);
        g.fillRect(panelX, panelY, panelWidth, panelHeight);

        g.setColor(BACKGROUND_COLOR);
        int border = blockSize / 4;
        g.fillRect(panelX + border, panelY + border,
                panelWidth - 2 * border, panelHeight - 2 * border);

        String s = "You died!";
        drawText(g, centerX, y, blockSize * 4, s);
        y += blockSize * 4;
        drawText(g, centerX, y, blockSize * 3 / 2, String.format("Level: % 2d", game.getLevel()));

        y += blockSize * 3 / 2;
        drawText(g, centerX - panelWidth / 4, y, blockSize * 3 / 2, "A - Play again");
        drawText(g, centerX + panelWidth / 4, y, blockSize * 3 / 2, "B - Quit");
    }

    /**
     * Create a font that has approximately the requested height in pixels.
     *
     * @param g              The Graphics object to use for Font manipulation
     * @param font           The base font to use to create new fonts
     * @param fontSizePixels The requested font height in pixels
     * @return A new font that has approximately the requested height in pixels
     */
    private Font getFontWithSize(Graphics g, Font font, int fontSizePixels) {
        Font cached = pixelSizeToFont.get(fontSizePixels);
        if (cached != null && cached.getFontName().equals(font.getFontName())) {
            return cached;
        }
        Font f;
        float size = 1f;
        while (g.getFontMetrics(f = font.deriveFont(size)).getHeight() < fontSizePixels) {
            size++;
        }
        pixelSizeToFont.put(fontSizePixels, f);
        return f;
    }

    private void drawText(Graphics g, int x, int y, int fontSizePixels, String s) {
        Font f = getFontWithSize(g, g.getFont(), fontSizePixels);
        g.setFont(f);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(s);
        int textHeight = metrics.getHeight();
        g.setColor(Color.WHITE);
        g.drawString(s, x - textWidth / 2, y + textHeight);
    }

    private int toPixelsX(int blocks) {
        return blocks * blockSize + origin.getX();
    }

    private int toPixelsY(int blocks) {
        return blocks * blockSize + origin.getY();
    }
}
