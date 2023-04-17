package ru.nsu.fit.smolyakov.snake.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.nsu.fit.smolyakov.snake.model.Apple;
import ru.nsu.fit.smolyakov.snake.model.Barrier;
import ru.nsu.fit.smolyakov.snake.model.Point;
import ru.nsu.fit.smolyakov.snake.model.Snake;
import ru.nsu.fit.smolyakov.snake.presenter.Presenter;
import ru.nsu.fit.smolyakov.snake.properties.GameFieldProperties;
import ru.nsu.fit.smolyakov.snake.properties.JavaFxProperties;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

public class JavaFxView implements View, Initializable {
    private Presenter presenter;

    private JavaFxProperties javaFxProperties;
    private int proportion;

    @FXML
    private Canvas canvas;

    @FXML
    private Text scoreAmountText;

    // resources
    // TODO вынести отдельно и доделать других змеек
    private class Resources {
        Image apple;
        Image barrier;
        Image playerSnakeHead;
        Image playerSnakeTail;
        Image enemySnakeHead;
        Image enemySnakeTail;
    }

    private Resources resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private Image imageInstance(String path) {
        return new Image(
            Objects.requireNonNull(getClass().getResourceAsStream(path)),
            proportion,
            proportion,
            true,
            true
        );
    }

    public void createField(GameFieldProperties properties,
                            JavaFxProperties javaFxProperties,
                            Presenter presenter) {
        if (presenter == null) {
            throw new IllegalArgumentException("presenter is null");
        } else if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        } else if (javaFxProperties == null) {
            throw new IllegalArgumentException("javaFxProperties is null");
        }

        if (javaFxProperties.resX() % (2 * properties.width()) != 0
            || javaFxProperties.resY() % (2 * properties.height()) != 0) {
            throw new IllegalArgumentException("Resolution is not divisible by game field size");
        }

        if (javaFxProperties.resX() / properties.width() !=
            javaFxProperties.resY() / properties.height()) {
            throw new IllegalArgumentException("Resolution is not proportional to game field size");
        }

        this.presenter = presenter;
        this.javaFxProperties = javaFxProperties;
        this.proportion = javaFxProperties.resX() / properties.width();

        this.canvas.getScene().setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP -> presenter.onUpKeyPressed();
                case DOWN -> presenter.onDownKeyPressed();
                case LEFT -> presenter.onLeftKeyPressed();
                case RIGHT -> presenter.onRightKeyPressed();
                case Q -> presenter.onExitKeyPressed();
                case R -> presenter.onRestartKeyPressed();
            }
        });

        this.canvas.getScene().getWindow().setOnCloseRequest(e ->
            presenter.onExitKeyPressed()
        );

        canvas.setWidth(javaFxProperties.resX());
        canvas.setHeight(javaFxProperties.resY());

        // TODO вынести в отдельный класс
        // TODO другие змейки и барьер
        resources = new Resources();
        resources.apple = imageInstance("/sprites/apple.png");
        resources.barrier = imageInstance("/sprites/barrier.png");
        resources.playerSnakeHead = imageInstance("/sprites/player/head.png");
        resources.playerSnakeTail = imageInstance("/sprites/player/tail.png");
        resources.enemySnakeHead = imageInstance("/sprites/enemy/head.png");
        resources.enemySnakeTail = imageInstance("/sprites/enemy/tail.png");
    }

    private void drawFigure(Point point, Image image) {
        var graphicsContext = canvas.getGraphicsContext2D(); // TODO надо ли хранить в поле или получать каждый раз???

        graphicsContext.drawImage(
            image,
            point.x() * proportion,
            point.y() * proportion,
            proportion,
            proportion
        );
    }

    public void setScoreAmount(int scoreAmount) {
        this.scoreAmountText.setText(String.valueOf(scoreAmount));
    }

    public void drawAppleSet(Set<Apple> appleSet) {
        appleSet.forEach(apple -> drawFigure(apple.point(), resources.apple));
    }

    public void drawBarrier(Barrier barrier) {
        barrier.barrierPoints().forEach(point -> drawFigure(point, resources.barrier));
    }

    public void drawPlayerSnake(Snake snake) {
        drawFigure(snake.getSnakeBody().getHead(), resources.playerSnakeHead); // TODO сделать поворот
        snake.getSnakeBody().getTail().forEach(point -> drawFigure(point, resources.playerSnakeTail));
    }

    public void clear() {
        canvas.getGraphicsContext2D().clearRect(0, 0, javaFxProperties.resX(), javaFxProperties.resY());
    }

    public void showMessage(String message) {
        canvas.getGraphicsContext2D().strokeText(message, javaFxProperties.resX() / 3, javaFxProperties.resY() / 2);
        // TODO сделать нормально в ссене билдере
    }

    public void close() {
        ((Stage) canvas.getScene().getWindow()).close();
    }
}
