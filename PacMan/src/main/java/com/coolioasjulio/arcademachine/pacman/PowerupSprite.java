package com.coolioasjulio.arcademachine.pacman;

import com.coolioasjulio.arcademachine.pacman.engine.BoxCollider;
import com.coolioasjulio.arcademachine.pacman.engine.Drawer;
import com.coolioasjulio.arcademachine.pacman.engine.Sprite;

import java.awt.*;

public class PowerupSprite extends Sprite {
    private final int size;
    private final Color color;

    public PowerupSprite(int size, Color color) {
        super(0, 0, 0);
        this.size = size;
        this.color = color;
    }

    public BoxCollider getBoundingCollider() {
        return new BoxCollider(size / 4, size / 4, size / 2, size / 2);
    }

    @Override
    public int numImages() {
        return 1;
    }

    @Override
    public void drawActiveImage(Drawer d) {
        d.setColor(color);
        d.fillOval(getX() + size / 4, getY() + size / 4, size / 2, size / 2);
    }
}
