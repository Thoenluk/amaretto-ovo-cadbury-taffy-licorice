package ch.thoenluk.ut;

import java.util.function.UnaryOperator;

public record ThreeDPosition(int x, int y, int z) {

    public int getDistanceFrom(ThreeDPosition other) {
        return Math.abs(x() - other.x())
                + Math.abs(y() - other.y())
                + Math.abs(z() - other.z());
    }

    public ThreeDPosition add(ThreeDPosition other) {
        return new ThreeDPosition(
                this.x() + other.x(),
                this.y() + other.y(),
                this.z() + other.z());
    }

    public ThreeDPosition subtract(ThreeDPosition other) {
        return new ThreeDPosition(
                this.x() - other.x(),
                this.y() - other.y(),
                this.z() - other.z()
        );
    }

    public ThreeDPosition negate() {
        return new ThreeDPosition(
                -1 * this.x(),
                -1 * this.y(),
                -1 * this.z());
    }

    public boolean hasDistinctMagnitudeCoordinates() {
        final int absoluteX = Math.abs(x);
        final int absoluteY = Math.abs(y);
        final int absoluteZ = Math.abs(z);

        return absoluteX != absoluteY
                && absoluteX != absoluteZ
                && absoluteY != absoluteZ;
    }

    public UnaryOperator<ThreeDPosition> getRearrangerToMatch(ThreeDPosition other) {
        final int absoluteX = Math.abs(x);
        final int absoluteY = Math.abs(y);
        final int absoluteZ = Math.abs(z);
        final int otherAbsoluteX = Math.abs(other.x());
        final int otherAbsoluteY = Math.abs(other.y());
        final int otherAbsoluteZ = Math.abs(other.z());

        if (absoluteX == otherAbsoluteX) {
            if (absoluteY == otherAbsoluteY) {
                // (x, y, z)
                return position -> position;
            } else {
                // (x, z, y)
                return position -> new ThreeDPosition(position.x(), position.z(), position.y());
            }
        }
        else if (absoluteX == otherAbsoluteY) {
            if (absoluteZ == otherAbsoluteZ) {
                // (y, x, z)
                return position -> new ThreeDPosition(position.y(), position.x(), position.z());
            }
            else {
                // (z, x, y)
                return position -> new ThreeDPosition(position.z(), position.x(), position.y());
            }
        }
        else if (absoluteX == otherAbsoluteZ){
            if (absoluteY == otherAbsoluteY) {
                // (z, y, x)
                return position -> new ThreeDPosition(position.z(), position.y(), position.x());
            }
            else {
                // (y, z, x)
                return position -> new ThreeDPosition(position.y(), position.z(), position.x());
            }
        }
        else {
            throw new IllegalStateException("Vectors are not permutations of each other!");
        }
    }

    public UnaryOperator<ThreeDPosition> getSignerToMatch(ThreeDPosition other) {
        if (this.x() == other.x()) {
            if (this.y() == other.y()) {
                if (this.z() == other.z()) {
                    // all match
                    return position -> position;
                }
                else {
                    // Invert z
                    return position -> new ThreeDPosition(position.x(), position.y(), -1 * position.z());
                }
            }
            else {
                if (this.z() == other.z()) {
                    // Invert y
                    return position -> new ThreeDPosition(position.x(), -1 * position.y(), position.z());
                }
                else {
                    // Invert y and z
                    return position -> new ThreeDPosition(position.x(), -1 * position.y(), -1 * position.z());
                }
            }
        }
        else {
            if (this.y() == other.y()) {
                if (this.z() == other.z()) {
                    // Invert x
                    return position -> new ThreeDPosition(-1 * position.x(), position.y(), position.z());
                }
                else {
                    // Invert x and z
                    return position -> new ThreeDPosition(-1 * position.x(), position.y(), -1 * position.z());
                }
            }
            else {
                if (this.z() == other.z()) {
                    // Invert x and y
                    return position -> new ThreeDPosition(-1 * position.x(), -1 * position.y(), position.z());
                }
                else {
                    // Invert all
                    return position -> new ThreeDPosition(-1 * position.x(), -1 * position.y(), -1 * position.z());
                }
            }
        }
    }
}
