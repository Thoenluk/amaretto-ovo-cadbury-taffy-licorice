package ch.thoenluk.solvers.challenge17;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

public class HeatLossEliminatorinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final Map<Position, Integer> city = UtParsing.multilineStringToPositionIntegerMap(input);
        final Path startingPath = new Path(0, new Position(0, 0), new MovementAllowance(0, 3, 3, 0));
        return calculateShortestPath(startingPath, city);
    }

    private String calculateShortestPath(Path startingPath, Map<Position, Integer> city) {
        final Map<Position, Set<Path>> bestPaths = new HashMap<>();
        final Position goal = city.keySet().stream()
                .max(Position::compareAsCoordinates)
                .orElseThrow();
        bestPaths.put(new Position(0, 0), new HashSet<>(Set.of(startingPath)));
        final List<Path> pathsToExplore = new LinkedList<>();
        pathsToExplore.add(startingPath);

        while (true) {
            final Path path = pathsToExplore.remove(0);
            pathsToExplore.remove(path);
            final MovementAllowance movementAllowance = path.movementAllowance();
            for (Position direction : Position.NeighbourDirection.CARDINAL.getDirections()) {
                if (movementAllowance.canMoveIn(direction)) {
                    final Position neighbour = path.location().offsetBy(direction);
                    if (!city.containsKey(neighbour)) {
                        continue;
                    }
                    final int costToNeighbour = path.cost() + city.get(neighbour);
                    final Path pathToNeighbour = new Path(costToNeighbour, neighbour, movementAllowance.moveIn(direction));
                    if (goal.equals(neighbour)
                            && pathToNeighbour.movementAllowance().canStop()) {
                        return Integer.toString(costToNeighbour);
                    }
                    final Set<Path> bestPathsToNeighbour = bestPaths.computeIfAbsent(neighbour, k -> new HashSet<>());
                    bestPathsToNeighbour.removeIf(pathToNeighbour::isBetterThan);
                    final boolean nextToGoal = neighbour.getDistanceFrom(goal) == 1;
                    if (nextToGoal || bestPathsToNeighbour.stream().noneMatch(p -> p.isBetterThan(pathToNeighbour))) {
                        pathsToExplore.add(pathToNeighbour);
                        bestPathsToNeighbour.add(pathToNeighbour);
                    }
                }
            }
            pathsToExplore.sort(Comparator.comparingInt(Path::cost).thenComparingInt(p -> p.location().getDistanceFrom(goal)));
        }
    }

    @Override
    public String saveChristmasAgain(String input) {
        final Map<Position, Integer> city = UtParsing.multilineStringToPositionIntegerMap(input);
        final Path startingPath = new Path(0, new Position(0, 0), new ULTRAMovementAllowance(0, 10, 10, 0));
        return calculateShortestPath(startingPath, city);
        // Fuck this challenge. Four minutes is still calculable.
        // I'm sure there is better subpath pruning to be implemented yet, maybe a heuristic estimation of cost-to-goal
        // rather than using cost-from-start as the Dijkstra determinant, but I'm going to claim victory because
        // A. It worked
        // B. I've learned what I will from this challenge.
    }

    private record Path(int cost, Position location, MovementAllowance movementAllowance) {
        public boolean isBetterThan(final Path other) {
            return cost() < other.cost() && movementAllowance().isEqualOrBetterThan(other.movementAllowance());
        }
    }

}
