package ch.thoenluk.solvers.challenge11;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class SpaceExpandinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        return getDistanceSumForExpansionFactor(input, 2);
    }

    private String getDistanceSumForExpansionFactor(final String input, final int expansionFactor) {
        final String[] lines = UtStrings.splitMultilineString(input);
        final List<Integer> expandedRows = IntStream.range(0, lines.length)
                .filter(i -> lines[i].matches("\\.+"))
                .boxed()
                .toList();
        final List<Integer> expandedColumns = IntStream.range(0, lines[0].length())
                .filter(i -> isColumnEmpty(i, lines))
                .boxed()
                .toList();
        final List<Position> galaxies = UtParsing.multilineStringToPositionCharacterMap(input).entrySet().stream()
                .filter(entry -> entry.getValue() == '#')
                .map(Map.Entry::getKey)
                .toList();
        return UtMath.restOfTheLongOwl(IntStream.range(0, galaxies.size())
                .boxed()
                .map(galaxyIndex -> getAllDistancesFor(galaxyIndex, galaxies, expandedRows, expandedColumns, expansionFactor)));
    }

    private boolean isColumnEmpty(final int column, final String[] lines) {
        return Arrays.stream(lines).allMatch(line -> line.charAt(column) == '.');
    }

    private long getAllDistancesFor(final int galaxyIndex, final List<Position> galaxies, final List<Integer> expandedRows, final List<Integer> expandedColumns, final int expansionFactor) {
        return IntStream.range(galaxyIndex, galaxies.size())
                .boxed()
                .map(secondGalaxyIndex -> getDistanceBetween(galaxyIndex, secondGalaxyIndex, galaxies, expandedRows, expandedColumns, expansionFactor))
                .reduce(UtMath::superOverflowSafeSum)
                .orElseThrow();
    }

    private long getDistanceBetween(final int firstGalaxyIndex, final int secondGalaxyIndex, final List<Position> galaxies, final List<Integer> expandedRows, final List<Integer> expandedColumns, final int expansionFactor) {
        final Position firstGalaxy = galaxies.get(firstGalaxyIndex);
        final Position secondGalaxy = galaxies.get(secondGalaxyIndex);
        final int lesserX = Math.min(firstGalaxy.x(), secondGalaxy.x());
        final int greaterX = Math.max(firstGalaxy.x(), secondGalaxy.x());
        final int lesserY = Math.min(firstGalaxy.y(), secondGalaxy.y());
        final int greaterY = Math.max(firstGalaxy.y(), secondGalaxy.y());

        long distance = firstGalaxy.getDistanceFrom(secondGalaxy);
        distance += expandedRows.stream()
                .filter(row -> lesserY < row && row < greaterY)
                .count() * (expansionFactor - 1);
        distance += expandedColumns.stream()
                .filter(column -> lesserX < column && column < greaterX)
                .count() * (expansionFactor - 1);
        return distance;
    }

    @Override
    public String saveChristmasAgain(String input) {
        return getDistanceSumForExpansionFactor(input, 1_000_000);
    }
}
