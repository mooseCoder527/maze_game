package com.maze;

public final class MazeMap {
    private static String[] LAYOUT = {
        "#############",
        "#.....#.....#",
        "#.###.#.###.#",
        "#...#.#...#.#",
        "###.#.###.#.#",
        "#...#.....#.#",
        "#.#########.#",
        "#...........#",
        "#############"
    };

    public static int ROWS = LAYOUT.length;
    public static int COLUMNS = LAYOUT[0].length();

    private int exitRow;
    private int exitColumn;

    public MazeMap(String[] LAYOUT, int exitRow, int exitColumn) {
        this.LAYOUT = LAYOUT;
        this.ROWS = LAYOUT.length;
        this.COLUMNS = LAYOUT[0].length();
        if (!isWalkable(exitRow, exitColumn)) {
            throw new IllegalArgumentException("Exit must be on a walkable cell.");
        }

        this.exitRow = exitRow;
        this.exitColumn = exitColumn;
    }

    public boolean isWall(int row, int column) {
        return !isInside(row, column) || LAYOUT[row].charAt(column) == '#';
    }

    public boolean isWalkable(int row, int column) {
        return isInside(row, column) && LAYOUT[row].charAt(column) != '#';
    }

    public boolean isExit(int row, int column) {
        return row == exitRow && column == exitColumn;
    }

    public int exitRow() {
        return exitRow;
    }

    public int exitColumn() {
        return exitColumn;
    }

    private boolean isInside(int row, int column) {
        return row >= 0 && row < ROWS && column >= 0 && column < COLUMNS;
    }
}
