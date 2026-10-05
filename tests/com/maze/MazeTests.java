package com.maze;

import java.util.Arrays;
import java.util.List;

public final class MazeTests {
    private static final int LEVEL_COUNT = 750;

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
        GridEntity enemy = new GridEntity(3, 3);
        assertPosition("player position", player, 1, 1);
        assertPosition("enemy position", enemy, 3, 3);
    }

    private static void movesToWalkableCell() {
        MazeRules rules = new MazeRules(testMap());
        GridEntity player = new GridEntity(1, 1);
        boolean moved = rules.tryMove(player, null, 0, 1);
        assertTrue("walkable movement", moved);
        assertPosition("walkable movement position", player, 1, 2);
    }

    private static void blocksWall() {
        MazeRules rules = new MazeRules(testMap());
        GridEntity player = new GridEntity(1, 1);
        boolean moved = rules.tryMove(player, null, -1, 0);
        assertFalse("wall movement", moved);
        assertPosition("wall movement position", player, 1, 1);
    }

    private static void movingPlayerDoesNotMoveEnemy() {
        MazeRules rules = new MazeRules(testMap());
        GridEntity player = new GridEntity(1, 1);
        GridEntity enemy = new GridEntity(1, 3);
        rules.tryMove(player, enemy, 0, 1);
        assertPosition("moved player", player, 1, 2);
        assertPosition("stationary enemy", enemy, 1, 3);
    }

    private static void blocksAnotherEntity() {
        MazeRules rules = new MazeRules(testMap());
        GridEntity player = new GridEntity(1, 1);
        GridEntity enemy = new GridEntity(1, 2);
        boolean moved = rules.tryMove(player, enemy, 0, 1);
        assertFalse("entity collision", moved);
        assertPosition("blocked player", player, 1, 1);
    }

    private static void difficultyProgressesAndKeepsOddMazeDimensions() {
        LevelDifficulty first = LevelDifficulty.calculateDifficulty(1, LEVEL_COUNT);
        LevelDifficulty middle = LevelDifficulty.calculateDifficulty(LEVEL_COUNT / 2, LEVEL_COUNT);
        LevelDifficulty last = LevelDifficulty.calculateDifficulty(LEVEL_COUNT, LEVEL_COUNT);

        assertEquals("first enemy interval", 0.5f, first.enemyMoveInterval(), 0.0001f);
        assertEquals("last enemy interval", 0.2f, last.enemyMoveInterval(), 0.0001f);
        assertTrue("middle interval is between endpoints",
                middle.enemyMoveInterval() < first.enemyMoveInterval()
                        && middle.enemyMoveInterval() > last.enemyMoveInterval());

        for (int level = 1; level <= LEVEL_COUNT; level++) {
            LevelDifficulty difficulty = LevelDifficulty.calculateDifficulty(level, LEVEL_COUNT);
            assertTrue("odd maze rows at level " + level, difficulty.mazeRows() % 2 != 0);
            assertTrue("odd maze columns at level " + level, difficulty.mazeColumns() % 2 != 0);
        }
    }

    private static void allGeneratedLevelsMeetPlayabilityContract() {
        ProceduralLevelFactory factory = new ProceduralLevelFactory();
        PathFinder pathFinder = new PathFinder();

        for (int level = 1; level <= LEVEL_COUNT; level++) {
            LevelDifficulty difficulty = LevelDifficulty.calculateDifficulty(level, LEVEL_COUNT);
            GeneratedLevel generated = factory.generate(level, difficulty);
            MazeMap map = new MazeMap(generated.layout(), generated.exit().row(), generated.exit().column());

            assertTrue("player walkable at level " + level,
                    map.isWalkable(generated.player().row(), generated.player().column()));
            assertTrue("enemy walkable at level " + level,
                    map.isWalkable(generated.enemy().row(), generated.enemy().column()));
            assertTrue("fruit walkable at level " + level,
                    map.isWalkable(generated.fruit().row(), generated.fruit().column()));
            assertTrue("exit walkable at level " + level,
                    map.isWalkable(generated.exit().row(), generated.exit().column()));

            List<Cell> solution = pathFinder.shortestPath(map, generated.player(), generated.exit());
            assertFalse("solution exists at level " + level, solution.isEmpty());
            assertFalse("enemy is not on initial solution path at level " + level,
                    solution.contains(generated.enemy()));
            assertTrue("fruit lies on escape route at level " + level,
                    solution.contains(generated.fruit()));

            int[][] fromPlayer = pathFinder.distancesFrom(map, generated.player());
            int[][] fromEnemy = pathFinder.distancesFrom(map, generated.enemy());
            int playerToFruit = fromPlayer[generated.fruit().row()][generated.fruit().column()];
            int enemyToFruit = fromEnemy[generated.fruit().row()][generated.fruit().column()];
            int minimumMargin = Math.max(4, (solution.size() - 1) / 10);
            assertTrue("player has fruit head start at level " + level,
                    enemyToFruit >= playerToFruit + minimumMargin);
        }
    }

    private static void generationIsDeterministic() {
        ProceduralLevelFactory factory = new ProceduralLevelFactory();
        int[] sampleLevels = {1, 7, 37, 100, 375, 750};

        for (int level : sampleLevels) {
            LevelDifficulty difficulty = LevelDifficulty.calculateDifficulty(level, LEVEL_COUNT);
            GeneratedLevel first = factory.generate(level, difficulty);
            GeneratedLevel second = factory.generate(level, difficulty);

            assertTrue("same layout for level " + level,
                    Arrays.equals(first.layout(), second.layout()));
            assertTrue("same player for level " + level, first.player().equals(second.player()));
            assertTrue("same enemy for level " + level, first.enemy().equals(second.enemy()));
            assertTrue("same exit for level " + level, first.exit().equals(second.exit()));
            assertTrue("same fruit for level " + level, first.fruit().equals(second.fruit()));
        }
    }

    private static void assertPosition(String name, GridEntity entity, int expectedRow, int expectedColumn) {
        if (entity.row() != expectedRow || entity.column() != expectedColumn) {
            throw new AssertionError(
                    name + ": expected (" + expectedRow + ", " + expectedColumn
                            + ") but got (" + entity.row() + ", " + entity.column() + ")"
            );
        }
    }

    private static void assertTrue(String name, boolean value) {
        if (!value) throw new AssertionError(name + ": expected true");
    }

    private static void assertFalse(String name, boolean value) {
        if (value) throw new AssertionError(name + ": expected false");
    }

    private static void assertEquals(String name, float expected, float actual, float tolerance) {
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError(name + ": expected " + expected + " but got " + actual);
        }
    }
}
