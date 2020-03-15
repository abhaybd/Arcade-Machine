package com.coolioasjulio.tetris;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.LinkedList;

public class TetrisBase {

    private static final int START_Y_OFFSET = 3;

    private Piece fallingPiece;
    private Map<Vector, Piece.PieceType> blockPile;
    private Piece.PieceType nextPieceType;
    private Random random;
    private int clearedLines;
    private int width, height;
    private int startX, startY;
    private boolean lost = false;

    public TetrisBase(int width, int height) {
        this.width = width;
        this.height = height;
        blockPile = new HashMap<>();
        random = new Random();

        nextPieceType = Piece.PieceType.values()[random.nextInt(Piece.PieceType.values().length)];

        startX = width / 2;
        startY = height - START_Y_OFFSET;
    }

    public synchronized void reset() {
        blockPile.clear();
        clearedLines = 0;
        fallingPiece = null;
        lost = false;
    }

    public boolean hasLost() {
        return lost;
    }

    public int getClearedLines() {
        return clearedLines;
    }

    public Piece.PieceType getNextPieceType() {
        return nextPieceType;
    }

    public synchronized boolean advanceStep() {
        if (lost) {
            return true;
        }
        if (fallingPiece == null) {
            if (canCreateNewFallingPiece()) {
                newFallingPiece();
            } else {
                System.out.println("You lose!");
                return lost = true;
            }
        } else {
            if (isFalling(fallingPiece)) {
                down();
            } else {
                addPieceToPile(fallingPiece);
                fallingPiece = null;
                clearLines();
            }
        }
        return false;
    }

    public synchronized Piece.PieceType[][] generateGrid() {
        Piece.PieceType[][] grid = new Piece.PieceType[height][width];
        for (Vector v : blockPile.keySet()) {
            grid[MathUtils.round(v.get(1))][MathUtils.round(v.get(0))] = blockPile.get(v);
        }

        if (fallingPiece != null) {
            for (Vector v : fallingPiece.getGlobalVectors()) {
                grid[MathUtils.round(v.get(1))][MathUtils.round(v.get(0))] = fallingPiece.getType();
            }
        }
        return grid;
    }

    private void clearLines() {
        for (int y = 0; y < height; y++) {
            List<Vector> vectors = new LinkedList<>();
            for (int x = 0; x < width; x++) {
                Vector v = new Vector(x, y);
                if (blockPile.containsKey(v)) {
                    vectors.add(v);
                } else {
                    break;
                }
            }
            if (vectors.size() == width) {
                vectors.forEach(blockPile::remove);
                clearedLines++;
                shiftDown(y--); // decrement y, so it will get incremented again to same value
            }
        }
    }

    private void shiftDown(int yStart) {
        Vector shift = new Vector(0, -1);
        for (int y = yStart + 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vector v = new Vector(x, y);
                Piece.PieceType type = blockPile.get(v);
                if (type != null) {
                    blockPile.remove(v);
                    blockPile.put(v.add(shift), type);
                }
            }
        }
    }

    private void addPieceToPile(Piece piece) {
        for (Vector v : piece.getGlobalVectors()) {
            blockPile.put(v, piece.getType());
        }
    }

    public synchronized void rotate() {
        if (fallingPiece == null) {
            return;
        }
        fallingPiece.rotateCW();
        if (!pieceFits(fallingPiece)) {
            fallingPiece.rotateCCW();
        }
    }

    public synchronized void left() {
        if (fallingPiece == null) {
            return;
        }
        fallingPiece.decrementX();
        if (!pieceFits(fallingPiece)) {
            fallingPiece.incrementX();
        }
    }

    public synchronized void right() {
        if (fallingPiece == null) {
            return;
        }
        fallingPiece.incrementX();
        if (!pieceFits(fallingPiece)) {
            fallingPiece.decrementX();
        }
    }

    public synchronized void down() {
        if (fallingPiece == null) {
            return;
        }
        fallingPiece.decrementY();
        if (!pieceFits(fallingPiece)) {
            fallingPiece.incrementY();
        }
    }

    private boolean pieceFits(Piece piece) {
        for (Vector v : piece.getGlobalVectors()) {
            if (!inRange(v.get(0), 0, width) || !inRange(v.get(1), 0, height)) {
                return false;
            }

            if (blockPile.containsKey(v)) {
                return false;
            }
        }
        return true;
    }

    private boolean inRange(double num, double low, double high) {
        return num >= low && num < high;
    }

    private boolean canCreateNewFallingPiece() {
        Piece p = Piece.createPiece(nextPieceType);
        p.setPos(startX, startY);
        return pieceFits(p);
    }

    private void newFallingPiece() {
        fallingPiece = Piece.createPiece(nextPieceType);
        System.out.println("new piece: " + fallingPiece.getType().name());
        fallingPiece.setPos(startX, startY);
        nextPieceType = Piece.PieceType.values()[random.nextInt(Piece.PieceType.values().length)];
    }

    private boolean isFalling(Piece piece) {
        piece.decrementY();
        boolean falling = pieceFits(piece);
        piece.incrementY();
        return falling;
    }
}
