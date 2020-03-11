package com.coolioasjulio.pacman.behaviors;

import com.coolioasjulio.pacman.Direction;
import com.coolioasjulio.pacman.Ghost;
import com.coolioasjulio.pacman.LevelMap;
import com.coolioasjulio.pacman.PacManGame;
import com.coolioasjulio.pacman.Utils;

import java.util.Arrays;

public class GhostFleeBehavior extends Ghost.GhostBehavior {
    public boolean started = false;
    public GhostFleeBehavior(Ghost ghost) {
        super(ghost);
    }

    @Override
    public Direction getDirection() {
        if (!started) {
            started = true;
            return Utils.opposite(ghost.getDirection());
        }
        int dx = Utils.getDeltaX(ghost.getDirection());
        int dy = Utils.getDeltaY(ghost.getDirection());
        LevelMap levelMap = PacManGame.getInstance().getLevelMap();
        if (levelMap.isOpen(ghost.getTile().x + dx, ghost.getTile().y + dy)) {
            return null;
        } else {
            boolean rightFirst = Math.random() > 0.5;
            int adjust = 1;
            if (!rightFirst) {
                dx = -dx;
                dy = -dy;
                adjust = -1;
            }
            int index = Arrays.asList(Direction.values()).indexOf(ghost.getDirection());
            int length = Direction.values().length;
            Direction ret;
            if (levelMap.isOpen(ghost.getTile().x - dy, ghost.getTile().y + dx)) {
                ret = Direction.values()[(((index + adjust) % length) + length) % length];
            } else {
                ret = Direction.values()[(((index - adjust) % length) + length) % length];
            }
            return ret;
        }
    }
}
