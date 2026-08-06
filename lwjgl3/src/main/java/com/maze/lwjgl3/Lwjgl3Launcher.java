package com.maze.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.maze.MazeGame;

public final class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration =
            new Lwjgl3ApplicationConfiguration();

        configuration.setTitle("Maze Escape");
        configuration.setWindowedMode(
            MazeGame.INITIAL_WINDOW_WIDTH,
            MazeGame.INITIAL_WINDOW_HEIGHT
        );
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);

        new Lwjgl3Application(new MazeGame(), configuration);
    }
}
