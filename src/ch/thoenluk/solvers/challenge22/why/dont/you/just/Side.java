package ch.thoenluk.solvers.challenge22.why.dont.you.just;

import ch.thoenluk.solvers.challenge22.HashMapLessThanPositionCommaMonkeyGreaterThan.Direction;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtStrings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.thoenluk.solvers.challenge22.HashMapLessThanPositionCommaMonkeyGreaterThan.AIR;
import static ch.thoenluk.solvers.challenge22.HashMapLessThanPositionCommaMonkeyGreaterThan.WALL;
import static ch.thoenluk.solvers.challenge22.why.dont.you.just.Side.Edge.*;

public class Side {

    //---- Statics

    private static final int SIDE_LENGTH = 50;


    //---- Static Methods

    public static Side createDieFromDescription(String description) {
        final Side top = new Side(new Position(0, SIDE_LENGTH));
        final Side right = new Side(new Position(0, 2 * SIDE_LENGTH));
        final Side front = new Side(new Position(SIDE_LENGTH, SIDE_LENGTH));
        final Side bottom = new Side(new Position(2 * SIDE_LENGTH, SIDE_LENGTH));
        final Side left = new Side(new Position(2 * SIDE_LENGTH, 0));
        final Side back = new Side(new Position(3 * SIDE_LENGTH, 0));
        final List<Side> sides = List.of(top, right, front, bottom, left, back);

        top.setNeighbour(RIGHT, right, LEFT);
        top.setNeighbour(BOTTOM, front, TOP);
        top.setNeighbour(LEFT, left, LEFT);
        top.setNeighbour(TOP, back, LEFT);

        right.setNeighbour(RIGHT, bottom, RIGHT);
        right.setNeighbour(BOTTOM, front, RIGHT);
        right.setNeighbour(TOP, back, BOTTOM);

        front.setNeighbour(BOTTOM, bottom, TOP);
        front.setNeighbour(LEFT, left, TOP);

        bottom.setNeighbour(LEFT, left, RIGHT);
        bottom.setNeighbour(BOTTOM, back, RIGHT);

        left.setNeighbour(BOTTOM, back, TOP);

        final String[] lines = UtStrings.splitMultilineString(description);

        for (final Side side : sides) {
            final Position topLeft = side.topLeft;

            for (int y = 0; y < SIDE_LENGTH; y++) {
                final String line = lines[y + topLeft.y()];

                for (int x = 0; x < SIDE_LENGTH; x++) {
                    final char terrain = line.charAt(x + topLeft.x());

                    if (terrain == AIR || terrain == WALL) {
                        side.setTerrainAt(new Position(y, x), terrain);
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }

        return top;
    }


    //---- Fields

    private final Map<Position, Character> board = new HashMap<>();
    private final Map<Edge, NeighbourEdge> neighbours = new HashMap<>();
    private final Position topLeft;


    //---- Constructor

    private Side(Position topLeft) {
        this.topLeft = topLeft;
    }


    //---- Methods

    public MovementResult moveInDirection(Position location, Direction facing) {
        final Position nextLocation = location.offsetBy(facing.getOffset());

        if (isOnBoard(nextLocation)) {
            if (getTerrainAt(nextLocation) == AIR) {
                return new MovementResult(nextLocation, this, facing);
            }
            else {
                return new MovementResult(location, this, facing);
            }
        }
        else {
            final Edge crossedEdge = Edge.getCrossedEdge(nextLocation);
            final NeighbourEdge neighbourEdge = neighbours.get(crossedEdge);
            final Edge enteringEdge = neighbourEdge.enteringEdge();
            final Side neighbour = neighbourEdge.neighbour();

            final Position locationOnNeighbour = enteringEdge.getStartingPositionFromCrossedEdge(crossedEdge, location);

            if (neighbour.getTerrainAt(locationOnNeighbour) == WALL) {
                return new MovementResult(location, this, facing);
            }

            final Direction newFacing = enteringEdge.getFacingWhenEntering();
            return new MovementResult(locationOnNeighbour, neighbour, newFacing);
        }
    }

    public char getTerrainAt(Position position) {
        return board.get(position);
    }

    public void setTerrainAt(Position position, char terrain) {
        board.put(position, terrain);
    }

    public void setNeighbour(Edge edge, Side neighbour, Edge contactingEdgeOfNeighbour) {
        if (neighbours.containsKey(edge)) {
            return;
        }

        neighbours.put(edge, new NeighbourEdge(neighbour, contactingEdgeOfNeighbour));
        neighbour.setNeighbour(contactingEdgeOfNeighbour, this, edge);
    }

    public Position getTopLeft() {
        return topLeft;
    }

    private boolean isOnBoard(Position position) {
        return position.y() >= 0
                && position.y() < SIDE_LENGTH
                && position.x() >= 0
                && position.x() < SIDE_LENGTH;
    }


    //---- Inner classes

    enum Edge {
        TOP(Direction.DOWN),
        RIGHT(Direction.LEFT),
        BOTTOM(Direction.UP),
        LEFT(Direction.RIGHT);

        private final Direction facingWhenEntering;

        Edge(Direction facingWhenEntering) {
            this.facingWhenEntering = facingWhenEntering;
        }

        public static Edge getCrossedEdge(Position position) {
            if (position.y() == -1) {
                return TOP;
            }
            else if (position.y() == SIDE_LENGTH) {
                return BOTTOM;
            }
            else if (position.x() == -1) {
                return LEFT;
            }
            else if (position.x() == SIDE_LENGTH) {
                return RIGHT;
            }
            throw new IllegalArgumentException();
        }

        public Direction getFacingWhenEntering() {
            return facingWhenEntering;
        }

        public Position getStartingPositionFromCrossedEdge(Edge crossedEdge, Position positionOnOriginalBoard) {
            return switch (this) {
                case TOP -> switch (crossedEdge) {
                    case TOP -> new Position(0, SIDE_LENGTH - positionOnOriginalBoard.x() - 1);
                    case RIGHT -> new Position(0, SIDE_LENGTH - positionOnOriginalBoard.y() - 1);
                    case BOTTOM -> new Position(0, positionOnOriginalBoard.x());
                    case LEFT -> new Position(0, positionOnOriginalBoard.y());
                };
                case RIGHT -> switch (crossedEdge) {
                    case TOP -> new Position(SIDE_LENGTH - positionOnOriginalBoard.x() - 1, SIDE_LENGTH - 1);
                    case RIGHT -> new Position(SIDE_LENGTH - positionOnOriginalBoard.y() - 1, SIDE_LENGTH - 1);
                    case BOTTOM -> new Position(positionOnOriginalBoard.x(), SIDE_LENGTH - 1);
                    case LEFT -> new Position( positionOnOriginalBoard.y(), SIDE_LENGTH - 1);
                };
                case BOTTOM -> switch (crossedEdge) {
                    case TOP -> new Position(SIDE_LENGTH - 1, positionOnOriginalBoard.x());
                    case RIGHT -> new Position(SIDE_LENGTH - 1, positionOnOriginalBoard.y());
                    case BOTTOM -> new Position(SIDE_LENGTH - 1, SIDE_LENGTH - positionOnOriginalBoard.x() - 1);
                    case LEFT -> new Position(SIDE_LENGTH - 1, SIDE_LENGTH - positionOnOriginalBoard.y() - 1);
                };
                case LEFT -> switch (crossedEdge) {
                    case TOP -> new Position(positionOnOriginalBoard.x(), 0);
                    case RIGHT -> new Position(positionOnOriginalBoard.y(), 0);
                    case BOTTOM -> new Position(SIDE_LENGTH - positionOnOriginalBoard.x() - 1, 0);
                    case LEFT -> new Position(SIDE_LENGTH - positionOnOriginalBoard.y() - 1, 0);
                };
            };
        }
    }

    private record NeighbourEdge(Side neighbour, Edge enteringEdge) {}

    public record MovementResult(Position location, Side side, Direction facing) {}
}
