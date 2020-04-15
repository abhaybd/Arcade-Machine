package com.coolioasjulio.arcademachine.snake;

public class Utils {
    public static int mod(int a, int b) {
        return ((a % b) + b) % b;
    }

    public static boolean inRange(int num, int low, int high) {
        return num >= low && num < high;
    }
}
