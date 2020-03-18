package com.coolioasjulio.arcademachine.launcher.pacman.behaviors;

import com.coolioasjulio.arcademachine.launcher.pacman.Direction;
import com.coolioasjulio.arcademachine.launcher.pacman.Ghost;
import com.coolioasjulio.arcademachine.launcher.pacman.LevelMap;
import com.coolioasjulio.arcademachine.launcher.pacman.Utils;
import com.coolioasjulio.arcademachine.launcher.pacman.PacManGame;

import java.util.Arrays;

/**
 * This is the flee behavior. All ghosts switch to this behavior when the powerup is active.
 */
public class GhostFleeBehavior extends Ghost.GhostBehavior {
    public boolean started = false;
    public GhostFleeBehavior(Ghost ghost) {
        super(ghost);
    }

    @Override
    public Direction getDirection() {
        // When just started, make a U-turn
        if (!started) {
            started = true;
            return Utils.opposite(ghost.getDirection());
        }
        int dx = Utils.getDeltaX(ghost.getDirection());
        int dy = Utils.getDeltaY(ghost.getDirection());
        LevelMap levelMap = PacManGame.getInstance().getLevelMap();
        // If the front tile is open, keep going forward
        if (levelMap.isOpen(ghost.getTile().x + dx, ghost.getTile().y + dy)) {
            return null;
        } else {
            // Otherwise, turn either left or right
            boolean rightFirst = Math.random() > 0.5; // If true, check if right is open before left. Otherwise, check left first
            int adjust = 1;
            // If we're checking left first, reverse dx and dy so the rotation is correct
            if (!rightFirst) {
                dx = -dx;
                dy = -dy;
                adjust = -1;
            }
            // The current index of the direction
            int index = Arrays.asList(Direction.values()).indexOf(ghost.getDirection());
            int length = Direction.values().length;
            Direction ret;
            // [dx, dy] rotated 90 degrees CW is [-dy, dx] (since positive y is down)
            // Due to the order of the Direction enum, rotating left or right is achieved by adding or subtracting one from the index
            if (levelMap.isOpen(ghost.getTile().x - dy, ghost.getTile().y + dx)) {
                // This operation does "true modulo" (e.g. (-1) % 4 -> 3, not -1)
                ret = Direction.values()[(((index + adjust) % length) + length) % length];
            } else {
                // This operation does "true modulo" (e.g. (-1) % 4 -> 3, not -1)
                ret = Direction.values()[(((index - adjust) % length) + length) % length];
            }
            // Since there are no dead ends, ret is guaranteed to lead to an open tile
            return ret;
        }
    }
}
