package ch.thoenluk.solvers.challenge12;

import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;
import java.util.stream.IntStream;

final class ConditionRecord {
    private final String springs;
    private final List<Integer> damagedSprings;
    private final Map<PathFragment, Long> seenSubpaths;

    private ConditionRecord(String springs, List<Integer> damagedSprings, Map<PathFragment, Long> seenSubpaths) {
        this.springs = springs;
        this.damagedSprings = damagedSprings;
        this.seenSubpaths = seenSubpaths;
    }

    public static ch.thoenluk.solvers.challenge12.ConditionRecord fromString(final String line) {
        final String[] pieces = line.split(UtStrings.WHITE_SPACE_REGEX);
        return new ch.thoenluk.solvers.challenge12.ConditionRecord(pieces[0], UtParsing.commaSeparatedStringToIntegerList(pieces[1]), new HashMap<>());
    }

    public static ch.thoenluk.solvers.challenge12.ConditionRecord fromStringUnfolded(final String line) {
        final String[] pieces = line.split(UtStrings.WHITE_SPACE_REGEX);
        final StringBuilder springs = new StringBuilder();
        final List<Integer> foldedDamagedSprings = UtParsing.commaSeparatedStringToIntegerList(pieces[1]);
        final List<Integer> damagedSprings = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            springs.append(pieces[0]);
            if (i < 4) {
                springs.append('?');
            }
            damagedSprings.addAll(foldedDamagedSprings);
        }
        return new ch.thoenluk.solvers.challenge12.ConditionRecord(springs.toString(), damagedSprings, new HashMap<>());
    }

    public long getPossibleArrangements() {
        return getArrangementsStartingAt(0, damagedSprings());
    }

    public int bruteforceArrangements() {
        final boolean[] springsDamaged = new boolean[springs().length()];
        final List<Integer> uncertainIndices = new LinkedList<>();
        for (int i = 0; i < springsDamaged.length; i++) {
            if (springs().charAt(i) == '?') {
                uncertainIndices.add(i);
            } else {
                springsDamaged[i] = springs().charAt(i) == '#';
            }
        }
        int arrangements = 0;
        final long permutations = 1L << uncertainIndices.size();
        for (long permutation = 0; permutation < permutations; permutation++) {
            arrangeSpringsDamaged(springsDamaged, permutation, uncertainIndices);
            final List<Integer> groups = getGroups(springsDamaged);
            if (groups.equals(damagedSprings)) {
                arrangements++;
            }
        }
        return arrangements;
    }

    public long getUnfoldedArrangements() {
        final long onceUnfoldedArrangements = unfoldOnce().getPossibleArrangements();
        final long arrangements = getPossibleArrangements();
        final double unfoldingFactor = (double) onceUnfoldedArrangements / arrangements;
        long unfoldedArrangements = (long) (arrangements * Math.pow(unfoldingFactor, 4));
        UtStrings.println(String.format("%s from %d to %d with %f to %d", this, arrangements, onceUnfoldedArrangements, unfoldingFactor, unfoldedArrangements));
        return unfoldedArrangements;
    }

    private ch.thoenluk.solvers.challenge12.ConditionRecord unfoldOnce() {
        final String unfoldedSprings = springs() + '?' + springs();
        final List<Integer> unfoldedDamagedSprings = new LinkedList<>();
        unfoldedDamagedSprings.addAll(damagedSprings());
        unfoldedDamagedSprings.addAll(damagedSprings());
        return new ch.thoenluk.solvers.challenge12.ConditionRecord(unfoldedSprings, unfoldedDamagedSprings, new HashMap<>());
    }

    private void arrangeSpringsDamaged(final boolean[] springsDamaged, final long permutation, final List<Integer> uncertainIndices) {
        for (int i = 0; i < uncertainIndices.size(); i++) {
            springsDamaged[uncertainIndices.get(i)] = (permutation & (1L << i)) >= 1;
        }
    }

    private List<Integer> getGroups(final boolean[] springsDamaged) {
        final List<Integer> groups = new LinkedList<>();
        int groupLength = 0;
        for (boolean b : springsDamaged) {
            if (b) {
                groupLength++;
            } else if (groupLength > 0) {
                groups.add(groupLength);
                groupLength = 0;
            }
        }
        if (groupLength > 0) {
            groups.add(groupLength);
        }
        return groups;
    }

    private long getArrangementsStartingAt(final int startingIndex, final List<Integer> remainingGroups) {
        if (remainingGroups.isEmpty()) {
            return springs().indexOf('#', startingIndex) == -1 ? 1 : 0;
        }
        final PathFragment subpath = new PathFragment(startingIndex, remainingGroups);
        if (seenSubpaths.containsKey(subpath)) {
            return seenSubpaths.get(subpath);
        }
        final int groupLength = remainingGroups.get(0);
        final List<Integer> furtherGroups = remainingGroups.subList(1, remainingGroups.size());
        final long arrangements = IntStream.range(startingIndex, springs().length())
                .filter(i -> doesNotSkipNextDamagedSpring(startingIndex, i))
                .filter(i -> canPlaceGroupAt(i, groupLength))
                .boxed()
                .map(i -> getArrangementsStartingAt(i + groupLength + 1, furtherGroups))
                .reduce(UtMath::superOverflowSafeSum)
                .orElse(0L);
        seenSubpaths.put(subpath, arrangements);
        return arrangements;
    }

    private boolean canPlaceGroupAt(final int index, final int groupLength) {
        if (!groupCanStartAt(index) || !groupCanEndAt(index + groupLength) || index + groupLength > springs().length()) {
            return false;
        }
        for (int spring = 0; spring < groupLength; spring++) {
            if (!isSpringPossiblyDamaged(index + spring)) {
                return false;
            }
        }
        return true;
    }

    private boolean groupCanStartAt(final int index) {
        return index == 0 || isSpringPossiblySafe(index - 1);
    }

    private boolean groupCanEndAt(final int index) {
        return index >= springs().length() || isSpringPossiblySafe(index);
    }

    private boolean isSpringPossiblyDamaged(final int index) {
        return springs().charAt(index) == '#' || springs().charAt(index) == '?';
    }

    private boolean isSpringPossiblySafe(final int index) {
        return springs().charAt(index) == '.' || springs().charAt(index) == '?';
    }

    private boolean doesNotSkipNextDamagedSpring(final int startingIndex, final int index) {
        final int nextDamagedSpringIndex = springs().indexOf('#', startingIndex);
        return nextDamagedSpringIndex == -1 || index <= nextDamagedSpringIndex;
    }

    public String springs() {
        return springs;
    }

    public List<Integer> damagedSprings() {
        return damagedSprings;
    }

    public Map<PathFragment, Long> seenSubpaths() {
        return seenSubpaths;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ch.thoenluk.solvers.challenge12.ConditionRecord) obj;
        return Objects.equals(this.springs, that.springs) &&
                Objects.equals(this.damagedSprings, that.damagedSprings) &&
                Objects.equals(this.seenSubpaths, that.seenSubpaths);
    }

    @Override
    public int hashCode() {
        return Objects.hash(springs, damagedSprings, seenSubpaths);
    }

    @Override
    public String toString() {
        return "ConditionRecord[" +
                "springs=" + springs + ", " +
                "damagedSprings=" + damagedSprings + ", " +
                "seenSubpaths=" + seenSubpaths + ']';
    }

    private record PathFragment(int startingIndex, List<Integer> remainingGroups) {}

}
