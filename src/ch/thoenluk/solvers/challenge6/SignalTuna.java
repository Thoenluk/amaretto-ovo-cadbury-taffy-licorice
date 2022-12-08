package ch.thoenluk.solvers.challenge6;

import ch.thoenluk.ChristmasSaver;

import java.util.HashSet;
import java.util.Set;

public class SignalTuna implements ChristmasSaver {

    //---- Statics

    private static final int PACKET_MARKER_SIZE = 4;
    private static final int MESSAGE_MARKER_SIZE = 14;


    //---- Methods

    @Override
    public String saveChristmas(String input) {
        return Integer.toString(findStartMarker(input, PACKET_MARKER_SIZE));
    }

    @Override
    public String saveChristmasAgain(String input) {
        return Integer.toString(findStartMarker(input, MESSAGE_MARKER_SIZE));
    }

    private int findStartMarker(String input, int markerSize) {
        final Set<Character> previousCharacters = new HashSet<>();
        boolean isSOPMarker;

        for (int position = markerSize - 1; position < input.length(); position++) {
            isSOPMarker = true;
            previousCharacters.clear();

            for (int indexInMarker = 0; indexInMarker < markerSize; indexInMarker++) {
                isSOPMarker &= previousCharacters.add(input.charAt(position - indexInMarker));
            }

            if (isSOPMarker) {
                return position + 1;
            }
        }

        throw new IllegalArgumentException("The given input string does not seem to contain the desired marker.");
    }
}
