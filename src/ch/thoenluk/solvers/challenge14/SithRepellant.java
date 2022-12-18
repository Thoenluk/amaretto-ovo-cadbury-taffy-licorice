package ch.thoenluk.solvers.challenge14;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtStrings;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SithRepellant implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final Map<Position, Material> cave = parseCave(input);

        final int deepestY = cave.keySet().stream().map(Position::y).max(Comparator.naturalOrder()).orElseThrow();

        Position sandLocation;
        Position nextLocation;

        for (int sandResting = 0; sandResting < 10000; sandResting++) {
            sandLocation = new Position(0, 500);

            do {
                nextLocation = getNextLocationToFallTo(sandLocation, cave);

                if (nextLocation == null) {
                    cave.put(sandLocation, Material.SAND);
                }
                else {
                    sandLocation = nextLocation;
                }

                if (sandLocation.y() > deepestY) {
                    // logCave(cave);
                    return Integer.toString(sandResting);
                }
            } while (nextLocation != null);
        }

        throw new IllegalStateException("Program should have halted long ago. Aborting for infinite loop prevention.");
    }

    @Override
    public String saveChristmasAgain(String input) {
        final Map<Position, Material> cave = parseCave(input);

        final int deepestY = cave.keySet().stream().map(Position::y).max(Comparator.naturalOrder()).orElseThrow();

        final Position sandSource = new Position(0, 500);
        Position sandLocation;
        Position nextLocation;

        for (int sandResting = 0; sandResting < 100000; sandResting++) {
            sandLocation = sandSource;

            do {
                nextLocation = getNextLocationToFallTo(sandLocation, cave, deepestY);

                if (nextLocation == null) {
                    cave.put(sandLocation, Material.SAND);

                    if (sandLocation.equals(sandSource)) {
                        // logCave(cave);
                        return Integer.toString(sandResting + 1);
                    }
                }
                else {
                    sandLocation = nextLocation;
                }
            } while (nextLocation != null);
        }

        throw new IllegalStateException("Program should have halted long ago. Aborting for infinite loop prevention.");
    }

    private Position getNextLocationToFallTo(Position location, Map<Position, Material> cave) {
        return getNextLocationToFallTo(location, cave, Integer.MAX_VALUE);
    }

    private Position getNextLocationToFallTo(Position location, Map<Position, Material> cave, int deepestY) {
        for (Direction direction : Direction.values()) {
            final Position nextLocation = location.offsetBy(direction.getOffset());
            final Material materialInDirection = cave.computeIfAbsent(nextLocation, p -> location.y() > deepestY ? Material.ROCK : Material.AIR);

            if (!materialInDirection.blocks()) {
                return nextLocation;
            }
        }

        return null;
    }

    private Map<Position, Material> parseCave(String input) {
        final Map<Position, Material> cave = new HashMap<>();
        final String[] lines = UtStrings.splitMultilineString(input);

        for (String line : lines) {
            final String[] points = line.split(" -> ");

            for (int i = 1; i < points.length; i++) {
                final Position start = Position.fromString(points[i - 1]);
                final Position end = Position.fromString(points[i]);

                final List<Position> path = start.getPathTo(end);

                for (Position position : path) {
                    cave.put(position, Material.ROCK);
                }
            }
        }

        cave.put(new Position(0, 500), Material.SAND_SOURCE);

        return cave;
    }

    // Usages are commented out to save performance because apparently we care about that now.
    private void logCave(Map<Position, Material> cave) {
        final int minX = cave.keySet().stream().map(Position::x).min(Comparator.naturalOrder()).orElseThrow();
        final int maxX = cave.keySet().stream().map(Position::x).max(Comparator.naturalOrder()).orElseThrow();
        final int minY = cave.keySet().stream().map(Position::y).min(Comparator.naturalOrder()).orElseThrow();
        final int maxY = cave.keySet().stream().map(Position::y).max(Comparator.naturalOrder()).orElseThrow();

        System.out.println();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                final Material material = cave.computeIfAbsent(new Position(y, x), p -> Material.AIR);

                System.out.print(switch (material) {
                    case ROCK -> '#';
                    case AIR -> '.';
                    case SAND -> 'o';
                    case SAND_SOURCE -> '+';
                });
            }
            System.out.println(" " + y);
        }
    }

    private enum Material {
        AIR,
        ROCK,
        SAND,
        SAND_SOURCE;

        public boolean blocks() {
            return this != AIR;
        }
    }

    private enum Direction {
        DOWN(new Position(1, 0)),
        DOWN_LEFT(new Position(1, -1)),
        DOWN_RIGHT(new Position(1, 1));

        private final Position offset;

        Direction(Position offset) {
            this.offset = offset;
        }

        public Position getOffset() {
            return offset;
        }
    }
}
