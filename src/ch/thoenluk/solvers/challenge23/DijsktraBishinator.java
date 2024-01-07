package ch.thoenluk.solvers.challenge23;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

public class DijsktraBishinator implements ChristmasSaver {
    private static final Map<Position, Character> PATHABLE_SLOPES = Map.of(
            Position.UP, '^',
            Position.RIGHT, '>',
            Position.LEFT, '<',
            Position.DOWN, 'v'
    );

    @Override
    public String saveChristmas(final String input) {
        return calculateLongestPathIterative(input, false);
    }

    private String calculateLongestPath(final String input, final boolean permissiveSlopes) {
        final Map<Position, Character> map = UtParsing.multilineStringToPositionCharacterMap(input); // As in a map of the area!

        final List<Path> pathsToExplore = new LinkedList<>();
        final Set<Path> exploredPaths = new HashSet<>();
        int longestPath = 0;
        final Position start = map.keySet().stream()
                .filter(p -> p.y() == 0)
                .filter(p -> map.get(p) == '.')
                .findFirst()
                .orElseThrow();
        pathsToExplore.add(new Path(start));

        while (!pathsToExplore.isEmpty()) {
            final Path path = pathsToExplore.remove(0);
            UtStrings.println(String.format("Only %d paths remaining!", pathsToExplore.size()));

            List<Position> explorableTiles = permissiveSlopes ? getExplorableTilesPermissive(path, map) : getExplorableTiles(path, map);
            while (!explorableTiles.isEmpty()) {
                for (int i = 1; i < explorableTiles.size(); i++) {
                    final Path alternativePath = path.copy();
                    alternativePath.add(explorableTiles.get(i));
                    if (!exploredPaths.contains(alternativePath)) {
                        pathsToExplore.add(alternativePath);
                        exploredPaths.add(alternativePath);
                    }
                }
                path.add(explorableTiles.get(0));
                explorableTiles = permissiveSlopes ? getExplorableTilesPermissive(path, map) : getExplorableTiles(path, map);
            }
            longestPath = Math.max(longestPath, path.size());
        }

        return Integer.toString(longestPath - 1);
    }

    private String calculateLongestPathReverseDijkstra(final String input, final boolean permissiveSlopes) {
        final Map<Position, Character> map = UtParsing.multilineStringToPositionCharacterMap(input); // As in a map of the area!
        final Map<Position, Integer> hikeLengths = new HashMap<>();
        map.keySet().forEach(position -> hikeLengths.put(position, map.get(position) == '#' ? Integer.MAX_VALUE : Integer.MIN_VALUE));

        final Position start = map.keySet().stream()
                .filter(p -> p.y() == 0)
                .filter(p -> map.get(p) == '.')
                .findFirst()
                .orElseThrow();

        final Position end = map.keySet().stream()
                .filter(p -> map.get(p) == '.')
                .max(Comparator.comparingInt(Position::y))
                .orElseThrow();
        final List<Position> positionsToExplore = new ArrayList<>(List.of(start));
        hikeLengths.put(start, 0);

        while (!positionsToExplore.isEmpty()) {
            final Position current = positionsToExplore.remove(0);
            if (current == end) {
                return Integer.toString(hikeLengths.get(end));
            }
            final int hikeLength = hikeLengths.get(current) + 1;
            final List<Position> explorableTiles = permissiveSlopes ? getExplorableTilesPermissive(current, map) : getExplorableTiles(current, map);
            explorableTiles.stream()
                    .filter(neighbour -> hikeLengths.get(neighbour) < hikeLength)
                    .filter(neighbour -> hikeLengths.get(neighbour) != hikeLength - 2)
                    .forEach(neighbour -> {
                        hikeLengths.put(neighbour, hikeLength);
                        final int index = Collections.binarySearch(positionsToExplore, neighbour, Comparator.comparingInt(hikeLengths::get).reversed());
                        if (index < 0) {
                            positionsToExplore.add(-index - 1, neighbour);
                        }
                        else {
                            positionsToExplore.add(index, neighbour);
                        }
                    });
        }

        return Integer.toString(hikeLengths.get(end));
    }

    private String calculateLongestPathRecursive(final String input, final boolean permissiveSlopes) {
        final Map<Position, Character> map = UtParsing.multilineStringToPositionCharacterMap(input); // As in a map of the area!

        final Position start = map.keySet().stream()
                .filter(p -> p.y() == 0)
                .filter(p -> map.get(p) == '.')
                .findFirst()
                .orElseThrow();

        return Integer.toString(calculateLongestPathRecursive(Set.of(), start, map, permissiveSlopes) - 1);
    }

