package com.maze;

public final class MazeTests {
    public static void main(String[] args) {
        keepsEntityPositionsIndependent();
        movesToWalkableCell();
        blocksWall();
        movingPlayerDoesNotMoveEnemy();
        locksAnotherEntity();

        System.out.println("All tests passed.");
    }

    private static void keepsEntityPositionsIndependent() {
        GridEntity player = new GridEntity(1, 1);
        GridEntity enemy = new GridEntity(7, 1);

        assertPosition("player position", player, 1, 1);
        assertPosition("enemy position", enemy, 7, 1);
        System.out.println("PASS: entity positions are independent");
    }

    private static void movesToWalkableCell() {
        MazeRules rules = new MazeRules(new MazeMap(5, 11));
        GridEntity player = new GridEntity(1, 1);

        boolean moved = rules.tryMove(player, 0, 1);

        assertTrue("walkable movement", moved);
        assertPosition("walkable movement position", player, 1, 2);
        System.out.println("PASS: moves to a walkable cell");
    }

    private static void blocksWall() {
        MazeRules rules = new MazeRules(new MazeMap(5, 11));
        GridEntity player = new GridEntity(1, 1);
        GridEntity enemy = new GridEntity(1, 3);

        boolean moved = rules.tryMove(player, enemy, -1, 0);

        assertFalse("wall movement", moved);
        assertPosition("wall movement position", player, 1, 1);
        System.out.println("PASS: blocks walls");
    }

    private static void movingPlayerDoesNotMoveEnemy() {
        MazeRules rules = new MazeRules(new MazeMap(5, 11));
        GridEntity player = new GridEntity(1, 1);
        GridEntity enemy = new GridEntity(1, 3);

        rules.tryMove(player, enemy, 0, 1);

        assertPosition("moved player", player, 1, 2);
        assertPosition("stationary enemy", enemy, 1, 3);
        System.out.println("PASS: moving one entity does not move another");
    }

    private static void blocksAnotherEntity() {
        MazeRules rules = new MazeRules(new MazeMap(5, 11));
        GridEntity player = new GridEntity(1, 1);
        GridEntity enemy = new GridEntity(1, 2);

        boolean moved = rules.tryMove(player, enemy, 0, 1);

        assertFalse("entity collision", moved);
        assertPosition("blocked player", player, 1, 1);
        assertPosition("stationary enemy", enemy, 1, 2);
        System.out.println("PASS: blocks another entity");
    }

    private static void assertPosition(
        String name,
        GridEntity entity,
        int expectedRow,
        int expectedColumn
    ) {
        if (entity.row() != expectedRow || entity.column() != expectedColumn) {
            throw new AssertionError(
                name + ": expected (" + expectedRow + ", " + expectedColumn
                    + ") but got (" + entity.row() + ", " + entity.column() + ")"
            );
        }
    }

    private static void assertTrue(String name, boolean value) {
        if (!value) {
            throw new AssertionError(name + ": expected true");
        }
    }

    private static void assertFalse(String name, boolean value) {
        if (value) {
            throw new AssertionError(name + ": expected false");
        }
    }
}
