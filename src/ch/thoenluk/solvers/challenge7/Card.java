package ch.thoenluk.solvers.challenge7;

import java.util.Arrays;

/* package private */ enum Card {
    JOKER('R'),
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),
    TEN('T'),
    JACK('J'),
    QUEEN('Q'),
    KING('K'),
    ACE('A');

    private final char charRepresentation;

    Card(final char charRepresentation) {
        this.charRepresentation = charRepresentation;
    }

    public static Card fromChar(final char charRepresentation) {
        return Arrays.stream(values())
                .filter(card -> card.charRepresentation == charRepresentation)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No Card that is represented by %s exists!", charRepresentation)));
    }
}
