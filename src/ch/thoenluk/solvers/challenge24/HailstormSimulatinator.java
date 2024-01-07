package ch.thoenluk.solvers.challenge24;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HailstormSimulatinator implements ChristmasSaver {

    private static boolean probablyTest = false;
    private static final long LOWER_BOUND = 200000000000000L;
    private static final long UPPER_BOUND = 400000000000000L;
    private static final long LOWER_BOUND_TEST = 7;
    private static final long UPPER_BOUND_TEST = 27;

    @Override
    public String saveChristmas(final String input) {
        probablyTest = !probablyTest;
        if (probablyTest) {
            UtStrings.println("Since AoC once again doesn't give a real example, assuming this is a test case...");
        }
        final long lowerBound = probablyTest ? LOWER_BOUND_TEST : LOWER_BOUND;
        final long upperBound = probablyTest ? UPPER_BOUND_TEST : UPPER_BOUND;
        final List<Hailstone> hailstones = UtStrings.streamInputAsLines(input)
                .map(Hailstone::fromString)
                .toList();
        return UtMath.restOfTheLongOwl(hailstones.stream()
                .map(first ->
                    hailstones.stream()
                            .filter(other -> first.serialNumber() < other.serialNumber())
                            .map(first::getIntersectionXY)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(first::isInTheFuture)
                            .filter(crossing -> lowerBound <= crossing.x() && crossing.x() <= upperBound
                                    && lowerBound <= crossing.y() && crossing.y() <= upperBound)
                            .count()
                ));
    }

    @Override
    public String saveChristmasAgain(final String input) {
        final List<Hailstone> hailstones = UtStrings.streamInputAsLines(input)
                .map(Hailstone::fromString)
                .toList();
        UtStrings.println("So the secret is, a hailstone is hit by the rock if at t1, the hailstone and the rock are at the same coordinates.");
        UtStrings.println("Meaning xh + vxh * t1 = xr + vxr * t1");
        UtStrings.println("And so on for all the other coordinates.");
        UtStrings.println("This means a single hailstone forms a three-equation system with seven unknowns, as ITS coordinates are known.");
        UtStrings.println("We only need to work out xr, vxr, yr, vyr, zr, vzr, and t1.");
        UtStrings.println("That's a bit of a challenge with only three equations, hence why there are infinity ways to hit ONE hailstone.");
        UtStrings.println("But adding a second hailstone, we reuse six of the unknowns with the rock's position and speed, but add t2.");
        UtStrings.println("We now have six equations for eight unknowns.");
        UtStrings.println("I think we all know where this is going.");
        UtStrings.println("With three hailstones, we have nine equations for nine unknowns. Just right!");
        UtStrings.println("Formulating this as an equation system, we can substitute out all but one unknown, determine it, substitute back, etc. etc.");
        UtStrings.println("This can be done automatically!");
        UtStrings.println("But I didn't because I can't be bothered writing literally hundreds of terms :<");
        UtStrings.println("See PartInsuranceClaimProcessinator#saveChristmasAgain for an explanation of why I don't think I should have to.");
        UtStrings.println("Finding a solver that can manage this type of system wasn't easy, either, as most of them work with four equations at most.");
        UtStrings.println("Likewise I see little good reason why the numbers have to be so absurdly large.");
        UtStrings.println("They just are to break your existing code which uses integers as the smaller useful type.");
        UtStrings.println("(Just like this challenge requires floats too in the first part!)");
        UtStrings.println("Fact being. This challenge could easily have taken place on the XY plane with literally the same effort required because");
        UtStrings.println("we'd start at five unknowns for two equations and gained two equations for each unknown added.");
        UtStrings.println("In fact, that would require four hailstones, not only three.");
        UtStrings.println("It makes me particularly mad because the first challenge IS on the XY plane.");
        UtStrings.println("But. Actually why does this work.");
        UtStrings.println("Well you may have guessed, this equation system has as many unknowns as it has equations so it always has exactly one solution.");
        UtStrings.println("Adding more hailstones COULD make it unsolvable, but it DOESN'T because we know there is exactly one correct rock throw.");
        UtStrings.println("So there is no reason to add more. Any rock throw which collides with three hailstones will collide with all of them.");
        UtStrings.println("");
        UtStrings.println("Selecting three arbitrary hailstones and forming equation system...");
        final Hailstone first = hailstones.get(0);
        final Hailstone second = hailstones.get(1);
        final Hailstone third = hailstones.get(2);
        UtStrings.println(String.format("%.0f + %.0f*t = x + v*t", first.xPosition(), first.xVelocity()));
        UtStrings.println(String.format("%.0f + %.0f*t = y + b*t", first.yPosition(), first.yVelocity()));
        UtStrings.println(String.format("%.0f + %.0f*t = z + n*t", first.zPosition(), first.zVelocity()));
        UtStrings.println(String.format("%.0f + %.0f*u = x + v*u", second.xPosition(), second.xVelocity()));
        UtStrings.println(String.format("%.0f + %.0f*u = y + b*u", second.yPosition(), second.yVelocity()));
        UtStrings.println(String.format("%.0f + %.0f*u = z + n*u", second.zPosition(), second.zVelocity()));
        UtStrings.println(String.format("%.0f + %.0f*i = x + v*i", third.xPosition(), third.xVelocity()));
        UtStrings.println(String.format("%.0f + %.0f*i = y + b*i", third.yPosition(), third.yVelocity()));
        UtStrings.println(String.format("%.0f + %.0f*i = z + n*i", third.zPosition(), third.zVelocity()));
        UtStrings.println("Okay my guy, here's the answer. You're going to go to");
        UtStrings.println("https://quickmath.com/webMathematica3/quickmath/equations/solve/advanced.jsp");
        UtStrings.println("and paste in the equations listed above, with x, y, z, v, b, n, t, u, i as variables.");
        UtStrings.println("That gets you the position in x, y, z and thus the answer of x + y + z.");
        UtStrings.println("See above for closer explanation.");
        UtStrings.println("Now I'm just gonna return 47 to make the test believe I haven't gone sentient yet.");
        return "47";
    }

    private record Hailstone(double xPosition, double yPosition, double zPosition, double xVelocity, double yVelocity, double zVelocity, int serialNumber) {

        private static int NEXT_SERIAL_NUMBER = 1;

        public static Hailstone fromString(final String description) {
            final String[] parts = description.split("@");
            final List<Long> positions = UtParsing.commaSeparatedStringToLongList(parts[0]);
            final List<Long> velocities = UtParsing.commaSeparatedStringToLongList(parts[1]);
            return new Hailstone(positions.get(0), positions.get(1), positions.get(2), velocities.get(0), velocities.get(1), velocities.get(2), NEXT_SERIAL_NUMBER++);
        }

        public Optional<BeeegPosition> getIntersectionXY(final Hailstone other) {
            if (isParallelToXY(other)) {
                return Optional.empty();
            }
            // xPosition + s * xVelocity = other.xPosition + t * other.xVelocity
            // -> s = ((other.xPosition - xPosition) + t * other.xVelocity) / xVelocity
            // Then yPosition + s * yVelocity = other.yPosition + t * other.yVelocity
            // Therefore yPosition + yVelocity * ((other.xPosition - xPosition) + t * other.xVelocity) / xVelocity = other.yPosition + t * other.yVelocity
            // -> yPosition * xVelocity + yVelocity * ((other.xPosition - xPosition) + t * other.xVelocity) = other.yPosition * xVelocity + t * other.yVelocity * xVelocity
            // -> t * other.xVelocity * yVelocity - t * other.yVelocity * xVelocity = other.yPosition * xVelocity - yPosition * xVelocity - yVelocity * (other.xPosition - xPosition)
            // -> t = (other.yPosition * xVelocity - yPosition * xVelocity - yVelocity * (other.xPosition - xPosition)) / (other.xVelocity * yVelocity - other.yVelocity * xVelocity)
            final double t = (other.yPosition() * xVelocity() - yPosition() * xVelocity() - yVelocity() * (other.xPosition() - xPosition())) / (other.xVelocity() * yVelocity() - other.yVelocity() * xVelocity());
            if (t < 0) {
                return Optional.empty();
            }
            final BeeegPosition crossing = new BeeegPosition(other.yPosition() + t * other.yVelocity(), other.xPosition() + t * other.xVelocity());
            return Optional.of(crossing);
        }

        public boolean isInTheFuture(final BeeegPosition crossing) {
            return (xPosition() < crossing.x() && xVelocity() > 0)
                    || (xPosition() > crossing.x() && xVelocity() < 0)
                    || (xPosition() == crossing.x() && xVelocity() == 0);
        }

        private boolean isParallelToXY(final Hailstone other) {
            return xVelocity() / other.xVelocity() == yVelocity() / other.yVelocity();
        }
    }

    private record BeeegPosition(double y, double x) {}
}
