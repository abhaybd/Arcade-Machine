package com.coolioasjulio.arcademachine.tetris;

public class MathUtils {
    /**
     * Rotate a vector CCW.
     *
     * @param degrees The number of degrees CCW to rotate
     * @param vec     The vector to rotate
     * @return The resultant vector after the rotation transformation
     */
    public static Vector rotateCCW(double degrees, Vector vec) {
        return createCCWRotationMatrix(degrees).apply(vec);
    }

    /**
     * Rotate a vector CW.
     *
     * @param degrees The number of degrees CW to rotate
     * @param vec     The vector to rotate
     * @return The resultant vector after the rotation transformation
     */
    public static Vector rotateCW(double degrees, Vector vec) {
        return rotateCCW(-degrees, vec);
    }

    /**
     * Create a rotation matrix that will rotate a 2D vector CCW by the supplied amount of degrees.
     *
     * @param degrees The number of degrees to rotate CCW
     * @return A 2x2 matrix representing the rotation transformation
     */
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
