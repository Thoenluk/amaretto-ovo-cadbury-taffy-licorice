package ch.thoenluk.solvers.challenge17;

import ch.thoenluk.ut.Position;

import java.util.Objects;

class MovementAllowance {
    private final int up;
    private final int right;
    private final int down;
    private final int left;
    protected int maximumMovement = 3;

    MovementAllowance(int up, int right, int down, int left) {
        this.up = up;
        this.right = right;
        this.down = down;
        this.left = left;
    }

    public MovementAllowance moveIn(final Position direction) {
        if (direction == Position.UP) {
            return createNew(up() - 1, maximumMovement, 0, maximumMovement);
        }
        if (direction == Position.RIGHT) {
            return createNew(maximumMovement, right() - 1, maximumMovement, 0);
        }
        if (direction == Position.DOWN) {
            return createNew(0, maximumMovement, down() - 1, maximumMovement);
        }
        if (direction == Position.LEFT) {
            return createNew(maximumMovement, 0, maximumMovement, left() - 1);
        }
        throw new IllegalArgumentException();
    }

    public boolean canMoveIn(final Position direction) {
        if (direction == Position.UP) {
            return up() != 0;
        }
        if (direction == Position.RIGHT) {
            return right() != 0;
        }
        if (direction == Position.DOWN) {
            return down() != 0;
        }
        if (direction == Position.LEFT) {
            return left() != 0;
        }
        throw new IllegalArgumentException();
    }

    public boolean isEqualOrBetterThan(final MovementAllowance other) {
        return up() >= other.up()
                && right() >= other.right()
                && down() >= other.down()
                && left() >= other.left();
    }

    public boolean canStop() {
        return true;
    }

    public int up() {
        return up;
    }

    public int right() {
        return right;
    }

    public int down() {
        return down;
    }

    public int left() {
        return left;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MovementAllowance) obj;
        return this.up == that.up &&
                this.right == that.right &&
                this.down == that.down &&
                this.left == that.left;
    }

    @Override
    public int hashCode() {
        return Objects.hash(up, right, down, left);
    }

    @Override
    public String toString() {
        return "MovementAllowance[" +
                "up=" + up + ", " +
                "right=" + right + ", " +
                "down=" + down + ", " +
                "left=" + left + ']';
    }

    protected MovementAllowance createNew(final int up, final int right, final int down, final int left) {
        return new MovementAllowance(up, right, down, left);
    }

}
