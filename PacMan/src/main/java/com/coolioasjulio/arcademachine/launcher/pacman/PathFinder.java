package com.coolioasjulio.arcademachine.launcher.pacman;

import com.coolioasjulio.arcademachine.launcher.pacman.engine.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PathFinder {

    /**
     * Find a path between two points in the level.
     *
     * @param levelMap   The level to navigate through.
     * @param from       The starting point of the path.
     * @param to         The ending point of the path.
     * @param exclusions Any points to exclude from the path finding search. These points are treated like walls.
     * @return A {@link PathFindingResult} object representing the direction to travel in.
     */
    public static PathFindingResult pathFind(LevelMap levelMap, Coord from, Coord to, Coord... exclusions) {
        return pathFind(levelMap, from.x, from.y, to.x, to.y, exclusions);
    }

    /**
     * Find a path between two points in the level.
     *
     * @param levelMap   The level to navigate through.
     * @param fromX      The x coordinate of the starting point.
     * @param fromY      The y coordinate of the starting point.
     * @param toX        The x coordinate of the ending point.
     * @param toY        The y coordinate of the ending point.
     * @param exclusions Any points to exclude from the path finding search. These points are treated like walls.
     * @return A {@link PathFindingResult} object representing the direction to travel in.
     */
    public static PathFindingResult pathFind(LevelMap levelMap, int fromX, int fromY, int toX, int toY, Coord... exclusions) {
        if (!levelMap.isOpen(fromX, fromY) || !levelMap.isOpen(toX, toY)) {
            // If the starting and ending points are invalid, return an empty direction
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
            return String.format("PathFindingResult(dist=%d, dir=%s)", distance, direction.name());
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

    /**
     * This is an implementation of Dijkstra's Algorithm to find a path between the two points.
     *
     * @return A PathFindingResult object representing the path to take
     */
    private PathFindingResult pathFind() {
        // If the starting point is the ending point, return immediately.
        if (from == to) {
            return new PathFindingResult(0, Direction.EAST);
        }
        // Initialize the set of cells to search through
        Set<Cell> neighbors = new HashSet<>();
        neighbors.add(from); // The first cell is the starting cell
        while (true) {
            // If there are no more neighbors to search through and we haven't found the path yet, there is no possible path.
            if (neighbors.isEmpty()) {
                return new PathFindingResult(0, null);
            }
            // The cell to search is the cheapest cell to get to from the set of neighbors
            Cell cell = neighbors.stream().min(Comparator.comparing(c -> c.cost)).orElseThrow(IllegalStateException::new);
            // If the searched cell is the ending point, we've found the optimal path
            if (cell == to) {
                break;
            }
            // Remove the cell from the neighbors set
            neighbors.remove(cell);
            // Initialize the costs and paths for each neighbor of this cell, iff the new path is more optimal, or if the cell is unvisited
            Set<Cell> newNeighbors = getNeighbors(cell);
            int newCost = cell.cost + 1;
            for (Cell c : newNeighbors) {
                // If unvisited or new path is more optimal, initialize it and add it to the neighbors list
                if (c.cost > newCost) {
                    c.cost = newCost;
                    c.path.clear();
                    c.path.addAll(cell.path);
                    c.path.add(c);
                    neighbors.add(c);
                }
            }
        }
        // If we've reached this point, the optimal path has been found.
        Coord start = to.path.get(0).pos; // The ending cell has been modified in place, so it has the information we need.
        Coord end = to.path.get(1).pos;
        int dx = end.x - start.x;
        int dy = end.y - start.y;

        // Return the result of the pathfinding
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
                // Don't evaluate this coordinate if it's a wall, it's an exclusion, or if it's diagonal from the current cell.
                if (Math.abs(dx) == Math.abs(dy) ||
                        !levelMap.isOpen(x + dx, y + dy) ||
                        exclusions.contains(new Coord(x + dx, y + dy))) {
                    continue;
                }
                cellSet.add(cells[y + dy][x + dx]);
            }
        }
        return cellSet;
    }

    /**
     * Data container class, representing a tile on the level map.
     */
    private static class Cell {
        /**
         * The tile position represented by this object.
         */
        public Coord pos;
        /**
         * The path to take from the starting tile to this tile.
         */
        public List<Cell> path = new ArrayList<>();
        /**
         * The cost to get from the starting tile to this tile.
         */
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
