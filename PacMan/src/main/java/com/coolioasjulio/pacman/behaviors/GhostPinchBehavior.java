package com.coolioasjulio.pacman.behaviors;

import com.coolioasjulio.pacman.Direction;
import com.coolioasjulio.pacman.Ghost;
import com.coolioasjulio.pacman.PacManGame;
import com.coolioasjulio.pacman.PathFinder;
import com.coolioasjulio.pacman.Utils;

/**
 * This is the behavior of the 3rd ghost (index=2). This works together with the 1st ghost to pinch the player.
 * It draws a line from the other ghost to pac man, and then navigates to a point on that line past pac man.
 * Basically, it tries to put pac man between itself and the other ghost.
 */
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

        // Calculate a line between the other ghost and pac man
        double x = 2 * pacManX - otherX;
        double y = 2 * pacManY - otherY;
        double absSlope = Math.abs((pacManY - otherY) / (double) (pacManX - otherX));
        double dx = Math.signum(otherX - pacManX);
        double dy = Math.signum(otherY - pacManY);
        // Go along the line until the first open tile is found
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
        // Navigate to that open tile
        return PathFinder.pathFind(game.getLevelMap(),
                tileX, tileY,
                Utils.round(x), Utils.round(y), ghost.getPrevTile(), other.getTile()).direction;
    }
}
