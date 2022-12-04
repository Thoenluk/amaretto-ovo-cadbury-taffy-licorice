package ch.thoenluk.solvers.challenge3;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtStrings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RucksackDeSacker implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] rucksacks = UtStrings.splitMultilineString(input);

        int totalPriority = 0;

        for (String rucksack : rucksacks) {
            if (rucksack.length() % 2 != 0) {
                throw new AssertionError("They got tricksy and used imbalanced rucksacks!");
            }

            final Set<Character> firstCompartment = new HashSet<>();
            final char[] rucksackArray = rucksack.toCharArray();

            for (int i = 0; i < rucksackArray.length / 2; i++) {
                firstCompartment.add(rucksackArray[i]);
            }

            for (int i = rucksackArray.length / 2; i < rucksackArray.length; i++) {
                final char itemType = rucksackArray[i];
                if (firstCompartment.contains(itemType)) {
                    totalPriority += getPriority(itemType);
                    break;
                }
            }

        }

        return Integer.toString(totalPriority);
    }

    private int getPriority(char itemType) {
        if (Character.isLowerCase(itemType)) {
            return itemType - 'a' + 1;
        }
        else {
            return itemType - 'A' + 27;
        }
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] rucksacks = UtStrings.splitMultilineString(input);

        int totalPriority = 0;

        for (int group = 0; group < rucksacks.length; group += 3) {
            final int[] occurences = new int[53];

            for (int elf = 0; elf < 3; elf++) {
                final String rucksack = rucksacks[group + elf];
                final Set<Character> itemTypes = new HashSet<>();
                for (char itemType : rucksack.toCharArray()) {
                    itemTypes.add(itemType);
                }

                for (Character itemType : itemTypes) {
                    occurences[getPriority(itemType)]++;
                }
            }

            // ewww, 1-index
            for (int priority = 1; priority < occurences.length; priority++) {
                if (occurences[priority] == 3) {
                    totalPriority += priority;
                    break;
                }
            }
        }

        return Integer.toString(totalPriority);
    }
}
