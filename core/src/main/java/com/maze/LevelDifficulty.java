package com.maze;

public record LevelDifficulty(int mazeRows, int mazeColumns, float enemyMoveInterval, float fleeDuration) {
    public static final int LEVELS_PER_SIZE = 3;
    public static final int MINIMUM_ROWS = 7;
    public static final int MINIMUM_COLUMNS = 11;
    public static final int MAXIMUM_ROWS = 19;
    public static final int MAXIMUM_COLUMNS = 25;
    public static final float EASY_MOVE_INTERVAL = 0.5f;
    public static final float HARD_MOVE_INTERVAL = 0.2f;
    public static final float EASY_FLEE_DURATION = 10f;
    public static final float HARD_FLEE_DURATION = 2f;


    public static LevelDifficulty calculateDifficulty(int levelNumber, int maximumLevel){
        if(maximumLevel < 2 || levelNumber < 1 || levelNumber > maximumLevel){
            throw new IllegalArgumentException("Invalid level: " + levelNumber);
        }
        int sizeBand = (levelNumber - 1) / LEVELS_PER_SIZE;
        int oddGrowth = (sizeBand / 2) * 2;
        int rows = Math.min(MINIMUM_ROWS + oddGrowth, MAXIMUM_ROWS);
        int columns = Math.min(MINIMUM_COLUMNS + oddGrowth, MAXIMUM_COLUMNS);
        float progress = (levelNumber - 1f)/(maximumLevel - 1f);
        float enemyMoveInterval = lerp(progress, HARD_MOVE_INTERVAL, EASY_MOVE_INTERVAL);
        float fleeDuration = lerp(progress, HARD_FLEE_DURATION, EASY_FLEE_DURATION);
        return new LevelDifficulty(rows, columns, enemyMoveInterval,fleeDuration);
    }
    public static float lerp( float progress, float max, float min){
        return min + (max - min) * progress;
    }

}
