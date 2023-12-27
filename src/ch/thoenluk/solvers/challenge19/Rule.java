package ch.thoenluk.solvers.challenge19;

import ch.thoenluk.ut.UtParsing;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Rule implements Predicate<Part> {

    //---- Static fields

    private static final Function<Part, Integer> GET_X = Part::x;
    private static final Function<Part, Integer> GET_M = Part::m;
    private static final Function<Part, Integer> GET_A = Part::a;
    private static final Function<Part, Integer> GET_S = Part::s;
    private static final BiFunction<Integer, Integer, Boolean> LESS_THAN = Rule::lessThan;
    private static final BiFunction<Integer, Integer, Boolean> GREATER_THAN = Rule::greaterThan;


    //---- Static methods

    public static Rule fromString(final String description) {
        if (!description.matches(".[<>].*")) {
            return acceptAll(description);
        }
        final Function<Part, Integer> getter = switch (description.charAt(0)) {
            case 'x' -> GET_X;
            case 'm' -> GET_M;
            case 'a' -> GET_A;
            case 's' -> GET_S;
            default -> throw new IllegalArgumentException(); // You are now aware that the fields spell XMAS.
        };
        final BiFunction<Integer, Integer, Boolean> matcher = description.charAt(1) == '<' ? LESS_THAN : GREATER_THAN;
        final String[] comparisonValueAndDestinationLabel = description.substring(2).split(":");
        return new Rule(getter, matcher, UtParsing.cachedParseInt(comparisonValueAndDestinationLabel[0]), comparisonValueAndDestinationLabel[1]);
    }

    private static Rule acceptAll(final String destinationLabel) {
        return new Rule(p -> 0, Rule::matchAll, 0, destinationLabel);
    }

    private static boolean matchAll(final int field, final int compValue) {
        return true;
    }

    private static boolean lessThan(final int field, final int compValue) {
        return field < compValue;
    }

    private static boolean greaterThan(final int field, final int compValue) {
        return field > compValue;
    }


    //---- Fields

    private final Function<Part, Integer> getter;
    private final BiFunction<Integer, Integer, Boolean> matcher;
    private final int comparisonValue;
    private final String destinationLabel;


    //---- Constructor

    private Rule(final Function<Part, Integer> getter, final BiFunction<Integer, Integer, Boolean> matcher, final int comparisonValue, final String destinationLabel) {
        this.getter = getter;
        this.matcher = matcher;
        this.comparisonValue = comparisonValue;
        this.destinationLabel = destinationLabel;
    }


    //---- Methods

    @Override
    public boolean test(final Part part) {
        return matcher.apply(getter.apply(part), comparisonValue);
    }

    public String getDestinationLabel() {
        return destinationLabel;
    }

    public int getComparisonValue() {
        return comparisonValue;
    }

    public boolean testsX() {
        return getter == GET_X;
    }

    public boolean testsM() {
        return getter == GET_M;
    }

    public boolean testsA() {
        return getter == GET_A;
    }

    public boolean testsS() {
        return getter == GET_S;
    }

    public boolean testsLessThan() {
        return matcher == LESS_THAN;
    }
}
