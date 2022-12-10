package ch.thoenluk.solvers.challenge10;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.ArrayList;
import java.util.List;

public class RayTubeCathodiser implements ChristmasSaver {

    //---- Statics

    private static final String NOOP = "noop";
    private static final String ADDX = "addx";


    //---- Methods

    @Override
    public String saveChristmas(String input) {
        final List<Integer> xValues = generateXValues(input);
        int signalStrengths = 0;

        for (int cycle = 20; cycle <= 220; cycle += 40) {
            signalStrengths += cycle * xValues.get(cycle);
        }

        return Long.toString(signalStrengths);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final List<Integer> xValues = generateXValues(input);

        final StringBuilder display = new StringBuilder();

        for (int cycle = 1; cycle <= 240; cycle++) {
            int xAtCycle = xValues.get(cycle);
            int pixel = (cycle - 1) % 40;

            if (Math.abs(xAtCycle - pixel) <= 1) {
                display.append('#');
            }
            else {
                display.append('.');
            }

            if (pixel == 39) {
                display.append('\n');
            }
        }

        return display.toString();
    }

    private List<Integer> generateXValues(String input) {
        final String[] program = UtStrings.splitMultilineString(input);
        int x = 1;
        final List<Integer> xValues = new ArrayList<>(250);
        xValues.add(0); // It's supposed to be 1-indexed, so pad with a NULL.
        // I'm aware Integer 0 is not NULL but if we pretend we're on super low level it is.

        for (String instruction : program) {
            final String[] parts = instruction.split(UtStrings.WHITE_SPACE_REGEX);

            if (parts[0].equals(NOOP)) {
                xValues.add(x);
            }
            else if (parts[0].equals(ADDX)) {
                xValues.add(x);
                xValues.add(x);
                x += UtParsing.cachedParseInt(parts[1]);
            }
            else {
                throw new IllegalStateException();
            }
        }

        return xValues;
    }
}
