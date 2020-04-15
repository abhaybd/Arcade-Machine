package com.coolioasjulio.arcademachine.pacman.engine;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * This is basically a wrapper around the swing Graphics class that fulfills the implementation-agnostic Drawer contract.
 */
public class AwtGraphicsAdapter implements Drawer {
    private Graphics g;

    public AwtGraphicsAdapter(Graphics g) {
        this.g = g;
    }

    public void drawOval(int x, int y, int width, int height) {
        g.drawOval(x, y, width, height);
    }

    public void drawRect(int x, int y, int width, int height) {
        g.drawRect(x, y, width, height);
    }

    public void fillOval(int x, int y, int width, int height) {
        g.fillOval(x, y, width, height);
    }

    public void fillRect(int x, int y, int width, int height) {
        g.fillRect(x, y, width, height);
    }

    public void setColor(Color c) {
        g.setColor(c);
    }

    public void drawImage(BufferedImage image, int x, int y) {
        g.drawImage(image, x, y, null);
    }

    public void fillPolygon(int[] x, int[] y) {
        g.fillPolygon(x, y, x.length);
    }
}
