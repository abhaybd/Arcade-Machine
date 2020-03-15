package com.coolioasjulio.arcademachine;

import com.studiohartman.jamepad.ControllerState;

import java.util.function.Supplier;

public class HardwareJoystick extends Joystick {
    private Supplier<Float> xSupplier;
    private Supplier<Float> ySupplier;
    private Supplier<ControllerState> stateSupplier;

    public HardwareJoystick(Supplier<ControllerState> stateSupplier)
    {
        this.stateSupplier = stateSupplier;
    }

    public HardwareJoystick(Supplier<Float> xSupplier, Supplier<Float> ySupplier) {
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
    }

    private double deadband(double d)
    {
        return Math.abs(d) >= 0.2 ? d : 0;
    }

    @Override
    public Direction getDirection() {
        double x, y;
        if (stateSupplier == null)
        {
            x = xSupplier.get();
            y = ySupplier.get();
        }
        else
        {
            ControllerState state = stateSupplier.get();
            x = state.dpadLeft ? -1 : state.dpadRight ? 1 : 0;
            y = state.dpadDown ? -1 : state.dpadUp ? 1 : 0;
            if (x == 0 && y == 0)
            {
                x = state.leftStickX;
                y = state.leftStickY;
            }
        }
        x = deadband(x);
        y = deadband(y);
        if (Math.hypot(x, y) < 0.5) return null;
        double theta = Math.toDegrees(Math.atan2(x, y));
        if (theta < 0) theta += 360;
        int index = (int) Math.round(theta / 90.0);
        if (index == 4) index = 0;
        return Direction.values()[index];
    }
}
