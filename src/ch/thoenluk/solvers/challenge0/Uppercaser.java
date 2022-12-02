package ch.thoenluk.solvers.challenge0;

import ch.thoenluk.ChristmasSaver;

/**
 *
 * @author Lukas Thöni lukas.thoeni@gmx.ch
 */
public class Uppercaser implements ChristmasSaver {

    @Override
    public String saveChristmas(String input) {
        return input.toUpperCase();
    }

    @Override
    public String saveChristmasAgain(String input) { return input.toUpperCase(); }
}
