package com.maze;

import java.util.Random;
import java.util.Stack;

public class MazeGenerator {

    public String[] generateLevel(int rows, int columns, int seed){
        Random random = new Random(seed);
        if (!validateSize(columns,rows)){
            throw new IllegalArgumentException("Size is invalid!!");
        }
        String[] level;
        char[][] layout = new char[rows][columns];
        boolean[][] visited = new boolean[rows][columns];
        Stack<Cell> stack = new Stack<Cell>();
        Cell start = new Cell(1,1);
        stack.push(start);
        visited[start.row()][start.column()] = true;
        layout[start.row()][start.column()] = '.';
        while(!stack.isEmpty()){
            Cell current;
            current = stack.peek();

        }
        return null;
    }
    public boolean validateSize(int columns, int rows){
        if(rows < 1 || columns < 1){
            return false;
        }
        if(rows > 20 || columns > 20){
            return false;
        }
        return true;
    }
}
