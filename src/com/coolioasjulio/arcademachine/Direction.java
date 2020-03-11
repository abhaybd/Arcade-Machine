package com.coolioasjulio.arcademachine;

import java.awt.event.KeyEvent;

public enum Direction {
    NORTH(KeyEvent.VK_UP), EAST(KeyEvent.VK_RIGHT), SOUTH(KeyEvent.VK_DOWN), WEST(KeyEvent.VK_LEFT);

    private int keycode;

    Direction(int keycode) {
        this.keycode = keycode;
    }

    public int getKeycode() {
        return keycode;
    }
}
