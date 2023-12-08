package ch.thoenluk.solvers.challenge7;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.List;
import java.util.stream.IntStream;

public class CardCamelinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final List<Hand> hands = UtStrings.streamInputAsLines(input)
                .map(Hand::fromString)
                .sorted()
                .toList();
        return UtMath.restOfTheOwl(IntStream.range(0, hands.size())
                .map(index -> hands.get(index).bid() * (index + 1)));
    }

    @Override
    public String saveChristmasAgain(String input) {
        final List<Hand> hands = UtStrings.streamInputAsLines(input)
                .map(Hand::fromStringWithJokers)
                .sorted()
                .toList();
        return UtMath.restOfTheOwl(IntStream.range(0, hands.size())
                .map(index -> hands.get(index).bid() * (index + 1)));
    }

    private record Hand(List<Card> cards, Type type, int bid) implements Comparable<Hand> {
        public static Hand fromString(final String line) {
            final String[] pieces = line.split(UtStrings.WHITE_SPACE_REGEX);
            final List<Card> cards = pieces[0].chars()
                    .mapToObj(c -> Card.fromChar((char) c))
                    .toList();
            final Type type = Type.fromCards(cards);
            final int bid = UtParsing.cachedParseInt(pieces[1]);
            return new Hand(cards, type, bid);
        }

        public static Hand fromStringWithJokers(final String line) {
            return fromString(line.replaceAll("J", "R"));
        }

        @Override
        public int compareTo(final Hand other) {
            if (this.type() != other.type()) {
                return this.type().compareTo(other.type());
            }
            return IntStream.range(0, cards().size())
                    .map(index -> cards().get(index).compareTo(other.cards().get(index)))
                    .filter(order -> order != 0)
                    .findFirst()
                    .orElse(0);
        }
    }
}
