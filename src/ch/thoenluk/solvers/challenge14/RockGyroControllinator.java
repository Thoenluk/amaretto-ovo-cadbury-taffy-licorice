package ch.thoenluk.solvers.challenge14;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class RockGyroControllinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);
        return UtMath.restOfTheOwl(IntStream.range(0, lines[0].length())
                .map(i -> calculateWeightFromColumn(i, lines)));
        // I keep this code, not because this challenge wouldn't work with the code for the second challenge
        // (it does, if you apply a single tiltNorth() and then call calculateWeightAsIs())
        // but because it's really clever and completes in 1ms due to not actually doing any tilt. And really, that's
        // software engineering: Being congratulated on how clever you are for not doing stuff.
    }

    private int calculateWeightFromColumn(final int column, final String[] lines) {
        int weight = 0;
        int numberOfRocks = 0;
        int startingWeight = lines.length;
        for (int i = 0; i < lines.length; i++) {
            switch (lines[i].charAt(column)) {
                case 'O' -> numberOfRocks++;
                case '#' -> {
                    weight += calculateWeight(startingWeight, numberOfRocks);
                    numberOfRocks = 0;
                    startingWeight = lines.length - i - 1;
                }
            }
        }
        weight += calculateWeight(startingWeight, numberOfRocks);
        return weight;
    }

    private int calculateWeight(final int startingWeight, final int numberOfRocks) {
        final int rockFreeTiles = startingWeight - numberOfRocks;
        return (startingWeight * (startingWeight + 1)) / 2 - (rockFreeTiles * (rockFreeTiles + 1)) / 2;
    }

    private int calculateWeightAsIs(final int height, final int width, final Map<Position, Character> dish) {
        return IntStream.range(0, width)
                .map(i -> calculateWeightAsIsInColumn(i, height, dish))
                .reduce(UtMath::overflowSafeSum)
                .orElseThrow();
    }

    private int calculateWeightAsIsInColumn(final int column, final int height, final Map<Position, Character> dish) {
        return IntStream.range(0, height)
                .filter(i -> dish.get(new Position(i, column)) == 'O')
                .map(i -> height - i)
                .reduce(UtMath::overflowSafeSum)
                .orElse(0);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final Map<Position, Character> dish = UtParsing.multilineStringToPositionCharacterMap(input);
        final int height = (int) dish.keySet().stream()
                .filter(position -> position.x() == 0)
                .count();
        final int width = (int) dish.keySet().stream()
                .filter(position -> position.y() == 0)
                .count();
        final Map<String, Integer> seenStates = new HashMap<>();
        boolean foundLastIteration = false;

        for (int i = 0; i < 1_000_000_000; i++) {
            shake(dish, height, width);

            if (!foundLastIteration) {
                final String state = String.join("\n", UtParsing.positionCharacterMapToStringArray(dish));
                if (seenStates.containsKey(state)) {
                    // I think we all know where this is going so let's just skip to the end.
                    final int previouslySeenIndex = seenStates.get(state);
                    final int loopLength = i - previouslySeenIndex;
                    final int remainingIterations = 1_000_000_000 - i;
                    final int loops = remainingIterations / loopLength;
                    i += loops * loopLength;
                    foundLastIteration = true;
                }
                else {
                    seenStates.put(state, i);
                }
            }
        }

        return Integer.toString(calculateWeightAsIs(height, width, dish));
    }

    private void shake(final Map<Position, Character> dish, final int height, final int width) {
        tiltNorth(width, dish);
        tiltWest(height, dish);
        tiltSouth(height, width, dish);
        tiltEast(height, width, dish);
    }

    //65
    //        63
    //        68
    //        69
    //        69
    //        65
    //        64

    private void tiltNorth(final int width, final Map<Position, Character> dish) {
        IntStream.range(0, width)
                .mapToObj(i -> new Position(0, i))
                .forEach(position -> tilt(position, Position.DOWN, dish));
    }

    private void tiltWest(final int height, final Map<Position, Character> dish) {
        IntStream.range(0, height)
                .mapToObj(i -> new Position(i, 0))
                .forEach(position -> tilt(position, Position.RIGHT, dish));
    }

    private void tiltSouth(final int height, final int width, final Map<Position, Character> dish) {
        IntStream.range(0, width)
                .mapToObj(i -> new Position(height - 1, i))
                .forEach(position -> tilt(position, Position.UP, dish));
    }

    private void tiltEast(final int height, final int width, final Map<Position, Character> dish) {
        IntStream.range(0, height)
                .mapToObj(i -> new Position(i, width - 1))
                .forEach(position -> tilt(position, Position.LEFT, dish));
    }

    private void tilt(final Position start, final Position direction, final Map<Position, Character> dish) {
        Position current = start;
        Position resting = start;
        int numberOfRocks = 0;
        while (dish.containsKey(current)) {
            switch (dish.get(current)) {
                case 'O' -> {
                    numberOfRocks++;
                    dish.put(current, '.');
                }
                case '#' -> {
                    fillRocks(resting, numberOfRocks, direction, dish);
                    numberOfRocks = 0;
                    resting = current.offsetBy(direction);
                }
            }
            current = current.offsetBy(direction);
        }
        fillRocks(resting, numberOfRocks, direction, dish);
    }

    private void fillRocks(final Position resting, final int numberOfRocks, final Position direction, final Map<Position, Character> dish) {
        Position current = resting;
        for (int i = 0; i < numberOfRocks; i++) {
            dish.put(current, 'O');
            current = current.offsetBy(direction);
        }
    }
}
