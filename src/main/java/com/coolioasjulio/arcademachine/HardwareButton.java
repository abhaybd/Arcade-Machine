package com.coolioasjulio.arcademachine;

import net.java.games.input.Component;

public class HardwareButton extends Button {
    private final Component component;

    public HardwareButton(int keycode, Component component) {
        super(keycode);
        this.component = component;
    }

    @Override
    public boolean isDown() {
        return component.getPollData() >= 0.5f;
    }
}
