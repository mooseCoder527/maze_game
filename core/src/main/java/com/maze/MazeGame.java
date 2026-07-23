package com.maze;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static java.lang.Thread.sleep;

public final class MazeGame extends ApplicationAdapter {
    public static final int TILE_SIZE = 48;
    public static final int WORLD_WIDTH = MazeMap.COLUMNS * TILE_SIZE;
    public static final int WORLD_HEIGHT = MazeMap.ROWS * TILE_SIZE;
    private static final String[] LAYOUT_1 = {
            "#############",
            "#.....#.....#",
            "#.###.#.###.#",
            "#...#.#...#.#",
            "###.#.###.#.#",
            "#...#.....#.#",
            "#.#########.#",
            "#...........#",
            "#############"
    };
    private static final String[] LAYOUT_2 = {
            "#############",
            "#.......#####",
            "#.###.#.###.#",
            "#...#.#...#.#",
            "###.#.###.#.#",
            "#...#.......#",
            "#.#########.#",
            "#...........#",
            "#############"
    };
    private static final String[] LAYOUT_3 = {
            "#######",
            "#...#.#",
            "#.#.#.#",
            "#.#...#",
            "#.###.#",
            "#...#.#",
            "#######",
    };
    private int level_counter = 1;
    private MazeMap map;
    private MazeRules rules;
    private GridEntity player;
    private GridEntity enemy;
    private ShapeRenderer renderer;
    private OrthographicCamera camera;
    private Viewport viewport;

    @Override
    public void create() {
        map = new MazeMap(LAYOUT_1, 5,11);
        rules = new MazeRules(map);
        player = new GridEntity(1, 1);
        enemy = new GridEntity(7, 1);

        renderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();
    }

    @Override
    public void render() {
        handleInput();

        Gdx.gl.glClearColor(0.06f, 0.07f, 0.09f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        drawMap();
        drawCell(map.exitRow(), map.exitColumn(), Color.GREEN);
        drawCell(enemy.row(), enemy.column(), Color.RED);
        drawCell(player.row(), player.column(), Color.CYAN);

        renderer.end();

        if (map.isExit(player.row(), player.column())) {
            Gdx.graphics.setTitle("Maze Escape - Escaped");
            if(level_counter == 1){
                switch_level(LAYOUT_2,1,7,1,7,2,1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                level_counter = 2;
            }


            else if(level_counter == 2){
                switch_level(LAYOUT_3,1,5,3,5,5,4);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                level_counter = 3;
            }
            else if(level_counter == 3){
                Gdx.graphics.setTitle("Congratulations! You Won!");
            }
        } else {
            Gdx.graphics.setTitle("Maze Escape - Level " + level_counter);
        }
    }


    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)
            || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            rules.tryMove(player, enemy, -1, 0);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.S)
            || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            rules.tryMove(player, enemy, 1, 0);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.A)
            || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            rules.tryMove(player, enemy, 0, -1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)
            || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            rules.tryMove(player, enemy, 0, 1);
        }
    }

    private void drawMap() {
        for (int row = 0; row < MazeMap.ROWS; row++) {
            for (int column = 0; column < MazeMap.COLUMNS; column++) {
                Color color = map.isWall(row, column)
                    ? Color.DARK_GRAY
                    : Color.valueOf("20242b");

                drawCell(row, column, color);
            }
        }
    }

    private void drawCell(int row, int column, Color color) {
        float x = column * TILE_SIZE;
        float y = (MazeMap.ROWS - 1 - row) * TILE_SIZE;

        renderer.setColor(color);
        renderer.rect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
    }

    public void switch_level(String[] LAYOUT, int exit_row, int exit_column,int player_x,int player_y,int enemy_x, int enemy_y){
        map = new MazeMap(LAYOUT, exit_row,exit_column);
        rules = new MazeRules(map);
        player = new GridEntity(player_y, player_x);
        enemy = new GridEntity(enemy_y, enemy_x);

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
