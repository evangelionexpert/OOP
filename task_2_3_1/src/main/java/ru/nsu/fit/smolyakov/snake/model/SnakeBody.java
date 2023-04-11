package ru.nsu.fit.smolyakov.snake.model;

import java.util.LinkedList;
import java.util.List;

public class SnakeBody {
    private final Point head;
    private final List<Point> tail = new LinkedList<>();

    protected SnakeBody(Point head, List<Point> tail) {
        this.head = head;
        this.tail.addAll(tail);
    }

    public static class Factory {
        private final GameField gameField;

        public Factory(GameField gameField) {
            this.gameField = gameField;
        }

        public SnakeBody generateRandom(int iterations) {
            for (int i = 0; i < iterations; i++) {
                var initialHeadLocation = Point.random(gameField.getWidth(), gameField.getHeight());
                var initialTailLocation = initialHeadLocation.move(new Point(0, -1)); // чучуть вниз

                if (gameField.isFree(initialHeadLocation) && gameField.isFree(initialTailLocation)) {
                    return new SnakeBody(initialHeadLocation, List.of(initialTailLocation));
                }
            }

            throw new IllegalStateException("Cannot create a snake " +
                "(maybe the field is too busy, " +
                "try to increase the amount of iterations, " +
                "increase the field size or remove some barriers)");
        }
    }

    public boolean headCollision(Point point) {
        return head.equals(point);
    }

    public boolean tailCollision(Point point) {
        return tail.contains(point);
    }

    public boolean cutTail(Point point) {
        // TODO сделать?????????????????????????????????????????????????????????????????????????????????????????????
        return false;
    }
}
