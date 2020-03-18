package com.coolioasjulio.arcademachine;

public abstract class Button {

    public enum Event {
        PRESSED, RELEASED, NONE
    }

    private boolean prevValue = false;
    private int keycode;

    public Button(int keycode) {
        this.keycode = keycode;
    }

    public abstract boolean isDown();

    public int getKeycode() {
        return keycode;
    }

    public Event getEvent()
    {
        boolean down = isDown();
        Event e = Event.NONE;
        if (down != prevValue)
        {
            if (down) e = Event.PRESSED;
            else e = Event.RELEASED;
            prevValue = down;
        }
        return e;
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
