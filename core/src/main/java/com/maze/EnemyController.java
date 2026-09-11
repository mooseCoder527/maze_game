package com.maze;

import com.maze.behaviours.ChaseBehaviour;
import com.maze.behaviours.EnemyBehaviour;
import com.maze.behaviours.FleeBehaviour;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EnemyController {
    private final float interval;
    private final MazeMap map;
    private float elapsedTime;
    private EnemyBehaviour enemyBehaviour;

    public void setEnemyBehaviour(EnemyBehaviour enemyBehaviour) {
        this.enemyBehaviour = enemyBehaviour;
    }

    public EnemyController(float interval, MazeMap map) {
        if(interval < 0){
            throw new IllegalArgumentException("interval is less than 0!");
        }
        this.interval = interval;
        this.map = map;
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

        Cell next_cell = enemyBehaviour.nextStep(map,enemy,player);


        if (next_cell == null){
            return false;
        }

        enemy.moveTo(next_cell.row(), next_cell.column());
        return true;
    }
}
