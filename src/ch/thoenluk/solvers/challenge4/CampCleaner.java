package ch.thoenluk.solvers.challenge4;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

public class CampCleaner implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] assignmentPairs = UtStrings.splitMultilineString(input);
        int containedRanges = 0;

        for (String assignmentPair : assignmentPairs) {
            final String[] assignments = assignmentPair.split(",");
            final Assignment first = Assignment.from(assignments[0]);
            final Assignment second = Assignment.from(assignments[1]);

            if (first.fullyContains(second) || second.fullyContains(first)) {
                containedRanges++;
            }
        }

        return Integer.toString(containedRanges);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] assignmentPairs = UtStrings.splitMultilineString(input);
        int overlappingRanges = 0;

        for (String assignmentPair : assignmentPairs) {
            final String[] assignments = assignmentPair.split(",");
            final Assignment first = Assignment.from(assignments[0]);
            final Assignment second = Assignment.from(assignments[1]);

            if (first.overlaps(second)) {
                overlappingRanges++;
            }
        }

        return Integer.toString(overlappingRanges);
    }

    private record Assignment(int start, int end) {
        public static Assignment from(String description) {
            final String[] points = description.split("-");
            return new Assignment(UtParsing.cachedParseInt(points[0]), UtParsing.cachedParseInt(points[1]));
        }

        public boolean fullyContains(Assignment other) {
            return contains(other.start) && contains(other.end);
        }

        public boolean overlaps(Assignment other) {
            return contains(other.start) || contains(other.end) || other.fullyContains(this);
        }

        private boolean contains(int point) {
            return start <= point && point <= end;
        }
    }
}
