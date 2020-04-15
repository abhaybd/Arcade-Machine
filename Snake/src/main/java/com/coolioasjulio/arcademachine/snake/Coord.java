package com.coolioasjulio.arcademachine.snake;

import java.util.Objects;

public class Coord {
    private int x, y;
    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Coord)) {
            return false;
        }
        Coord c = (Coord) o;
        return c.x == x && c.y == y;
    }

}
