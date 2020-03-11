package com.coolioasjulio.pacman.behaviors;

import com.coolioasjulio.pacman.Direction;
import com.coolioasjulio.pacman.Ghost;
import com.coolioasjulio.pacman.PacManGame;
import com.coolioasjulio.pacman.PathFinder;
import com.coolioasjulio.pacman.Utils;

public class GhostPinchBehavior extends Ghost.GhostBehavior {
    private Ghost other;

    public GhostPinchBehavior(Ghost ghost, Ghost other) {
        super(ghost);
        this.other = other;
    }

    @Override
    public Direction getDirection() {
        PacManGame game = PacManGame.getInstance();
        int pacManX = game.getPacManTileX();
        int pacManY = game.getPacManTileY();

        int size = game.getSize();
        int otherX = other.getTileX(size);
        int otherY = other.getTileY(size);

        double x = 2 * pacManX - otherX;
        double y = 2 * pacManY - otherY;
        double absSlope = Math.abs((pacManY - otherY) / (double) (pacManX - otherX));
        double dx = Math.signum(otherX - pacManX);
        double dy = Math.signum(otherY - pacManY);
        while (!game.getLevelMap().isOpen(Utils.round(x), Utils.round(y))) {
            if (absSlope >= 1) {
                x += dx / absSlope;
                y += dy;
            } else if (absSlope == 0) { // horizontal line
                x += dx;
            } else if (absSlope < 1) {
                x += dx;
                y += dy * absSlope;
            } else { // NaN (vertical line)
                y += dy;
            }
        }
        int tileX = ghost.getTileX(size);
        int tileY = ghost.getTileY(size);
        return PathFinder.pathFind(game.getLevelMap(),
                tileX, tileY,
                Utils.round(x), Utils.round(y), ghost.getPrevTile(), other.getTile()).direction;
    }
}
