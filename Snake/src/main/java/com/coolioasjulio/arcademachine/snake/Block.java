package com.coolioasjulio.arcademachine.snake;

public class Block {
    private Coord coord;
    private Block upstream, downstream;

    public Block(Coord coord) {
        this.coord = coord;
    }

    public Block(Coord coord, Block upstream) {
        this.coord = coord;
        this.upstream = upstream;
    }

    public void incX() {
        coord = new Coord(coord.getX() + 1, coord.getY());
    }

    public void decX() {
        coord = new Coord(coord.getX() - 1, coord.getY());
    }

    public void incY() {
        coord = new Coord(coord.getX(), coord.getY() + 1);
    }

    public void decY() {
        coord = new Coord(coord.getX(), coord.getY() - 1);
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public Coord getCoord() {
        return coord;
    }

    public Block getUpstreamBlock() {
        return upstream;
    }

    public Block getDownstreamBlock() {
        return downstream;
    }

    public void setDownstreamBlock(Block downstream) {
        this.downstream = downstream;
    }

    public void setUpstreamBlock(Block upstream) {
        this.upstream = upstream;
    }

    @Override
    public String toString() {
        return coord.toString();
    }
}
