package ch.thoenluk.solvers.challenge6;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

public class BoyToatinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);
        final Pattern pattern = Pattern.compile(UtStrings.NUMBERS_REGEX);
        final List<Integer> times = new LinkedList<>(pattern.matcher(lines[0]).results()
                .map(MatchResult::group)
                .map(UtParsing::cachedParseInt)
                .toList());
        return pattern.matcher(lines[1]).results()
                .map(MatchResult::group)
                .map(UtParsing::cachedParseInt)
                .map(distance -> getNumberOfWaysToWin(times.remove(0), distance))
                .reduce(UtMath::superOverflowSafeProduct)
                .orElseThrow()
                .toString();
    }

    private long getNumberOfWaysToWin(final long time, final long distance) {
        return LongStream.range(0, time)
                .map(holdingTime -> holdingTime * (time - holdingTime))
                .filter(distanceTravelled -> distanceTravelled > distance)
                .count();
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);
        final long time = UtParsing.cachedParseLong(lines[0].replaceAll("\\D", ""));
        final long distance = UtParsing.cachedParseLong(lines[1].replaceAll("\\D", ""));
        return Long.toString(getNumberOfWaysToWin(time, distance)); // Don't need smart when you have enough brute force.
        // The answer would probably be to determine the least and greatest holding times that win, then the answer is
        // their difference. Given distance and time are fixed constants, you can even solve for holdingTime directly:
        // distanceTravelled = time * holdingTime - holdingTime^2 and distanceTravelled >! distance, therefore
        // holdingTime^2 - time * holdingTime + distance > 0. Apply your favorite solving algorithm (mine is WolframAlpha)
        // and there's yer answer. The way the example is phrased even hints that the answer is a difference.
        // Apparently it's x > 30_854_533 + 2 * sqrt(77_220_948_256_762) and x < 30_854_533 - 2 * sqrt(77_220_948_256_762).
        // However, that's nerd sh!t and this returns in 62ms, so I'm sending this to code review lmao.
    }
}
