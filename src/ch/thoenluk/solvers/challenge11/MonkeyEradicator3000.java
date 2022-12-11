package ch.thoenluk.solvers.challenge11;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.solvers.challenge11.monke.Monkey;
import ch.thoenluk.ut.UtStrings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MonkeyEradicator3000 implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] monkeyDescriptions = UtStrings.splitStringWithEmptyLines(input);
        final List<Monkey> monkeys = new ArrayList<>();

        for (String monkeyDescription : monkeyDescriptions) {
            monkeys.add(new Monkey(monkeyDescription));
        }

        for (int round = 0; round < 20; round++) {
            for (Monkey monkey : monkeys) {
                final List<Monkey.Throw> tosses = monkey.takeTurn();

                for (Monkey.Throw toss : tosses) {
                    monkeys.get(toss.monkey()).acquire(toss.item());
                }
            }
        }

        monkeys.sort(Comparator.comparing(Monkey::getItemsInspected).reversed());
        final int monkeyBusiness = monkeys.get(0).getItemsInspected() * monkeys.get(1).getItemsInspected();

        return Integer.toString(monkeyBusiness);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] monkeyDescriptions = UtStrings.splitStringWithEmptyLines(input);
        final List<Monkey> monkeys = new ArrayList<>();
        int panicMitigationFactor = 1;

        for (String monkeyDescription : monkeyDescriptions) {
            final Monkey monkey = new Monkey(monkeyDescription);
            monkeys.add(monkey);
            panicMitigationFactor *= monkey.getDivisor();
        }

        for (Monkey monkey : monkeys) {
            monkey.setPanicMitigationFactor(panicMitigationFactor);
        }

        for (int round = 0; round < 10000; round++) {
            for (Monkey monkey : monkeys) {
                final List<Monkey.Throw> tosses = monkey.takeTurn();

                for (Monkey.Throw toss : tosses) {
                    monkeys.get(toss.monkey()).acquire(toss.item());
                }
            }
        }

        monkeys.sort(Comparator.comparing(Monkey::getItemsInspected).reversed());
        final long monkeyBusiness = ((long) monkeys.get(0).getItemsInspected()) * monkeys.get(1).getItemsInspected();

        return Long.toString(monkeyBusiness);
    }
}
