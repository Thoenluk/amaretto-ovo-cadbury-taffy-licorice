package ch.thoenluk.solvers.challenge10;

import ch.thoenluk.ut.Position;

import java.util.Arrays;
import java.util.List;

public enum TileType {
    VERTICAL('|', Position.UP, Position.DOWN),
    HORIZONTAL('-', Position.LEFT, Position.RIGHT),
    NORTHEAST('L', Position.UP, Position.RIGHT),
    NORTHWEST('J', Position.UP, Position.LEFT),
    SOUTHWEST('7', Position.DOWN, Position.LEFT),
    SOUTHEAST('F', Position.DOWN, Position.RIGHT),
    GROUND('.'),
    START('S');

    private final char charRepresentation;
    private final List<Position> connections;

    TileType(final char charRepresentation, Position... connections) {
        this.charRepresentation = charRepresentation;
        this.connections = Arrays.stream(connections).toList();
    }

    public List<Position> getConnections() {
        return connections;
    }

    public static TileType fromChar(final char charRepresentation) {
        return Arrays.stream(values()).filter(tt -> tt.charRepresentation == charRepresentation).findFirst().orElseThrow();
    }
}
