package com.coolioasjulio.arcademachine.snake;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Snake {

    public enum Direction {NORTH, EAST, SOUTH, WEST}

    private List<Coord> body;
    private int directionIndex; // TODO: change this to use a list or whatever

    public Snake(Coord headCoord, Coord tailCoord) {
        int dx = headCoord.getX() - tailCoord.getX();
        int dy = headCoord.getY() - tailCoord.getY();
        if (dx * dy != 0 || Math.abs(dx + dy) != 1) {
            throw new IllegalArgumentException("Invalid head and tail coords!");
        }

        if (dx == 0) {
            directionIndex = dy > 0 ? 2 : 0;
        } else {
            directionIndex = dx > 0 ? 1 : 3;
        }
        body = new LinkedList<>();
        body.add(headCoord);
        body.add(tailCoord);
    }

    public int getDirectionIndex() {
        return directionIndex;
    }

    public Direction getDirection() {
        return Direction.values()[directionIndex];
    }

    public Coord getHead() {
        return body.get(0);
    }

    public Coord getTail() {
        return body.get(body.size() - 1);
    }

    public List<Coord> getBody() {
        return body;
    }

    public void grow() {
        Coord secondToLast = body.get(body.size() - 2);
        Coord tail = getTail();
        int dx = tail.getX() - secondToLast.getX();
        int dy = tail.getY() - secondToLast.getY();
        Coord newTail = new Coord(tail.getX() + dx, tail.getY() + dy);
        body.add(newTail);
    }

    private boolean validDirection(Direction direction) {
        Coord coord;
        Coord head = getHead();
        switch (direction) {
            default:
            case NORTH:
                coord = new Coord(head.getX(), head.getY() - 1);
                break;

            case EAST:
                coord = new Coord(head.getX() + 1, head.getY());
                break;

            case SOUTH:
                coord = new Coord(head.getX(), head.getY() + 1);
                break;

            case WEST:
                coord = new Coord(head.getX() - 1, head.getY());
                break;
        }
        return !coord.equals(body.get(1));
    }

    private boolean validDirection(int newDirIndex) {
        return validDirection(Direction.values()[newDirIndex]);
    }

    public void turnTo(Direction direction) {
        if (validDirection(direction)) {
            directionIndex = Arrays.asList(Direction.values()).indexOf(direction);
        }
    }

    public void turnLeft() {
        int newDirIndex = Utils.mod(directionIndex - 1, Direction.values().length);
        if (validDirection(newDirIndex)) {
            directionIndex = newDirIndex;
        }
    }

    public void turnRight() {
        int newDirIndex = Utils.mod(directionIndex + 1, Direction.values().length);
        if (validDirection(newDirIndex)) {
            directionIndex = newDirIndex;
        }
    }

    public void forward() {
        Direction dir = Direction.values()[directionIndex];
        Coord newHead = getHead();
        switch (dir) {
            case NORTH:
                newHead = newHead.decY();
                break;

            case EAST:
                newHead = newHead.incX();
                break;

            case SOUTH:
                newHead = newHead.incY();
                break;

            case WEST:
                newHead = newHead.decX();
                break;
        }
        body.add(0, newHead);
        body.remove(body.size() - 1);
    }
}
