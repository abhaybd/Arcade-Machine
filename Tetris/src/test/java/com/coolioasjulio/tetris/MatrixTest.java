package com.coolioasjulio.tetris;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixTest {

    public MatrixTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void matrixMulTest() {
        Matrix a = Matrix.identity(4);
        Matrix b = new Matrix(new double[][]{{1, 2, 3, 4, 5}, {1, 2, 3, 4, 5}, {1, 2, 3, 4, 5}, {1, 2, 3, 4, 5}});
        assertEquals(a.mul(b), b);
    }

    @Test
    public void matrixApplyVecTest() {
        Matrix m = Matrix.identity(4).scalarMultiply(2.0);
        assertEquals(m.apply(new Vector(1, 2, 3, 4)), new Vector(2, 4, 6, 8));
    }
}
