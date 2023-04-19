package ru.nsu.fit.smolyakov.snakegame.model;

import ru.nsu.fit.smolyakov.snakegame.point.Point;
import ru.nsu.fit.smolyakov.snakegame.properties.GameProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

/**
 * Represents a barrier that is located on {@link GameModel}.
 * The snake cannot pass through the barrier.
 */
public record Barrier(Set<Point> barrierPoints) {
    /**
     * Creates a barrier from the specified text file.
     * One should use '*' to represent a barrier point, and '.' to represent an empty point.
     * The amount of rows and columns have to correspond with the game field size,
     * however, this is not a requirement:
     *
     * @param properties properties of the game field
     * @return a barrier; if file wasn't found, returns an empty barrier
     */
    public static Barrier fromResource(GameProperties properties) {
        var file = Objects.requireNonNull(new File(properties.barrierFilePath()));
        Scanner reader;
        try {
            reader = new Scanner(file).useDelimiter("\\A");
        } catch (FileNotFoundException e) {
            return new Barrier(Set.of());
        }

        Set<Point> points = new HashSet<>();
        for (int y = 0; y < properties.height() && reader.hasNextLine(); y++) {
            String line = reader.nextLine();

            var colums = Integer.min(line.length(), properties.width());
            for (int x = 0; x < properties.width(); x++) {
                if (line.charAt(x) == '*') {
                    points.add(new Point(x, y));
                }
            }
        }

        return new Barrier(points);
    }

    /**
     * Checks if the point is located on the barrier.
     *
     * @param point point
     * @return {@code true} if the point is located on the barrier, false otherwise
     */
    public boolean met(Point point) {
        return barrierPoints.contains(point);
    }
}
