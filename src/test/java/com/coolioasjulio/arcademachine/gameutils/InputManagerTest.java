package com.coolioasjulio.arcademachine.gameutils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class InputManagerTest {

    private static InputStream oldIn;
    private static DataOutputStream out;

    @BeforeAll
    static void setUp() {
        try {
            oldIn = System.in;
            PipedOutputStream pipedOut = new PipedOutputStream();
            PipedInputStream in = new PipedInputStream(pipedOut);
            System.setIn(in);
            out = new DataOutputStream(pipedOut);
            InputManager.enable();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @AfterAll
    static void tearDown() {
        System.setIn(oldIn);
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void keyPressedTest() {
        try {
            int[] arr = new int[]{3, 6, 9};
            DataOutputStream dataOutputStream = out;
            for (int i : arr) {
                dataOutputStream.writeInt(i);
                dataOutputStream.flush();
            }
            Thread.sleep(10);
            assertFalse(InputManager.keyPressed(3));
            InputManager.fetchInputs();
            assertTrue(InputManager.keyPressed(3));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void inputTest() {
        try {
            int[] arr = new int[]{3, 6, 9};
            DataOutputStream dataOutputStream = out;
            for (int i : arr) {
                dataOutputStream.writeInt(i);
                dataOutputStream.flush();
            }
            Thread.sleep(10);
            InputManager.fetchInputs();
            assertArrayEquals(arr, InputManager.getPressed());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}