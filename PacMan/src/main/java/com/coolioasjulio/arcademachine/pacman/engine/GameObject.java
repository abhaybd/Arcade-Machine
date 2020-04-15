package com.coolioasjulio.arcademachine.pacman.engine;

import com.coolioasjulio.arcademachine.pacman.Utils;

import java.util.Arrays;

public class GameObject implements Locatable {
    private int x, y;
    private Sprite[] sprites;
    private BoxCollider collider;
    private Locatable parent;
    private int activeSprite;

    public GameObject(int x, int y, BoxCollider collider, Sprite... sprites) {
        this.x = x;
        this.y = y;
        this.sprites = sprites;
        this.collider = collider;
        if (collider != null) this.collider.setParent(this);
        Arrays.stream(sprites).forEach(e -> e.setParent(this));
    }

    public BoxCollider getCollider() {
        return collider;
    }

    public Sprite[] getSprites() {
        return sprites;
    }

    public void setActiveSprite(int activeSprite) {
        this.activeSprite = activeSprite;
    }

    /**
     * Get the active sprite of the game object.
     *
     * @return The active sprite, or null if there are no sprites or invalid activeSprite index.
     */
    public Sprite getActiveSprite() {
        return Utils.inRange(activeSprite, 0, sprites.length) ? sprites[activeSprite] : null;
    }

    public Coord getLocalPosition() {
        return new Coord(x, y);
    }

    public void setLocalPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Drawer d) {
        Sprite sprite = getActiveSprite();
        if (sprite != null) {
            sprite.drawActiveImage(d);
        }
    }

    public boolean intersects(BoxCollider c) {
        return collider != null && collider.intersects(c);
    }

    public boolean intersects(GameObject go) {
        if (go.getCollider() == null) return false;
        return intersects(go.getCollider());
    }

    public void moveX(int dx) {
        x += dx;
    }

    public void moveY(int dy) {
        y += dy;
    }

    public void setParent(Locatable parent) {
        this.parent = parent;
    }

    public Locatable getParent() {
        return parent;
    }
}
