package com.coolioasjulio.arcademachine.tetris;

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

    /**
     * Get the locations of each block in this piece, in local space. This accounts for the rotation of the block, but not position.
     *
     * @return A {@link Vector} array, where each vector represents the local position of a block in this piece.
     */
    public Vector[] getLocalVectors() {
        return vectors;
    }

    /**
     * Get the locations of each block in this piece, in global space.
     * This accounts for the rotation and position of the piece.
     *
     * @return A {@link Vector} array, where each vector represents the global position of a block in this piece.
     */
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

    /**
     * Move this piece to the right by one block.
     */
    public void incrementX() {
        x++;
    }

    /**
     * Move this piece to the left by one block.
     */
    public void decrementX() {
        x--;
    }

    /**
     * Move this piece up by one block.
     */
    public void incrementY() {
        y++;
    }

    /**
     * Move this piece down by one block.
     */
    public void decrementY() {
        y--;
    }

    /**
     * Rotate this piece CW by 90 degrees about the center of rotation. (the origin in local space)
     */
    public void rotateCW() {
        // For each vector (each block) rotate CW by 90 degrees
        for (int i = 0; i < vectors.length; i++) {
            // After the rotation, round to the nearest int
            // Since we're rotating by 90 degrees, each result should be an integer, but due to floating point rounding, may not be
            // Rounding to an int guarantees that the vectors after rotation are all integers
            vectors[i] = new Vector(Arrays.stream(MathUtils.rotateCW(90, vectors[i]).getArray()).map(MathUtils::round).toArray());
        }
    }

    /**
     * Rotate this piece CCW by 90 degrees about the center of rotation. (the origin in local space)
     */
    public void rotateCCW() {
        // For each vector (each block) rotate CCW by 90 degrees
        for (int i = 0; i < vectors.length; i++) {
            // After the rotation, round to the nearest int
            // Since we're rotating by 90 degrees, each result should be an integer, but due to floating point rounding, may not be
            // Rounding to an int guarantees that the vectors after rotation are all integers
            vectors[i] = new Vector(Arrays.stream(MathUtils.rotateCCW(90, vectors[i]).getArray()).map(MathUtils::round).toArray());
        }
    }

    @Override
    public String toString() {
        return String.format("Piece(Type: %s, vectors=%s)", type.name(), Arrays.toString(vectors));
    }
}
