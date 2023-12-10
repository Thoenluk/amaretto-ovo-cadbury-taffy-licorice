package ch.thoenluk.solvers.challenge10;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtStrings;

import java.util.*;
import java.util.stream.IntStream;

public class PipeinatorMazinatorinator implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final List<Tile> loop = getLoop(input);
        return Integer.toString(loop.size() / 2);
    }

    private List<Tile> getLoop(String input) {
        final Map<Position, Tile> sketch = new HashMap<>();
        final String[] lines = UtStrings.splitMultilineString(input);
        IntStream.range(0, lines.length)
                .forEach(y -> IntStream.range(0, lines[y].length())
                        .forEach(x -> {
                            final Tile tile = Tile.fromCoordinates(y, x, lines);
                            sketch.put(tile.position(), tile);
                        })); // I hate literally everything about this.
        final Tile start = sketch.values().stream()
                .filter(e -> e.type().equals(TileType.START))
                .findFirst()
                .orElseThrow();
        final List<Tile> tilesToExamine = new LinkedList<>(List.of(start.position().getCardinalNeighbours().stream()
                .filter(sketch::containsKey)
                .map(sketch::get)
                .filter(tile -> tile.connectedPositions().contains(start.position()))
                .findFirst()
                .orElseThrow()));
        final List<Tile> loop = new ArrayList<>(List.of(start));
        final Set<Tile> examinedTiles = new HashSet<>(loop);

        while (!tilesToExamine.isEmpty()) {
            final Tile tile = tilesToExamine.remove(0);
            examinedTiles.add(tile);
            loop.add(tile);
            tilesToExamine.addAll(tile.connectedPositions().stream()
                    .map(sketch::get)
                    .filter(examinableTile -> !examinedTiles.contains(examinableTile))
                    .toList());
        }
        return loop;
    }

    @Override
    public String saveChristmasAgain(String input) {
        final List<Tile> loop = getLoop(input);
        long enclosedSpace = 0;
        for (int i = 0; i < loop.size(); i++) {
            final int nextIndex = (i + 1) % loop.size();
            final Position currentVertex = loop.get(i).position();
            final Position nextVertex = loop.get(nextIndex).position();
            enclosedSpace += (long) currentVertex.x() * nextVertex.y() - (long) currentVertex.y() * nextVertex.x();
        }
        enclosedSpace /= 2;
        enclosedSpace = Math.abs(enclosedSpace);
        enclosedSpace -= loop.size() / 2;
        enclosedSpace++; // This ++ is necessary in every example, but I know not why. enclosedSpace is always even, so
        // it isn't a rounding error. The formula is simple enough, calculating the polygon's total size and subtracting
        // its circumference - by two, the reason for which I frankly don't know either, because you'd think you need
        // to take off the entire circumference and not just half of it, and yes I made sure that the loop contains
        // only distinct nodes trust me. Come to think of it, if we pretended the polygon is a square with sides a,
        // then its total area comes to a^2 and its enclosed area comes to (a - 1)^2. Factorising this as we all know
        // how to do (because it's the second binomial) brings us to a^2 - 2a + 1. And that's exactly what we do:
        // 1. Take the total area (everything including enclosedSpace = Math.abs(enclosedSpace))
        // 2. Subtract half the circumference, because the entire circumference would be 4a
        // 3. Add 1 because that's a dangling summand from the binomial.
        // I'm going to pretend that I did this math and transferred it onto arbitrary convex polygons before writing the
        // code, because to admit that I just slapped on random factors and a ++ to make the first test case pass and was
        // then very surprised to find every other test passing would greatly upset some masters of mathematics I'm afraid.
        // Also, this method returns in half the time of saveChristmas despite doing exactly the same thing and some more.
        // My guess is it has to do with the String pool and maybe the JVM pools records too, or it optimised identical
        // method calls, or it just had very conveniently sized portions of memory allocated and garbage collected immediately
        // before allocating them again in the exact same size and order.
        return Long.toString(enclosedSpace);
    }

    private record Tile(Position position, TileType type, List<Position> connectedPositions) {
        public static Tile fromCoordinates(final int y, final int x, final String[] lines) {
            final TileType type = TileType.fromChar(lines[y].charAt(x));
            final Position position = new Position(y, x);
            final List<Position> connectedTiles = type.getConnections().stream()
                    .map(position::offsetBy)
                    .toList();
            return new Tile(position, type, connectedTiles);
        }
    }
}
