package ch.thoenluk.solvers.challenge11.monke;

import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Monkey {

    //---- Fields

    private final List<Long> heldItems = new ArrayList<>();
    private final Operation operation;
    private final int operand;
    private final int divisor;
    private final int monkeyIfTrue;
    private final int monkeyIfFalse;

    private Integer panicMitigationFactor;
    private int itemsInspected = 0;


    //---- Constructor

    public Monkey(String description) {
        final String[] lines = UtStrings.splitMultilineString(description);

        heldItems.addAll(UtParsing.commaSeparatedStringToIntegerList(lines[1].replaceAll("[^\\d,]", "")).stream().map(Long::valueOf).toList());

        final String[] operationParts = lines[2].trim().split(UtStrings.WHITE_SPACE_REGEX);

        if (operationParts[5].equals("old")) {
            operation = Operation.SQUARE;
            operand = Integer.MIN_VALUE;
        }
        else {
            operation = Operation.fromString(operationParts[4]);
            operand = UtParsing.cachedParseInt(operationParts[5]);
        }

        divisor = UtParsing.cachedParseInt(lines[3].replaceAll("\\D", ""));
        monkeyIfTrue = UtParsing.cachedParseInt(lines[4].replaceAll("\\D", ""));
        monkeyIfFalse = UtParsing.cachedParseInt(lines[5].replaceAll("\\D", ""));
    }


    //---- Methods

    public List<Throw> takeTurn() {
        final List<Throw> tosses = new LinkedList<>(); // stupid reserved words

        while (!heldItems.isEmpty()) {
            final long item = heldItems.remove(0);
            final long newWorryLevel = inspectItem(item);
            tosses.add(new Throw(getTargetMonkey(newWorryLevel), newWorryLevel));
        }

        return tosses;
    }

    public void acquire(long item) { // stupid reserved words
        heldItems.add(item);
    }

    public int getDivisor() {
        return divisor;
    }

    public int getItemsInspected() {
        return itemsInspected;
    }

    public void setPanicMitigationFactor(Integer panicMitigationFactor) {
        this.panicMitigationFactor = panicMitigationFactor;
    }

    private long inspectItem(long item) {
        itemsInspected++;

        long worryLevel = switch(operation) {
            case ADD -> item + operand;
            case MULTIPLY -> item * operand;
            case SQUARE -> item * item;
        };

        if (panicMitigationFactor == null) {
            worryLevel /= 3;
        }
        else {
            worryLevel %= panicMitigationFactor;
        }

        return worryLevel;
    }

    private int getTargetMonkey(long worryLevel) {
        if (testItem(worryLevel)) {
            return monkeyIfTrue;
        }
        return monkeyIfFalse;
    }

    private boolean testItem(long worryLevel) {
        return worryLevel % divisor == 0;
    }


    //---- Inner classes

    public record Throw(int monkey, long item) {}

    private enum Operation {
        ADD,
        MULTIPLY,
        SQUARE;

        public static Operation fromString(String description) {
            return switch (description) {
                case "+" -> ADD;
                case "*" -> MULTIPLY;
                default -> throw new IllegalArgumentException();
            };
        }
    }
}
