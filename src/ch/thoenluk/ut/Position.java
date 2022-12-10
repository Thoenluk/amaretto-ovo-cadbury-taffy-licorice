package ch.thoenluk.ut;

import java.util.LinkedList;
import java.util.List;

public record Position(int y, int x) {

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