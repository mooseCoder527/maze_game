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

    public static final float ENEMY_MOVE_INTERVAL = 0.5f;
    public static final float LEVEL_TRANSITION_DELAY = 1.0f;

    public static int LEVEL_COUNT = 3;

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

    public static final int INITIAL_WINDOW_WIDTH = LAYOUT_1[0].length() * TILE_SIZE;
    public static final int INITIAL_WINDOW_HEIGHT = LAYOUT_1.length * TILE_SIZE;
    private int levelNumber = 1;
    private MazeMap map;
    private MazeRules rules;
    private GridEntity player;
    private GridEntity enemy;
    private ShapeRenderer renderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private GameState gameState;
    private EnemyController enemyController;
    private float elapsedTime;

    @Override
    public void create() {
        renderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        loadLevel(1);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if(gameState == GameState.PLAYING){
            handleInput();
            enemyController.update(delta, enemy, player);
            updateGameState();
        }
        else{
            updateFinishedState(delta);
        }
        renderGame();
        updateWindowTitle();
    }

    private void updateWindowTitle() {
        String title = switch(gameState){
            case GameState.PLAYING -> "Maze Escape - level: " + levelNumber;
            case GameState.CAUGHT -> "Caught! press r to restart.";
            case GameState.ESCAPED -> "You Escaped Level " + levelNumber;
        };
        Gdx.graphics.setTitle(title);
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

    public void loadLevel(int level){
        levelNumber = level;
        switch(levelNumber){
            case 1 -> createLevel(LAYOUT_1, 5, 11, 7, 1, 1, 1);
            case 2 -> createLevel(LAYOUT_2,1,7,7,1,1,2);
            case 3 -> createLevel(LAYOUT_3,1,5,5,3,4,5);
            default -> throw new IllegalStateException("unknown level");
        }
        gameState = GameState.PLAYING;
        elapsedTime = 0;
        configureViewport();

    }

    private void createLevel(
            String[] layout,
            int exitRow,
            int exitColumn,
            int playerRow,
            int playerColumn,
            int enemyRow,
            int enemyColumn
    ) {
        map = new MazeMap(
                layout,
                exitRow,
                exitColumn
        );


        rules = new MazeRules(map);


        player = new GridEntity(
                playerRow,
                playerColumn
        );


        enemy = new GridEntity(
                enemyRow,
                enemyColumn
        );


        enemyController = new EnemyController(
                ENEMY_MOVE_INTERVAL,
                map,
                new PathFinder()
        );
    }


    private void configureViewport() {
        float worldWidth =
                map.columns() * TILE_SIZE;


        float worldHeight =
                map.rows() * TILE_SIZE;


        viewport = new FitViewport(
                worldWidth,
                worldHeight,
                camera
        );


        viewport.update(
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight(),
                true
        );


        camera.position.set(
                worldWidth / 2f,
                worldHeight / 2f,
                0
        );


        camera.update();
    }
    private void drawMap() {
        for (int row = 0; row < map.rows(); row++) {
            for (int column = 0; column < map.columns(); column++) {
                Color color = map.isWall(row, column)
                    ? Color.DARK_GRAY
                    : Color.valueOf("20242b");

                drawCell(row, column, color);
            }
        }
    }

    private void drawCell(int row, int column, Color color) {
        float x = column * TILE_SIZE;
        float y = (map.rows() - 1 - row) * TILE_SIZE;

        renderer.setColor(color);
        renderer.rect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
    }


    private void updateGameState() {
        if (
                enemy.occupies(
                        player.row(),
                        player.column()
                )
        ) {
            gameState = GameState.CAUGHT;
            elapsedTime = 0;
            return;
        }


        if (
                map.isExit(
                        player.row(),
                        player.column()
                )
        ) {
            gameState = GameState.ESCAPED;
            elapsedTime = 0;
        }
    }


    private void updateFinishedState(float delta) {
        if (
                Gdx.input.isKeyJustPressed(Input.Keys.R)
        ) {
            loadLevel(levelNumber);
            return;
        }


        if (
                gameState == GameState.ESCAPED
                        && levelNumber < LEVEL_COUNT
        ) {
            elapsedTime += delta;
            System.out.println(elapsedTime);

            if (
                    elapsedTime
                            >= LEVEL_TRANSITION_DELAY
            ) {
                loadLevel(levelNumber + 1);
            }
        }
    }


    private void renderGame() {
        Gdx.gl.glClearColor(
                0.06f,
                0.07f,
                0.09f,
                1f
        );


        Gdx.gl.glClear(
                GL20.GL_COLOR_BUFFER_BIT
        );


        renderer.setProjectionMatrix(
                camera.combined
        );


        renderer.begin(
                ShapeRenderer.ShapeType.Filled
        );


        drawMap();


        drawCell(
                map.exitRow(),
                map.exitColumn(),
                Color.GREEN
        );


        drawCell(
                player.row(),
                player.column(),
                Color.CYAN
        );


        /*
         * Draw the enemy after the player.
         *
         * When both occupy the same cell, the enemy remains visible
         * and clearly shows that the player was caught.
         */
        drawCell(
                enemy.row(),
                enemy.column(),
                Color.RED
        );


        renderer.end();
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
