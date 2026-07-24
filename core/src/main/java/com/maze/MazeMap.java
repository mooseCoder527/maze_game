package com.maze;

import java.util.ArrayList;
import java.util.List;

public final class MazeMap {
    private final String[] layout;
    private final int rows;
    private final int columns;

    private final int exitRow;
    private final int exitColumn;

    public MazeMap(
            String[] layout,
            int exitRow,
            int exitColumn
    ) {
        validateLayout(layout);

        this.layout = layout.clone();
        this.rows = layout.length;
        this.columns = layout[0].length();

        if (!isWalkable(exitRow, exitColumn)) {
            throw new IllegalArgumentException(
                    "Exit must be on a walkable cell."
            );
        }

        this.exitRow = exitRow;
        this.exitColumn = exitColumn;
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return columns;
    }

    public boolean isWall(
            int row,
            int column
    ) {
        return !isInside(row, column)
                || layout[row].charAt(column) == '#';
    }

    public boolean isWalkable(
            int row,
            int column
    ) {
        return isInside(row, column)
                && layout[row].charAt(column) != '#';
    }

    public boolean isExit(
            int row,
            int column
    ) {
        return row == exitRow
                && column == exitColumn;
    }

    public int exitRow() {
        return exitRow;
    }

    public int exitColumn() {
        return exitColumn;
    }

    public List<Cell> neighbors(Cell cell) {
        int[] rowChanges = {
                -1,
                0,
                1,
                0
        };

        int[] columnChanges = {
                0,
                1,
                0,
                -1
        };

        List<Cell> neighbors =
                new ArrayList<>(4);

        for (int i = 0; i < rowChanges.length; i++) {
            int nextRow =
                    cell.row() + rowChanges[i];

            int nextColumn =
                    cell.column() + columnChanges[i];

            if (isWalkable(nextRow, nextColumn)) {
                neighbors.add(
                        new Cell(
                                nextRow,
                                nextColumn
                        )
                );
            }
        }

        return neighbors;
    }

    private boolean isInside(
            int row,
            int column
    ) {
        return row >= 0
                && row < rows
                && column >= 0
                && column < columns;
    }

    private static void validateLayout(
            String[] layout
    ) {
        if (layout == null || layout.length == 0) {
            throw new IllegalArgumentException(
                    "Layout must not be empty."
            );
        }

        if (
                layout[0] == null
                        || layout[0].isEmpty()
        ) {
            throw new IllegalArgumentException(
                    "Layout rows must not be empty."
            );
        }

        int expectedColumns =
                layout[0].length();

        for (String row : layout) {
            if (
                    row == null
                            || row.length() != expectedColumns
            ) {
                throw new IllegalArgumentException(
                        "Every layout row must have "
                                + "the same length."
                );
            }
        }
    }
}