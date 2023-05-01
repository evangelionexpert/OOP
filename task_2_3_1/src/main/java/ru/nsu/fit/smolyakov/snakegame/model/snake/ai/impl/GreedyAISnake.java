package ru.nsu.fit.smolyakov.snakegame.model.snake.ai.impl;

import ru.nsu.fit.smolyakov.snakegame.model.Apple;
import ru.nsu.fit.smolyakov.snakegame.model.GameModel;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

/**
 * This one hunts for an apple, and tries not to die at the same time.
 * Though, he doesn't look ahead more than for a one step, so could easily put
 * himself to a problematic situation.
 */
@SuppressWarnings("unused")
public class GreedyAISnake extends StayinAliveAISnake {
    private final Random rand = new SecureRandom();
    private Apple target = null;

    /**
     * {@inheritDoc}
     */
    public GreedyAISnake(GameModel gameModel) {
        super(gameModel);
    }

    /**
     * Finds the closest apple to the snake's head.
     *
     * @return {@link Optional} of the closest apple,
     * or {@link Optional#empty()} if there are no apples on the field
     */
    protected Optional<Apple> findNewTarget() {
        return getGameField()
            .getApplesSet()
            .stream()
            .min((Apple a, Apple b) -> {
                var aDist = getSnakeBody().getHead().distance(a.point());
                var bDist = getSnakeBody().getHead().distance(b.point());
                return Double.compare(aDist, bDist);
            });
    }

    /**
     * Does such choices that the snake will move towards the closest apple
     * and will not collide with the barrier, other snakes or its own body.
     */
    @Override
    public void thinkAboutTurn() {
        if (target == null || !getGameField().getApplesSet().contains(target)) {
            target = findNewTarget().orElse(null);
        }

        if (target == null) {
            return;
        }

        var xDiff = target.point().x() - getSnakeBody().getHead().x();
        var yDiff = target.point().y() - getSnakeBody().getHead().y();

        if (xDiff > 0 && isNonCollidingTurn(MovingDirection.RIGHT)) {
            setMovingDirection(MovingDirection.RIGHT);
        } else if (xDiff < 0 && isNonCollidingTurn(MovingDirection.LEFT)) {
            setMovingDirection(MovingDirection.LEFT);
        } else if (xDiff > 0 && isNonCollidingTurn(MovingDirection.LEFT)) {
            setMovingDirection(MovingDirection.LEFT);
        } else if (xDiff < 0 && isNonCollidingTurn(MovingDirection.RIGHT)) {
            setMovingDirection(MovingDirection.RIGHT);

        } else if (yDiff > 0 && isNonCollidingTurn(MovingDirection.DOWN)) {
            setMovingDirection(MovingDirection.DOWN);
        } else if (yDiff < 0 && isNonCollidingTurn(MovingDirection.UP)) {
            setMovingDirection(MovingDirection.UP);
        } else if (yDiff > 0 && isNonCollidingTurn(MovingDirection.UP)) {
            setMovingDirection(MovingDirection.UP);
        } else if (yDiff < 0 && isNonCollidingTurn(MovingDirection.DOWN)) {
            setMovingDirection(MovingDirection.DOWN);
        }
    }
}
