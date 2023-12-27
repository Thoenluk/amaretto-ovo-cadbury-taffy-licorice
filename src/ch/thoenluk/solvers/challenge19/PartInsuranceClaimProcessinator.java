package ch.thoenluk.solvers.challenge19;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtStrings;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PartInsuranceClaimProcessinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] workflowsAndParts = UtStrings.splitStringWithEmptyLines(input);
        final Map<String, Workflow> workflows = Arrays.stream(UtStrings.splitMultilineString(workflowsAndParts[0]))
                .map(Workflow::fromString)
                .collect(Collectors.toMap(Workflow::label, workflow -> workflow));
        final List<Part> parts = Arrays.stream(UtStrings.splitMultilineString(workflowsAndParts[1]))
                .map(Part::fromString)
                .toList();
        return UtMath.restOfTheOwl(parts.stream().filter(part -> isAccepted(part, workflows)).map(Part::calculateTotalRating));
    }

    final boolean isAccepted(final Part part, final Map<String, Workflow> workflows) {
        String workflowLabel = "in";
        while (true) {
            final Workflow workflow = workflows.get(workflowLabel);
            workflowLabel = workflow.process(part);
            if (workflowLabel.equals("A")) {
                return true;
            }
            else if (workflowLabel.equals("R")) {
                return false;
            }
        }
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] workflowsAndParts = UtStrings.splitStringWithEmptyLines(input);
        final Map<String, Workflow> workflows = Arrays.stream(UtStrings.splitMultilineString(workflowsAndParts[0]))
                .map(Workflow::fromString)
                .collect(Collectors.toMap(Workflow::label, workflow -> workflow));
        UtStrings.println();
        final List<AcceptedRange> acceptedRanges = findPathsFrom(workflows.get("in"), workflows).stream()
                .map(AcceptedRange::fromPath)
                .toList();
        final List<AcceptedRange> distinctRanges = acceptedRanges.stream()
                .filter(acceptedRange -> acceptedRanges.stream().noneMatch(acceptedRange::isContainedIn))
                .toList();
        final List<List<AcceptedRange>> nthDegreeOverlaps = new LinkedList<>();
        nthDegreeOverlaps.add(distinctRanges);
        List<AcceptedRange> overlaps;
        do {
            final List<AcceptedRange> rangesToOverlap = nthDegreeOverlaps.get(nthDegreeOverlaps.size() - 1);
            overlaps = new ArrayList<>();
            for (int i = 0; i < rangesToOverlap.size(); i++) {
                for (int j = i + 1; j < rangesToOverlap.size(); j++) {
                    final AcceptedRange intersection = rangesToOverlap.get(i).determineIntersection(rangesToOverlap.get(j));
                    overlaps.add(intersection);
                    if (intersection != null) {
                        UtStrings.println("Found intersection between:");
                        UtStrings.println(rangesToOverlap.get(i));
                        UtStrings.println(rangesToOverlap.get(j));
                        UtStrings.println("which is: ");
                        UtStrings.println(intersection);
                        UtStrings.println();
                    }
                }
            }
            overlaps = overlaps.stream()
                        .filter(Objects::nonNull)
                        .filter(acceptedRange -> acceptedRanges.stream().noneMatch(acceptedRange::isContainedIn))
                        .toList();
            nthDegreeOverlaps.add(overlaps);
        } while (!overlaps.isEmpty());
        return UtMath.restOfTheLongOwl(
                IntStream.range(0, nthDegreeOverlaps.size())
                .mapToLong(i -> {
                            boolean seen = false;
                            Long acc = null;
                            for (AcceptedRange acceptedRange : nthDegreeOverlaps.get(i)) {
                                Long calculateSize = acceptedRange.calculateSize();
                                if (!seen) {
                                    seen = true;
                                    acc = calculateSize;
                                } else {
                                    acc = UtMath.superOverflowSafeSum(acc, calculateSize);
                                }
                            }
                            return (long) Math.pow(-1, i) * (seen ? acc : 0L);
                        }
                )
        );
        // You'll note this doesn't work. And I don't know why.
        // On account of my time regrettably being of limited quantity, I can't be bothered finding the exact implementation
        // flaw when it won't get me anything meaningful - learning.
        //
        // The concept is sound, I know it is. Consider the AcceptedRanges to be cuboids in a four-dimensional coordinate
        // system. It is then trivial to see that the number of accepted combinations equals their total volume accounting
        // for overlaps by subtracting the overlapped volume - but then, re-adding any double overlapped volume, and so on.
        //
        // It doesn't even have to be that complicated. The failing example features only single overlaps because it features
        // only three cuboids that aren't fully contained within one another.
        //
        // While AoC is strictly a competition, it isn't for me and software development is the exact opposite of one.
        // Part of it is knowing to ask for help, whether the other person knows more or is just a fresh look.
        // I could of course spend about two hours poring over others' solutions and then implementing my own by tracing
        // theirs, but what's the point in that. THAT is just plagiarism.
        //
        // Thus does this challenge earn this year's spot of the one so needlessly overcomplicated that it cannot be
        // reasonably debugged and thus made me throw 'em up and run it through someone else's solver.
        //
        // Before we go, I'd actually speak a word of criticism: Did it truly need to be four-dimensional? Going to three
        // would make part 1 easier, yes, but it's not exactly hard as it is. There are 2^16 * 10^12 points in the solution
        // space, which can be achieved just as incalculably with three dimensions going 1 - 320_000.
        //
        // The point being, staying in three or even two dimensions would leave us with something humans can literally
        // at all imagine and greatly reduce the needed complexity i.e. code volume and therefore space for dumb bugs.
        // A challenge should be simple enough to implement once you know what you're actually doing, whether that be
        // treating the ranges as cuboids, implementing proper subtree pruning, treating the pits as cuboid, treating
        // the pipe loop as a cuboid - say, there's a lot of challenges that work like this in 2023 huh.
        // The more complex the solution must be, not only the longer does it take to write despite knowing what you have
        // to do, the many more opportunities are there for dumb bugs, and the harder are they to find and remove.
        // Both inherently, and because it becomes more complex to build test cases for yourself.
        // See The Worst Puzzle of All-Time: https://www.youtube.com/watch?v=42SDc2Fhkm8
        //
        // On looking back, it shouldn't be impossible to treat the ranges as vertex-based cuboids rather than still
        // pretending they had limits, and combining any that overlap. Then the solution is to calculate these convex
        // axis-aligned polygons' volume knowing that any which would overlap have been merged. Simple enough, but have
        // fun implementing a four-coordinate cross product. Easy, but time-consuming and prone to bugs from sheer number
        // of coordinates, given I spent a good chunk of time with the ranges' constructor arguments in the wrong order.
        // (Side note: This is why any constructor with more than at most 2 arguments, ESPECIALLY of the same type,
        // should be replaced with a builder. No exceptions. I'm counting this as a learning and therefore a success in
        // this challenge after all.
        //
        // Lastly: Yes these grapes are quite sour thank you for your concern.
    }

    private List<List<Rule>> findPathsFrom(final Workflow workflow, final Map<String, Workflow> workflows) {
        final List<List<Rule>> paths = new LinkedList<>();
        for (final Rule rule : workflow.rules()) {
            final String destinationLabel = rule.getDestinationLabel();
            if (destinationLabel.equals("A")) {
                paths.add(List.of(rule));
            }
            else if (!destinationLabel.equals("R")) {
                for (final List<Rule> subpath : findPathsFrom(workflows.get(destinationLabel), workflows)) {
                    final List<Rule> path = new LinkedList<>();
                    path.add(rule);
                    path.addAll(subpath);
                    paths.add(path);
                }
            }
        }
        return paths;
    }

    private record Workflow(String label, List<Rule> rules) {
        public static Workflow fromString(final String description) {
            final String[] labelAndRules = description.split("\\{");
            final String[] rules = labelAndRules[1].substring(0, labelAndRules[1].length() - 1).split(",");
            return new Workflow(labelAndRules[0], Arrays.stream(rules).map(Rule::fromString).toList());
        }

        public String process(final Part part) {
            for (final Rule rule : rules) {
                if (rule.test(part)) {
                    return rule.getDestinationLabel();
                }
            }
            throw new IllegalStateException("At least one rule should have accepted part " + part);
        }
    }

    private record AcceptedRange(int minX, int maxX, int minM, int maxM, int minA, int maxA, int minS, int maxS) {
        public static AcceptedRange fromPath(final List<Rule> rules) {
            int minX = 1;
            int maxX = 4000;
            int minM = 1;
            int maxM = 4000;
            int minA = 1;
            int maxA = 4000;
            int minS = 1;
            int maxS = 4000;
            for (final Rule rule : rules) {
                if (rule.testsX()) {
                    if (rule.testsLessThan()) {
                        maxX = Math.min(maxX, rule.getComparisonValue() - 1);
                    }
                    else {
                        minX = Math.max(minX, rule.getComparisonValue() + 1);
                    }
                }
                if (rule.testsM()) {
                    if (rule.testsLessThan()) {
                        maxM = Math.min(maxM, rule.getComparisonValue() - 1);
                    }
                    else {
                        minM = Math.max(minM, rule.getComparisonValue() + 1);
                    }
                }
                if (rule.testsA()) {
                    if (rule.testsLessThan()) {
                        maxA = Math.min(maxA, rule.getComparisonValue() - 1);
                    }
                    else {
                        minA = Math.max(minA, rule.getComparisonValue() + 1);
                    }
                }
                if (rule.testsS()) {
                    if (rule.testsLessThan()) {
                        maxS = Math.min(maxS, rule.getComparisonValue() - 1);
                    }
                    else {
                        minS = Math.max(minS, rule.getComparisonValue() + 1);
                    }
                }
            }
            return new AcceptedRange(minX, maxX, minM, maxM, minA, maxA, minS, maxS);
        }

        public boolean isContainedIn(final AcceptedRange other) {
            return this != other
                    && inRange(this.minX(), other.minX(), other.maxX())
                    && inRange(this.maxX(), other.minX(), other.maxX())
                    && inRange(this.minM(), other.minM(), other.maxM())
                    && inRange(this.maxM(), other.minM(), other.maxM())
                    && inRange(this.minA(), other.minA(), other.maxA())
                    && inRange(this.maxA(), other.minA(), other.maxA())
                    && inRange(this.minS(), other.minS(), other.maxS())
                    && inRange(this.maxS(), other.minS(), other.maxS());
        }

        public long calculateSize() {
            return ((long) maxX() - minX() + 1) * (maxM() - minM() + 1) * (maxA() - minA() + 1) * (maxS() - minS() + 1);
        }

        public AcceptedRange determineIntersection(final AcceptedRange other) {
            if (
                    minX() > other.maxX()
                    || minM() > other.maxM()
                    || minA() > other.maxA()
                    || minS() > other.maxS()
                    || maxX() < other.minX()
                    || maxM() < other.minM()
                    || maxA() < other.minA()
                    || maxS() < other.minS()
            ) {
                return null;
            }

            final int intersectionMinX;
            if (minX() >= other.minX()) {
                intersectionMinX = minX();
            }
            else if (other.minX() <= maxX()) {
                intersectionMinX = other.minX();
            }
            else {
                throw new IllegalStateException();
            }

            final int intersectionMinM;
            if (minM() >= other.minM()) {
                intersectionMinM = minM();
            }
            else if (other.minM() <= maxM()) {
                intersectionMinM = other.minM();
            }
            else {
                throw new IllegalStateException();
            }

            final int intersectionMinA;
            if (minA() >= other.minA()) {
                intersectionMinA = minA();
            }
            else if (other.minA() <= maxA()) {
                intersectionMinA = other.minA();
            }
            else {
                throw new IllegalStateException();
            }

            final int intersectionMinS;
            if (minS() >= other.minS()) {
                intersectionMinS = minS();
            }
            else if (other.minS() <= maxS()) {
                intersectionMinS = other.minS();
            }
            else {
                throw new IllegalStateException();
            }

            final int intersectionMaxX = Math.min(maxX(), other.maxX());
            final int intersectionMaxM = Math.min(maxM(), other.maxM());
            final int intersectionMaxA = Math.min(maxA(), other.maxA());
            final int intersectionMaxS = Math.min(maxS(), other.maxS());

            return new AcceptedRange(
                intersectionMinX,
                intersectionMaxX,
                intersectionMinM,
                intersectionMaxM,
                intersectionMinA,
                intersectionMaxA,
                intersectionMinS,
                intersectionMaxS
            );
        }
        private boolean inRange(final int in, final int lowerBound, final int upperBound) {
            return lowerBound <= in && in <= upperBound;
        }
    }
}
