package com.coolioasjulio.arcademachine.pacman.behaviors;

import com.coolioasjulio.arcademachine.pacman.Direction;
import com.coolioasjulio.arcademachine.pacman.Ghost;
import com.coolioasjulio.arcademachine.pacman.LevelMap;
import com.coolioasjulio.arcademachine.pacman.PacManGame;
import com.coolioasjulio.arcademachine.pacman.PathFinder;
import com.coolioasjulio.arcademachine.pacman.engine.Coord;

import java.util.Random;

/**
 * This is the behavior of the 4th ghost (index=3). It will chase pac man until it's within 8 tiles, and then it will wander
 * to a random tile on the map.
 */
public class GhostObliviousBehavior extends Ghost.GhostBehavior {
    private Coord randomCoord;

    public GhostObliviousBehavior(Ghost ghost) {
        super(ghost);
    }

    @Override
    public Direction getDirection() {
        PacManGame game = PacManGame.getInstance();
        LevelMap levelMap = game.getLevelMap();
        // If we're farther than 8 tiles from pac man, go towards pac man
        PathFinder.PathFindingResult toPacMan = PathFinder.pathFind(levelMap,
                ghost.getTile(), game.getPacMan().getTile(), ghost.getPrevTile(), game.getPacMan().getPrevTile());
        if (toPacMan.distance > 8) return toPacMan.direction;

        // Go towards the randomly selected point until the ghost is 1 tile away
        if (randomCoord != null) {
            PathFinder.PathFindingResult toRandom = PathFinder.pathFind(levelMap, ghost.getTile(), randomCoord, ghost.getPrevTile(),
                    game.getPacMan().getPrevTile());
            if (toRandom.distance > 1) {
                return toRandom.direction;
            }
        }

        // Select a new random point to go towards
        Coord c = new Coord(0, 0);
        Random r = new Random();
        // Select random points until an open tile is found
        do {
            c.x = r.nextInt(levelMap.getWidth());
            c.y = r.nextInt(levelMap.getHeight());
        } while (levelMap.isWall(c.x, c.y));
        randomCoord = c;
        // Navigate towards the randomly selected point
        return PathFinder.pathFind(levelMap, ghost.getTile(), randomCoord, ghost.getPrevTile(),
                game.getPacMan().getPrevTile()).direction;
    }
}
