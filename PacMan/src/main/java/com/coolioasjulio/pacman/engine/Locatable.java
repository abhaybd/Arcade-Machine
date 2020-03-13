package com.coolioasjulio.pacman.engine;

public interface Locatable {
    /**
     * Get the position relative to the parent object. If parent is null, this is the absolute position as well.
     *
     * @return The relative position to the parent, or absolute position if no parent, in pixels.
     */
    Coord getLocalPosition();

    /**
     * Set the parent of this Locatable object. The relative position will be relative to this parent.
     *
     * @param parent The new parent of this object.
     */
    void setParent(Locatable parent);

    /**
     * Get the parent of the Locatable.
     *
     * @return The parent of this object. Null if there is no parent.
     */
    Locatable getParent();

    /**
     * Get the x-coordinate of the {@link Coord} object returned by {@link Locatable#getLocalPosition()}
     *
     * @return The local x-coordinate in pixels.
     */
    default int getLocalX() {
        return getLocalPosition().x;
    }

    /**
     * Get the y-coordinate of the {@link Coord} object returned by {@link Locatable#getLocalPosition()}
     *
     * @return The local y-coordinate in pixels.
     */
    default int getLocalY() {
        return getLocalPosition().y;
    }

    /**
     * Returns the absolute position.
     *
     * @return The absolute position in pixels.
     */
    default Coord getPosition() {
        return new Coord(getX(), getY());
    }

    /**
     * Returns the absolute x-coordinate of this object. This considers the positions of all parents of this object.
     *
     * @return This absolute x-coordinate in pixels.
     */
    default int getX() {
        Locatable parent = getParent();
        int localX = getLocalX();
        return parent == null ? localX : parent.getX() + localX;
    }

    /**
     * Returns the absolute y-coordinate of this object. This considers the positions of all parents of this object.
     *
     * @return This absolute y-coordinate in pixels.
     */
    default int getY() {
        Locatable parent = getParent();
        int localY = getLocalY();
        return parent == null ? localY : parent.getY() + localY;
    }

    /**
     * Get the absolute tile position. This is used for games where there are discrete "tiles", which are squares of a certain size.
     * This does floor division by the tile size to get the tile position.
     *
     * @param size The side length of the tile, in pixels.
     * @return The x-coordinate of the object, in tiles.
     */
    default int getTileX(int size) {
        return (int) Math.round(getX() / (double) size);
    }

    /**
     * Get the absolute tile position. This is used for games where there are discrete "tiles", which are squares of a certain size.
     * This does floor division by the tile size to get the tile position.
     *
     * @param size The side length of the tile, in pixels.
     * @return The y-coordinate of the object, in tiles.
     */
    default int getTileY(int size) {
        return (int) Math.round(getY() / (double) size);
    }
}