    private int calculateLongestPathRecursive(final Set<Position> path, final Position nextStep, final Map<Position, Character> map, final boolean permissiveSlopes) {
        final Set<Position> pathAfterStep = new HashSet<>(path);
        pathAfterStep.add(nextStep);
        final List<Position> explorableTiles = permissiveSlopes ? getExplorableTilesPermissive(nextStep, map) : getExplorableTiles(nextStep, map);
        return explorableTiles.stream()
                .filter(neighbour -> !pathAfterStep.contains(neighbour))
                .map(neighbour -> calculateLongestPathRecursive(pathAfterStep, neighbour, map, permissiveSlopes))
                .max(Comparator.naturalOrder())
                .orElse(pathAfterStep.size());
    }

    private String calculateLongestPathIterative(final String input, final boolean permissiveSlopes) {
        final Map<Position, Character> map = UtParsing.multilineStringToPositionCharacterMap(input); // As in a map of the area!

        final Position start = map.keySet().stream()
                .filter(p -> p.y() == 0)
                .filter(p -> map.get(p) == '.')
                .findFirst()
                .orElseThrow();

        final Position end = map.keySet().stream()
                .filter(p -> map.get(p) == '.')
                .max(Comparator.comparingInt(Position::y))
                .orElseThrow();

        int longestHike = Integer.MIN_VALUE;

        final Stack<Set<Position>> paths = new Stack<>();
        paths.push(Set.of());

        final Stack<Position> nextSteps = new Stack<>();
        nextSteps.push(start);

        final Set<Subtree> seenSubtrees = new HashSet<>();

        while (!paths.isEmpty()) {
            final Set<Position> path = paths.pop();
            final Position nextStep = nextSteps.pop();
            final Set<Position> pathAfterStep = new HashSet<>(path);
            pathAfterStep.add(nextStep);
            final List<Position> explorableTiles = permissiveSlopes ? getExplorableTilesPermissive(nextStep, map) : getExplorableTiles(nextStep, map);
            final List<Position> notInPath = explorableTiles.stream()
                    .filter(neighbour -> !pathAfterStep.contains(neighbour))
                    .toList();
            if (notInPath.isEmpty() && nextStep.equals(end)) {
                longestHike = Math.max(longestHike, path.size());
            }
            else {
                if (notInPath.size() > 1) {
                    final Subtree subtree = new Subtree(path.size(), nextStep);
                    if (seenSubtrees.contains(subtree)) {
                        continue;
                    }
                    seenSubtrees.add(subtree);
                }
                notInPath.forEach(neighbour -> {
                    paths.push(pathAfterStep);
                    nextSteps.push(neighbour);
                });
            }
        }

        return Integer.toString(longestHike);
    }

    private List<Position> getExplorableTiles(final Path path, final Map<Position, Character> map) {
        final Position tail = path.track().get(path.track().size() - 1);
        return Position.NeighbourDirection.CARDINAL.getDirections().stream()
                .filter(direction -> {
                    final Position neighbour = tail.offsetBy(direction);
                    if (!map.containsKey(neighbour) || path.visitedPositions().contains(neighbour)) {
                        return false;
                    }
                    final char terrain = map.get(neighbour);
                    return terrain == '.' || terrain == PATHABLE_SLOPES.get(direction);
                })
                .map(tail::offsetBy)
                .toList();
    }

    private List<Position> getExplorableTilesPermissive(final Path path, final Map<Position, Character> map) {
        final Position tail = path.track().get(path.track().size() - 1);
        return Position.NeighbourDirection.CARDINAL.getDirections().stream()
                .map(tail::offsetBy)
                .filter(map::containsKey)
                .filter(neighbour -> map.get(neighbour) != '#')
                .filter(neighbour -> !path.visitedPositions().contains(neighbour))
                .toList();
    }

    private List<Position> getExplorableTiles(final Position position, final Map<Position, Character> map) {
        return Position.NeighbourDirection.CARDINAL.getDirections().stream()
                .filter(direction -> {
                    final Position neighbour = position.offsetBy(direction);
                    if (!map.containsKey(neighbour)) {
                        return false;
                    }
                    final char terrain = map.get(neighbour);
                    return terrain == '.' || terrain == PATHABLE_SLOPES.get(direction);
                })
                .map(position::offsetBy)
                .toList();
    }

    private List<Position> getExplorableTilesPermissive(final Position position, final Map<Position, Character> map) {
        return Position.NeighbourDirection.CARDINAL.getDirections().stream()
                .map(position::offsetBy)
                .filter(map::containsKey)
                .filter(neighbour -> map.get(neighbour) != '#')
                .toList();
    }

    @Override
    public String saveChristmasAgain(final String input) {
        return calculateLongestPathIterative(input, true);
    }

    private record Path(List<Position> track, Set<Position> visitedPositions) {
        public Path(final Position start) {
            this(new ArrayList<>(List.of(start)), new HashSet<>(Set.of(start)));
        }

        public void add(final Position position) {
            track().add(position);
            visitedPositions().add(position);
        }

        public int size() {
            return track().size();
        }

        public Path copy() {
            return new Path(new ArrayList<>(track()), new HashSet<>(visitedPositions()));
        }
    }

    private record Subtree(int hikeLength, Position nextStep) {}
}
