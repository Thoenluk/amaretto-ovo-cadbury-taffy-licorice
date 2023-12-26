package ch.thoenluk.solvers.challenge16;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;
import java.util.stream.IntStream;

public class Johnsoninator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final Map<Position, Character> layout = UtParsing.multilineStringToPositionCharacterMap(input);
        return Integer.toString(calculateEnergisedTiles(layout, new Movement(new Position(0, 0), Position.RIGHT)));
    }

    private int calculateEnergisedTiles(final Map<Position, Character> layout, final Movement start) {
        final Map<Position, Set<Position>> enteredDirections = new HashMap<>();
        final List<Movement> movementsToExplore = new LinkedList<>();
        movementsToExplore.add(start);

        while (!movementsToExplore.isEmpty()) {
            final Movement movement = movementsToExplore.remove(0);
            final Set<Position> enteredDirectionsForTile = enteredDirections.computeIfAbsent(movement.from(), p -> new HashSet<>());
            if (enteredDirectionsForTile.add(movement.direction())) {
                final char tileUnderMovement = layout.get(movement.from());
                calculateNextMovementDirections(tileUnderMovement, movement.direction()).stream()
                        .map(direction -> new Movement(movement.from().offsetBy(direction), direction))
                        .filter(m -> layout.containsKey(m.from()))
                        .forEach(movementsToExplore::add);
            }
        }
        return enteredDirections.keySet().size();
    }

    private Set<Position> calculateNextMovementDirections(final char tile, final Position direction) {
        return switch (tile) {
            case '.' -> Set.of(direction);
            case '|' -> {
                if (direction == Position.UP || direction == Position.DOWN) {
                    yield Set.of(direction);
                }
                yield Set.of(Position.UP, Position.DOWN);
            }
            case '-' -> {
                if (direction == Position.RIGHT || direction == Position.LEFT) {
                    yield Set.of(direction);
                }
                yield Set.of(Position.RIGHT, Position.LEFT);
            }
            case '/' -> Set.of(rightFacingMirror(direction));
            case '\\' -> Set.of(leftFacingMirror(direction));
            default -> throw new IllegalStateException("Unexpected value: " + tile);
        };
    }

    private Position rightFacingMirror(final Position direction) {
        if (direction == Position.UP) {
            return Position.RIGHT;
        }
        if (direction == Position.RIGHT) {
            return Position.UP;
        }
        if (direction == Position.DOWN) {
            return Position.LEFT;
        }
        if (direction == Position.LEFT) {
            return Position.DOWN;
        }
        throw new IllegalArgumentException();
    }

    private Position leftFacingMirror(final Position direction) {
        if (direction == Position.UP) {
            return Position.LEFT;
        }
        if (direction == Position.RIGHT) {
            return Position.DOWN;
        }
        if (direction == Position.DOWN) {
            return Position.RIGHT;
        }
        if (direction == Position.LEFT) {
            return Position.UP;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String saveChristmasAgain(String input) {
        final Map<Position, Character> layout = UtParsing.multilineStringToPositionCharacterMap(input);
        final String[] lines = UtStrings.splitMultilineString(input);
        final int height = lines.length;
        final int width = lines[0].length();
        final List<Movement> startingPositions = new LinkedList<>();
        IntStream.range(0, height)
                .mapToObj(i -> new Movement(new Position(i, 0), Position.RIGHT))
                .forEach(startingPositions::add);
        IntStream.range(0, height)
                .mapToObj(i -> new Movement(new Position(i, width - 1), Position.LEFT))
                .forEach(startingPositions::add);
        IntStream.range(0, width)
                .mapToObj(i -> new Movement(new Position(0, i), Position.DOWN))
                .forEach(startingPositions::add);
        IntStream.range(0, width)
                .mapToObj(i -> new Movement(new Position(height - 1, i), Position.UP))
                .forEach(startingPositions::add);

        return startingPositions.stream()
                .map(m -> calculateEnergisedTiles(layout, m))
                .max(Comparator.naturalOrder())
                .orElseThrow()
                .toString();

        // haha CPU go brrrrr
        // The dutiful answer here would be that you make calculateEnergisedTiles return not only the total energised
        // tiles, but also where the beam exited the layout, i.e. every movement such that layout.containsKey(movement.from())
        // but not layout.containsKey(movement.from().offsetBy(movement.direction())).
        // Since the movement is commutative here, if the beam e.g. exited the layout on tile 0,0 heading LEFT, then it
        // will perform the same movement as it just did if it enters on 0,0 heading RIGHT.
        // Therefore, you can prune what I'm guessing would be 90% of calculation by removing all of these entry points
        // from the list of entry points to be explored.
        // However comma, since you already need subpath pruning to prevent infinite loops, my solution returns in 800ms
        // and any solution you can compute is a success.
        //
        // Update: This is not true, of course, because splitters are magic and not commutative. CPU go brrrrr
    }

    private record Movement(Position from, Position direction) {}
}
