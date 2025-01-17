package ru.nsu.fit.smolyakov.snakegame.presenter;

import ru.nsu.fit.smolyakov.snakegame.model.Apple;
import ru.nsu.fit.smolyakov.snakegame.model.Barrier;
import ru.nsu.fit.smolyakov.snakegame.model.GameModel;
import ru.nsu.fit.smolyakov.snakegame.model.snake.Snake;
import ru.nsu.fit.smolyakov.snakegame.properties.GameProperties;
import ru.nsu.fit.smolyakov.snakegame.properties.GameSpeed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

/**
 * A presenter that connects a model and a view.
 *
 * <p>{@link #start()} method starts a new thread with the game's main loop.
 */
public abstract class SnakePresenter {
    /**
     * A time to sleep between the countdown frame updates.
     */
    protected final static int START_SLEEP_TIME_MILLIS = 1000;
    private final List<Future<?>> futureList = new ArrayList<>();

    private GameProperties properties;

    private GameModel model;
    private ScheduledExecutorService executorService;

    /**
     * Returns the game properties.
     *
     * @return the game properties
     */
    public GameProperties getProperties() {
        return properties;
    }

    /**
     * Sets game properties.
     *
     * @param properties the game properties
     */
    public void setProperties(GameProperties properties) {
        this.properties = properties;
    }

    /**
     * Sets a model to work with.
     *
     * @param model the model to work with
     */
    public void setModel(GameModel model) {
        this.model = model;
    }

    /**
     * Draws a frame.
     */
    protected void update() {
        for (Future<?> future : futureList) {
            try {
                future.get();
            } catch (InterruptedException e) {
                return;
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        var playerAlive = model.update();
        drawFrame();

        if (!playerAlive) {
            this.showMessage("You died! You earned " + model.getPlayerSnake().getPoints() + " points.");
            this.refresh();
            executorService.shutdownNow();
            stopFramesUpdater();
            return;
        }

        futureList.clear();
        model.getAISnakeList().forEach(aiSnake -> futureList.add(executorService.submit(aiSnake::thinkAboutTurn)));
    }

    /**
     * Starts a new game.
     */
    public void start() {
        model = model.newGame();
        executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        runFramesUpdater();
    }

    /**
     * Runs the timer which updates the game state
     * and the view once per {@link GameSpeed#getFrameDelayMillis()} milliseconds,
     * specified in #link {@link #properties}.
     */
    protected abstract void runFramesUpdater();

    /**
     * Stops the timer which was started by {@link #runFramesUpdater()}.
     */
    protected abstract void stopFramesUpdater();

    /**
     * Draws a single frame.
     */
    protected void drawFrame() {
        this.clear();

        this.drawBarrier(model.getBarrier());
        model.getApplesSet().forEach(this::drawApple);
        this.drawPlayerSnake(model.getPlayerSnake());
        model.getAISnakeList().forEach(this::drawEnemySnake);
        this.setScoreAmount(model.getPlayerSnake().getPoints());

        this.refresh();
    }

    /**
     * Changes the direction of the player snake to left.
     */
    private void onLeftPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.LEFT);
    }

    /**
     * Changes the direction of the player snake to right.
     */
    private void onRightPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.RIGHT);
    }

    /**
     * Changes the direction of the player snake to up.
     */
    private void onUpPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.UP);
    }

    /**
     * Changes the direction of the player snake to down.
     */
    private void onDownPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.DOWN);
    }

    /**
     * Restarts the game.
     */
    private void onRestartPressed() {
        executorService.shutdownNow();
        stopFramesUpdater();
        start();
    }

    /**
     * Stops the game.
     */
    private void onExitPressed() {
        executorService.shutdownNow();
        stopFramesUpdater();
        this.close();
    }

    /**
     * Sets the current amount of points the player
     * has on an attached scoreboard.
     *
     * @param scoreAmount the amount of points
     */
    protected abstract void setScoreAmount(int scoreAmount);

    /**
     * Draws an apple on the game field.
     *
     * @param apple the apple to draw
     */
    protected abstract void drawApple(Apple apple);

    /**
     * Draws a barrier on the game field.
     *
     * @param barrier the barrier to draw
     */
    protected abstract void drawBarrier(Barrier barrier);

    /**
     * Draws a snake on the game field.
     *
     * @param snake the snake to draw
     */
    protected abstract void drawPlayerSnake(Snake snake);

    /**
     * Draws a snake on the game field.
     *
     * @param snake the snake to draw
     */
    protected abstract void drawEnemySnake(Snake snake);

    /**
     * Shows a message on the attached information panel.
     *
     * @param message the message to show
     */
    protected abstract void showMessage(String message);

    /**
     * Clears the game field.
     */
    protected abstract void clear();

    /**
     * Safely closes all running view components.
     */
    protected abstract void close();

    /**
     * Applies changes.
     */
    protected abstract void refresh();

    /**
     * An enum that represents user-associated actions that can be performed by the view.
     * Each action is connected to an event method of the presenter.
     */
    public enum EventAction {
        /**
         * A left key is pressed.
         */
        LEFT(SnakePresenter::onLeftPressed),

        /**
         * A right key is pressed.
         */
        RIGHT(SnakePresenter::onRightPressed),

        /**
         * An up key is pressed.
         */
        UP(SnakePresenter::onUpPressed),

        /**
         * A down key is pressed.
         */
        DOWN(SnakePresenter::onDownPressed),

        /**
         * A restart button is pressed.
         */
        RESTART(SnakePresenter::onRestartPressed),

        /**
         * An exit button is pressed.
         */
        EXIT(SnakePresenter::onExitPressed);

        private final Consumer<SnakePresenter> action;

        EventAction(Consumer<SnakePresenter> action) {
            this.action = action;
        }

        /**
         * Executes the {@link Consumer} that is connected to the action.
         *
         * @param snakePresenter a snakePresenter
         */
        public void execute(SnakePresenter snakePresenter) {
            action.accept(snakePresenter);
        }
    }
}
