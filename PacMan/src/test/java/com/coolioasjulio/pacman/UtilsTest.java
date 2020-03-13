package com.coolioasjulio.pacman;

import com.coolioasjulio.pacman.engine.GameObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
    @Test
    void snapGameObjTest() {
        GameObject obj = new GameObject(3, 1, null);
        int size = 10;
        Utils.snapGameObject(obj, size, size / 4);
        assertEquals(3, obj.getX());
        assertEquals(0, obj.getY());
    }

    @Test
    void moveGameObjTest() {
        GhostTest test = new GhostTest();
        test.setUp();
        test.updateNoCollision();
        test.tearDown();
        test.setUp();
        test.updateWithCollision();
        test.tearDown();
    }
}