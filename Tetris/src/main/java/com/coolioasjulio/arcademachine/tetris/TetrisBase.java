package com.coolioasjulio.arcademachine.tetris;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.LinkedList;

public class TetrisBase {

    private static final int START_Y_OFFSET = 3;

    private Piece fallingPiece;
    /**
     * This represents every block on the block pile. (all blocks that aren't falling)
     * We need to associate every block with a PieceType in order to know what color to draw it.
     */
    private final Map<Vector, Piece.PieceType> blockPile;
    private final Random random;
    private final int width, height;
    private final int startX, startY;
    private Piece.PieceType nextPieceType;
    private int clearedLines;
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

    /**
     * Reset the game to the starting state.
     */
    public synchronized void reset() {
        blockPile.clear();
        clearedLines = 0;
        fallingPiece = null;
        lost = false;
    }

    /**
     * Check if the player has lost.
     *
     * @return True if the player has lost, false otherwise.
     */
    public boolean hasLost() {
        return lost;
    }

    /**
     * Get the number of lines the player has cleared this game.
     *
     * @return The number of lines the player has cleared this game.
     */
    public int getClearedLines() {
        return clearedLines;
    }

    /**
     * Get the type of piece that will be spawned next.
     *
     * @return The type of piece that is queued up to be spawned next.
     */
    public Piece.PieceType getNextPieceType() {
        return nextPieceType;
    }

    /**
     * Advance the game forward by one time step.
     *
     * @return True if the player has lost, false otherwise.
     */
    public synchronized boolean advanceStep() {
        if (lost) {
            return true;
        }
        if (fallingPiece == null) {
            // If there's no falling piece, spawn a new one if possible
            if (canCreateNewFallingPiece()) {
                newFallingPiece();
            } else {
                // If a new piece cannot be spawned, the player has lost
                System.out.println("You lose!");
                return lost = true; // set lost to true and return true
            }
        } else {
            // If the piece is still falling, move it down
            if (isFalling(fallingPiece)) {
                down();
            } else {
                // Otherwise, assimilate it into the block pile
                addPieceToPile(fallingPiece);
                fallingPiece = null;
                clearLines(); // clear filled lines if necessary
            }
        }
        return false;
    }

    /**
     * Generate a 2D array of {@link Piece.PieceType}.
     * The data is normally stored sparsely, so this generates an explicit grid that shows each block on the screen.
     * This can be used to render the game, since this is basically a freeze of the game at a given time.
     *
     * @return A 2D array representing the state of the game at this time.
     */
    public synchronized Piece.PieceType[][] generateGrid() {
        Piece.PieceType[][] grid = new Piece.PieceType[height][width];
        // Put each block in the block pile into the grid
        for (Vector v : blockPile.keySet()) {
            // The grid is [row,col], but vectors are [x,y]
            grid[MathUtils.round(v.get(1))][MathUtils.round(v.get(0))] = blockPile.get(v);
        }

        // Add the blocks from the falling piece into the grid
        if (fallingPiece != null) {
            for (Vector v : fallingPiece.getGlobalVectors()) {
                // The grid is [row,col], but vectors are [x,y]
                grid[MathUtils.round(v.get(1))][MathUtils.round(v.get(0))] = fallingPiece.getType();
            }
        }
        return grid;
    }

    /**
     * Check if any rows are full, and clear them. Does nothing if no rows are full.
     */
    private void clearLines() {
        for (int y = 0; y < height; y++) {
            List<Vector> vectors = new LinkedList<>();
            // Iterate through the row and check for blocks at every position
            for (int x = 0; x < width; x++) {
                Vector v = new Vector(x, y);
                if (blockPile.containsKey(v)) {
                    vectors.add(v);
                } else {
                    // If we find an empty spot, this row is not full
                    break;
                }
            }
            // If every spot has a block in it, we need to clear this row
            if (vectors.size() == width) {
                // Remove each block from the pile
                vectors.forEach(blockPile::remove);
                clearedLines++;
                shiftDown(y--); // decrement y, so it will get incremented again to same value at the end of the loop
            }
        }
    }

