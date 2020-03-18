package com.coolioasjulio.arcademachine.launcher.gameutils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements a common input platform for use in all games. The reason this is required is because all other games
 * are packaged as individual jar files. This means that if they were to directly read input from the user, they would have to read from the physical input devices.
 * This means that they are bound to the hardware of the machine. Instead, they receive their inputs from their process input stream using
 * this InputManager. The main arcade machine program will monitor the hardware inputs and send the inputs to the game process using the process input stream.
 * This means that the games just have to accept their inputs from this platform, and all the hardware code is localized to the main Arcade Machine code.
 */
public class InputManager {
    private static DataInputStream in;
    private static Set<Integer> currentlyDown;
    private static Set<Integer> currTickPressed, currTickReleased;
    private static Set<Integer> prevTickPressed, prevTickReleased;
    private static final Object currTickLock = new Object();
    private static final Object prevTickLock = new Object();
    private static Thread inputThread;

    /**
     * Enable the input manager, using System.in as the input stream.
     */
    public static void enable() {
        enable(System.in);
    }

    /**
     * Enable the input manager, using the supplied input stream.
     *
     * @param inputStream The input stream to use to get input.
     */
    public static void enable(InputStream inputStream) {
        if (inputThread == null || !inputThread.isAlive()) {
            in = new DataInputStream(inputStream);
            currentlyDown = Collections.synchronizedSet(new HashSet<>());
            currTickPressed = new HashSet<>();
            currTickReleased = new HashSet<>();
            prevTickReleased = new HashSet<>();
            prevTickPressed = new HashSet<>();
            inputThread = new Thread(InputManager::inputTask);
            inputThread.setDaemon(true);
            inputThread.start();
        }
    }

    private static void inputTask() {
        while (!Thread.interrupted()) {
            int i;
            boolean pressed;
            try {
                pressed = in.readBoolean();
                i = in.readInt();
            } catch (IOException e) {
                break;
            }
            synchronized (currTickLock) {
                if (pressed) {
                    currTickPressed.add(i);
                    currTickReleased.remove(i);
                    currentlyDown.add(i);
                } else {
                    currTickPressed.remove(i);
                    currTickReleased.add(i);
                    currentlyDown.remove(i);
                }
            }
        }
    }

    /**
     * Find if this key was pressed.
     * More specifically, find if this key was in the int array returned by the last getInputs() call.
     *
     * @param key The keycode to check for input.
     * @return True if this key was pressed, false otherwise.
     */
    public static boolean keyPressed(int key) {
        synchronized (prevTickLock) {
            return prevTickPressed.contains(key);
        }
    }

    public static boolean keyReleased(int key) {
        synchronized (prevTickLock) {
            return prevTickReleased.contains(key);
        }
    }

    public static boolean keyDown(int key) {
        return currentlyDown.contains(key);
    }

    public static void fetchInputs() {
        Integer[] pressed, released;
        synchronized (currTickLock) {
            pressed = currTickPressed.toArray(new Integer[0]);
            released = currTickReleased.toArray(new Integer[0]);
            currTickPressed.clear();
            currTickReleased.clear();
        }
        synchronized (prevTickLock) {
            prevTickPressed.clear();
            prevTickReleased.clear();
            prevTickPressed.addAll(Arrays.asList(pressed));
            prevTickReleased.addAll(Arrays.asList(released));
        }
    }

    public static int[] getPressed()
    {
        Integer[] pressed;
        synchronized (prevTickLock) {
            pressed = prevTickPressed.toArray(new Integer[0]);
        }
        return unboxArray(pressed);
    }

    public static int[] getReleased()
    {
        Integer[] released;
        synchronized (prevTickLock) {
            released = prevTickReleased.toArray(new Integer[0]);
        }
        return unboxArray(released);
    }

    public static int[] getDown()
    {
        return unboxArray(currentlyDown.toArray(new Integer[0]));
    }

    private static int[] unboxArray(Integer[] arr)
    {
        return Arrays.stream(arr).mapToInt(Integer::intValue).toArray();
    }
}
