package ch.thoenluk.solvers.challenge2.enums;

public enum Play {
    ROCK(1),
    PAPER(2),
    SCISSORS(3);

    //---- Static methods

    public static Play fromChar(char character) {
        return switch (character) {
            case 'A', 'X' -> ROCK;
            case 'B', 'Y' -> PAPER;
            case 'C', 'Z' -> SCISSORS;
            default -> throw new IllegalStateException("Unexpected value: " + character);
        };
    }

    //---- Fields

    final int score;


    //---- Constructor

    Play(int score) {
        this.score = score;
    }


    //---- Methods

    public int getScoreAgainst(Play other) {
        return score + getResultScoreAgainst(other);
    }

    // Can't do forward references from ROCK to PAPER :<
    public Play getPlayDefeatingThis() {
        return switch (this) {
            case ROCK -> PAPER;
            case PAPER -> SCISSORS;
            case SCISSORS -> ROCK;
        };
    }

    public Play getPlayThisDefeats() {
        return switch (this) {
            case ROCK -> SCISSORS;
            case PAPER -> ROCK;
            case SCISSORS -> PAPER;
        };
    }

    private int getResultScoreAgainst(Play other) {
        if (getPlayThisDefeats() == other) {
            return 6;
        }
        else if (this == other) {
            return 3;
        }
        return 0;
    }
}
