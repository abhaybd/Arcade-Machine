package com.coolioasjulio.arcademachine.launcher.pacman;

import com.coolioasjulio.arcademachine.launcher.pacman.engine.BoxCollider;
import com.coolioasjulio.arcademachine.launcher.pacman.engine.Drawer;
import com.coolioasjulio.arcademachine.launcher.pacman.engine.Sprite;

import java.awt.*;

public class PelletSprite extends Sprite {

    private final int size;
    private final Color color;

    public PelletSprite(int size, Color color) {
        super(0, 0, 0);
        this.size = size;
        this.color = color;
    }

    public BoxCollider getBoundingCollider() {
        return new BoxCollider(size * 3 / 8, size * 3 / 8, size / 4, size / 4);
    }

    @Override
    public int numImages() {
        return 1;
    }

    @Override
    public void drawActiveImage(Drawer d) {
        d.setColor(color);
        d.fillRect(getX() + size * 3 / 8, getY() + size * 3 / 8, size / 4, size / 4);
    }
}
