package com.coolioasjulio.pacman.engine;

import java.awt.image.BufferedImage;

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
