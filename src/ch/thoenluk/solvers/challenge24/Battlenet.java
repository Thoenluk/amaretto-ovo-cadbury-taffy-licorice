package ch.thoenluk.solvers.challenge24;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

import static ch.thoenluk.ut.Position.NeighbourDirection.CARDINAL_AND_SELF;

public class Battlenet implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);

        final int maxY = lines.length - 1;
        final int maxX = lines[0].length() - 1;

        final Map<Position, Direction> blizzards = createBlizzards(lines);
        return Integer.toString(pathTo(new Position(0, 1), new Position(maxY, maxX - 1), 0, blizzards, maxY, maxX));
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);

        final int maxY = lines.length - 1;
        final int maxX = lines[0].length() - 1;

        final Map<Position, Direction> blizzards = createBlizzards(lines);

        final int there = pathTo(new Position(0, 1), new Position(maxY, maxX - 1), 0, blizzards, maxY, maxX);
        final int back = pathTo(new Position(maxY, maxX - 1), new Position(0, 1), there, blizzards, maxY, maxX);
        final int thereAgain = pathTo(new Position(0, 1), new Position(maxY, maxX - 1), back, blizzards, maxY, maxX);

        return Integer.toString(thereAgain);
    }

    private int pathTo(Position start, Position end, int startingTime, Map<Position, Direction> blizzards, int maxY, int maxX) {
        Set<Position> possibleExpeditionLocations = Set.of(start);
        int time = startingTime;

        while (true) {
            time++;

            final Set<Position> possibleExpeditionLocationsInNextStep = new HashSet<>();

            for (Position possibleExpeditionLocation : possibleExpeditionLocations) {
                for (Position neighbour : possibleExpeditionLocation.getNeighbours(CARDINAL_AND_SELF)) {
                    if (neighbour.equals(end)) {
                        return time;
                    }

                    if (neighbour.y() <= 0
                            || neighbour.y() >= maxY
                            || neighbour.x() <= 0
                            || neighbour.x() >= maxX
                            || possibleExpeditionLocationsInNextStep.contains(neighbour)) {
                        if (!neighbour.equals(possibleExpeditionLocation)) {
                            continue;
                        }
                    }

                    if (!willContainBlizzardAtTime(neighbour, time, blizzards, maxY, maxX)) {
                        possibleExpeditionLocationsInNextStep.add(neighbour);
                    }
                }
            }

            possibleExpeditionLocations = possibleExpeditionLocationsInNextStep;
        }
    }

    private Map<Position, Direction> createBlizzards(String[] lines) {
        final Map<Position, Direction> blizzards = new HashMap<>();

        for (int y = 0; y < lines.length; y++) {
            final String line = lines[y];

            for (int x = 0; x < line.length(); x++) {
                switch (line.charAt(x)) {
                    case '>' -> blizzards.put(new Position(y, x), Direction.RIGHT);
                    case 'v' -> blizzards.put(new Position(y, x), Direction.DOWN);
                    case '<' -> blizzards.put(new Position(y, x), Direction.LEFT);
                    case '^' -> blizzards.put(new Position(y, x), Direction.UP);
                }
            }
        }

        return blizzards;
    }

    private boolean willContainBlizzardAtTime(Position position, int time, Map<Position, Direction> blizzards, int maxY, int maxX) {
        for (int y = 1; y < maxY; y++) {
            final Direction blizzard = blizzards.get(new Position(y, position.x()));

            if (blizzard == Direction.DOWN) {
                final int yAtTime = UtMath.wrap(y + time, maxY - 1);

                if (yAtTime == position.y()) {
                    return true;
                }
            }
            else if (blizzard == Direction.UP) {
                final int yAtTime = UtMath.wrap(y - time, maxY - 1);

                if (yAtTime == position.y()) {
                    return true;
                }
            }
        }

        for (int x = 1; x < maxX; x++) {
            final Direction blizzard = blizzards.get(new Position(position.y(), x));

            if (blizzard == Direction.RIGHT) {
                final int xAtTime = UtMath.wrap(x + time, maxX - 1);

                if (xAtTime == position.x()) {
                    return true;
                }
            }
            else if (blizzard == Direction.LEFT) {
                final int xAtTime = UtMath.wrap(x - time, maxX - 1);

                if (xAtTime == position.x()) {
                    return true;
                }
            }
        }

        return false;
    }

    private enum Direction {
        RIGHT(new Position(0, 1)),
        DOWN(new Position(1, 0)),
        LEFT(new Position(0, -1)),
        UP(new Position(-1, 0));

        private final Position offset;

        Direction(Position offset) {
            this.offset = offset;
        }

        public Position getOffset() {
            return offset;
        }
    }
}
