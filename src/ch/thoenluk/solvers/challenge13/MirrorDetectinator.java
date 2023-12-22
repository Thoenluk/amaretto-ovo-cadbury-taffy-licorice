package ch.thoenluk.solvers.challenge13;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtStrings;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class MirrorDetectinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        return calculateOutput(input, 0);
    }

    private String calculateOutput(final String input, final int desiredHammingDistance) {
        return UtMath.restOfTheOwl(
                Arrays.stream(UtStrings.splitStringWithEmptyLines(input))
                        .map(UtStrings::splitMultilineString)
                        .map(line -> calculateSummary(desiredHammingDistance, line))
        );
    }

    @Override
    public String saveChristmasAgain(String input) {
        return calculateOutput(input, 1);
    }

    private int calculateSummary(final int desiredHammingDistance, final String[] notes) {
        return calculateLinesLeftOfVerticalLineOfReflection(desiredHammingDistance, notes)
                .orElseGet(() -> calculateLinesAboveHorizontalLineOfReflection(desiredHammingDistance, notes).orElseThrow() * 100);
    }

    private OptionalInt calculateLinesAboveHorizontalLineOfReflection(final int desiredHammingDistance, final String[] notes) {
        return IntStream.range(1, notes.length)
                .filter(i -> calculateTotalHammingDistanceAbove(i, notes) == desiredHammingDistance)
                .findFirst();
    }

    private long calculateTotalHammingDistanceAbove(final int row, final String[] notes) {
        final int limit = Math.min(row, notes.length - row);
        return IntStream.range(0, limit)
                .boxed()
                .map(i -> getHammingDistance(notes[row - 1 - i], notes[row + i]))
                .reduce(UtMath::superOverflowSafeSum)
                .orElseThrow();
    }

    private long getHammingDistance(final String first, final String second) {
        return IntStream.range(0, first.length())
                .filter(i -> first.charAt(i) != second.charAt(i))
                .count();
    }

    private OptionalInt calculateLinesLeftOfVerticalLineOfReflection(final int desiredHammingDistance, final String[] notes) {
        return IntStream.range(1, notes[0].length())
                .filter(i -> calculateTotalHammingDistanceLeftOf(i, notes) == desiredHammingDistance)
                .findFirst();
    }

    private long calculateTotalHammingDistanceLeftOf(final int column, final String[] notes) {
        return Arrays.stream(notes).map(line -> calculateHammingDistanceLeftOf(column, line)).reduce(UtMath::superOverflowSafeSum).orElseThrow();
    }

    private long calculateHammingDistanceLeftOf(final int column, final String line) {
        final int limit = Math.min(column, line.length() - column);
        return IntStream.range(0, limit).filter(i -> line.charAt(column - 1 - i) != line.charAt(column + i)).count();
    }
}
