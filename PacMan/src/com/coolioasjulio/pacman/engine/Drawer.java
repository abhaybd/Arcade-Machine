package com.coolioasjulio.pacman.engine;

import java.awt.image.BufferedImage;
import java.awt.Color;

public interface Drawer {
    void drawOval(int x, int y, int width, int height);

    void drawRect(int x, int y, int width, int height);

    void fillOval(int x, int y, int width, int height);

    void fillRect(int x, int y, int width, int height);

    void setColor(Color c);

    void drawImage(BufferedImage image, int x, int y);

    void fillPolygon(int[] x, int[] y);
}
