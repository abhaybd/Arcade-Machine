package com.coolioasjulio.arcademachine;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class Input {

    private static Input instance;

    public static Input getInstance() {
        if (instance == null) instance = new Input();
        return instance;
    }

    private Button buttonA, buttonB;
    private Joystick joystick;
    private Direction lastDirection;
    private Thread inputThread;
    private Consumer<Integer> onPressed;

    private Input() {
        if (ArcadeMachineGUI.MOCK_INPUT) {
            JFrame frame = new JFrame();
            frame.setSize(500, 500);
            frame.setAlwaysOnTop(true);
            frame.setVisible(true);
            frame.requestFocus();
            buttonA = new MockButton(KeyEvent.VK_A);
            buttonB = new MockButton(KeyEvent.VK_B);
            joystick = new MockJoystick();
        } else {
            // TODO: test this with the actual hardware
            final ControllerManager manager = new ControllerManager();
            manager.initSDLGamepad();
            System.out.printf("Detected %d controllers\n", manager.getNumControllers());
            buttonA = new HardwareButton(KeyEvent.VK_A, () -> manager.getState(0).a);
            buttonB = new HardwareButton(KeyEvent.VK_B, () -> manager.getState(0).b);
            joystick = new HardwareJoystick(() -> manager.getState(0));
        }
        onPressed = i -> {
        };
        inputThread = new Thread(this::inputTask);
        inputThread.setDaemon(true);
        inputThread.start();
    }

    public void addOnPressedCallback(Consumer<Integer> callback) {
        onPressed = onPressed.andThen(callback);
    }

    public void clearOnPressedCallbacks() {
        onPressed = i -> {
        };
    }

    private void inputTask() {
        while (!Thread.interrupted()) {
            if (buttonA.pressed()) {
                onPressed.accept(buttonA.getKeycode());
            }

            if (buttonB.pressed()) {
                onPressed.accept(buttonB.getKeycode());
            }

            Direction dir = joystick.getDirection();
            if (dir != lastDirection && dir != null) {
                onPressed.accept(dir.getKeycode());
            }
            lastDirection = dir;
            Thread.yield();
        }
    }

    public Button getButtonA() {
        return buttonA;
    }

    public Button getButtonB() {
        return buttonB;
    }

    public Joystick getJoystick() {
        return joystick;
    }
}
