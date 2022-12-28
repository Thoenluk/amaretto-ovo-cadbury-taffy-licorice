package ch.thoenluk.solvers.challenge23;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

import static ch.thoenluk.ut.Position.NeighbourDirection.OMNIDIRECTIONAL;

public class ElfDistributor implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        return simulate(input, 10);
    }

    @Override
    public String saveChristmasAgain(String input) {
        return simulate(input, Integer.MAX_VALUE);
    }

    private String simulate(String input, int limit) {
        final Set<Position> elfPositions = createElfPositions(input);
        final List<List<Position>> directions = new LinkedList<>();

        directions.add(List.of(new Position(-1, 0), new Position(-1, -1), new Position(-1, 1)));
        directions.add(List.of(new Position(1, 0), new Position(1, -1), new Position(1, 1)));
        directions.add(List.of(new Position(0, -1), new Position(-1, -1), new Position(1, -1)));
        directions.add(List.of(new Position(0, 1), new Position(-1, 1), new Position(1, 1)));

        final Map<Position, Position> proposedMovements = new HashMap<>();

        boolean movementOccured;

        for (int i = 0; i < limit; i++) {
            movementOccured = false;
            for (Position elf : elfPositions) {
                if (anyElvesInLocations(elf.getNeighbours(OMNIDIRECTIONAL), elfPositions)) {
                    final Position proposedOffset = getProposedMove(elf, directions, elfPositions);

                    if (proposedOffset != null) {
                        final Position proposedTarget = elf.offsetBy(proposedOffset);
                        if (proposedMovements.containsKey(proposedTarget)) {
                            proposedMovements.put(proposedTarget, null);
                        }
                        else {
                            proposedMovements.put(proposedTarget, elf);
                        }
                    }
                }
            }

            for (Map.Entry<Position, Position> proposedMovement : proposedMovements.entrySet()) {
                if (proposedMovement.getValue() != null) {
                    movementOccured = true;
                    elfPositions.add(proposedMovement.getKey());
                    elfPositions.remove(proposedMovement.getValue());
                }
            }

            if (!movementOccured) {
                return Integer.toString(i + 1);
            }

            proposedMovements.clear();
            directions.add(directions.size() - 1, directions.remove(0));
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Position elf : elfPositions) {
            if (minX > elf.x()) {
                minX = elf.x();
            }
            else if (maxX < elf.x()) {
                maxX = elf.x();
            }

            if (minY > elf.y()) {
                minY = elf.y();
            }
            else if (maxY < elf.y()) {
                maxY = elf.y();
            }
        }

        int freeSpots = 0;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (!elfPositions.contains(new Position(y, x))) {
                    freeSpots++;
                }
            }
        }

        return Integer.toString(freeSpots);
    }

    private Set<Position> createElfPositions(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);
        final Set<Position> elfPositions = new HashSet<>();

        for (int y = 0; y < lines.length; y++) {
            final String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    elfPositions.add(new Position(y, x));
                }
            }
        }

        return elfPositions;
    }

    private boolean anyElvesInLocations(Collection<Position> locations, Set<Position> elfPositions) {
        for (Position location : locations) {
            if (elfPositions.contains(location)) {
                return true;
            }
        }
        return false;
    }

    private Position getProposedMove(Position elf, List<List<Position>> directions, Set<Position> elfPositions) {
        for (List<Position> direction : directions) {
            if (!anyElvesInLocations(elf.offsetBy(direction), elfPositions)) {
                return direction.get(0);
            }
        }
        return null;
    }
}
