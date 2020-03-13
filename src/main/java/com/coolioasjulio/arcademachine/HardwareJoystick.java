package com.coolioasjulio.arcademachine;

import net.java.games.input.Component;

public class HardwareJoystick extends Joystick {
    private Component component;

    public HardwareJoystick(Component component) {
        this.component = component;
    }

    @Override
    public Direction getDirection() {
        return Direction.values()[((int) (component.getPollData() * 4f)) % Direction.values().length];
    }
}
