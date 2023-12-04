package ch.thoenluk.solvers.challenge3;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;
import java.util.stream.IntStream;

public class EngineAnalysinator implements ChristmasSaver {

    private final Map<Position, Character> engineSchematic = new HashMap<>();
    private final Map<Position, List<Integer>> gearRatios = new HashMap<>();

    @Override
    public String saveChristmas(String input) {
        engineSchematic.clear();
        final String[] lines = UtStrings.splitMultilineString(input);
        IntStream.range(0, lines.length)
                .mapToObj(y -> new Line(y, lines[y]))
                .forEach(this::insertIntoSchematic);
        return IntStream.range(0, lines.length)
                .mapToObj(y -> new Line(y, lines[y]))
                .map(this::mapToPartNumberSum)
                .reduce(UtMath::overflowSafeSum)
                .orElseThrow()
                .toString();
    }

    private void insertIntoSchematic(final Line line) {
        final String content = line.content();
        IntStream.range(0, content.length())
                .forEach(x -> engineSchematic.put(new Position(line.y(), x), content.charAt(x)));
    }

    private int mapToPartNumberSum(final Line line) {
        return IntStream.range(0, line.content().length())
                .mapToObj(x -> new Position(line.y(), x))
                .filter(this::isStartOfNumberInSchematic)
                .map(this::getPartNumber)
                .reduce(UtMath::overflowSafeSum)
                .orElse(0);
    }

    private boolean isStartOfNumberInSchematic(final Position position) {
        final Position leftNeighbour = position.offsetBy(0, -1);
        return isDigitInSchematic(position) && !isDigitInSchematic(leftNeighbour);
    }

    private boolean isDigitInSchematic(final Position position) {
        return engineSchematic.containsKey(position) && Character.isDigit(engineSchematic.get(position));
    }

    private int getPartNumber(final Position startingPosition) {
        final StringBuilder numberBuilder = new StringBuilder();
        Position position = startingPosition;
        boolean isNextToSymbol = false;

        while(isDigitInSchematic(position)) {
            numberBuilder.append(engineSchematic.get(position));

            if (!isNextToSymbol) {
                isNextToSymbol = hasNeighbouringSymbolInSchematic(position);
            }

            position = position.offsetBy(0, 1);
        }

        if (!isNextToSymbol) {
            return 0;
        }

        return UtParsing.cachedParseInt(numberBuilder.toString());
    }

    private boolean hasNeighbouringSymbolInSchematic(Position position) {
        return position.getOmnidirectionalNeighbours().stream()
                .map(engineSchematic::get)
                .filter(Objects::nonNull)
                .anyMatch(this::isNonDotSymbol);
    }

    private boolean isNonDotSymbol(char character) {
        return !Character.isDigit(character) && character != '.';
    }

    @Override
    public String saveChristmasAgain(String input) {
        engineSchematic.clear();
        gearRatios.clear();
        final String[] lines = UtStrings.splitMultilineString(input);
        IntStream.range(0, lines.length)
                .mapToObj(y -> new Line(y, lines[y]))
                .forEach(this::insertIntoSchematic);
        IntStream.range(0, lines.length)
                .mapToObj(y -> new Line(y, lines[y]))
                .forEach(this::insertIntoGearRatios);

        return gearRatios.values().stream()
                .filter(l -> l.size() == 2)
                .map(l -> UtMath.overflowSafeProduct(l.get(0), l.get(1)))
                .reduce(UtMath::overflowSafeSum)
                .orElseThrow()
                .toString();
    }

    private void insertIntoGearRatios(final Line line) {
        IntStream.range(0, line.content().length())
                .mapToObj(x -> new Position(line.y(), x))
                .filter(this::isStartOfNumberInSchematic)
                .forEach(this::insertIntoGearRatiosIfNumberNextToGear);
    }

    private void insertIntoGearRatiosIfNumberNextToGear(final Position startingPosition) {
        Position position = startingPosition;

        while (isDigitInSchematic(position)) {
            final Optional<Position> gearPosition = getPositionOfGearAround(position);
            if (gearPosition.isPresent()) {
                gearRatios.computeIfAbsent(gearPosition.get(), p -> new LinkedList<>())
                        .add(getPartNumber(startingPosition));
                return;
            }

            position = position.offsetBy(0, 1);
        }
    }

    private Optional<Position> getPositionOfGearAround(final Position position) {
        return position.getOmnidirectionalNeighbours().stream()
                .filter(engineSchematic::containsKey)
                .filter(p -> engineSchematic.get(p).equals('*'))
                .findAny();
    }

    private record Line(int y, String content) {}
}
