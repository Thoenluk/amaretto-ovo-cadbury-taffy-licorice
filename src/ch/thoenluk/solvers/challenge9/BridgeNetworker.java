package ch.thoenluk.solvers.challenge9;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

public class BridgeNetworker implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] instructions = UtStrings.splitMultilineString(input);
        final Set<Position> visitedPositions = new HashSet<>();
        visitedPositions.add(new Position(0, 0));

        Position head = new Position(0, 0);
        Position tail = new Position(0, 0);

        for (String instruction : instructions) {
            final String[] parts = instruction.split(UtStrings.WHITE_SPACE_REGEX);
            final Direction direction = Direction.forKey(parts[0].charAt(0));
            final int distance = UtParsing.cachedParseInt(parts[1]);

            for (int i = 0; i < distance; i++) {
                head = head.offsetBy(direction.getOffset());

                if (tail.getRoleplayingDistanceFrom(head) > 1) {
                    tail = stepTowards(tail, head);
                    visitedPositions.add(tail);
                }
            }
        }

        return Integer.toString(visitedPositions.size());
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] instructions = UtStrings.splitMultilineString(input);
        final Set<Position> visitedPositions = new HashSet<>();
        visitedPositions.add(new Position(0, 0));

        final Position[] rope = new Position[10];

        for (int i = 0; i < 10; i++) {
            rope[i] = new Position(0, 0);
        }

        for (String instruction : instructions) {
            final String[] parts = instruction.split(UtStrings.WHITE_SPACE_REGEX);
            final Direction direction = Direction.forKey(parts[0].charAt(0));
            final int distance = UtParsing.cachedParseInt(parts[1]);

            for (int i = 0; i < distance; i++) {
                rope[0] = rope[0].offsetBy(direction.getOffset());

                for (int knot = 1; knot < rope.length; knot++) {
                    if (rope[knot].getRoleplayingDistanceFrom(rope[knot - 1]) > 1) {
                        rope[knot] = stepTowards(rope[knot], rope[knot - 1]);
                    }
                }

                visitedPositions.add(rope[9]);
            }
        }

        return Integer.toString(visitedPositions.size());
    }

    private Position stepTowards(Position tail, Position head) {
        final int yMovement = (int) Math.signum(head.y() - tail.y());
        final int xMovement = (int) Math.signum(head.x() - tail.x());
        return new Position(tail.y() + yMovement, tail.x() + xMovement);
    }

    private enum Direction {
        RIGHT(new Position(0, 1)),
        UP(new Position(-1, 0)),
        LEFT(new Position(0, -1)),
        DOWN(new Position(1, 0));


        //---- Statics

        private static final Map<Character, Direction> directionsByKey = Map.ofEntries(
                Map.entry('R', RIGHT),
                Map.entry('U', UP),
                Map.entry('L', LEFT),
                Map.entry('D', DOWN)
        );

        public static Direction forKey(char key) {
            if (!directionsByKey.containsKey(key)) {
                throw new IllegalArgumentException("No Direction exists for key " + key);
            }
            return directionsByKey.get(key);
        }


        //---- Fields

        private final Position offset;


        //---- Constructor

        Direction(Position offset) {
            this.offset = offset;
        }


        //---- Methods

        public Position getOffset() {
            return offset;
        }
    }
}
