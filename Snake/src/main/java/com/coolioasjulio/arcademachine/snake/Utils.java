package com.coolioasjulio.arcademachine.snake;

import java.util.function.Function;
import java.util.Random;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Utils {
    public static double mod(double a, double b) {
        return ((a % b) + b) % b;
    }
    
    public static int mod(int a, int b) {
        return ((a % b) + b) % b;
    }
    
    public static int round(double d) {
        return (int) Math.floor(d + 0.5);
    }
    
    public static boolean inRange(int num, int low, int high) {
        return num >= low && num < high;
    }
    
    public static <T> T[] uniqueRandoms(Function<Random,T> randomSupplier, Class<T> clazz, int numSamples) {
        T[] samples = (T[]) Array.newInstance(clazz, numSamples);
        Random r = new Random();
        for(int i = 0; i < numSamples; i++) {
            final T sample = randomSupplier.apply(r);
            if (Arrays.stream(samples).anyMatch(e -> sample.equals(e))) {
                i--; // i will increment back to current value after current loop iteration
            } else {
                samples[i] = sample;
            }
        }
        return samples;
    }
}
