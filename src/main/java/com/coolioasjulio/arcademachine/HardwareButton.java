package com.coolioasjulio.arcademachine;

import java.util.function.Supplier;

public class HardwareButton extends Button {


    private final Supplier<Boolean> buttonPressedSupplier;

    public HardwareButton(int keycode, Supplier<Boolean> buttonPressedSupplier) {
        super(keycode);
        this.buttonPressedSupplier = buttonPressedSupplier;
    }

    @Override
    public boolean isDown() {
        return buttonPressedSupplier.get();
    }
}
