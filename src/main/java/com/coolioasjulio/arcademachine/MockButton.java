package com.coolioasjulio.arcademachine;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

public class MockButton extends Button implements AWTEventListener {
    private volatile boolean down = false;

    public MockButton(int keycode) {
        super(keycode);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
    }

    @Override
    public boolean isDown() {
        return down;
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        try {
            KeyEvent evt = (KeyEvent) event;
            if (evt.getKeyCode() == getKeycode()) {
                down = evt.getID() == KeyEvent.KEY_PRESSED;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
