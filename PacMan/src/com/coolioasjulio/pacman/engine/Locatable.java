package com.coolioasjulio.pacman.engine;

public interface Locatable {
    Coord getLocalPosition();

    void setParent(Locatable parent);

    Locatable getParent();

    default int getLocalX() {
        return getLocalPosition().x;
    }

    default int getLocalY() {
        return getLocalPosition().y;
    }

    default Coord getPosition() {
        return new Coord(getX(), getY());
    }

    default int getX() {
        Locatable parent = getParent();
        int localX = getLocalX();
        return parent == null ? localX : parent.getX() + localX;
    }

    default int getY() {
        Locatable parent = getParent();
        int localY = getLocalY();
        return parent == null ? localY : parent.getY() + localY;
    }

    default int getTileX(int size) {
        return (int) Math.round(getX() / (double) size);
    }

    default int getTileY(int size) {
        return (int) Math.round(getY() / (double) size);
    }
}
