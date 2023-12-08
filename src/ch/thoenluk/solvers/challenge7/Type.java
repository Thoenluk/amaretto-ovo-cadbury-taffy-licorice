package ch.thoenluk.solvers.challenge7;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/* package private */  enum Type {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND;

    public static Type fromCards(final List<Card> cards) {
        final List<Integer> occurences = new ArrayList<>(cards.stream()
                .filter(card -> !card.equals(Card.JOKER))
                .distinct()
                .map(card -> getOccurences(card, cards))
                .map(Long::intValue)
                .sorted(Comparator.reverseOrder())
                .toList());
        if (occurences.isEmpty()) {
            occurences.add(0);
        }
        final int numberOfJokers = (int) cards.stream()
                .filter(Card.JOKER::equals)
                .count();
        occurences.set(0, occurences.get(0) + numberOfJokers);
        switch (occurences.get(0)) {
            case 5:
                return FIVE_OF_A_KIND;
            case 4:
                return FOUR_OF_A_KIND;
            case 3:
                if (occurences.get(1) == 2) {
                    return FULL_HOUSE;
                }
                return THREE_OF_A_KIND;
            case 2:
                if (occurences.get(1) == 2) {
                    return TWO_PAIR;
                }
                return ONE_PAIR;
            default:
                return HIGH_CARD;
        }
    }

    private static long getOccurences(final Card card, final List<Card> cards) {
        return cards.stream()
                .filter(card1 -> card1.equals(card))
                .count();
    }
}
