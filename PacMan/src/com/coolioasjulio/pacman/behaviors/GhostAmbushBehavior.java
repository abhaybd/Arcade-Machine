package com.coolioasjulio.pacman.behaviors;

import com.coolioasjulio.pacman.Direction;
import com.coolioasjulio.pacman.Ghost;
import com.coolioasjulio.pacman.PacManGame;
import com.coolioasjulio.pacman.PathFinder;
import com.coolioasjulio.pacman.Utils;

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
        PathFinder.PathFindingResult toPacMan =
                PathFinder.pathFind(game.getLevelMap(), tileX, tileY, x, y, ghost.getPrevTile(), game.getPacMan().getPrevTile());
        if (toPacMan.distance < 6) return toPacMan.direction;
        int dx = Utils.getDeltaX(game.getPacManDirection());
        int dy = Utils.getDeltaY(game.getPacManDirection());
        int i = 0;
        while (i++ < 5 && game.getLevelMap().isOpen(x + dx, y + dy)) {
            x += dx;
            y += dy;
        }
        PathFinder.PathFindingResult toAmbush =
                PathFinder.pathFind(game.getLevelMap(), tileX, tileY, x, y, ghost.getPrevTile(), game.getPacMan().getPrevTile());
        if (toAmbush.distance == 0) return Utils.opposite(game.getPacManDirection());
        return toAmbush.distance < toPacMan.distance ? toAmbush.direction : toPacMan.direction;
    }
}
