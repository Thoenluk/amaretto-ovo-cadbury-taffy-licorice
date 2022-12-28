package ch.thoenluk.ut;

import java.util.LinkedList;
import java.util.List;

public record Position(int y, int x) {

    //---- Static Methods

    public static Position fromString(String description) {
        final String[] coordinates = UtStrings.splitCommaSeparatedString(description);
        return new Position(UtParsing.cachedParseInt(coordinates[1]), UtParsing.cachedParseInt(coordinates[0]));
    }


    //---- Methods

    public int getDistanceFrom(Position other) {
        return Math.abs(x() - other.x())
                + Math.abs(y() - other.y());
    }

    public int getRoleplayingDistanceFrom(Position other) {
        return Math.max(
                Math.abs(x() - other.x()),
                Math.abs(y() - other.y())
        );
    }

    public List<Position> getNeighbours(NeighbourDirection neighbourDirection) {
        List<Position> neighbours = new LinkedList<>();
        for (Position direction : neighbourDirection.getDirections()) {
            neighbours.add(offsetBy(direction));
        }
        return neighbours;
    }

    public Position offsetBy(Position offset) {
        return new Position(this.y + offset.y(), this.x + offset.x());
    }

    public List<Position> offsetBy(List<Position> offsets) {
        final List<Position> offsetPositions = new LinkedList<>();
        for (Position offset : offsets) {
            offsetPositions.add(offsetBy(offset));
        }
        return offsetPositions;
    }

    public List<Position> getPathTo(Position other) {
        final List<Position> path = new LinkedList<>();
        final Position offset = new Position(other.y() - y(), other.x() - x());

        if (
                !(
                    offset.y() == 0
                    || offset.x() == 0
                    || Math.abs(offset.y()) == Math.abs(offset.x())
                )
        ) {
            throw new IllegalArgumentException(String.format("Requested path between %s and %s would require sub-Position precision!", this, other));
        }

        final Position direction = new Position(
                (int) Math.signum(offset.y()),
                (int) Math.signum(offset.x())
        );

        for (int i = 0; i <= getRoleplayingDistanceFrom(other); i++) {
            path.add(this.offsetBy(new Position(direction.y() * i, direction.x() * i)));
        }

        return path;
    }


    //---- Inner enum

    public enum NeighbourDirection {
        CARDINAL(List.of(new Position(-1, 0), new Position(0, -1), new Position(0, 1), new Position(1, 0))),
        DIAGONAL(List.of(new Position(-1, -1), new Position(-1, 1), new Position(1, -1), new Position(1, 1))),
        OMNIDIRECTIONAL(List.of(new Position(-1, -1), new Position(-1, 0), new Position(-1, 1),
                                new Position(0, -1),                            new Position(0, 1),
                                new Position(1, -1), new Position(1, 0), new Position(1, 1)
        )),
        OMNIDIRECTIONAL_AND_SELF(List.of(new Position(-1, -1), new Position(-1, 0), new Position(-1, 1),
                                        new Position(0, -1), new Position(0, 0), new Position(0, 1),
                                        new Position(1, -1), new Position(1, 0), new Position(1, 1)
        ));

        private final List<Position> directions;

        /* private */ NeighbourDirection(List<Position> directions) {
            this.directions = directions;
        }

        public List<Position> getDirections() {
            return directions;
        }
    }
}