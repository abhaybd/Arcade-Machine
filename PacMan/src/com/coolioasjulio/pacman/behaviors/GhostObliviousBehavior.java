package com.coolioasjulio.pacman.behaviors;

import com.coolioasjulio.pacman.Direction;
import com.coolioasjulio.pacman.Ghost;
import com.coolioasjulio.pacman.LevelMap;
import com.coolioasjulio.pacman.PacManGame;
import com.coolioasjulio.pacman.PathFinder;
import com.coolioasjulio.pacman.engine.Coord;

import java.util.Random;

public class GhostObliviousBehavior extends Ghost.GhostBehavior {
    private Coord randomCoord;

    public GhostObliviousBehavior(Ghost ghost) {
        super(ghost);
    }

    @Override
    public Direction getDirection() {
        PacManGame game = PacManGame.getInstance();
        LevelMap levelMap = game.getLevelMap();
        PathFinder.PathFindingResult res = PathFinder.pathFind(levelMap,
                ghost.getTile(), game.getPacMan().getTile(), ghost.getPrevTile(), game.getPacMan().getPrevTile());
        if (res.distance > 8) return res.direction;

        if (randomCoord != null) {
            PathFinder.PathFindingResult toRandom = PathFinder.pathFind(levelMap, randomCoord, game.getPacMan().getTile(), ghost.getPrevTile(),
                    game.getPacMan().getPrevTile());
            if (toRandom.distance > 1) {
                return toRandom.direction;
            }
        }

        Coord c = new Coord(0, 0);
        Random r = new Random();
        do {
            c.x = r.nextInt(levelMap.getWidth());
            c.y = r.nextInt(levelMap.getHeight());
        } while (levelMap.isWall(c.x, c.y));
        randomCoord = c;
        return PathFinder.pathFind(levelMap, c, game.getPacMan().getTile(), ghost.getPrevTile(),
                game.getPacMan().getPrevTile()).direction;
    }
}
