package ch.thoenluk.solvers.challenge1;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.Map;

public class TrebuchetCalibratinator implements ChristmasSaver {

    private static final Map<String, String> NUMBER_WORDS = Map.of(
            "one", "1",
            "two", "2",
            "three", "3",
            "four", "4",
            "five", "5",
            "six", "6",
            "seven", "7",
            "eight", "8",
            "nine", "9",
            "zero", "0"
    );

    @Override
    public String saveChristmas(String input) {
        return UtMath.restOfTheOwl(UtStrings.streamInputAsLines(input)
                .map(this::getCalibrationValue)
                .map(UtParsing::cachedParseInt));
    }

    private String getCalibrationValue(final String line) {
        final String digits = line.replaceAll("\\D", "");
        return digits.charAt(0) + "" + digits.charAt(digits.length() - 1);
    }

    @Override
    public String saveChristmasAgain(String input) {
        return UtMath.restOfTheOwl(UtStrings.streamInputAsLines(input)
                .map(this::replaceNumberWordsWithDigits)
                .map(this::getCalibrationValue)
                .map(UtParsing::cachedParseInt));
    }

    private String replaceNumberWordsWithDigits(final String line) {
        String result = line;
        for (int i = 0; i < result.length(); i++) {
            result = replaceNumberWordsAt(result, i);
        }
        return result;
    }

    private String replaceNumberWordsAt(final String toReplaceIn, final int index) {
        for (final String numberWord : NUMBER_WORDS.keySet()) {
            if (isSubstringAt(toReplaceIn, index, numberWord)) {
                return spliceDigit(toReplaceIn, index, numberWord);
            }
        }
        return toReplaceIn;
    }

    private boolean isSubstringAt(final String containing, final int index, final String substring) {
        return containing.startsWith(substring, index);
    }

    private String spliceDigit(final String toBeSpliced, final int index, final String numberWord) {
        return toBeSpliced.substring(0, index) + NUMBER_WORDS.get(numberWord) + toBeSpliced.substring(index + 1);
    }
}
