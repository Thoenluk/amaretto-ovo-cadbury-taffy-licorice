package ch.thoenluk.solvers.challenge15;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TotallyNotProgrammingLanguaginator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        return UtMath.restOfTheOwl(Arrays.stream(input.replaceAll(UtStrings.NEWLINE_REGEX, "").split(","))
                .map(HolidayAsciiStringHelper::new)
                .map(HolidayAsciiStringHelper::hashCode));
    }

    @Override
    public String saveChristmasAgain(String input) {
        final Map<Integer, LinkedHashMap<String, Lens>> boxes = new HashMap<>();
        final String[] instructions = input.replaceAll(UtStrings.NEWLINE_REGEX, "").split(",");
        for (final String instruction : instructions) {
            final HolidayAsciiStringHelper boxHelper = new HolidayAsciiStringHelper(toLabel(instruction));
            final Map<String, Lens> box = boxes.computeIfAbsent(boxHelper.hashCode(), k -> new LinkedHashMap<>());
            if (isPut(instruction)) {
                final Lens lens = Lens.fromString(instruction);
                box.put(lens.label(), lens);
            }
            else {
                box.remove(boxHelper.content());
            }
        }

        return UtMath.restOfTheOwl(boxes.entrySet().stream()
                .map(entry -> calculateFocusingPowers(entry.getKey(), entry.getValue())));
    }

    private String toLabel(final String instruction) {
        return instruction.replaceAll("[^a-z]", "");
    }

    private boolean isPut(final String instruction) {
        return instruction.contains("=");
    }

    private int calculateFocusingPowers(final int boxNumber, final LinkedHashMap<String, Lens> lenses) {
        int index = 1;
        int sum = 0;
        for (final Lens lens : lenses.values()) {
            final int focusingPower = (1 + boxNumber) * index * lens.focalLength();
            sum = UtMath.overflowSafeSum(sum, focusingPower);
            index++;
        }
        return sum;
    }

    private record HolidayAsciiStringHelper(String content) {
        @Override
        public int hashCode() {
            return content.chars()
                    .reduce(0, (hash, character) -> {
                        hash += character;
                        hash *= 17;
                        hash %= 256;
                        return hash;
                    });
        }
    }

    private record Lens(String label, int focalLength) {
        public static Lens fromString(String description) {
            final String[] pieces = description.split("=");
            return new Lens(pieces[0], UtParsing.cachedParseInt(pieces[1]));
        }
    }
}
