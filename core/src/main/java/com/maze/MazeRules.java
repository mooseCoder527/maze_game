package com.maze;

public final class MazeRules {
    private final MazeMap map;

    public MazeRules(MazeMap map) {
        this.map = map;
    }

    public boolean tryMove(GridEntity entity, GridEntity blocker, int rowChange, int columnChange) {
        int next_row = entity.row() + rowChange;
        int next_column = entity.column() + columnChange;

        if (!map.isWalkable(entity.row() + rowChange,entity.column() + columnChange)){
            return false;
        }
        if (blocker.occupies(next_row,next_column)){
            return false;
        }
        entity.moveTo(next_row,next_column);
        return true;
    }
}
