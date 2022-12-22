package ch.thoenluk.solvers.challenge17;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;

import java.util.*;

public class ChristmasSaverWhoArrangesTheRocks implements ChristmasSaver {

    private final static Position LEFT = new Position(0, -1);
    private final static Position RIGHT = new Position(0, 1);
    private final static Position DOWN = new Position(-1, 0);

    @Override
    public String saveChristmas(String input) {
        final char[] jetPattern = input.trim().toCharArray(); // Finally, a challenge that doesn't begin with a call to UtParsing or UtStrings!

        final Set<Position> cavern = new HashSet<>();
        int maxY = 0;
        int jetPatternIndex = 0;

        final Rock[] rocks = Rock.values();

        for (int i = 0; i < 2022; i++) {
            final Rock rock = rocks[i % rocks.length];
            Position position = new Position(maxY + 3, 2);

            while (true) {
                final char jetDirection = jetPattern[jetPatternIndex];
                jetPatternIndex = (jetPatternIndex + 1) % jetPattern.length;

                final Position offset = jetDirection == '<' ? LEFT : RIGHT;
                final Position offsetPosition = position.offsetBy(offset);
                if (!rock.touchesAnything(offsetPosition, cavern)) {
                    position = offsetPosition;
                }

                final Position fallPosition = position.offsetBy(DOWN);

                if (rock.touchesAnything(fallPosition, cavern)) {
                    for (Position part : rock.getBody()) {
                        cavern.add(position.offsetBy(part));
                    }
                    final int highestY = position.y() + rock.getMaxY();

                    if (maxY < highestY) {
                        maxY = highestY;
                    }
                    break;
                }
                else {
                    position = fallPosition;
                }
            }
        }

        return Integer.toString(maxY);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final char[] jetPattern = input.trim().toCharArray(); // Finally, a challenge that doesn't begin with a call to UtParsing or UtStrings!

        final Set<Position> cavern = new HashSet<>();
        int maxY = 0;
        int jetPatternIndex = 0;

        final Rock[] rocks = Rock.values();

        final List<Integer> iterationsWithFloors = new ArrayList<>();
        iterationsWithFloors.add(0);

        final List<Integer> deltas = new ArrayList<>();
        deltas.add(0);

        final List<Integer> maxYs = new ArrayList<>(5000);
        maxYs.add(0);

        for (int rockIteration = 0; rockIteration < 5000; rockIteration++) {
            final Rock rock = rocks[rockIteration % rocks.length];
            Position position = new Position(maxY + 3, 2);

            while (true) {
                final char jetDirection = jetPattern[jetPatternIndex];
                jetPatternIndex = (jetPatternIndex + 1) % jetPattern.length;

                final Position offset = jetDirection == '<' ? LEFT : RIGHT;
                final Position offsetPosition = position.offsetBy(offset);
                if (!rock.touchesAnything(offsetPosition, cavern)) {
                    position = offsetPosition;
                }

                final Position fallPosition = position.offsetBy(DOWN);

                if (rock.touchesAnything(fallPosition, cavern)) {
                    for (Position part : rock.getBody()) {
                        cavern.add(position.offsetBy(part));
                    }
                    final int highestY = position.y() + rock.getMaxY();

                    if (maxY < highestY) {
                        maxY = highestY;
                    }

                    boolean hasFloor = true;

                    for (int x = 0; x < 7; x++) {
                        hasFloor &= cavern.contains(new Position(position.y(), x));
                    }

                    if (hasFloor) {
                        final int delta = rockIteration - iterationsWithFloors.get(iterationsWithFloors.size() - 1);

                        iterationsWithFloors.add(rockIteration);
                        deltas.add(delta);
                    }

                    maxYs.add(maxY);

                    break;
                }
                else {
                    position = fallPosition;
                }
            }
        }

        loopSearch:
        for (int i = 0; i < deltas.size(); i++) {
            final int firstOccurence = deltas.indexOf(deltas.get(i));

            if (firstOccurence == i) {
                continue;
            }

            final int deltasInLoop = i - firstOccurence;
            for (int k = 0; k < deltasInLoop; k++) {
                if (!Objects.equals(deltas.get(firstOccurence + k), deltas.get(i + k))) {
                    continue loopSearch;
                }
            }

            final int firstIterationOfLoop = iterationsWithFloors.get(firstOccurence);
            final int loopLength = iterationsWithFloors.get(i) - firstIterationOfLoop;

            System.out.println("Found loop starting at " + firstIterationOfLoop + " and " + loopLength + " iterations long!");

            final int maxYAtLoopStart = maxYs.get(firstIterationOfLoop);
            final int maxYAtLoopEnd = maxYs.get(firstIterationOfLoop + loopLength);
            final int maxYDelta = maxYAtLoopEnd - maxYAtLoopStart;

            final long iterationsLeftToGo = 1_000_000_000_000L - firstIterationOfLoop;
            final long loops = iterationsLeftToGo / loopLength;
            final long maxYIncreaseDuringLoops = loops * maxYDelta;
            final long maxYAfterLoops = maxYIncreaseDuringLoops + maxYAtLoopStart;

            final int iterationsLeftAfterLoops = (int) (iterationsLeftToGo % loopLength);
            final int maxYAfterFinalIterations = maxYs.get(firstIterationOfLoop + iterationsLeftAfterLoops);
            final int maxYDeltaAfterFinalIterations = maxYAfterFinalIterations - maxYAtLoopStart;

            return Long.toString(maxYAfterLoops + maxYDeltaAfterFinalIterations);
        }


        return Integer.toString(maxY);
    }

    private enum Rock {
        LINE(new Position(0, 0), new Position(0, 1), new Position(0, 2), new Position(0, 3)),
        PLUS(new Position(0, 1), new Position(1, 0), new Position(1, 1), new Position(1, 2), new Position(2, 1)),
        CORNER(new Position(0, 0), new Position(0, 1), new Position(0, 2), new Position(1, 2), new Position(2, 2)),
        POLE(new Position(0, 0), new Position(1, 0), new Position(2, 0), new Position(3, 0)),
        BLOCK(new Position(0, 0), new Position(0, 1), new Position(1, 0), new Position(1, 1));

        private final Position[] body;
        private final int maxY;

        Rock(Position... body) {
            this.body = body;

            int y = 0;
            for (Position part : body) {
                if (y < part.y()) {
                    y = part.y();
                }
            }
            maxY = y + 1;
        }

        public Position[] getBody() {
            return body;
        }

        public int getMaxY() {
            return maxY;
        }

        public boolean touchesAnything(Position positionOfOrigin, Set<Position> cavern) {
            for (Position part : body) {
                final Position partLocation = positionOfOrigin.offsetBy(part);
                if (partLocation.x() < 0
                        || partLocation.x() > 6
                        || partLocation.y() < 0
                        || cavern.contains(partLocation)) {
                    return true;
                }
            }
            return false;
        }
    }
}
