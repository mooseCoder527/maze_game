package com.maze;

import java.util.ArrayDeque;
import java.util.Queue;

public final class PathFinder {

    public Cell nextStep(
            MazeMap map,
            GridEntity enemy,
            GridEntity player
    ) {
        Cell start = new Cell(
                enemy.row(),
                enemy.column()
        );

        Cell goal = new Cell(
                player.row(),
                player.column()
        );

        if (start.equals(goal)) {
            return start;
        }

        Queue<Cell> frontier =
                new ArrayDeque<>();

        boolean[][] visited =
                new boolean[
                        map.rows()
                        ][
                        map.columns()
                        ];

        Cell[][] parent =
                new Cell[
                        map.rows()
                        ][
                        map.columns()
                        ];

        frontier.offer(start);

        visited[
                start.row()
                ][
                start.column()
                ] = true;

        while (!frontier.isEmpty()) {
            Cell current = frontier.poll();

            if (current.equals(goal)) {
                return findFirstStep(
                        start,
                        goal,
                        parent
                );
            }

            for (
                    Cell neighbor
                    : map.neighbors(current)
            ) {
                int row = neighbor.row();
                int column = neighbor.column();

                if (visited[row][column]) {
                    continue;
                }

                visited[row][column] = true;
                parent[row][column] = current;

                frontier.offer(neighbor);
            }
        }

        return null;
    }

    private Cell findFirstStep(
            Cell start,
            Cell goal,
            Cell[][] parent
    ) {
        Cell current = goal;

        while (true) {
            Cell previous =
                    parent[
                            current.row()
                            ][
                            current.column()
                            ];

            if (previous == null) {
                return null;
            }

            if (previous.equals(start)) {
                return current;
            }

            current = previous;
        }
    }
}