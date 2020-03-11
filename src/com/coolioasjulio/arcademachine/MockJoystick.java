package com.coolioasjulio.arcademachine;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

public class MockJoystick extends Joystick implements AWTEventListener {
    private volatile Direction direction = null;

    public MockJoystick() {
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        try {
            KeyEvent evt = (KeyEvent) event;
            Direction newDir = null;
            if (evt.getKeyCode() == KeyEvent.VK_UP) {
                newDir = Direction.NORTH;
            } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
                newDir = Direction.EAST;
            } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                newDir = Direction.SOUTH;
            } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
                newDir = Direction.WEST;
            }
            if (newDir != null) {
                direction = evt.getID() == KeyEvent.KEY_PRESSED ? newDir : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
