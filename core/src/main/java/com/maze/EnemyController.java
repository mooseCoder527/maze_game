package com.maze;

import com.maze.behaviours.ChaseBehaviour;
import com.maze.behaviours.FleeBehaviour;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EnemyController {
    private final float interval;
    private final MazeMap map;
    private float elapsedTime;
    private final FleeBehaviour chaseBehaviour;

    public EnemyController(float interval, MazeMap map, PathFinder pathFinder) {
        if(interval < 0){
            throw new IllegalArgumentException("interval is less than 0!");
        }
        this.interval = interval;
        this.map = map;
        this.chaseBehaviour = new FleeBehaviour( pathFinder );
    }
    public boolean update(
            float delta,
            GridEntity enemy,
            GridEntity player
    ){
        elapsedTime += delta;
        if(elapsedTime < interval){
            return false;
        }
        elapsedTime -= interval;

        Cell next_cell = chaseBehaviour.nextStep(map,enemy,player);


        if (next_cell == null){
            return false;
        }

        enemy.moveTo(next_cell.row(), next_cell.column());
        return true;
    }
}
