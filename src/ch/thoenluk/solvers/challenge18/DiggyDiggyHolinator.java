package ch.thoenluk.solvers.challenge18;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.ArrayList;
import java.util.List;

public class DiggyDiggyHolinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final List<Position> vertices = new ArrayList<>();
        vertices.add(new Position(0, 0));
        UtStrings.streamInputAsLines(input)
                .map(Dig::fromString)
                .forEach(dig -> vertices.add(dig.offset(vertices.get(vertices.size() - 1))));
        long enclosedSpace = calculateArea(vertices);
        return Long.toString(enclosedSpace);
    }

    private long calculateArea(List<Position> vertices) {
        long area = 0;
        for (int i = 0; i < vertices.size(); i++) {
            final int nextIndex = (i + 1) % vertices.size();
            final Position currentVertex = vertices.get(i);
            final Position nextVertex = vertices.get(nextIndex);
            area += (long) currentVertex.x() * nextVertex.y() - (long) currentVertex.y() * nextVertex.x();
        }
        area /= 2;
        area = Math.abs(area);
        area += getCircumference(vertices) / 2;
        area++;
        return area;
    }

    private int getCircumference(List<Position> vertices) {
        int circumference = 0;
        for (int i = 0; i < vertices.size() - 1; i++) {
            circumference += vertices.get(i).getDistanceFrom(vertices.get(i + 1));
        }
        return circumference;
    }

    @Override
    public String saveChristmasAgain(String input) {
        final List<Position> vertices = new ArrayList<>();
        vertices.add(new Position(0, 0));
        UtStrings.streamInputAsLines(input)
                .map(Dig::fromString)
                .forEach(dig -> vertices.add(dig.offsetWithHexcode(vertices.get(vertices.size() - 1))));
        long enclosedSpace = calculateArea(vertices);
        return Long.toString(enclosedSpace);
    }

    private record Dig(Position direction, int distance, String colour) {
        public static Dig fromString(final String line) {
            final String[] pieces = line.split(UtStrings.WHITE_SPACE_REGEX);
            final Position direction = switch (pieces[0]) {
                case "R" -> Position.RIGHT;
                case "U" -> Position.UP;
                case "D" -> Position.DOWN;
                case "L" -> Position.LEFT;
                default -> throw new IllegalArgumentException();
            };
            final int distance = UtParsing.cachedParseInt(pieces[1]);
            final String colour = pieces[2].substring(1, pieces[2].length() - 1);
            return new Dig(direction, distance, colour);
        }

        public Position offset(final Position start) {
            return calculateOffset(start, distance(), direction());
        }

        private Position calculateOffset(final Position start, final int distance, final Position direction) {
            return start.offsetByDistance(direction, distance);
        }

        public Position offsetWithHexcode(final Position start) {
            final int distance = UtParsing.cachedParseInt(colour().substring(1, 6), 16);
            final Position direction = switch (UtParsing.cachedParseInt(colour().substring(6))) {
                case 0 -> Position.RIGHT;
                case 1 -> Position.DOWN;
                case 2 -> Position.LEFT;
                case 3 -> Position.UP;
                default -> throw new IllegalArgumentException();
            };
            return calculateOffset(start, distance, direction);
        }
    }
}
