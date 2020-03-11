package com.coolioasjulio.arcademachine;

public abstract class Button {
    private boolean prevValue = false;
    private int keycode;

    public Button(int keycode) {
        this.keycode = keycode;
    }

    public abstract boolean isDown();

    public int getKeycode() {
        return keycode;
    }

    public boolean pressed() {
        boolean down = isDown();
        boolean ret = down && !prevValue;
        prevValue = down;
        return ret;
    }

    public boolean released() {
        boolean down = isDown();
        boolean ret = !down && prevValue;
        prevValue = down;
        return ret;
    }
}
