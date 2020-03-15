package com.coolioasjulio.tetris;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VectorTest {

    public VectorTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void vectorAddTest() {
        Vector a = new Vector(1, 2);
        Vector b = new Vector(4, 2);
        assertEquals(a.add(b), new Vector(5, 4));
    }

    @Test
    public void vectorAddTestDimMismatch() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(2).add(new Vector(4)));
    }

    @Test
    public void vectorDotTest() {
        assertEquals(new Vector(1, 2).dot(new Vector(-3, 0)), -3, 1e-9);
    }

    @Test
    public void vectorDotTestDimMismatch() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(1).dot(new Vector(2)));
    }

    @Test
    public void vectorDimTest() {
        int dim = 4;
        assertEquals(new Vector(dim).getDimension(), dim);
    }
}

