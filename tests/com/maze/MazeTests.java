package com.maze;

import java.util.Arrays;
import java.util.List;

public final class MazeTests {
    public static void main(String[] args) {
        keepsEntityPositionsIndependent();
        movesToWalkableCell();
        blocksWall();
        movingPlayerDoesNotMoveEnemy();
        blocksAnotherEntity();
        difficultyProgressesAndKeepsOddMazeDimensions();
        allGeneratedLevelsMeetPlayabilityContract();
        generationIsDeterministic();

        System.out.println("All tests passed.");
    }

    private static MazeMap testMap() {
        String[] layout = {
                "#####",
                "#...#",
                "###.#",
                "#...#",
                "#####"
        };
        return new MazeMap(layout, 1, 1);
    }

    private static void keepsEntityPositionsIndependent() {
        GridEntity player = new GridEntity(1, 1);
        GridEntity enemy = new GridEntity(7, 1);

        assertPosition("player position", player, 1, 1);
        assertPosition("enemy position", enemy, 7, 1);
        System.out.println("PASS: entity positions are independent");
    }

    private static void movesToWalkableCell() {
        MazeRules rules = new MazeRules(testMap());
        GridEntity player = new GridEntity(1, 1);

        boolean moved = rules.tryMove(player, null, 0, 1);

        assertTrue("walkable movement", moved);
        assertPosition("walkable movement position", player, 1, 2);
        System.out.println("PASS: moves to a walkable cell");
    }

    private static void blocksWall() {
        MazeRules rules = new MazeRules(testMap());
        GridEntity player = new GridEntity(1, 1);
        GridEntity enemy = new GridEntity(1, 3);

        boolean moved = rules.tryMove(player, enemy, -1, 0);

        assertFalse("wall movement", moved);
        assertPosition("wall movement position", player, 1, 1);
        System.out.println("PASS: blocks walls");
    }

    private static void movingPlayerDoesNotMoveEnemy() {
        MazeRules rules = new MazeRules(testMap());
        GridEntity player = new GridEntity(1, 1);
        GridEntity enemy = new GridEntity(1, 3);

        rules.tryMove(player, enemy, 0, 1);

        assertPosition("moved player", player, 1, 2);
        assertPosition("stationary enemy", enemy, 1, 3);
        System.out.println("PASS: moving one entity does not move another");
    }

    private static void blocksAnotherEntity() {
        MazeRules rules = new MazeRules(testMap());
        GridEntity player = new GridEntity(1, 1);
        GridEntity enemy = new GridEntity(1, 2);

        boolean moved = rules.tryMove(player, enemy, 0, 1);

        assertFalse("entity collision", moved);
        assertPosition("blocked player", player, 1, 1);
        assertPosition("stationary enemy", enemy, 1, 2);
        System.out.println("PASS: blocks another entity");
    }

    private static void difficultyProgressesAndKeepsOddMazeDimensions() {
        LevelDifficulty first = LevelDifficulty.calculateDifficulty(1, 750);
        LevelDifficulty middle = LevelDifficulty.calculateDifficulty(375, 750);
        LevelDifficulty last = LevelDifficulty.calculateDifficulty(750, 750);
        assertTrue("enemy interval progresses", first.enemyMoveInterval() > middle.enemyMoveInterval()
                && middle.enemyMoveInterval() > last.enemyMoveInterval());
        assertTrue("flee duration progresses", first.fleeDuration() > middle.fleeDuration()
                && middle.fleeDuration() > last.fleeDuration());
        for (int level = 1; level <= 750; level++) {
            LevelDifficulty difficulty = LevelDifficulty.calculateDifficulty(level, 750);
            assertTrue("odd maze rows", difficulty.mazeRows() % 2 != 0);
            assertTrue("odd maze columns", difficulty.mazeColumns() % 2 != 0);
            assertTrue("valid generated size",
                    new MazeGenerator().validateSize(difficulty.mazeColumns(), difficulty.mazeRows()));
        }
    }

    private static void allGeneratedLevelsMeetPlayabilityContract() {
        ProceduralLevelFactory factory = new ProceduralLevelFactory();
        PathFinder finder = new PathFinder();
        for (int level = 1; level <= 750; level++) {
            LevelDifficulty difficulty = LevelDifficulty.calculateDifficulty(level, 750);
            GeneratedLevel generated = factory.generate(level, difficulty);
            MazeMap map = new MazeMap(generated.layout(), generated.exit().row(), generated.exit().column());
            assertTrue("player walkable at level " + level,
                    map.isWalkable(generated.player().row(), generated.player().column()));
            assertTrue("enemy walkable at level " + level,
                    map.isWalkable(generated.enemy().row(), generated.enemy().column()));
            assertTrue("fruit walkable at level " + level,
                    map.isWalkable(generated.fruit().row(), generated.fruit().column()));
            List<Cell> route = finder.shortestPath(map, generated.player(), generated.exit());
            assertFalse("route exists at level " + level, route.isEmpty());
            assertFalse("enemy blocks route at level " + level, route.contains(generated.enemy()));
            assertTrue("fruit on route at level " + level, route.contains(generated.fruit()));
            int[][] fromPlayer = finder.distancesFrom(map, generated.player());
            int[][] fromEnemy = finder.distancesFrom(map, generated.enemy());
            int playerToFruit = fromPlayer[generated.fruit().row()][generated.fruit().column()];
            int enemyToFruit = fromEnemy[generated.fruit().row()][generated.fruit().column()];
            assertTrue("fruit safety at level " + level,
                    enemyToFruit >= playerToFruit + Math.max(4, (route.size() - 1) / 10));
        }
    }

    private static void generationIsDeterministic() {
        ProceduralLevelFactory factory = new ProceduralLevelFactory();
        for (int level : new int[]{1, 7, 37, 100, 375, 750}) {
            LevelDifficulty difficulty = LevelDifficulty.calculateDifficulty(level, 750);
            GeneratedLevel first = factory.generate(level, difficulty);
            GeneratedLevel second = factory.generate(level, difficulty);
            assertTrue("same layout", Arrays.equals(first.layout(), second.layout()));
            assertTrue("same enemy", first.enemy().equals(second.enemy()));
            assertTrue("same exit", first.exit().equals(second.exit()));
            assertTrue("same fruit", first.fruit().equals(second.fruit()));
        }
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
