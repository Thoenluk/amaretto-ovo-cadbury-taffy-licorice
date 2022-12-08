package ch.thoenluk.solvers.challenge5;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

public class StackCrater implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] inputParts = UtStrings.splitStringWithEmptyLines(input);
        final String startingCratesDescription = inputParts[0];
        final String rearrangementinstructionsDescription = inputParts[1];

        final Map<Integer, Stack<Character>> crates = createStartingCrates(startingCratesDescription);
        final List<RearrangementInstruction> rearrangementInstructions = RearrangementInstruction.fromDescription(rearrangementinstructionsDescription);

        for (RearrangementInstruction instruction : rearrangementInstructions) {
            final Stack<Character> from = crates.get(instruction.from);
            final Stack<Character> to = crates.get(instruction.to);

            for (int i = 0; i < instruction.amount; i++) {
                to.push(from.pop());
            }
        }

        return extractMessage(crates);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] inputParts = UtStrings.splitStringWithEmptyLines(input);
        final String startingCratesDescription = inputParts[0];
        final String rearrangementinstructionsDescription = inputParts[1];

        final Map<Integer, Stack<Character>> crates = createStartingCrates(startingCratesDescription);
        final List<RearrangementInstruction> rearrangementInstructions = RearrangementInstruction.fromDescription(rearrangementinstructionsDescription);
        final Stack<Character> reverser = new Stack<>();

        for (RearrangementInstruction instruction : rearrangementInstructions) {
            final Stack<Character> from = crates.get(instruction.from);
            final Stack<Character> to = crates.get(instruction.to);

            for (int i = 0; i < instruction.amount; i++) {
                // For what is normal order if not reverse order reversed.
                reverser.push(from.pop());
            }

            for (int i = 0; i < instruction.amount; i++) {
                // Why are you booing me? I'm right!
                to.push(reverser.pop());
            }
        }

        return extractMessage(crates);
    }

    private Map<Integer, Stack<Character>> createStartingCrates(String startingCratesDescription) {
        final Map<Integer, Stack<Character>> startingCrates = new HashMap<>();
        final String[] lines = UtStrings.splitMultilineString(startingCratesDescription);

        final String stackIndexDescription = lines[lines.length - 1];
        final String[] stackIndeces = stackIndexDescription.trim().split("\\D+");

        for (String stackIndex : stackIndeces) {
            startingCrates.put(UtParsing.cachedParseInt(stackIndex), new Stack<>());
        }

        for (int i = lines.length - 2; i >= 0; i--) {
            final String line = lines[i];
            for (int stackIndex = 1; (stackIndex - 1) * 4 + 1 < line.length(); stackIndex++) {
                final char crate = line.charAt((stackIndex - 1) * 4 + 1);
                if (crate != ' ') {
                    startingCrates.get(stackIndex).push(crate);
                }
            }
        }

        return startingCrates;
    }

    private String extractMessage(Map<Integer, Stack<Character>> crates) {
        final List<Map.Entry<Integer, Stack<Character>>> stacks = new ArrayList<>(crates.entrySet());
        stacks.sort(Map.Entry.comparingByKey());

        final StringBuilder message = new StringBuilder();
        for (Map.Entry<Integer, Stack<Character>> stack : stacks) {
            message.append(stack.getValue().peek());
        }

        return message.toString();
    }

    private record RearrangementInstruction(int amount, int from, int to){
        public static List<RearrangementInstruction> fromDescription(String description) {
            final List<RearrangementInstruction> rearrangementInstructions = new LinkedList<>();
            final String[] lines = UtStrings.splitMultilineString(description);

            for (String line : lines) {
                final String[] points = line.split("\\D+");
                rearrangementInstructions.add(
                    new RearrangementInstruction(
                        UtParsing.cachedParseInt(points[1]),
                        UtParsing.cachedParseInt(points[2]),
                        UtParsing.cachedParseInt(points[3])
                    )
                );
            }

            return rearrangementInstructions;
        }
    }
}
