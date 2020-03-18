package com.coolioasjulio.arcademachine.launcher;

import com.studiohartman.jamepad.ControllerManager;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.function.BiConsumer;

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
    private BiConsumer<Boolean, Integer> onEvent;

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
            final ControllerManager manager = new ControllerManager();
            manager.initSDLGamepad();
            System.out.printf("Detected %d controllers\n", manager.getNumControllers());
            buttonA = new HardwareButton(KeyEvent.VK_A, () -> manager.getState(0).a);
            buttonB = new HardwareButton(KeyEvent.VK_B, () -> manager.getState(0).b);
            joystick = new HardwareJoystick(() -> manager.getState(0));
        }
        onEvent = (p, i) -> {
        };
        inputThread = new Thread(this::inputTask);
        inputThread.setDaemon(true);
        inputThread.start();
    }

    public void addEventCallback(BiConsumer<Boolean, Integer> callback) {
        onEvent = onEvent.andThen(callback);
    }

    public void clearEventCallbacks() {
        onEvent = (p, i) -> {
        };
    }

    private void inputTask() {
        while (!Thread.interrupted()) {
            Button.Event aEvent = buttonA.getEvent();
            Button.Event bEvent = buttonB.getEvent();

            if (aEvent != Button.Event.NONE)
            {
                onEvent.accept(aEvent == Button.Event.PRESSED, buttonA.getKeycode());
            }

            if (bEvent != Button.Event.NONE)
            {
                onEvent.accept(bEvent == Button.Event.PRESSED, buttonB.getKeycode());
            }

            Direction dir = joystick.getDirection();
            if (dir != lastDirection) {
                if (lastDirection != null)
                {
                    onEvent.accept(false, lastDirection.getKeycode());
                }
                if (dir != null)
                {
                    onEvent.accept(true, dir.getKeycode());
                }
                lastDirection = dir;
            }
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
