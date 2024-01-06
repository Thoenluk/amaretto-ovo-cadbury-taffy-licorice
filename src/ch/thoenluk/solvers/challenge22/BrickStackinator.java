package ch.thoenluk.solvers.challenge22;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtStrings;

import java.util.*;
import java.util.stream.Collectors;

public class BrickStackinator implements ChristmasSaver {
    @Override
    public String saveChristmas(final String input) {
        final List<Brick> bricks = getBricks(input);
        return Long.toString(calculateSafeBricksInfinityBrained(bricks));
    }

    private long calculateSafeBricksNaive(final List<Brick> bricks) {
        return bricks.size() - bricks.stream()
                .map(brick -> brick.determineSupportingBricks(bricks))
                .filter(supportingBricks -> supportingBricks.size() == 1)
                .flatMap(Set::stream)
                .distinct()
                .count();
    }

    private long calculcateSafeBricksGalaxyBrained(final List<Brick> bricks) {
        return bricks.size() - bricks.stream()
                .map(Brick::getSupportingBricks)
                .filter(supportingBricks -> supportingBricks.size() == 1)
                .flatMap(Set::stream)
                .distinct()
                .count(); // This doesn't actually run notably faster; In fact, if getBricks didn't always run
        // brick.findSupport(), it would do the same thing and run in the same time.
        // The joke is that I have preserved the solution for the second challenge.
    }

    private long calculateSafeBricksInfinityBrained(final List<Brick> bricks) {
        return bricks.stream()
                .filter(brick -> {
                    for (final Brick supportedBrick : brick.getBricksSupported()) {
                        if (supportedBrick.getSupportingBricks().size() == 1) {
                            return false;
                        }
                    }
                    return true;
                })
                .count();
        // This is also not any faster. Fixed overhead is a bastard, innit.
    }

    private List<Brick> getBricks(final String input) {
        final Map<Position, Integer> highestZs = new HashMap<>();
        final List<Brick> bricks = UtStrings.streamInputAsLines(input)
                .map(Brick::fromString)
                .sorted(Comparator.comparingInt(brick -> brick.start().z()))
                .toList();
        bricks.forEach(brick -> drop(brick, highestZs));
        bricks.forEach(brick -> brick.findSupport(bricks));
        return bricks;
    }

    private void drop(final Brick brick, final Map<Position, Integer> highestZs) {
        final List<Position> asXY = brick.asXY();
        final int restingZ = asXY.stream()
                .map(highestZs::get)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
        brick.fallToZ(restingZ);
        asXY.forEach(position -> highestZs.put(position, brick.end().z()));
    }

    @Override
    public String saveChristmasAgain(final String input) {
        final long millisAtStart = System.currentTimeMillis();
        final List<Brick> bricks = getBricks(input);
        final long millisAfterBricks = System.currentTimeMillis();
        UtStrings.println(String.format("Built bricks \"graph\" in %d ms!", millisAfterBricks - millisAtStart));
        final int disintegratedBricks = bricks.stream().map(this::disintegrateRecursivelyInfinityBrained).reduce(UtMath::overflowSafeSum).orElseThrow();
        UtStrings.println(String.format("Disintegrated bricks in %d ms!", System.currentTimeMillis() - millisAfterBricks));
        return Integer.toString(disintegratedBricks);
    }

    private int disintegrateRecursivelyNaive(final List<Brick> bricks, final Brick brickToDisintegrate) {
        final Set<Brick> bricksStillStanding = new HashSet<>(bricks);
        final List<Brick> bricksToDisintegrate = new LinkedList<>(List.of(brickToDisintegrate));
        while (!bricksToDisintegrate.isEmpty()) {
            if (bricksStillStanding.remove(bricksToDisintegrate.remove(0))) {
                bricksStillStanding.stream()
                        .filter(b -> b.isUnsupported(bricksStillStanding))
                        .forEach(bricksToDisintegrate::add);
            }
        }
        // Only 9 minutes 40 seconds on my machine. CPU go brrrrr!
        return bricks.size() - bricksStillStanding.size() - 1;
    }

