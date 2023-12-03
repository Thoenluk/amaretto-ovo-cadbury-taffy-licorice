package ch.thoenluk.solvers.challenge2;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class CubePossibilinator implements ChristmasSaver {

    private static final String NUMBER_PATTERN = "(\\d+)";

    @Override
    public String saveChristmas(String input) {
        return Arrays.stream(UtStrings.splitMultilineString(input))
                .map(Game::fromString)
                .filter(this::isPossible)
                .map(Game::id)
                .reduce(UtMath::overflowSafeSum)
                .orElseThrow()
                .toString();
    }

    private boolean isPossible(Game game) {
        return game.red() <= 12
                && game.green() <= 13
                && game.blue() <= 14;
    }

    @Override
    public String saveChristmasAgain(String input) {
        return Arrays.stream(UtStrings.splitMultilineString(input))
                .map(Game::fromString)
                .map(Game::power)
                .reduce(UtMath::overflowSafeSum)
                .orElseThrow()
                .toString();
    }

    private record Game(int id, int red, int green, int blue) {
        public static Game fromString(String description) {
            final int id = extractMatchedGroup("Game " + NUMBER_PATTERN, description).findFirst().orElseThrow();
            final int red = extractMatchedGroup(NUMBER_PATTERN + " red", description).max().orElse(0);
            final int green = extractMatchedGroup(NUMBER_PATTERN + " green", description).max().orElse(0);
            final int blue = extractMatchedGroup(NUMBER_PATTERN + " blue", description).max().orElse(0);
            return new Game(id, red, green, blue);
        }

        private static IntStream extractMatchedGroup(String pattern, String description) {
            return Pattern.compile(pattern).matcher(description).results()
                    .map(mr -> mr.group(1))
                    .mapToInt(UtParsing::cachedParseInt);
        }

        public int power() {
            return UtMath.overflowSafeProduct(red(), green(), blue());
        }
    }
}
