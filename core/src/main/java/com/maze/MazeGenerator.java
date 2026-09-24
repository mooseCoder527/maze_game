package com.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MazeGenerator {

    public String[] generateLevel(int rows, int columns, int seed) {
        Random random = new Random(seed);
        if (!validateSize(columns, rows)) {
            throw new IllegalArgumentException("Size is invalid!!");
        }
        String[] level;
        char[][] layout = new char[rows][columns];
        boolean[][] visited = new boolean[rows][columns];
        Stack<Cell> stack = new Stack<Cell>();
        Cell start = new Cell(1, 1);
        stack.push(start);
        visited[start.row()][start.column()] = true;
        layout[start.row()][start.column()] = '.';
        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Cell> neighbours = unvisitedNeighbors(current,visited,rows,columns);
            if(neighbours.isEmpty()){
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

    public boolean validateSize(int columns, int rows) {
        if (rows < 1 || columns < 1) {
            return false;
        }
        if (rows > 20 || columns > 20) {
            return false;
        }
        return true;
    }

    private List<Cell> unvisitedNeighbors(
            Cell current,
            boolean[][] visited,
            int rows,
            int columns
    ) {
        int[][] directions = {
                {-2, 0},
                {0, 2},
                {2, 0},
                {0, -2}
        };

        List<Cell> neighbors =
                new ArrayList<>(4);

        for (int[] direction : directions) {

            int row =
                    current.row()
                            + direction[0];

            int column =
                    current.column()
                            + direction[1];

            if (row <= 0
                    || row >= rows - 1
                    || column <= 0
                    || column >= columns - 1
            ) {
                continue;
            }

            if (!visited[row][column]) {
                neighbors.add(new Cell(row, column));
            }
        }
        return neighbors;
    }

    private void carve(char[][] layout, Cell current, Cell next){
        int pathRow = (current.row() + next.row())/2;
        int pathColumn = (current.column() + next.column())/2;
        layout[pathRow][pathColumn] = '.';
        layout[next.row()][next.column()] = '.';
    }
    private String[] toStringMap(char[][] layout){
        //[[#.#.###]
         //[#...###]]
        String[] result = new String[layout.length];
        for(int row = 0; row <= layout.length; row++){
            result[row] = new String(layout[row]);
        }
        return result;
    }
}
