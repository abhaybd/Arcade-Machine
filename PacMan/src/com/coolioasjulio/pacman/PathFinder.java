package com.coolioasjulio.pacman;

import com.coolioasjulio.pacman.engine.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PathFinder {

    public static PathFindingResult pathFind(LevelMap levelMap, Coord from, Coord to, Coord... exclusions) {
        return pathFind(levelMap, from.x, from.y, to.x, to.y, exclusions);
    }

    public static PathFindingResult pathFind(LevelMap levelMap, int fromX, int fromY, int toX, int toY, Coord... exclusions) {
        if (!levelMap.isOpen(fromX, fromY) || !levelMap.isOpen(toX, toY)) {
            return new PathFindingResult(0, null);
        }
        return new PathFinder(levelMap, fromX, fromY, toX, toY, exclusions).pathFind();
    }

    public static class PathFindingResult {
        public int distance;
        public Direction direction;

        public PathFindingResult(int distance, Direction direction) {
            this.distance = distance;
            this.direction = direction;
        }

        public PathFindingResult() {
        }

        public String toString() {
            return String.format("(dist=%d, dir=%s)", distance, direction.name());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathFindingResult that = (PathFindingResult) o;
            return distance == that.distance &&
                    direction == that.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(distance, direction);
        }
    }

    private final LevelMap levelMap;
    private Cell[][] cells;
    private Cell from, to;
    private Set<Coord> exclusions;

    private PathFinder(LevelMap levelMap, int fromX, int fromY, int toX, int toY, Coord... exclusions) {
        this.levelMap = levelMap;

        cells = new Cell[levelMap.getHeight()][levelMap.getWidth()];
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[row].length; col++) {
                cells[row][col] = new Cell(new Coord(col, row));
            }
        }
        from = cells[fromY][fromX];
        to = cells[toY][toX];
        from.path.add(from);
        from.cost = 0;

        this.exclusions = new HashSet<>();
        this.exclusions.addAll(Arrays.asList(exclusions));
    }

    private PathFindingResult pathFind() {
        if (from == to) {
            return new PathFindingResult(0, Direction.EAST);
        }
        Set<Cell> neighbors = new HashSet<>();
        neighbors.add(from);
        while(true) {
            if (neighbors.isEmpty()) {
                return new PathFindingResult(0, null);
            }
            Cell cell = neighbors.stream().min(Comparator.comparing(c -> c.cost)).orElseThrow(IllegalStateException::new);
            if (cell == to) {
                break;
            }
            neighbors.remove(cell);
            Set<Cell> newNeighbors = getNeighbors(cell);
            int newCost = cell.cost + 1;
            for (Cell c : newNeighbors) {
                if (c.cost > newCost) {
                    c.cost = newCost;
                    c.path.clear();
                    c.path.addAll(cell.path);
                    c.path.add(c);
                    neighbors.add(c);
                }
            }
        }
        Coord start = to.path.get(0).pos;
        Coord end = to.path.get(1).pos;
        int dx = end.x - start.x;
        int dy = end.y - start.y;

        PathFindingResult result = new PathFindingResult();
        result.distance = to.path.size();
        if (dx == 0 && dy == 1) result.direction = Direction.SOUTH;
        else if (dx == 0 && dy == -1) result.direction = Direction.NORTH;
        else if (dx == 1 && dy == 0) result.direction = Direction.EAST;
        else if (dx == -1 && dy == 0) result.direction = Direction.WEST;
        else throw new IllegalStateException();
        return result;
    }

    private Set<Cell> getNeighbors(Cell cell) {
        int x = cell.pos.x;
        int y = cell.pos.y;
        Set<Cell> cellSet = new HashSet<>();
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (Math.abs(dx) == Math.abs(dy) ||
                        !levelMap.isOpen(x+dx, y+dy) ||
                        exclusions.contains(new Coord(x+dx, y+dy))) {
                    continue;
                }
                cellSet.add(cells[y+dy][x+dx]);
            }
        }
        return cellSet;
    }

    private static class Cell {
        public Coord pos;
        public List<Cell> path = new ArrayList<>();
        public int cost = Integer.MAX_VALUE;

        public Cell(Coord pos) {
            this.pos = pos;
        }

        public int hashCode() {
            return pos.hashCode();
        }

        public boolean equals(Object o) {
            if (!(o instanceof Cell)) return false;
            Cell c = ((Cell) o);
            return c.pos.equals(pos);
        }

        public String toString() {
            return pos.toString();
        }
    }
}