    private int disintegrateRecursivelyGalaxyBrained(final List<Brick> bricks, final Brick brickToDisintegrate) {
        final Set<Brick> bricksDisintegrated = new HashSet<>();
        final Set<Brick> bricksStillStanding = new HashSet<>(bricks);
        final List<Brick> bricksToDisintegrate = new LinkedList<>(List.of(brickToDisintegrate));
        while (!bricksToDisintegrate.isEmpty()) {
            if (bricksDisintegrated.add(bricksToDisintegrate.remove(0))) {
                final Set<Brick> fallingBricks = bricksStillStanding.stream()
                        .filter(b -> b.isUnsupportedGalaxybrained(bricksDisintegrated))
                        .collect(Collectors.toSet());
                bricksStillStanding.removeAll(fallingBricks);
                bricksToDisintegrate.addAll(fallingBricks);
            }
        }
        // This returns in 6 seconds, meanwhile. If you like your power bill low.
        // I'm sure it can be brought into the double-digit millisecond range with some optimisations:
        // 1. I have an unreasonable suspicion that duplicate elements are added to bricksToDisintegrate.
        //      However, while about 5% performance seems to be gained with duplicate prevention,
        //      its own cost makes it more effort than worth.
        //      One could make bricksToDisintegrate into a Set, but iterating over it while not empty is a bother :<
        //
        // 2. The obvious: Subtree pruning.
        //      Remember that the full state includes the bricks still standing / disintegrated (either one) and the brick
        //      currently being disintegrated. From a naive approach, it doesn't appear that this ever matches (even WITH
        //      properly overriding equals to consider all members of the record.)
        //      In other words, the set of bricks disintegrated by recursively disintegrating brick A is not necessarily equal to
        //      the set of bricks disintegrated by disintegrating A and recursively disintegrating all bricks only A supports with the
        //      full tower otherwise still standing.
        //
        // 3. The so obvious and simple I would do it if I didn't have dailies waiting: Remove the unfalling bricks.
        //      Any brick that is immediately on the ground will never disintegrate unless it is itself the starting brick.
        //      It follows that the same is true for all bricks it supports, recursively.
        //      Therefore, we could reduce our search time to about a microsecond per brick if we filter out all such bricks.
        //      Alternatively, make each brick aware of all bricks it supports instead of those it is supported by.
        //      Then, instead of iterating over the remaining set each time like an idiot, just take brick A, add it
        //      to the set of disintegrated bricks, then for each brick A supports alone, repeat.
        //
        // Unfortunately, no one pays me to implement this, so it shall be an exercise to the reader.
        // But hey Education Guild, maybe next year we can all collaborate on an optimised LP solution set?

        return bricksDisintegrated.size() - 1;
    }

    private int disintegrateRecursivelyInfinityBrained(final Brick brickToDisintegrate) {
        final Set<Brick> bricksDisintegrated = new HashSet<>();
        final List<Brick> bricksToDisintegrate = new LinkedList<>(List.of(brickToDisintegrate));
        while (!bricksToDisintegrate.isEmpty()) {
            final Brick brick = bricksToDisintegrate.remove(0);
            bricksDisintegrated.add(brick);
            for (final Brick fallingBrick : brick.getBricksSupported()) {
                if (bricksDisintegrated.containsAll(fallingBrick.getSupportingBricks())) {
                    bricksToDisintegrate.add(fallingBrick);
                }
            }
        }
        // I did it anyway tee hee.
        // So I was right that this returns in a double-digit number of ms with no other optimisations: 65ms.
        // 25ms of that is building the bricks "graph" so we finish the search in 40ms, a hot 31.25 microseconds per
        // brick (on average; many bricks will be trivial and cause nothing to fall.)
        return bricksDisintegrated.size() - 1;
    }
}
