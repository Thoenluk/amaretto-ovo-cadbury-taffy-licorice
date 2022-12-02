package ch.thoenluk.solvers.challenge2;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.solvers.challenge2.enums.Play;
import ch.thoenluk.ut.UtStrings;

public class TotallyNotRPSCheater implements ChristmasSaver {

    //---- Statics

    private static final char LOSE = 'X';
    private static final char DRAW = 'Y';
    private static final char WIN = 'Z';


    @Override
    public String saveChristmas(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);

        int totalScore = 0;

        for (String line : lines) {
            final String[] round = line.split(" ");
            final Play elfPlay = Play.fromChar(round[0].charAt(0));
            final Play myPlay = Play.fromChar(round[1].charAt(0));
            totalScore += myPlay.getScoreAgainst(elfPlay);
        }

        return Integer.toString(totalScore);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);

        int totalScore = 0;

        for (String line : lines) {
            final String[] round = line.split(" ");
            final Play elfPlay = Play.fromChar(round[0].charAt(0));
            final char strategy = round[1].charAt(0);
            totalScore += getStrategyGuidePlay(elfPlay, strategy).getScoreAgainst(elfPlay);
        }

        return Integer.toString(totalScore);
    }

    private Play getStrategyGuidePlay(Play elfPlay, char strategy) {
        return switch (strategy) {
            case LOSE -> elfPlay.getPlayThisDefeats();
            case DRAW -> elfPlay;
            case WIN -> elfPlay.getPlayDefeatingThis();
            default -> throw new IllegalStateException("Unexpected value: " + strategy);
        };
    }
}
