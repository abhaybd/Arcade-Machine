package com.coolioasjulio.tetris;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.Objects;

public class Matrix {

    public static Matrix identity(int size) {
        Matrix m = new Matrix(size, size);
        for (int i = 0; i < size; i++) {
            m.matrix[i][i] = 1.0;
        }
        return m;
    }

    private double[][] matrix;
    private int rows, cols;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        matrix = new double[rows][cols];
    }

    public Matrix(int rows, int cols, double[] data) {
        if (rows * cols != data.length) {
            throw new IllegalArgumentException("Length of data must be rows * cols!");
        }
        this.rows = rows;
        this.cols = cols;
        matrix = new double[rows][cols];
        for (int i = 0; i < data.length; i++) {
            int row = i / cols;
            int col = i % cols;
            matrix[row][col] = data[i];
        }
    }

    public Matrix(double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        for (int row = 0; row < rows; row++) {
            if (data[row].length != cols) {
                throw new IllegalArgumentException("data cannot be jagged!");
            }
        }
        this.matrix = data;
    }

    public double[] flatten() {
        return Arrays.stream(matrix).flatMapToDouble(DoubleStream::of).toArray();
    }

    public void set(int row, int col, double value) {
        matrix[row][col] = value;
    }

    public double get(int row, int col) {
        return matrix[row][col];
    }

    public Matrix scalarMultiply(double scalar) {
        return new Matrix(rows, cols, Arrays.stream(flatten()).map(d -> d * scalar).toArray());
    }

    public Vector getRow(int row) {
        return new Vector(matrix[row]);
    }

    public Vector getCol(int col) {
        Vector v = new Vector(rows);
        for (int row = 0; row < rows; row++) {
            v.set(row, matrix[row][col]);
        }
        return v;
    }

    public Matrix mul(Matrix b) {
        if (cols != b.rows) {
            throw new IllegalArgumentException("Invalid matrix dimensions!");
        }

        Matrix result = new Matrix(rows, b.cols);
        for (int row = 0; row < rows; row++) {
            Vector v1 = getRow(row);
            for (int col = 0; col < b.cols; col++) {
                Vector v2 = b.getCol(col);
                result.set(row, col, v1.dot(v2));
            }
        }

        return result;
    }

    public Vector apply(Vector v) {
        Matrix m = new Matrix(v.getDimension(), 1, v.getArray());
        return new Vector(mul(m).flatten());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int row = 0; row < rows; row++) {
            sb.append(Arrays.toString(matrix[row]));
            if (row != rows - 1) {
                sb.append(";\n");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(Arrays.stream(matrix).mapToInt(Arrays::hashCode).toArray());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Matrix)) {
            return false;
        }
        Matrix m = (Matrix) o;
        if (m.rows != rows) {
            return false;
        }
        for (int row = 0; row < rows; row++) {
            if (!getRow(row).equals(m.getRow(row))) {
                return false;
            }
        }
        return true;
    }
}
