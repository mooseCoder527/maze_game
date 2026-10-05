package com.maze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class MazeGenerator {

    public String[] generateLevel(int rows, int columns, int seed) {
        if (!validateSize(rows, columns)) {
            throw new IllegalArgumentException("Maze dimensions must be odd and at least 5.");
        }

        Random random = new Random(seed);
        char[][] layout = new char[rows][columns];
        for (char[] row : layout) {
            Arrays.fill(row, '#');
        }

        boolean[][] visited = new boolean[rows][columns];
        ArrayDeque<Cell> stack = new ArrayDeque<>();
        Cell start = new Cell(1, 1);
        visited[start.row()][start.column()] = true;
        layout[start.row()][start.column()] = '.';
        stack.push(start);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Cell> neighbours = unvisitedNeighbors(current, visited, rows, columns);
            if (neighbours.isEmpty()) {
                stack.pop();
                continue;
            }

            Cell next = neighbours.get(random.nextInt(neighbours.size()));
            carve(layout, current, next);
            visited[next.row()][next.column()] = true;
            stack.push(next);
        }

        return toStringMap(layout);
    }

    public boolean validateSize(int rows, int columns) {
        return rows >= 5
                && columns >= 5
                && rows % 2 != 0
                && columns % 2 != 0;
    }

    private List<Cell> unvisitedNeighbors(Cell current, boolean[][] visited, int rows, int columns) {
        int[][] directions = {{-2, 0}, {0, 2}, {2, 0}, {0, -2}};
        List<Cell> neighbors = new ArrayList<>(4);
        for (int[] direction : directions) {
            int row = current.row() + direction[0];
            int column = current.column() + direction[1];
            if (row <= 0 || row >= rows - 1 || column <= 0 || column >= columns - 1) continue;
            if (!visited[row][column]) neighbors.add(new Cell(row, column));
        }
        return neighbors;
    }

    private void carve(char[][] layout, Cell current, Cell next) {
        int pathRow = (current.row() + next.row()) / 2;
        int pathColumn = (current.column() + next.column()) / 2;
        layout[pathRow][pathColumn] = '.';
        layout[next.row()][next.column()] = '.';
    }

    private String[] toStringMap(char[][] layout) {
        String[] result = new String[layout.length];
        for (int row = 0; row < layout.length; row++) {
            result[row] = new String(layout[row]);
        }
        return result;
    }
}
