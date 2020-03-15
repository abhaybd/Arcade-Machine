package com.coolioasjulio.tetris;

public class MathUtils {
    public static Vector rotateCCW(double degrees, Vector vec) {
        return createCCWRotationMatrix(degrees).apply(vec);
    }

    public static Vector rotateCW(double degrees, Vector vec) {
        return rotateCCW(-degrees, vec);
    }

    public static Matrix createCCWRotationMatrix(double degrees) {
        double theta = Math.toRadians(degrees);
        return new Matrix(
                new double[][]{
                        {Math.cos(theta), -Math.sin(theta)},
                        {Math.sin(theta), Math.cos(theta)}});
    }

    public static int round(double d) {
        return (int) Math.floor(d + 0.5);
    }
}
