package com.maze.behaviours;

import com.maze.Cell;
import com.maze.GridEntity;
import com.maze.MazeMap;

public interface EnemyBehaviour {
    Cell nextStep(MazeMap map, GridEntity enemy, GridEntity Player);
}
