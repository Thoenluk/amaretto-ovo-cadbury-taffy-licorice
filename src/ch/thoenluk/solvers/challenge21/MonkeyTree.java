package ch.thoenluk.solvers.challenge21;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.solvers.challenge21.monke.Monkey;
import ch.thoenluk.ut.UtStrings;

import java.util.LinkedList;
import java.util.List;

public class MonkeyTree implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final Monkey root = createMonkeysAndGetRoot(input);

        return Long.toString(root.calculateValue());
    }

    @Override
    public String saveChristmasAgain(String input) {
        createMonkeysAndGetRoot(input);

        final Monkey[] directUnderlings = Monkey.getDependenciesOfRoot();
        final Monkey leftHand = directUnderlings[0];
        final Monkey rightHand = directUnderlings[1];

        Monkey.setHumanValue(0L);
        int previousSignum = (int) Math.signum(leftHand.calculateValue() - rightHand.calculateValue());
        int signum;
        long humanValue = 1L;

        while (true) {
            Monkey.setHumanValue(humanValue);
            signum = (int) Math.signum(leftHand.calculateValue() - rightHand.calculateValue());

            if (signum != previousSignum) {
                break;
            }

            humanValue *= 2;
        }

        if (signum == 0) {
            return Long.toString(humanValue);
        }

        final Monkey lesser = signum == -1 ? leftHand : rightHand;
        final Monkey greater = signum == -1 ? rightHand : leftHand;

        long lowerBound = humanValue / 2;
        long upperBound = humanValue;

        while (lowerBound != upperBound) {
            final long average = (lowerBound + upperBound) / 2;
            Monkey.setHumanValue(average);

            if (lesser.calculateValue() < greater.calculateValue()) {
                upperBound = average - 1;
            }
            else {
                lowerBound = average;
            }
        }

        Monkey.setHumanValue(lowerBound - 4);

        return Long.toString(lowerBound - 4);
    }

    private Monkey createMonkeysAndGetRoot(String input) {
        Monkey root = null;

        for (String description : UtStrings.splitMultilineString(input)) {
            final Monkey monkey = Monkey.fromString(description);
            if (monkey.getName().equals("root")) {
                root = monkey;
            }
        }

        if (root == null) {
            throw new IllegalArgumentException("No monkey named root was in input!");
        }
        return root;
    }
}
