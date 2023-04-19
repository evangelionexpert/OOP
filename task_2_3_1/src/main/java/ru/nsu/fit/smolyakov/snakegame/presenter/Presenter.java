package ru.nsu.fit.smolyakov.snakegame.presenter;

import ru.nsu.fit.smolyakov.snakegame.model.GameField;
import ru.nsu.fit.smolyakov.snakegame.model.snake.Snake;
import ru.nsu.fit.smolyakov.snakegame.properties.PresenterProperties;
import ru.nsu.fit.smolyakov.snakegame.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * A presenter that connects a model and a view.
 *
 * <p>{@link #start()} method starts a new thread with the game's main loop.
 * {@link #onExitPressed()} method stops both this presenter and an attached view.
 * {@link #onRestartPressed()} method restarts the game.
 * {@link #onLeftPressed()}, {@link #onRightPressed()}, {@link #onUpPressed()} and
 * {@link #onDownPressed()} change the direction of the player snake.
 *
 * <p>One works correctly with both JavaFX and console views.
 */
public class Presenter {
    private final View view;
    private final PresenterProperties presenterProperties;
    private GameField model;

    private ScheduledExecutorService executorService;
    private List<Future<?>> futureList = new ArrayList<>();

    /**
     * An enum that represents user-associated actions that can be performed by the view.
     * Each action is connected to an event method of the presenter.
     */
    public enum EventAction {
        /**
         * A left key is pressed.
         */
        LEFT(Presenter::onLeftPressed),

        /**
         * A right key is pressed.
         */
        RIGHT(Presenter::onRightPressed),

        /**
         * An up key is pressed.
         */
        UP(Presenter::onUpPressed),

        /**
         * A down key is pressed.
         */
        DOWN(Presenter::onDownPressed),

        /**
         * A restart button is pressed.
         */
        RESTART(Presenter::onRestartPressed),

        /**
         * An exit button is pressed.
         */
        EXIT(Presenter::onExitPressed);

        private final Consumer<Presenter> action;

        EventAction(Consumer<Presenter> action) {
            this.action = action;
        }

        /**
         * Executes the {@link Consumer} that is connected to the action.
         *
         * @param presenter a presenter
         */
        public void execute(Presenter presenter) {
            action.accept(presenter);
        }
    }

    /**
     * Creates a presenter with the specified view, model and properties.
     *
     * @param view a view
     * @param model a model
     * @param properties properties of the presenter
     */
    public Presenter(View view, GameField model, PresenterProperties properties) {
        this.view = view;
        this.model = model;
        this.presenterProperties = properties;
    }

    private void startTimeOut() throws InterruptedException {
        for (int i = 3; i >= 0; i--) {
            showFrame();

            if (i > 0) {
                view.showMessage("Game starts in " + i);
                view.refresh();
            } else {
                view.showMessage("Go!");
                view.refresh();
            }
            Thread.sleep(presenterProperties.startTimeoutMillis());
        }
    }

    private void newFrame() { // TODO renaamee
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
        showFrame();

        if (!playerAlive) {
            view.showMessage("You died! You earned " + model.getPlayerSnake().getPoints() + " points.");
            view.refresh();
            executorService.shutdownNow(); // TODO хочется чтобы боты работали и после смерти, надо добавить флаг внутрь змейки
        }

        futureList.clear();
        model.getAISnakeList().forEach(aiSnake -> futureList.add(executorService.submit(aiSnake::thinkAboutTurn)));
    }

    /**
     * Starts a new game.
     */
    public void start() {
        model = model.newGame();

        // TODO вынести в отдельный метод?
        executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        executorService.submit(() -> {
            try {
                startTimeOut();
            } catch (InterruptedException e) {
                return;
            }
        });

        executorService.scheduleAtFixedRate(this::newFrame,
            (long) presenterProperties.startTimeoutMillis() * 4,
            (long) presenterProperties.speed().getFrameDelayMillis(),
            TimeUnit.MILLISECONDS);
    }


    private void showFrame() {
        view.clear();

        view.drawBarrier(model.getBarrier());
        model.getApplesSet().forEach(view::drawApple);
        view.drawPlayerSnake(model.getPlayerSnake());
        model.getAISnakeList().forEach(view::drawEnemySnake);
        view.setScoreAmount(model.getPlayerSnake().getPoints());

        view.refresh();
    }

    /**
     * Changes the direction of the player snake to left.
     */
    public void onLeftPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.LEFT);
    }

    /**
     * Changes the direction of the player snake to right.
     */
    public void onRightPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.RIGHT);
    }

    /**
     * Changes the direction of the player snake to up.
     */
    public void onUpPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.UP);
    }

    /**
     * Changes the direction of the player snake to down.
     */
    public void onDownPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.DOWN);
    }

    /**
     * Restarts the game.
     */
    public void onRestartPressed() {
        executorService.shutdownNow();
        start();
    }

    /**
     * Stops the game.
     */
    public void onExitPressed() {
        executorService.shutdownNow();
        view.close();
    }
}
