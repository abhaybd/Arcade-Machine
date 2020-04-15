package com.coolioasjulio.arcademachine.pacman.engine;

import java.awt.image.BufferedImage;

/**
 * This is an implementation of a Sprite that uses premade images and spritesheets for animations and rendering.
 */
public class ImageSprite extends Sprite {
    private BufferedImage[] spriteSheet;

    public ImageSprite(int x, int y, int fps, BufferedImage[] spriteSheet) {
        super(x, y, fps);
        this.spriteSheet = spriteSheet;
    }

    public int numImages() {
        return spriteSheet.length;
    }

    public void drawActiveImage(Drawer d) {
        d.drawImage(spriteSheet[getActiveImageIndex()], getX(), getY());
    }
}
