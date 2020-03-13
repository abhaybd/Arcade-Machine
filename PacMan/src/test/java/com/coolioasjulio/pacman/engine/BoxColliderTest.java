package com.coolioasjulio.pacman.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoxColliderTest {

    @Test
    void topLeftContained() {
        BoxCollider a = new BoxCollider(0, 0, 10, 10);
        BoxCollider b = new BoxCollider(3, 3, 10, 10);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    void bottomLeftContained() {
        BoxCollider a = new BoxCollider(0, 0, 10, 10);
        BoxCollider b = new BoxCollider(1, -5, 10, 10);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    void noVerticesContained() {
        BoxCollider a = new BoxCollider(0, 0, 10, 5);
        BoxCollider b = new BoxCollider(1, -2, 5, 10);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    void allVerticesContained() {
        BoxCollider a = new BoxCollider(0, 0, 10, 10);
        BoxCollider b = new BoxCollider(3, 3, 5, 5);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    void flushEdges() {
        BoxCollider a = new BoxCollider(0, 0, 10, 10);
        BoxCollider b = new BoxCollider(0, 10, 10, 10);
        assertFalse(a.intersects(b));
        assertFalse(b.intersects(a));
    }
}