package com.coolioasjulio.arcademachine.snake;

import java.util.Arrays;
import java.util.List;

public class Snake {

    public enum Direction {NORTH, EAST, SOUTH, WEST}

    private Block head;
    private Block tail;
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
        head = new Block(headCoord);
        tail = new Block(tailCoord, head);
        head.setDownstreamBlock(tail);
    }

    public int getDirectionIndex() {
        return directionIndex;
    }

    public Direction getDirection() {
        return Direction.values()[directionIndex];
    }

    public Block getHead() {
        return head;
    }

    public Block[] getBlockArray() {
        int size = 1;
        Block b = head;
        while ((b = b.getDownstreamBlock()) != null) {
            size++;
        }
        Block[] arr = new Block[size];
        int i = 0;
        for (b = head; b != null; b = b.getDownstreamBlock()) {
            arr[i++] = b;
        }
        return arr;
    }

    public List<Block> getBlocks() {
        return Arrays.asList(getBlockArray());
    }

    public void grow() {
        Block secondToLast = tail.getUpstreamBlock();
        int dx = tail.getCoord().getX() - secondToLast.getCoord().getX();
        int dy = tail.getCoord().getY() - secondToLast.getCoord().getY();
        Block newTail = new Block(new Coord(tail.getCoord().getX() + dx, tail.getCoord().getY() + dy), tail);
        tail.setDownstreamBlock(newTail);
        tail = newTail;
    }

    private boolean validDirection(Direction direction) {
        Coord headCoord = head.getCoord();
        Coord coord;
        switch (direction) {
            default:
            case NORTH:
                coord = new Coord(headCoord.getX(), headCoord.getY() - 1);
                break;

            case EAST:
                coord = new Coord(headCoord.getX() + 1, headCoord.getY());
                break;

            case SOUTH:
                coord = new Coord(headCoord.getX(), headCoord.getY() + 1);
                break;

            case WEST:
                coord = new Coord(headCoord.getX() - 1, headCoord.getY());
                break;
        }
        return !coord.equals(head.getDownstreamBlock().getCoord());
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
        Coord oldHeadCoord = head.getCoord();
        switch (dir) {
            case NORTH:
                head.decY();
                break;

            case EAST:
                head.incX();
                break;

            case SOUTH:
                head.incY();
                break;

            case WEST:
                head.decX();
                break;
        }
        Block downstream = head.getDownstreamBlock();
        if (downstream != tail) {
            Block newTail = tail.getUpstreamBlock();
            newTail.setDownstreamBlock(null);
            tail.setUpstreamBlock(head);
            downstream.setUpstreamBlock(tail);
            head.setDownstreamBlock(tail);
            tail.setDownstreamBlock(downstream);
            tail.setCoord(oldHeadCoord);
            tail = newTail;
        } else {
            tail.setCoord(oldHeadCoord);
        }
    }
}