    /**
     * Shift the block pile down by one row, starting at the specified y value. This is used when rows are cleared.
     *
     * @param yStart The y value (up is positive) of the row that was just cleared. All rows above this are shifted down.
     */
    private void shiftDown(int yStart) {
        // We'll be shifting down by one, so this vector will be added to each block position
        Vector shift = new Vector(0, -1);
        for (int y = yStart + 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // If there's a block at this position, remove it and insert a copy one row below
                Vector v = new Vector(x, y);
                Piece.PieceType type = blockPile.get(v);
                if (type != null) {
                    blockPile.remove(v);
                    blockPile.put(v.add(shift), type);
                }
            }
        }
    }

    /**
     * Add a piece to the block pile. This splits it up into its individual blocks and adds them all.
     *
     * @param piece The piece to add to the pile.
     */
    private void addPieceToPile(Piece piece) {
        for (Vector v : piece.getGlobalVectors()) {
            blockPile.put(v, piece.getType());
        }
    }

    /**
     * Rotate the falling piece CW by 90 degrees, if possible.
     */
    public synchronized void rotate() {
        // If there's  no falling piece, don't do anything
        if (fallingPiece == null) {
            return;
        }
        // Rotate 90 degrees CW
        fallingPiece.rotateCW();
        // If it doesn't fit, move back
        if (!pieceFits(fallingPiece)) {
            fallingPiece.rotateCCW();
        }
    }

    /**
     * Move the falling piece to the left by one column, if possible.
     */
    public synchronized void left() {
        // If there's no falling piece, don't do anything
        if (fallingPiece == null) {
            return;
        }
        // Move the piece to the left by one
        fallingPiece.decrementX();
        // If it doesn't fit, move back
        if (!pieceFits(fallingPiece)) {
            fallingPiece.incrementX();
        }
    }

    /**
     * Move the falling piece to the right by one column, if possible.
     */
    public synchronized void right() {
        // If there's no falling piece, don't do anything
        if (fallingPiece == null) {
            return;
        }
        // Move the piece to the right by one
        fallingPiece.incrementX();
        // If it doesn't fit, move back
        if (!pieceFits(fallingPiece)) {
            fallingPiece.decrementX();
        }
    }

    /**
     * Move the falling piece down one row, if possible.
     */
    public synchronized void down() {
        // If there's no falling piece, don't do anything
        if (fallingPiece == null) {
            return;
        }
        // Move the piece down by one
        fallingPiece.decrementY();
        // If it doesn't fit, move back up
        if (!pieceFits(fallingPiece)) {
            fallingPiece.incrementY();
        }
    }

    /**
     * Checks if the supplied piece can fit on the screen without intersecting any existing piece or going offscreen.
     *
     * @param piece The piece to check.
     * @return True if the supplied piece is entirely onscreen, and doesn't intersect any other piece onscreen.
     */
    private boolean pieceFits(Piece piece) {
        // Get the positions of each block in global space
        for (Vector v : piece.getGlobalVectors()) {
            // If any of them are offscreen (not in bounds) this piece doesn't fit
            if (!inRange(v.get(0), 0, width) || !inRange(v.get(1), 0, height)) {
                return false;
            }

            // If the position of this block is already taken (in the block pile) this piece doesn't fit
            if (blockPile.containsKey(v)) {
                return false;
            }
        }
        return true;
    }

    private boolean inRange(double num, double low, double high) {
        return num >= low && num < high;
    }

    /**
     * Checks if there is room to create a new falling piece.
     *
     * @return True if a newly created piece fits on the board, false otherwise.
     */
    private boolean canCreateNewFallingPiece() {
        // Create a new piece of the next piece type and see if it fits
        Piece p = Piece.createPiece(nextPieceType);
        p.setPos(startX, startY);
        return pieceFits(p);
    }

    /**
     * Create a new falling piece
     */
    private void newFallingPiece() {
        // Instantiate a new piece to fall, and place it in the starting position
        fallingPiece = Piece.createPiece(nextPieceType);
        fallingPiece.setPos(startX, startY);
        // Randomly select the next piece type
        nextPieceType = Piece.PieceType.values()[random.nextInt(Piece.PieceType.values().length)];
    }

    /**
     * Check if the supplied piece is falling. (can move down)
     *
     * @param piece The piece to check.
     * @return True if the piece can move down and still fit, false otherwise.
     */
    private boolean isFalling(Piece piece) {
        // Move it down and check if it fits
        piece.decrementY();
        boolean falling = pieceFits(piece);
        // Move it back up
        piece.incrementY();
        return falling;
    }
}
