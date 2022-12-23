package ch.thoenluk.solvers.challenge21.monke;

import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class Monkey {

    //---- Statics

    protected static final Map<String, Monkey> monkeys = new HashMap<>();


    //---- Static Methods

    public static Monkey fromString(String description) {
        final String[] words = description.split(UtStrings.WHITE_SPACE_REGEX);

        final Monkey monkey;

        if (words.length == 2) {
            monkey = new LiteralMonkey(toName(words[0]), UtParsing.cachedParseInt(words[1]));
        }
        else {
            final BiFunction<Long, Long, Long> operation = switch (words[2]) {
                case "+" -> Long::sum;
                case "-" -> (a, b) -> a - b;
                case "*" -> (a, b) -> a * b;
                case "/" -> (a, b) -> a / b;
                default -> throw new IllegalArgumentException("Uncovered operation " + words[2]);
            };
            monkey = new CalculatingMonkey(toName(words[0]), words[1], words[3], operation);
        }

        monkeys.put(monkey.getName(), monkey);

        return monkey;
    }

    public static void setHumanValue(long value) {
        ((LiteralMonkey) monkeys.get("humn")).setLiteral(value);
    }

    public static Monkey[] getDependenciesOfRoot() {
        final Monkey root = monkeys.get("root");

        if (root instanceof final CalculatingMonkey cRoot) {
            return new Monkey[]{monkeys.get(cRoot.firstDependencyName), monkeys.get(cRoot.secondDependencyName)};
        }

        throw new IllegalStateException("Root is not a CalculatingMonkey somehow!");
    }

    private static String toName(String nameWithColon) {
        return nameWithColon.substring(0, nameWithColon.length() - 1);
    }


    //---- Fields

    private final String name;


    //---- Constructor

    protected Monkey(String name) {
        this.name = name;
    }


    //---- Methods

    public String getName() {
        return name;
    }

    public abstract long calculateValue();


    //---- Implementing inner classes

    private static class LiteralMonkey extends Monkey {

        private long literal;

        protected LiteralMonkey(String name, long literal) {
            super(name);
            this.literal = literal;
        }

        @Override
        public long calculateValue() {
            return literal;
        }

        public void setLiteral(long literal) {
            this.literal = literal;
        }
    }

    private static class CalculatingMonkey extends Monkey {

        private final String firstDependencyName;
        private final String secondDependencyName;
        private final BiFunction<Long, Long, Long> operation;

        protected CalculatingMonkey(String name, String firstDependencyName, String secondDependencyName, BiFunction<Long, Long, Long> operation) {
            super(name);
            this.firstDependencyName = firstDependencyName;
            this.secondDependencyName = secondDependencyName;
            this.operation = operation;
        }


        @Override
        public long calculateValue() {
            return operation.apply(monkeys.get(firstDependencyName).calculateValue(), monkeys.get(secondDependencyName).calculateValue());
        }
    }
}
