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
import com.maze.behaviours.ChaseBehaviour;
import com.maze.behaviours.EnemyBehaviour;
import com.maze.behaviours.FleeBehaviour;
import com.maze.states.EnemyState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.maze.EnemyController.*;
import static java.lang.Thread.sleep;

public final class MazeGame extends ApplicationAdapter {
    public static final int TILE_SIZE = 48;
    public static final float LEVEL_TRANSITION_DELAY = 1.0f;
    public static int LEVEL_COUNT = 750;
    public static final int INITIAL_WINDOW_WIDTH = 25 * TILE_SIZE;
    public static final int INITIAL_WINDOW_HEIGHT = 20 * TILE_SIZE;
    private int levelNumber = 1;
    private MazeMap map;
    private MazeRules rules;
    private GridEntity player;
    private GridEntity enemy;
    private EnemyState enemyState;
    private float enemyStateElapsedTime = 0f;
    private ShapeRenderer renderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private GameState gameState;
    private final MazeGenerator mazeGenerator = new MazeGenerator();
    private LevelDifficulty levelDifficulty;
    private EnemyController enemyController;
    private FleeBehaviour fleeBehaviour;
    private ChaseBehaviour chaseBehaviour;
    private final PathFinder pathFinder = new PathFinder();
    private Cell fruit;
    private Color enemyColor = Color.RED;
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
            updateFruit(delta);
            if(enemy != null){
                enemyController.update(delta, enemy, player);
            }

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
            rules.tryMove(player, null, -1, 0);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.S)
            || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            rules.tryMove(player, null, 1, 0);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.A)
            || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            rules.tryMove(player, null, 0, -1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)
            || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            rules.tryMove(player, null, 0, 1);
        }
    }


    public void loadLevel(int level){
        if(level < 1|| level > LEVEL_COUNT){
            throw new IllegalArgumentException("Level number is invalid! level : " + level);
        }
        levelNumber = level;
        levelDifficulty = LevelDifficulty.calculateDifficulty(level, LEVEL_COUNT);
        createLevel(level);
        gameState = GameState.PLAYING;
        elapsedTime = 0;
        configureViewport();
    }


    private void createLevel(
            int level
    ) {
        String[] layout = mazeGenerator.generateLevel(levelDifficulty.mazeRows(),levelDifficulty.mazeColumns(),level);
        Cell playerCell = new Cell(2,2);
        MazeMap generatedMap = new MazeMap(layout, playerCell.row(), playerCell.column());
        Cell exitCell = pathFinder.furthestCell(generatedMap, playerCell);

        player = new GridEntity(playerCell.row(), playerCell.column());
        GridEntity exitEntity = new GridEntity(exitCell.row(), exitCell.column());
        Cell enemyCell = pathFinder.nextStep(generatedMap, exitEntity, player);
        enemy = new GridEntity(enemyCell.row(), enemyCell.column());
        fleeBehaviour = new FleeBehaviour(pathFinder);
        chaseBehaviour = new ChaseBehaviour(pathFinder);
        map = new MazeMap(layout, exitCell.row(), exitCell.column());
        enemyController = new EnemyController(levelDifficulty.enemyMoveInterval(),map);
        rules = new MazeRules(map);
        fruit = placeFruit(level,playerCell,enemyCell,exitCell);
        updateEnemyState(EnemyState.CHASING);
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
        if (enemy != null && enemy.occupies(player.row(), player.column())) {
            elapsedTime = 0;
            if(enemyState == EnemyState.CHASING){
                gameState = GameState.CAUGHT;
                return;
            }
            else{
                enemy = null;
                return;
            }

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

    private void updateFruit(float delta){
        if (fruit == null){
            if(enemyStateElapsedTime <= levelDifficulty.fleeDuration()){
                enemyStateElapsedTime += delta;
            }
            else{
                updateEnemyState(EnemyState.CHASING);
            }
            return;
        }
        if (player.occupies(fruit.row(),fruit.column())){
            fruit = null;
            updateEnemyState(EnemyState.FLEEING);
        }
    }

    private Cell placeFruit(int level,Cell player, Cell enemy, Cell exit){
        List<Cell> candidates =
                new ArrayList<>();

        for (
                int row = 0;
                row < map.rows();
                row++
        ) {
            for (
                    int column = 0;
                    column < map.columns();
                    column++
            ) {
                if (!map.isWalkable(
                        row,
                        column
                )) {
                    continue;
                }

                Cell candidate =
                        new Cell(
                                row,
                                column
                        );

                if (
                        candidate.equals(player)
                                || candidate.equals(enemy)
                                || candidate.equals(exit)
                ) {
                    continue;
                }

                candidates.add(candidate);
            }
        }

        Random random =
                new Random(
                        level * 17L
                );

        return candidates.get(
                random.nextInt(
                        candidates.size()
                )
        );
    }

    private void updateEnemyState(EnemyState state){
       enemyStateElapsedTime = 0f;
        if (state == EnemyState.FLEEING){
            enemyController.setEnemyBehaviour(fleeBehaviour);
            enemyColor = Color.NAVY;
        }
        if(state == EnemyState.CHASING){
            enemyController.setEnemyBehaviour(chaseBehaviour);
            enemyColor = Color.RED;
        }
        enemyState = state;

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

        if (fruit != null){
            drawCell(fruit.row(), fruit.column(), Color.MAGENTA);
        }

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
        if( enemy != null){
            drawCell(
                    enemy.row(),
                    enemy.column(),
                    enemyColor
            );
        }



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
