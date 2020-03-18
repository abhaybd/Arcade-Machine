package com.coolioasjulio.arcademachine.launcher.pacman.behaviors;

import com.coolioasjulio.arcademachine.launcher.pacman.Direction;
import com.coolioasjulio.arcademachine.launcher.pacman.Ghost;
import com.coolioasjulio.arcademachine.launcher.pacman.Utils;
import com.coolioasjulio.arcademachine.launcher.pacman.PacManGame;
import com.coolioasjulio.arcademachine.launcher.pacman.PathFinder;

import java.util.function.Supplier;

/**
 * This is the behavior of the 3rd ghost (index=2). This works together with the 1st ghost to pinch the player.
 * It draws a line from the other ghost to pac man, and then navigates to a point on that line past pac man.
 * Basically, it tries to put pac man between itself and the other ghost.
 */
public class GhostPinchBehavior extends Ghost.GhostBehavior {
    private Supplier<Ghost> otherSupplier;
    private GhostChaseBehavior chaseBehavior;

    /**
     * Create a GhostPinchBehavior.
     *
     * @param ghost         The ghost that has this behavior.
     * @param otherSupplier This supplier gives the ghost that it will work with. This is the first ghost. The reason it's
     *                      a supplier and not a ghost is because the other ghost may die and become replaced.
     */
    public GhostPinchBehavior(Ghost ghost, Supplier<Ghost> otherSupplier) {
        super(ghost);
        this.otherSupplier = otherSupplier;
        chaseBehavior = new GhostChaseBehavior(ghost);
    }

    @Override
    public Direction getDirection() {
        PacManGame game = PacManGame.getInstance();
        int pacManX = game.getPacManTileX();
        int pacManY = game.getPacManTileY();

        // Get the other ghost to use for calculations
        Ghost other = otherSupplier.get();
        // If that ghost is dead, default to the chase behavior
        if (other == null) {
            return chaseBehavior.getDirection();
        }

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
