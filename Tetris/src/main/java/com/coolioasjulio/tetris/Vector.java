package com.coolioasjulio.tetris;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Vector {
    private int length;
    private double[] vector;

    public Vector(int length) {
        this.length = length;
        vector = new double[length];
    }

    public Vector(double... data) {
        length = data.length;
        vector = data;
    }

    public int getDimension() {
        return length;
    }

    public double get(int pos) {
        return vector[pos];
    }

    public void set(int pos, double d) {
        vector[pos] = d;
    }

    public double[] getArray() {
        return vector;
    }

    /**
     * Performs a dot product operation with the supplied vector/\.
     *
     * @param v The vector to do the dot product with.
     * @return The dot product of this vector with the supplied vector.
     * @throws IllegalArgumentException If the dimensions of the vectors mismatch.
     */
    public double dot(Vector v) {
        if (v.length != length) {
            throw new IllegalArgumentException("Cannot do dot product with vectors of different length!");
        }
        return IntStream.range(0, length).mapToDouble(i -> get(i) * v.get(i)).sum();
    }

    /**
     * Adds this vector to the supplied vector.
     *
     * @param v The vector to add.
     * @return The resultant vector after addition.
     * @throws IllegalArgumentException If the dimensions of the vectors mismatch.
     */
    public Vector add(Vector v) {
        if (v.length != length) {
            throw new IllegalArgumentException("Cannot add vectors of different length!");
        }
        return new Vector(IntStream.range(0, length).mapToDouble(i -> get(i) + v.get(i)).toArray());
    }

    @Override
    public String toString() {
        return Arrays.toString(vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vector)) {
            return false;
        }
        Vector v = (Vector) o;
        return Arrays.equals(vector, v.vector);
    }
}
