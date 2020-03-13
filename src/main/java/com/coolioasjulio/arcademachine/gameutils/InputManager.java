package com.coolioasjulio.arcademachine.gameutils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Stack;

/**
 * This class implements a common input platform for use in all games. The reason this is required is because all other games
 * are packaged as individual jar files. This means that if they were to directly read input from the user, they would have to read from the physical input devices.
 * This means that they are bound to the hardware of the machine. Instead, they receive their inputs from their process input stream using
 * this InputManager. The main arcade machine program will monitor the hardware inputs and send the inputs to the game process using the process input stream.
 * This means that the games just have to accept their inputs from this platform, and all the hardware code is localized to the main Arcade Machine code.
 */
public class InputManager {
    private static DataInputStream in;
    private static Stack<Integer> currTickInputs;
    private static Stack<Integer> prevTickInputs;
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
            currTickInputs = new Stack<>();
            prevTickInputs = new Stack<>();
            inputThread = new Thread(InputManager::inputTask);
            inputThread.start();
        }
    }

    private static void inputTask() {
        while (!Thread.interrupted()) {
            int i;
            try {
                i = in.readInt();
            } catch (IOException e) {
                break;
            }
            synchronized (currTickLock) {
                currTickInputs.push(i);
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
            return prevTickInputs.contains(key);
        }
    }

    /**
     * Get the latest inputs from the input stream since the last getInputs() call.
     *
     * @return int array containing all latest inputs, in reverse chronological order.
     */
    public static int[] getInputs() {
        Integer[] inputs;
        synchronized (currTickLock) {
            inputs = currTickInputs.toArray(new Integer[0]);
            currTickInputs.clear();
        }
        synchronized (prevTickLock) {
            prevTickInputs.clear();
            prevTickInputs.addAll(Arrays.asList(inputs));
        }
        return Arrays.stream(inputs).mapToInt(Integer::intValue).toArray();
    }
}
