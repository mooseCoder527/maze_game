package com.maze;

public class EnemyController {
    private final float interval;
    private final MazeMap map;
    private final PathFinder pathFinder;
    private float elapsedTime;

    public EnemyController(float interval, MazeMap map, PathFinder pathFinder) {
        if(interval < 0){
            throw new IllegalArgumentException("interval is less than 0!");
        }
        this.interval = interval;
        this.map = map;
        this.pathFinder = pathFinder;
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
        Cell next_cell = pathFinder.nextStep(map, enemy, player);
        if (next_cell == null){
            return false;
        }

        enemy.moveTo(next_cell.row(), next_cell.column());
        return true;
    }
}
