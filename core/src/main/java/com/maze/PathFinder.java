package com.maze;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class PathFinder {

    public Cell nextStep(MazeMap map, GridEntity enemy, GridEntity player) {
        List<Cell> path = shortestPath(
                map,
                new Cell(enemy.row(), enemy.column()),
                new Cell(player.row(), player.column())
        );

        if (path.isEmpty()) {
            return null;
        }

        if (path.size() == 1) {
            return path.get(0);
        }

        return path.get(1);
    }

    public List<Cell> shortestPath(MazeMap map, Cell start, Cell goal) {
        Queue<Cell> frontier = new ArrayDeque<>();
        boolean[][] visited = new boolean[map.rows()][map.columns()];
        Cell[][] parent = new Cell[map.rows()][map.columns()];

        frontier.offer(start);
        visited[start.row()][start.column()] = true;

        while (!frontier.isEmpty()) {
            Cell current = frontier.poll();
            if (current.equals(goal)) {
                return buildPath(start, goal, parent);
            }

            for (Cell neighbor : map.neighbors(current)) {
                int row = neighbor.row();
                int column = neighbor.column();
                if (visited[row][column]) continue;
                visited[row][column] = true;
                parent[row][column] = current;
                frontier.offer(neighbor);
            }
        }

        return List.of();
    }

    public int[][] distancesFrom(MazeMap map, Cell start) {
        int[][] distances = new int[map.rows()][map.columns()];
        for (int[] row : distances) {
            Arrays.fill(row, -1);
        }

        Queue<Cell> frontier = new ArrayDeque<>();
        frontier.offer(start);
        distances[start.row()][start.column()] = 0;

        while (!frontier.isEmpty()) {
            Cell current = frontier.poll();
            for (Cell neighbor : map.neighbors(current)) {
                if (distances[neighbor.row()][neighbor.column()] != -1) continue;
                distances[neighbor.row()][neighbor.column()] =
                        distances[current.row()][current.column()] + 1;
                frontier.offer(neighbor);
            }
        }

        return distances;
    }

    public Cell furthestCell(MazeMap map, Cell start) {
        Queue<Cell> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[map.rows()][map.columns()];
        visited[start.row()][start.column()] = true;
        queue.offer(start);
        Cell farthest = start;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            farthest = current;
            for (Cell neighbour : map.neighbors(current)) {
                if (!visited[neighbour.row()][neighbour.column()]) {
                    visited[neighbour.row()][neighbour.column()] = true;
                    queue.offer(neighbour);
                }
            }
        }

        return farthest;
    }

    private List<Cell> buildPath(Cell start, Cell goal, Cell[][] parent) {
        LinkedList<Cell> path = new LinkedList<>();
        Cell current = goal;
        path.addFirst(current);

        while (!current.equals(start)) {
            current = parent[current.row()][current.column()];
            if (current == null) return List.of();
            path.addFirst(current);
        }

        return path;
    }
}
