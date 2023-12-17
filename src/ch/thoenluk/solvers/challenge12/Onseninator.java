package ch.thoenluk.solvers.challenge12;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtStrings;

public class Onseninator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
//        UtStrings.streamInputAsLines(input)
//                .map(ConditionRecord::fromString)
//                .filter(cr -> cr.getPossibleArrangements() != cr.bruteforceArrangements())
//                .forEach(cr -> UtStrings.println(cr + String.format(" where possibleArrangements is %d but brute force is %d!", cr.getPossibleArrangements(), cr.bruteforceArrangements())));

        return UtMath.restOfTheLongOwl(UtStrings.streamInputAsLines(input)
                .map(ConditionRecord::fromString)
                .map(ConditionRecord::getPossibleArrangements)
        );
    }

    @Override
    public String saveChristmasAgain(String input) {
        return UtMath.restOfTheLongOwl(UtStrings.streamInputAsLines(input)
                .map(ConditionRecord::fromStringUnfolded)
                .map(ConditionRecord::getPossibleArrangements)
        );
        // I wrote lengthy comments for the others, so I may as well.
        // This one's a bit of a bastard because once again the example implies something that isn't true about your input.
        // For each line in the example, the broken springs do not interact meaningfully between the repetitions.
        // i.e. the number of arrangements is given by n*k^x, where x is the number of times you repeat the line.
        // Even with a relatively brute forcey approach, it's easy to calculate this for the original line and for the
        // line repeated once, so x = 0 and x = 1. This means, because math, by folding our input once we can determine
        // both n and k.
        // I'm not sure why, I don't think there is an easy way to define it mathematically, but sometimes the arrangements
        // buck this trend. This is bad because some lines because untenable for x = 2, so with only two data points
        // you can't really describe more than a line.
        // I'm leaving in the ultimately unused code, but it boils down to using subtree pruning instead of any clever math.
        // Which is a bit of a shame because noticing the formula every example follows is really cool.
        // Also, I hate every challenge in which it's not reasonable to manually calculate a (part of a) result but
        // it depends on an edge case the examples don't address so you have to just submit it and hope.
    }

}
