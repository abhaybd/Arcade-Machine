package com.coolioasjulio.pacman.engine;

import com.coolioasjulio.pacman.Utils;

import java.util.Objects;

public class BoxCollider implements Locatable {
    private Locatable parent;
    private int x, y, width, height;

    public BoxCollider(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setParent(Locatable parent) {
        this.parent = parent;
    }

    public Locatable getParent() {
        return parent;
    }

    public Coord getLocalPosition() {
        return new Coord(x, y);
    }

    public boolean intersects(BoxCollider c) {
        int xmin1 = getX();
        int xmax1 = getX() + width;
        int ymin1 = getY();
        int ymax1 = getY() + height;

        int xmin2 = c.getX();
        int xmax2 = c.getX() + c.width;
        int ymin2 = c.getY();
        int ymax2 = c.getY() + c.height;

        boolean intersectsX =
                Utils.inRange(xmin1, xmin2, xmax2) ||
                        Utils.inRange(xmax1, xmin2 + 1, xmax2) ||
                        Utils.inRange(xmin2, xmin1, xmax1) ||
                        Utils.inRange(xmax2, xmin1 + 1, xmax1);
        boolean intersectsY =
                Utils.inRange(ymin1, ymin2, ymax2) ||
                        Utils.inRange(ymax1, ymin2 + 1, ymax2) ||
                        Utils.inRange(ymin2, ymin1, ymax1) ||
                        Utils.inRange(ymax2, ymin1 + 1, ymax1);
        return intersectsX && intersectsY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BoxCollider)) return false;
        BoxCollider c = (BoxCollider) o;
        return c.x == x && c.y == y && c.width == width && c.height == height;
    }
}
