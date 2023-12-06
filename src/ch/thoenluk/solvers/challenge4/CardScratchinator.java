package ch.thoenluk.solvers.challenge4;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CardScratchinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        return UtMath.restOfTheOwl(UtStrings.streamInputAsLines(input)
                .map(ScratchCard::fromString)
                .map(ScratchCard::getScore));
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);
        final int[] cardCounts = new int[lines.length];
        Arrays.fill(cardCounts, 1);
        Arrays.stream(lines)
                .map(ScratchCard::fromString)
                .forEach(scratchCard -> processWinnings(scratchCard, cardCounts));
        return UtMath.restOfTheOwl(Arrays.stream(cardCounts));
    }

    private void processWinnings(ScratchCard scratchCard, int[] cardCounts) {
        final long matchingNumbbers = scratchCard.getMatchingNumbersCount();
        final int index = scratchCard.cardNumber() - 1;
        final int numberOfScratchCards = cardCounts[index];
        for (int i = 0; i < matchingNumbbers; i++) {
            cardCounts[i + scratchCard.cardNumber()] += numberOfScratchCards;
        }
    }

    private record ScratchCard(int cardNumber, Set<Integer> winningNumbers, Set<Integer> haveNumbers) {
        public static ScratchCard fromString(String line) {
            final String[] pieces = line.split("[:|]");
            return new ScratchCard(parseCardnumber(pieces[0]), parsePiece(pieces[1]), parsePiece(pieces[2]));
        }

        private static int parseCardnumber(final String piece) {
            return UtParsing.cachedParseInt(piece.replaceAll("\\D", ""));
        }

        private static Set<Integer> parsePiece(final String piece) {
            return Arrays.stream(piece.split(" "))
                    .filter(s -> !s.isBlank())
                    .map(UtParsing::cachedParseInt)
                    .collect(Collectors.toSet());
        }

        public int getScore() {
            return (int) Math.pow(2, getMatchingNumbersCount() - 1);
        }

        public long getMatchingNumbersCount() {
            return haveNumbers().stream()
                    .filter(winningNumbers()::contains)
                    .count();
        }
    }
}
