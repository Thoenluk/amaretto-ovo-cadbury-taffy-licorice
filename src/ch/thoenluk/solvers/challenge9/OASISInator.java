package ch.thoenluk.solvers.challenge9;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.ArrayList;
import java.util.List;

public class OASISInator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        return UtMath.restOfTheLongOwl(UtStrings.streamInputAsLines(input)
                        .map(UtParsing::whitespaceSeparatedStringToLongList)
                        .map(ArrayList::new)
                        .map(this::getNextValue));
    }

    private long getNextValue(final List<Long> sequence) {
        if (sequence.stream().allMatch(value -> value == 0)) {
            return 0;
        }
        return sequence.get(sequence.size() - 1) + getNextValue(generateDifferences(sequence));
    }

    @Override
    public String saveChristmasAgain(String input) {
        return UtMath.restOfTheLongOwl(UtStrings.streamInputAsLines(input)
                .map(UtParsing::whitespaceSeparatedStringToLongList)
                .map(ArrayList::new)
                .map(this::getPreviousValue));
        // The harder challenge was to do literally the same thing but with one index swapped? What?
    }

    private long getPreviousValue(final List<Long> sequence) {
        if (sequence.stream().allMatch(value -> value == 0)) {
            return 0;
        }
        return sequence.get(0) - getPreviousValue(generateDifferences(sequence));
    }

    private List<Long> generateDifferences(final List<Long> sequence) {
        final List<Long> differences = new ArrayList<>(sequence.size() - 1);
        for (int i = 0; i < sequence.size() - 1; i++) {
            differences.add(sequence.get(i + 1) - sequence.get(i));
        }
        return differences;
    }
}
