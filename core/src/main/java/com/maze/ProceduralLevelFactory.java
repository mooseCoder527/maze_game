package com.maze;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class ProceduralLevelFactory {
    private static final int MAX_GENERATION_ATTEMPTS = 64;
    private static final int GENERATION_SEED_STRIDE = 10_000;

    private final MazeGenerator mazeGenerator = new MazeGenerator();
    private final PathFinder pathFinder = new PathFinder();

    public GeneratedLevel generate(int level, LevelDifficulty difficulty) {
        Cell playerCell = new Cell(1, 1);

        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            int seed = level + attempt * GENERATION_SEED_STRIDE;

            String[] layout = mazeGenerator.generateLevel(
                    difficulty.mazeRows(),
                    difficulty.mazeColumns(),
                    seed
            );

            MazeMap topologyMap = new MazeMap(
                    layout,
                    playerCell.row(),
                    playerCell.column()
            );

            Cell exitCell = pathFinder.furthestCell(topologyMap, playerCell);
            MazeMap candidateMap = new MazeMap(layout, exitCell.row(), exitCell.column());

            Placement placement = findPlayablePlacement(
                    candidateMap,
                    playerCell,
                    exitCell,
                    seed
            );

            if (placement == null) {
                continue;
            }

            return new GeneratedLevel(
                    layout,
                    playerCell,
                    placement.enemy(),
                    exitCell,
                    placement.fruit()
            );
        }

        throw new IllegalStateException("Could not generate a playable level: " + level);
    }

    private Placement findPlayablePlacement(
            MazeMap map,
            Cell playerCell,
            Cell exitCell,
            int seed
    ) {
        List<Cell> solutionPath = pathFinder.shortestPath(map, playerCell, exitCell);
        if (solutionPath.isEmpty()) {
            return null;
        }

        int solutionDistance = solutionPath.size() - 1;
        int minimumSolutionDistance = Math.max(8, (map.rows() + map.columns()) / 2);
        if (solutionDistance < minimumSolutionDistance) {
            return null;
        }

        Set<Cell> solutionCells = new HashSet<>(solutionPath);
        int[][] fromPlayer = pathFinder.distancesFrom(map, playerCell);
        int[][] fromExit = pathFinder.distancesFrom(map, exitCell);

        int minimumPlayerDistance = Math.max(6, solutionDistance / 2);
        int minimumExitDistance = Math.max(4, solutionDistance / 5);

        List<Cell> bestEnemyCandidates = new ArrayList<>();
        int bestSafety = -1;
        int bestTotalDistance = -1;

        for (int row = 0; row < map.rows(); row++) {
            for (int column = 0; column < map.columns(); column++) {
                if (!map.isWalkable(row, column)) continue;

                Cell candidate = new Cell(row, column);
                if (solutionCells.contains(candidate)) continue;

                int playerDistance = fromPlayer[row][column];
                int exitDistance = fromExit[row][column];
                if (playerDistance < minimumPlayerDistance || exitDistance < minimumExitDistance) continue;

                int safety = Math.min(playerDistance, exitDistance);
                int totalDistance = playerDistance + exitDistance;

                if (safety > bestSafety || (safety == bestSafety && totalDistance > bestTotalDistance)) {
                    bestSafety = safety;
                    bestTotalDistance = totalDistance;
                    bestEnemyCandidates.clear();
                    bestEnemyCandidates.add(candidate);
                } else if (safety == bestSafety && totalDistance == bestTotalDistance) {
                    bestEnemyCandidates.add(candidate);
                }
            }
        }

        if (bestEnemyCandidates.isEmpty()) {
            return null;
        }

        Random random = new Random(seed * 31L + 7);
        Cell enemyCell = bestEnemyCandidates.get(random.nextInt(bestEnemyCandidates.size()));
        int[][] fromEnemy = pathFinder.distancesFrom(map, enemyCell);

        int firstFruitIndex = Math.max(1, solutionDistance / 5);
        int lastFruitIndex = Math.min(
                solutionDistance - 1,
                Math.max(firstFruitIndex, solutionDistance * 2 / 5)
        );
        int safetyMargin = Math.max(4, solutionDistance / 10);

        List<Cell> fruitCandidates = new ArrayList<>();
        for (int index = firstFruitIndex; index <= lastFruitIndex; index++) {
            Cell candidate = solutionPath.get(index);
            int playerDistance = index;
            int enemyDistance = fromEnemy[candidate.row()][candidate.column()];

            if (enemyDistance >= playerDistance + safetyMargin) {
                fruitCandidates.add(candidate);
            }
        }

        if (fruitCandidates.isEmpty()) {
            return null;
        }

        Cell fruitCell = fruitCandidates.get(random.nextInt(fruitCandidates.size()));
        return new Placement(enemyCell, fruitCell);
    }

    private record Placement(Cell enemy, Cell fruit) {
    }
}
