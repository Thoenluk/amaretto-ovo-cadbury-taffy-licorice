package ch.thoenluk.solvers.challenge18;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.ThreeDArea;
import ch.thoenluk.ut.ThreeDPosition;
import ch.thoenluk.ut.ThreeDPosition.NeighbourDirection;
import ch.thoenluk.ut.UtStrings;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LavaSurfacer implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] cuba = UtStrings.splitMultilineString(input);
        final Set<ThreeDPosition> droplet = new HashSet<>();

        for (String cube : cuba) {
            droplet.add(ThreeDPosition.fromString(cube));
        }

        int surface = 0;

        for (ThreeDPosition cube : droplet) {
            for (ThreeDPosition neighbour : cube.getNeighbours(NeighbourDirection.CARDINAL)) {
                if (!droplet.contains(neighbour)) {
                    surface++;
                }
            }
        }

        return Integer.toString(surface);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] cuba = UtStrings.splitMultilineString(input);
        final Set<ThreeDPosition> droplet = new HashSet<>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (String cube : cuba) {
            final ThreeDPosition position = ThreeDPosition.fromString(cube);
            droplet.add(position);

            if (minX > position.x()) {
                minX = position.x();
            }

            if (minY > position.y()) {
                minY = position.y();
            }

            if (minZ > position.z()) {
                minZ = position.z();
            }

            if (maxX < position.x()) {
                maxX = position.x();
            }

            if (maxY < position.y()) {
                maxY = position.y();
            }

            if (maxZ < position.z()) {
                maxZ = position.z();
            }
        }

        final ThreeDArea boundingBox = new ThreeDArea(
                new ThreeDPosition(minX - 1, minY - 1, minZ - 1),
                new ThreeDPosition(maxX + 1, maxY + 1, maxZ + 1)
        );

        final Set<ThreeDPosition> outside = new HashSet<>();
        final List<ThreeDPosition> cubesToExplore = new LinkedList<>();
        cubesToExplore.add(new ThreeDPosition(minX - 1, minY - 1, minZ - 1));
        outside.add(cubesToExplore.get(0));

        while (!cubesToExplore.isEmpty()) {
            final ThreeDPosition cube = cubesToExplore.remove(0);

            for (ThreeDPosition neighbour : cube.getNeighbours(NeighbourDirection.CARDINAL)) {
                if (!outside.contains(neighbour)
                    && !droplet.contains(neighbour)
                    && boundingBox.containsPosition(neighbour)) {
                    outside.add(neighbour);
                    cubesToExplore.add(neighbour);
                }
            }
        }

        int surface = 0;

        for (ThreeDPosition cube : droplet) {
            for (ThreeDPosition neighbour : cube.getNeighbours(NeighbourDirection.CARDINAL)) {
                if (outside.contains(neighbour)) {
                    surface++;
                }
            }
        }

        return Integer.toString(surface);
    }

}
