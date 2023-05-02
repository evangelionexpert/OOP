package ru.nsu.fit.smolyakov.snakegame.integrationtests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.nsu.fit.smolyakov.snakegame.ExampleProperties;
import ru.nsu.fit.smolyakov.snakegame.model.GameModel;
import ru.nsu.fit.smolyakov.snakegame.model.GameModelImpl;
import ru.nsu.fit.smolyakov.snakegame.model.snake.Snake;
import ru.nsu.fit.smolyakov.snakegame.presenter.SnakePresenter;
import ru.nsu.fit.smolyakov.snakegame.properties.GameProperties;
import ru.nsu.fit.smolyakov.snakegame.properties.GameSpeed;
import ru.nsu.fit.smolyakov.snakegame.utils.Point;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ModelPresenterTest {
    TestingSnakePresenter presenter;
    GameModel model;
    GameProperties properties = ExampleProperties.properties;

    @BeforeEach
    void init() {
        presenter = new TestingSnakePresenter();
        model = new GameModelImpl(properties);

        presenter.setProperties(properties);
        presenter.setModel(model);
    }

    @Test
    public void barrierInitializationTest() {
        assertThat(model.getBarrier().barrierPoints())
            .isEqualTo(
                Set.of(
                    new Point(0, 0),
                    new Point(0, 1),
                    new Point(0, 2),
                    new Point(0, 3),
                    new Point(0, 4),
                    new Point(0, 5),

                    new Point(1, 0),
                    new Point(2, 0),
                    new Point(3, 0),
                    new Point(4, 0),
                    new Point(5, 0)
                )
            );
    }

    private Point head() {
        return model.getPlayerSnake().getSnakeBody().getHead();
    }

    @Test
    public void presenterLittleTest() {
        assertThat(presenter.frameUpdaterRunning).isFalse();
        presenter.start();

        assertThat(presenter.frameUpdaterRunning).isTrue();
        assertThat(presenter.scoreAmount).isEqualTo(0);
    }

    @Test
    @Timeout(value = 2)
    public void modelUpDeathTest() {
        boolean alive = model.update();
        while (alive) {
            alive = model.update();
        }

        assertThat(head().y()).isZero();
    }

    @Test
    @Timeout(value = 2)
    public void modelLeftDeathTest() {
        model.getPlayerSnake().setMovingDirection(Snake.MovingDirection.LEFT);

        boolean alive = model.update();
        while (alive) {
            alive = model.update();
        }

        assertThat(head().x()).isZero();
    }
}
