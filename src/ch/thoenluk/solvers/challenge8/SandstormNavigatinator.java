package ch.thoenluk.solvers.challenge8;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtStrings;

import java.util.List;

public class SandstormNavigatinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] instructions = UtStrings.splitStringWithEmptyLines(input);

        UtStrings.streamInputAsLines(instructions[1])
                .forEach(LabelledNode::integrate);

        final String directions = instructions[0];

        return Long.toString(traversePath(directions, LabelledNode.getFromNetwork("AAA")));
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] instructions = UtStrings.splitStringWithEmptyLines(input);

        final String directions = instructions[0];
        List<LabelledNode> nodes = UtStrings.streamInputAsLines(instructions[1])
                .map(LabelledNode::integrate)
                .filter(LabelledNode::isStart)
                .toList();

        final List<Long> loopLengths = nodes.stream()
                .map(node -> traversePath(directions, node))
                .toList();

        return Long.toString(UtMath.lcm(loopLengths));
        /*
         * Wow, that was disappointingly straightforward.
         * It happens that in this input, while paths don't necessarily return to their start point, they take exactly
         * as long to travel from start to end as they do to travel from end back to end.
         * This doesn't have to be; Imagine a path that takes 1000 steps to then travel back and forth between XXX and ZZZ.
         * Likewise, a path visits precisely one end node repeatedly. Imagine a path that loops every 8 steps, visiting
         * 11Z on step 8 but also stepping on 22Z on step 3, or 11Z but at a different point in the directions and thus
         * taking another path after 11Z. This path may end at either 8n or 8n + 3.
         * It would be an absolute nightmare to calculate to be sure; Loops with multiple end points would require tracking
         * all viable end points and finding the intersection of all paths' end point lists, while loops with startup times
         * would require finding both the startup and loop length, then using the Chinese Remainder Theorem to work out
         * at which step they all terminate simultaneously.
         * Wikipedia the CRT, it's a handy thing to know. While not exactly taught in grade school (where I live), it's
         * not unreasonable to demand of people with the technical prowess to complete the AoC. I know AoC agrees because
         * *it was required knowledge last year*.
         * Having both complications at the same time would be a massively huge pain that I don't know offhand how to
         * solve besides solving the CRT for, well, all combinations of all end points. That may be a bit much. But is
         * it too much to ask for one? To make it interesting beyond knowing the LCM exists?
         */
    }

    private long traversePath(final String directions, final LabelledNode start) {
        LabelledNode node = start;
        long steps = 0;
        int i = 0;

        while (!node.isEnd()) {
            node = directions.charAt(i) == 'L' ? node.getLeft() : node.getRight();
            steps++;
            i++;
            if (i == directions.length()) {
                i = 0;
            }
        }
        return steps;
    }
}
