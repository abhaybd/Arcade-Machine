package com.coolioasjulio.arcademachine.launcher.pacman.engine;

import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * This is an implementation-agnostic class used for UI rendering. The implementation of this class should wrap around an
 * existing rendering class, and just call their corresponding methods. The reason for this class is to not bound the games
 * to any specific UI implementation.
 */
public interface Drawer {
    void drawOval(int x, int y, int width, int height);

    void drawRect(int x, int y, int width, int height);

    void fillOval(int x, int y, int width, int height);

    void fillRect(int x, int y, int width, int height);

    void setColor(Color c);

    void drawImage(BufferedImage image, int x, int y);

    void fillPolygon(int[] x, int[] y);
}
