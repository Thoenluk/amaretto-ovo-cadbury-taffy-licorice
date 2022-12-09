package ch.thoenluk.solvers.challenge8;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

public class TreeSeer implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] forestLines = UtStrings.splitMultilineString(input);
        final int maxY = forestLines.length;
        final int maxX = forestLines[0].length();

        final Map<Position, Integer> forest = UtParsing.multilineStringToPositionIntegerMap(input);
        final Set<Position> visibleTrees = new HashSet<>();

        for (int y = 0; y < maxY; y++) {
            final List<Position> leftSightline = new LinkedList<>();
            final List<Position> rightSightline = new LinkedList<>();

            for (int x = 0; x < maxX; x++) {
                leftSightline.add(new Position(y, x));
                rightSightline.add(new Position(y, maxX - x - 1));
            }

            visibleTrees.addAll(getVisibleTreesInSightline(forest, leftSightline));
            visibleTrees.addAll(getVisibleTreesInSightline(forest, rightSightline));
        }

        for (int x = 0; x < maxX; x++) {
            final List<Position> downSightline = new LinkedList<>();
            final List<Position> upSightline = new LinkedList<>();

            for (int y = 0; y < maxY; y++) {
                downSightline.add(new Position(y, x));
                upSightline.add(new Position(maxY - y - 1, x));
            }

            visibleTrees.addAll(getVisibleTreesInSightline(forest, downSightline));
            visibleTrees.addAll(getVisibleTreesInSightline(forest, upSightline));
        }

        return Integer.toString(visibleTrees.size());
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] forestLines = UtStrings.splitMultilineString(input);
        final int maxY = forestLines.length;
        final int maxX = forestLines[0].length();

        final Map<Position, Integer> forest = UtParsing.multilineStringToPositionIntegerMap(input);

        int highestScenicScore = Integer.MIN_VALUE;

        for (Map.Entry<Position, Integer> entry : forest.entrySet()) {
            final Position position = entry.getKey();

            if (position.y() == 0 || position.y() == maxY - 1
                || position.x() == 0 || position.x() == maxX - 1) {
                continue;
            }

            final int height = entry.getValue();
            int scenicScore = 1;

            final List<Position> rightSightline = new LinkedList<>();
            for (int x = position.x() + 1; x < maxX; x++) {
                rightSightline.add(new Position(position.y(), x));
            }
            scenicScore *= getVisibleTreesFromTreehouse(forest, rightSightline, height).size();

            final List<Position> leftSightline = new LinkedList<>();
            for (int x = position.x() - 1; x >= 0; x--) {
                leftSightline.add(new Position(position.y(), x));
            }
            scenicScore *= getVisibleTreesFromTreehouse(forest, leftSightline, height).size();

            final List<Position> downSightline = new LinkedList<>();
            for (int y = position.y() + 1; y < maxY; y++) {
                downSightline.add(new Position(y, position.x()));
            }
            scenicScore *= getVisibleTreesFromTreehouse(forest, downSightline, height).size();

            final List<Position> upSightline = new LinkedList<>();
            for (int y = position.y() - 1; y >= 0; y--) {
                upSightline.add(new Position(y, position.x()));
            }
            scenicScore *= getVisibleTreesFromTreehouse(forest, upSightline, height).size();

            if (highestScenicScore < scenicScore) {
                highestScenicScore = scenicScore;
            }
        }

        return Integer.toString(highestScenicScore);
    }

    private Set<Position> getVisibleTreesInSightline(Map<Position, Integer> forest, List<Position> sightline) {
        final Set<Position> visibleTreesInSightline = new HashSet<>();

        int highestTree = -1;

        for (Position position : sightline) {
            final int tree = forest.get(position);

            if (tree > highestTree) {
                visibleTreesInSightline.add(position);

                if (tree == 9) {
                    break;
                }
                else {
                    highestTree = tree;
                }
            }
        }

        return visibleTreesInSightline;
    }

    private Set<Position> getVisibleTreesFromTreehouse(Map<Position, Integer> forest, List<Position> sightline, int treehouseHeight) {
        final Set<Position> visibleTreesInSightline = new HashSet<>();

        for (Position position : sightline) {
            final int tree = forest.get(position);
            visibleTreesInSightline.add(position);

            if (tree >= treehouseHeight) {
                break;
            }
        }

        return visibleTreesInSightline;

    }
}
