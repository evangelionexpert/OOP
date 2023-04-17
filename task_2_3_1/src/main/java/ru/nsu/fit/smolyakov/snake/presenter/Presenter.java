package ru.nsu.fit.smolyakov.snake.presenter;

import ru.nsu.fit.smolyakov.snake.model.GameField;
import ru.nsu.fit.smolyakov.snake.model.Snake;
import ru.nsu.fit.smolyakov.snake.properties.PresenterProperties;
import ru.nsu.fit.smolyakov.snake.view.View;

public class Presenter {
    private final View view;
    private final PresenterProperties presenterProperties;
    private GameField model;

    private Thread thread;

    public Presenter(View view, GameField model, PresenterProperties properties) {
        this.view = view;
        this.model = model;
        this.presenterProperties = properties;
    }

    private void startTimeOut() throws InterruptedException {
        for (int i = 3; i >= 0; i--) {
            updateView();

            if (i != 0) {
                view.showMessage("Game starts in " + i);
            } else {
                view.showMessage("Go!");
            }
            Thread.sleep(presenterProperties.startTimeoutMillis());
        }
    }

    private void drawFrameAndSleep() {

    }

    public void start() {
        model = model.newGame();
        
        thread = new Thread(() -> {
            try {
                startTimeOut();
            } catch (InterruptedException e) {
                return;
            }

            while (!Thread.currentThread().isInterrupted()) {
                var playerDeath = model.update();

                updateView();

                if (playerDeath) {
                    view.showMessage("You died! You earned " + model.getPlayerSnake().getPoints() + " points.");
                    return;
                }

                try {
                    Thread.sleep(presenterProperties.speed().getFrameDelayMillis());
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
        );
        thread.start();
    }

    public void updateView() {
        view.clear();
        view.drawBarrier(model.getBarrier());
        view.drawAppleSet(model.getApplesSet());
        view.drawPlayerSnake(model.getPlayerSnake());
        view.setScoreAmount(model.getPlayerSnake().getPoints());
    }

    public void onLeftKeyPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.LEFT);
    }

    public void onRightKeyPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.RIGHT);
    }

    public void onUpKeyPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.UP);
    }

    public void onDownKeyPressed() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.DOWN);
    }

    public void onRestartKeyPressed() {
        thread.interrupt();
        start();
    }
    
    public void onExitKeyPressed() {
        thread.interrupt();
        view.close();
    }
}
