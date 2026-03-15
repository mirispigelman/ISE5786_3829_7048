package primitives;

/**
 * Class Point represents a point in 3D space.
 *
 * @author Your Name
 */
public class Point {
    /**
     * The coordinates of the point
     */
    protected final Double3 _xyz;

    /**
     * Constant representing the origin point (0,0,0)
     */
    public static final Point ZERO = new Point(0.0, 0.0, 0.0);

    /**
     * Primary constructor for Point
     *
     * @param x coordinate on X axis
     * @param y coordinate on Y axis
     * @param z coordinate on Z axis
     */
    public Point(double x, double y, double z) {
        _xyz = new Double3(x, y, z);
    }

    /**
     * Constructor receiving Double3
     *
     * @param xyz coordinates object
     */
    public Point(Double3 xyz) {
        _xyz = xyz;
    }

    /**
     * Subtracts two points to create a vector
     *
     * @param other the point to subtract from the current point
     * @return a Vector from other to this point
     */
    public Vector subtract(Point other) {
        return new Vector(_xyz.subtract(other._xyz));
    }

    /**
     * Adds a vector to the point
     *
     * @param vector the vector to add
     * @return a new Point after displacement
     */
    public Point add(Vector vector) {
        return new Point(_xyz.add(vector._xyz));
    }

    /**
     * Calculates the squared distance between two points
     *
     * @param other the other point
     * @return squared distance
     */
    public double distanceSquared(Point other) {
        double x1 = _xyz._d1();
        double y1 = _xyz._d2();
        double z1 = _xyz._d3();

        double x2 = other._xyz._d1();
        double y2 = other._xyz._d2();
        double z2 = other._xyz._d3();

        return (x1 - x2) * (x1 - x2) +
                (y1 - y2) * (y1 - y2) +
                (z1 - z2) * (z1 - z2);
    }

    /**
     * Calculates the distance between two points
     *
     * @param other the other point
     * @return distance
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return _xyz.equals(point._xyz);
    }

    @Override
    public String toString() {
        return "Point: " + _xyz;
    }
}