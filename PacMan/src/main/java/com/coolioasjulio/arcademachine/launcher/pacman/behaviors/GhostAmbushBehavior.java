package com.coolioasjulio.arcademachine.launcher.pacman.behaviors;

import com.coolioasjulio.arcademachine.launcher.pacman.Direction;
import com.coolioasjulio.arcademachine.launcher.pacman.Ghost;
import com.coolioasjulio.arcademachine.launcher.pacman.Utils;
import com.coolioasjulio.arcademachine.launcher.pacman.PacManGame;
import com.coolioasjulio.arcademachine.launcher.pacman.PathFinder;

/**
 * This defines the ambush behavior. This is the 2nd ghost (index 1). This ghost will try to get in front of the player
 * and approach them from the front.
 */
public class GhostAmbushBehavior extends Ghost.GhostBehavior {
    public GhostAmbushBehavior(Ghost ghost) {
        super(ghost);
    }

    @Override
    public Direction getDirection() {
        PacManGame game = PacManGame.getInstance();
        int size = game.getSize();
        int x = game.getPacManTileX();
        int y = game.getPacManTileY();
        int tileX = ghost.getTileX(size);
        int tileY = ghost.getTileY(size);
        // Get the path to pac man, excluding the tile behind pac man
        // This forces the path to be in front of pac man
        PathFinder.PathFindingResult toPacMan =
                PathFinder.pathFind(game.getLevelMap(), tileX, tileY, x, y, ghost.getPrevTile(), game.getPacMan().getPrevTile());
        // If we're close to pac man, just go straight towards him
        if (toPacMan.distance < 6) return toPacMan.direction;
        // Otherwise, navigate to the tile at most 5 tiles in front of pac man
        int dx = Utils.getDeltaX(game.getPacManDirection());
        int dy = Utils.getDeltaY(game.getPacManDirection());
        int i = 0;
        // Keep going forward from pac man until 5 tiles or hit a wall
        while (i++ < 5 && game.getLevelMap().isOpen(x + dx, y + dy)) {
            x += dx;
            y += dy;
        }
        // Calculate a path to this ambush location
        PathFinder.PathFindingResult toAmbush =
                PathFinder.pathFind(game.getLevelMap(), tileX, tileY, x, y, ghost.getPrevTile(), game.getPacMan().getPrevTile());
        // If we're already there, go in the opposite direction pac man is facing, which in this case will be right towards him
        if (toAmbush.distance == 0) return Utils.opposite(game.getPacManDirection());
        // If we're closer to the ambush location, go there. Otherwise, go to pac man.
        return toAmbush.distance < toPacMan.distance ? toAmbush.direction : toPacMan.direction;
    }
}
