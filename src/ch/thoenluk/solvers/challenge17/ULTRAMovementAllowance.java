package ch.thoenluk.solvers.challenge17;

import ch.thoenluk.ut.Position;

import java.util.stream.Stream;

class ULTRAMovementAllowance extends MovementAllowance {

    ULTRAMovementAllowance(int up, int right, int down, int left) {
        super(up, right, down, left);
        maximumMovement = 10;
    }

    @Override
    public boolean canMoveIn(Position direction) {
        if (!super.canMoveIn(direction)) {
            return false;
        }
        if (direction == Position.UP || direction == Position.DOWN) {
            return Stream.of(right(), left()).allMatch(this::hasMovedEnough);
        }
        if (direction == Position.RIGHT || direction == Position.LEFT) {
            return Stream.of(up(), down()).allMatch(this::hasMovedEnough);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public boolean canStop() {
        return Stream.of(up(), right(), down(), left()).allMatch(this::hasMovedEnough);
    }

    private boolean hasMovedEnough(Integer i) {
        return i <= 6 || i == 10;
    }

    @Override
    protected MovementAllowance createNew(int up, int right, int down, int left) {
        return new ULTRAMovementAllowance(up, right, down, left);
    }

    @Override
    public boolean isEqualOrBetterThan(MovementAllowance other) {
        return canStop() && super.isEqualOrBetterThan(other);
    }
}
