package com.coolioasjulio.arcademachine.pacman.behaviors;

import com.coolioasjulio.arcademachine.pacman.Direction;
import com.coolioasjulio.arcademachine.pacman.Ghost;
import com.coolioasjulio.arcademachine.pacman.PacManGame;
import com.coolioasjulio.arcademachine.pacman.PathFinder;

/**
 * This is the simplest behavior. This is the first ghost (index 0). It just chases pac man and goes along the shortest route.
 */
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
        // Calculate a path to pac man and follow it. Exclude the ghost's previous tile to prevent U-turns
        return PathFinder.pathFind(
                game.getLevelMap(),
                x,
                y,
                game.getPacManTileX(),
                game.getPacManTileY(), ghost.getPrevTile()).direction;
    }
}
