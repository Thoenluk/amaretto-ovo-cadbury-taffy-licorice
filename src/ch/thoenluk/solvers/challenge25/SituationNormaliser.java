package ch.thoenluk.solvers.challenge25;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtStrings;

public class SituationNormaliser implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);
        long total = 0;

        for (String line : lines) {
            total = UtMath.superOverflowSafeSum(total, snafuToDecimal(line));
        }

        final StringBuilder result = new StringBuilder();

        while (total != 0) {
            long fives = (total + 2) / 5;
            int digit = (int) (total - 5 * fives);

            result.insert(0, switch (digit) {
                case -2 -> '=';
                case -1 -> '-';
                case 0, 1, 2 -> Integer.toString(digit);
                default -> throw new IllegalStateException("Unexpected value: " + digit);
            });

            total = fives;
        }

        return result.toString();
    }

    @Override
    public String saveChristmasAgain(String input) {
        return null;
    }

    private long snafuToDecimal(String snafu) {
        long decimal = 0;
        long placeValue = 1;

        for (int place = snafu.length() - 1; place >= 0; place--) {
            decimal += placeValue * switch (snafu.charAt(place)) {
                case '=' -> -2;
                case '-' -> -1;
                case '0' -> 0;
                case '1' -> 1;
                case '2' -> 2;
                default -> throw new IllegalStateException("Unexpected value: " + snafu.charAt(place));
            };
            placeValue *= 5;
        }

        return decimal;
    }
}
