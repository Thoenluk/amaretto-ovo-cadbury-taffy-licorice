package ch.thoenluk.solvers.challenge22;

import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.ThreeDPosition;

import java.util.*;
import java.util.stream.Collectors;

public class Brick {
    private ThreeDPosition start;
    private ThreeDPosition end;
    private final Set<Brick> supportingBricks;
    private final Set<Brick> bricksSupported;

    private Brick(final ThreeDPosition start, final ThreeDPosition end) {
        this.start = start;
        this.end = end;
        supportingBricks = new HashSet<>();
        bricksSupported = new HashSet<>();
    }

    public static Brick fromString(final String description) {
        final String[] pieces = description.split("~");
        return new Brick(ThreeDPosition.fromString(pieces[0]), ThreeDPosition.fromString(pieces[1]));
    }

    public List<Position> asXY() {
        final List<Position> positions = new LinkedList<>();
        for (int x = start().x(); x <= end().x(); x++) {
            for (int y = start().y(); y <= end().y(); y++) {
                positions.add(new Position(y, x));
            }
        }
        return positions;
    }

    public ThreeDPosition start() {
        return start;
    }

    public void setStart(final ThreeDPosition start) {
        this.start = start;
    }

    public ThreeDPosition end() {
        return end;
    }

    public void setEnd(final ThreeDPosition end) {
        this.end = end;
    }

    public Set<Brick> getSupportingBricks() {
        return supportingBricks;
    }

    public Set<Brick> getBricksSupported() {
        return bricksSupported;
    }

    public void fallToZ(final int restingZ) {
        final int fallingDistance = start().z() - restingZ;
        setStart(start().subtract(new ThreeDPosition(0, 0, fallingDistance)));
        setEnd(end().subtract(new ThreeDPosition(0, 0, fallingDistance)));
    }

    public boolean isUnsupported(final Collection<Brick> bricks) {
        return start().z() > 1 && determineSupportingBricks(bricks).isEmpty();
    }

    public boolean isUnsupportedGalaxybrained(final Collection<Brick> bricksDisintegrated) {
        return start().z() > 1 && bricksDisintegrated.containsAll(getSupportingBricks());
    }

    public void findSupport(final Collection<Brick> bricks) {
        supportingBricks.addAll(determineSupportingBricks(bricks));
        supportingBricks.forEach(brick -> brick.addSupportedBrick(this));
    }

    private void addSupportedBrick(final Brick brick) {
        bricksSupported.add(brick);
    }

    public Set<Brick> determineSupportingBricks(final Collection<Brick> bricks) {
        return bricks.stream()
                .filter(this::isSupportedBy)
                .collect(Collectors.toSet());
    }

    private boolean isSupportedBy(final Brick other) {
        return other.end().z() == start().z() - 1 && intersectsX(other) && intersectsY(other);
    }

    private boolean intersectsX(final Brick other) {
        return inRange(start().x(), other.start().x(), other.end().x())
                || inRange(other.start().x(), start().x(), end().x());
    }

    private boolean intersectsY(final Brick other) {
        return inRange(start().y(), other.start().y(), other.end().y())
                || inRange(other.start().y(), start().y(), end().y());
    }

    private boolean inRange(final int value, final int lowerBound, final int upperBound) {
        return lowerBound <= value && value <= upperBound;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        final var that = (Brick) obj;
        return Objects.equals(this.start, that.start) &&
                Objects.equals(this.end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "Brick[" +
                "start=" + start + ", " +
                "end=" + end + ']';
    }

}
