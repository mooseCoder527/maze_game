package com.maze.behaviours;

import com.maze.Cell;
import com.maze.GridEntity;
import com.maze.MazeMap;
import com.maze.PathFinder;

import java.util.List;

public class FleeBehaviour implements EnemyBehaviour {
    private PathFinder pathFinder;
    public int distance( Cell cell1, GridEntity player){
        return Math.abs((cell1.row() - player.row()) +(cell1.column() + player.column()));
    }

    public FleeBehaviour(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }
    @Override
    public Cell nextStep(
            MazeMap map,
            GridEntity enemy,
            GridEntity player
    ) {
        Cell enemyCell =
                new Cell(
                        enemy.row(),
                        enemy.column()
                );

        Cell playerCell =
                new Cell(
                        player.row(),
                        player.column()
                );

        Cell bestCell = pathFinder.furthestCell(map,playerCell);
        GridEntity bestEntity = new GridEntity(bestCell.row(), bestCell.column());
        return pathFinder.nextStep(map,enemy,bestEntity);
    }
}
