package ch.thoenluk.solvers.challenge1;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CalorieCounter implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] elves = UtStrings.splitStringWithEmptyLines(input);

        int mostCalories = Integer.MIN_VALUE;

        for (String elf : elves) {
            final List<Integer> meals = UtParsing.multilineStringToIntegerList(elf);
            final int calories = meals.stream().reduce(0, Integer::sum);

            if (calories > mostCalories) {
                mostCalories = calories;
            }
        }

        return Integer.toString(mostCalories);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] elves = UtStrings.splitStringWithEmptyLines(input);

        List<Integer> calorieCounts = new ArrayList<>();

        for (String elf : elves) {
            final List<Integer> meals = UtParsing.multilineStringToIntegerList(elf);
            final int calories = meals.stream().reduce(0, Integer::sum);
            calorieCounts.add(calories);
        }

        calorieCounts.sort(Comparator.reverseOrder());

        return Integer.toString(calorieCounts.get(0) + calorieCounts.get(1) + calorieCounts.get(2));
    }
}
