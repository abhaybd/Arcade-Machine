package com.coolioasjulio.arcademachine.gameutils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Stack;

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
        return Arrays.stream(inputs).mapToInt(i -> i).toArray();
    }
}
