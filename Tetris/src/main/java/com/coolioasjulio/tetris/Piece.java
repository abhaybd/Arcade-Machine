package com.coolioasjulio.tetris;

import java.util.Arrays;

public final class Piece {

    public enum PieceType {
        I, O, J, L, T, S, Z
    }

    public static Piece createPiece(PieceType type) {
        switch (type) {
            case I:
                return createIPiece();

            case O:
                return createOPiece();

            case J:
                return createJPiece();

            case L:
                return createLPiece();

            case T:
                return createTPiece();

            case S:
                return createSPiece();

            case Z:
                return createZPiece();

            default:
                throw new IllegalArgumentException("Unrecognized PieceType!");
        }
    }

    public static Piece createIPiece() {
        return new Piece(PieceType.I, new Vector(0, -1), new Vector(0, 0), new Vector(0, 1), new Vector(0, 2));
    }

    public static Piece createOPiece() {
        return new Piece(PieceType.O, new Vector(0, 0), new Vector(0, 1), new Vector(1, 1), new Vector(1, 0));
    }

    public static Piece createJPiece() {
        return new Piece(PieceType.J, new Vector(-1, -1), new Vector(0, -1), new Vector(0, 0), new Vector(0, 1));
    }

    public static Piece createLPiece() {
        return new Piece(PieceType.L, new Vector(1, -1), new Vector(0, -1), new Vector(0, 0), new Vector(0, 1));
    }

    public static Piece createTPiece() {
        return new Piece(PieceType.T, new Vector(-1, 0), new Vector(0, 0), new Vector(1, 0), new Vector(0, 1));
    }

    public static Piece createSPiece() {
        return new Piece(PieceType.S, new Vector(-1, 0), new Vector(0, 0), new Vector(0, 1), new Vector(1, 1));
    }

    public static Piece createZPiece() {
        return new Piece(PieceType.Z, new Vector(-1, 1), new Vector(0, 1), new Vector(0, 0), new Vector(1, 0));
    }

    private Vector[] vectors;
    private PieceType type;
    private int x, y;

    private Piece(PieceType type, Vector... vectors) {
        this.type = type;
        this.vectors = vectors;
    }

    public PieceType getType() {
        return type;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector[] getLocalVectors() {
        return vectors;
    }

    public Vector[] getGlobalVectors() {
        Vector pos = new Vector(x, y);
        return Arrays.stream(vectors).map(v -> v.add(pos)).toArray(Vector[]::new);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void incrementX() {
        x++;
    }

    public void decrementX() {
        x--;
    }

    public void incrementY() {
        y++;
    }

    public void decrementY() {
        y--;
    }

    public void rotateCW() {
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = new Vector(Arrays.stream(MathUtils.rotateCW(90, vectors[i]).getArray()).map(MathUtils::round).toArray());
        }
    }

    public void rotateCCW() {
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = new Vector(Arrays.stream(MathUtils.rotateCCW(90, vectors[i]).getArray()).map(MathUtils::round).toArray());
        }
    }

    @Override
    public String toString() {
        return String.format("(Type: %s, vectors=%s)", type.name(), Arrays.toString(vectors));
    }
}
