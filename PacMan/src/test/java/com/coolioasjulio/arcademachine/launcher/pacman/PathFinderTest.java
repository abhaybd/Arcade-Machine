package com.coolioasjulio.arcademachine.launcher.pacman;

import com.coolioasjulio.arcademachine.pacman.Direction;
import com.coolioasjulio.arcademachine.pacman.LevelMap;
import com.coolioasjulio.arcademachine.pacman.PathFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathFinderTest {
    private LevelMap levelMap;
    private boolean[][] walls;

    @BeforeEach
    void setUp() {
        walls = new boolean[10][10];
        levelMap = new LevelMap(walls, 0, 0);
    }

    @Test
    void noWallTest() {
        assertEquals(new PathFinder.PathFindingResult(5, Direction.EAST), PathFinder.pathFind(levelMap, 1, 1, 5, 1));
        assertEquals(new PathFinder.PathFindingResult(5, Direction.WEST), PathFinder.pathFind(levelMap, 5, 1, 1, 1));
        assertEquals(new PathFinder.PathFindingResult(5, Direction.NORTH), PathFinder.pathFind(levelMap, 1, 5, 1, 1));
        assertEquals(new PathFinder.PathFindingResult(5, Direction.SOUTH), PathFinder.pathFind(levelMap, 1, 1, 1, 5));
    }

    @Test
    void wallTest() {
        boolean[] row = new boolean[levelMap.getWidth()];
        for (int i = 1; i < row.length; i++) {
            row[i] = true;
        }
        walls[4] = row;
        assertNotEquals(Direction.EAST, PathFinder.pathFind(levelMap, 5, 1, 5, 5).direction);
        assertNotEquals(Direction.NORTH, PathFinder.pathFind(levelMap, 5, 1, 5, 5).direction);
    }
}