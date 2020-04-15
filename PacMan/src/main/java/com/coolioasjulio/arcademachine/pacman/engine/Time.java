package com.coolioasjulio.arcademachine.pacman.engine;

public class Time {
    private static long startTime;
    private static long prevTime, currTime;

    public static void start() {
        startTime = prevTime = currTime = System.currentTimeMillis();
    }

    public static void update() {
        prevTime = currTime;
        currTime = System.currentTimeMillis();
    }

    public static double deltaTime() {
        return (currTime - prevTime) / 1000.0;
    }

    public static double elapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000.0;
    }

}
