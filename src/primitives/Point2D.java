package primitives;

/**
 * Immutable value object representing a 2D point or offset in space.
 * Used for target area sampling (Blackboard).
 */
public final class Point2D {
    private final double x;
    private final double y;

    /**
     * Constructor initializing the 2D coordinates.
     * @param x coordinate on horizontal axis
     * @param y coordinate on vertical axis
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** @return the x coordinate */
    public double getX() {
        return x;
    }

    /** @return the y coordinate */
    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Point2D other) {
            return Util.isZero(this.x - other.x) && Util.isZero(this.y - other.y);
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}