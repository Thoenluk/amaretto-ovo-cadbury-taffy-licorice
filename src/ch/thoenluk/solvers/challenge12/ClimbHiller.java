package ch.thoenluk.solvers.challenge12;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtParsing;

import java.util.*;

public class ClimbHiller implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final Map<Position, Character> hill = UtParsing.multilineStringToPositionCharacterMap(input);
        final Map<Position, Integer> heights = new HashMap<>();
        Position start = null;
        Position end = null;

        for (Map.Entry<Position, Character> entry : hill.entrySet()) {
            heights.put(entry.getKey(),
                    switch (entry.getValue()) {
                        case 'S' -> UtParsing.cachedGetNumericValue('a');
                        case 'E' -> UtParsing.cachedGetNumericValue('z');
                        default -> UtParsing.cachedGetNumericValue(entry.getValue());
                    }
            );
            if (entry.getValue() == 'S') {
                start = entry.getKey();
            }
            else if (entry.getValue() == 'E') {
                end = entry.getKey();
            }
        }
        if (start == null || end == null) {
            throw new IllegalStateException("Could not locate start and/or end. There is no point continuing until this is fixed!");
        }

        return Integer.toString(dijkstraThoseBishes(heights, List.of(start), end));
    }

    @Override
    public String saveChristmasAgain(String input) {
        final Map<Position, Character> hill = UtParsing.multilineStringToPositionCharacterMap(input);
        final Map<Position, Integer> heights = new HashMap<>();
        final List<Position> starts = new LinkedList<>();
        Position end = null;

        for (Map.Entry<Position, Character> entry : hill.entrySet()) {
            heights.put(entry.getKey(),
                    switch (entry.getValue()) {
                        case 'S' -> UtParsing.cachedGetNumericValue('a');
                        case 'E' -> UtParsing.cachedGetNumericValue('z');
                        default -> UtParsing.cachedGetNumericValue(entry.getValue());
                    }
            );
            if (entry.getValue() == 'S' || entry.getValue() == 'a') {
                starts.add(entry.getKey());
            }
            else if (entry.getValue() == 'E') {
                end = entry.getKey();
            }
        }
        if (starts.isEmpty() || end == null) {
            throw new IllegalStateException("Could not locate start and/or end. There is no point continuing until this is fixed!");
        }

        return Integer.toString(dijkstraThoseBishes(heights, starts, end));
    }

    private int dijkstraThoseBishes(Map<Position, Integer> heights, Collection<Position> starts, Position end) {
        int leastDistance = Integer.MAX_VALUE;

        for (Position start : starts) {
            final Map<Position, Integer> distances = new HashMap<>();
            distances.put(start, 0);

            final List<Position> positionsToExplore = new ArrayList<>();
            positionsToExplore.add(start);

            while (!positionsToExplore.isEmpty()) {
                final Position current = positionsToExplore.remove(0);

                for (Position neighbour : current.getNeighbours(Position.NeighbourDirection.CARDINAL)) {
                    if (heights.containsKey(neighbour)
                            && !distances.containsKey(neighbour)
                            && isReachable(heights.get(current), heights.get(neighbour))) {
                        distances.put(neighbour, distances.get(current) + 1);
                        positionsToExplore.add(neighbour);
                    }
                }
            }

            final Integer endDistance = distances.get(end);

            if (endDistance != null && endDistance < leastDistance) {
                leastDistance = endDistance;
            }
        }

        return leastDistance;
    }

    private boolean isReachable(int start, int target) {
        return target - start <= 1;
    }
}
