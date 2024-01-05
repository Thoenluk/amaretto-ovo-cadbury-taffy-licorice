package ch.thoenluk.solvers.challenge21;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

public class BishDijkstrainator implements ChristmasSaver {
    @Override
    public String saveChristmas(final String input) {
        return Long.toString(calculateAccessiblePlots(input, 64));
    }

    private long calculateAccessiblePlots(final String input, final int steps) {
        final Map<Position, Character> plots = UtParsing.multilineStringToPositionCharacterMap(input);
        final Position start = plots.entrySet().stream()
                .filter(e -> e.getValue() == 'S')
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
        return calculateAccessiblePlotsFrom(start, steps, plots, true);
    }

    private long calculateAccessiblePlotsFrom(final Position start, final int steps, final Map<Position, Character> plots, final boolean evenParity) {
        final Map<Position, Integer> pathCosts = new HashMap<>();
        pathCosts.put(start, 0);
        final List<Path> pathsToExplore = new ArrayList<>();
        pathsToExplore.add(new Path(0, start));

        while (!pathsToExplore.isEmpty()) {
            final Path current = pathsToExplore.remove(0);
            current.location().getCardinalNeighbours().stream()
                    .filter(plots::containsKey)
                    .filter(position -> plots.get(position) == '.' || plots.get(position) == 'S')
                    .filter(position -> !pathCosts.containsKey(position))
                    .forEach(position -> {
                        pathCosts.put(position, current.cost() + 1);
                        final Path path = new Path(current.cost() + 1, position);
                        final int index = Collections.binarySearch(pathsToExplore, path, Comparator.comparingInt(Path::cost));
                        if (index < 0) {
                            pathsToExplore.add(-index - 1, path);
                        } else {
                            pathsToExplore.add(index, path);
                        }
                    });

        }

        return pathCosts.values().stream()
                .filter(i -> i <= steps)
                .filter(i -> i % 2 == (evenParity ? 0 : 1))
                .count();
    }

    private void renderAccessiblePlots(final int steps, final Map<Position, Character> plots, final Map<Position, Integer> pathCosts, final boolean evenParity) {
        plots.keySet().stream()
                .sorted(Comparator.comparingInt(Position::y).thenComparingInt(Position::x))
                .forEach(position -> {
                    if (position.x() == 0) {
                        UtStrings.println();
                    }
                    if (pathCosts.containsKey(position) && pathCosts.get(position) <= steps && pathCosts.get(position) % 2 == (evenParity ? 0 : 1)) {
                        UtStrings.print('O');
                    }
                    else {
                        UtStrings.print(plots.get(position));
                    }
                });
        UtStrings.println();
    }

    @Override
    public String saveChristmasAgain(final String input) {
        final String[] lines = UtStrings.splitMultilineString(input);
        final Map<Position, Character> plots = UtParsing.multilineStringToPositionCharacterMap(input);
        final int height = lines.length - 1;
        final int width = lines[0].length() - 1;
        final long tb = ((26_501_365) - height / 2) / (height + 1);
        final long tbOfEquivalentSquares = tb - 1;
        final Position start = plots.entrySet().stream()
                .filter(e -> e.getValue() == 'S')
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
        long accessiblePlots = 0;

        UtStrings.println(String.format("With a square side length of %d, one can walk precisely %d squares in any one direction before exactly reaching a border (= TB)", height + 1, tb));

        final long numberOfEvenSquares = tbOfEquivalentSquares / 2 * (tbOfEquivalentSquares / 2 + 1) * 4 + 1;
        final long accessibleOnEvenSquares = calculateAccessiblePlotsFrom(start, 262, plots, true);
        UtStrings.println(String.format("Of these, %d are even-parity squares with %d plots accessible", numberOfEvenSquares, accessibleOnEvenSquares));
        accessiblePlots = UtMath.superOverflowSafeSum(accessiblePlots, UtMath.superOverflowSafeProduct(numberOfEvenSquares, accessibleOnEvenSquares));

        final long numberOfOddSquares = (tbOfEquivalentSquares + 1) / 2 * (tbOfEquivalentSquares + 1) / 2 * 4;
        final long accessibleOnOddSquares = calculateAccessiblePlotsFrom(start, 262, plots, false);
        UtStrings.println(String.format("%d are odd-parity squares with %d plots accessible", numberOfOddSquares, accessibleOnOddSquares));
        accessiblePlots = UtMath.superOverflowSafeSum(accessiblePlots, UtMath.superOverflowSafeProduct(numberOfOddSquares, accessibleOnOddSquares));

        final List<Position> corners = List.of(new Position(0, 0), new Position(0, width), new Position(height, 0), new Position(height, width));
        final long accessibleOnThreeQuartersTiles = corners.stream()
                .map(position -> calculateAccessiblePlotsFrom(position, 195, plots, tb % 2 == 0))
                .reduce(UtMath::superOverflowSafeSum)
                .orElseThrow();
        UtStrings.println(String.format("There are %d three-quarters squares on each side with %d plots accessible (summing one from each side)", tbOfEquivalentSquares, accessibleOnThreeQuartersTiles));
        accessiblePlots = UtMath.superOverflowSafeSum(accessiblePlots, UtMath.superOverflowSafeProduct(tbOfEquivalentSquares, accessibleOnThreeQuartersTiles));

        final long accessibleOnCornerTiles = corners.stream()
                .map(position -> calculateAccessiblePlotsFrom(position, 65, plots, tb % 2 == 1))
                .reduce(UtMath::superOverflowSafeSum)
                .orElseThrow();
        UtStrings.println(String.format("There are further %d corner squares on each side with %d plots accessible (again summing one from each side)", tb, accessibleOnCornerTiles));
        accessiblePlots = UtMath.superOverflowSafeSum(accessiblePlots, UtMath.superOverflowSafeProduct(tb, accessibleOnCornerTiles));

        final long accessibleOnTop = calculateAccessiblePlotsFrom(new Position(height, width / 2), 131, plots, tb % 2 == 0);
        final long accessibleOnLeft = calculateAccessiblePlotsFrom(new Position(height / 2, width), 131, plots, tb % 2 == 0);
        final long accessibleOnRight = calculateAccessiblePlotsFrom(new Position(height / 2, 0), 131, plots, tb % 2 == 0);
        final long accessibleOnBottom = calculateAccessiblePlotsFrom(new Position(0, width / 2), 131, plots, tb % 2 == 0);
        UtStrings.println(String.format("Finally, there are the very corner squares with accessible plots TOP = %d, LEFT = %d, RIGHT = %d, BOTTOM = %d",
                accessibleOnTop, accessibleOnLeft, accessibleOnRight, accessibleOnBottom));
        accessiblePlots = UtMath.superOverflowSafeSum(accessiblePlots, accessibleOnTop, accessibleOnLeft, accessibleOnRight, accessibleOnBottom);

        UtStrings.println("Which brings us to a grand total of:");
        return Long.toString(accessiblePlots);
        // If AoC doesn't care to give an example to verify, I don't care to guess what edge case they have hidden.
    }

    private record Path(int cost, Position location) {
    }
}
