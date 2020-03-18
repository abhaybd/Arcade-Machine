package com.coolioasjulio.arcademachine.launcher.pacman;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GhostTest {

    private Ghost ghost;

    @BeforeEach
    void setUp() {
        ghost = new Ghost(0, 0, 10, null, null);
        ghost.setSpeed(5);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void updateNoCollision() {
        LevelMap level = new LevelMap(new boolean[][]{{false, true}}, 0, 0);
        ghost.setLocalPosition(-11, 0);
        ghost.update(level, 1);
        assertEquals(-6, ghost.getX());
        assertEquals(0, ghost.getY());
    }

    @Test
    void updateWithCollision() {
        LevelMap level = new LevelMap(new boolean[][]{{false, true}}, 0, 0);
        ghost.setLocalPosition(-1, 0);
        ghost.update(level, 1);
        assertEquals(0, ghost.getX());
        assertEquals(0, ghost.getY());
    }
}