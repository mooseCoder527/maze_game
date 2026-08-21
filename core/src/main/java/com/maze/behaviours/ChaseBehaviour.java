package com.maze.behaviours;

import com.maze.Cell;
import com.maze.GridEntity;
import com.maze.MazeMap;
import com.maze.PathFinder;

public class ChaseBehaviour implements EnemyBehaviour {
    private PathFinder pathFinder;

    public ChaseBehaviour(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }
    @Override
    public Cell nextStep(MazeMap map, GridEntity enemy, GridEntity player){
        return pathFinder.nextStep(map, enemy, player);
    }
}
