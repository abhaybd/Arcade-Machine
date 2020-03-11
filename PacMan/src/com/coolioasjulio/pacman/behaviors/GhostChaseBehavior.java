package com.coolioasjulio.pacman.behaviors;

import com.coolioasjulio.pacman.Direction;
import com.coolioasjulio.pacman.Ghost;
import com.coolioasjulio.pacman.PacManGame;
import com.coolioasjulio.pacman.PathFinder;

public class GhostChaseBehavior extends Ghost.GhostBehavior {

    public GhostChaseBehavior(Ghost ghost) {
        super(ghost);
    }

    @Override
    public Direction getDirection() {
        PacManGame game = PacManGame.getInstance();
        int size = game.getSize();
        int x = ghost.getTileX(size);
        int y = ghost.getTileY(size);
        return PathFinder.pathFind(
                game.getLevelMap(),
                x,
                y,
                game.getPacManTileX(),
                game.getPacManTileY(), ghost.getPrevTile()).direction;
    }
}
