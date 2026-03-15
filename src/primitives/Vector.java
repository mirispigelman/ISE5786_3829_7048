package primitives;

/**
 * Class Vector represents a direction and magnitude in 3D space.
 * Inherits from Point.
 *
 * @author Your Name
 */
public class Vector extends Point {
    /**
     * Constant representing a unit vector on the Z axis
     */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructor to initialize Vector with three double values.
     * Throws IllegalArgumentException if it's a zero vector.
     *
     * @param x coordinate on X axis
     * @param y coordinate on Y axis
     * @param z coordinate on Z axis
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    /**
     * Constructor to initialize Vector with a Double3 object.
     * Throws IllegalArgumentException if it's a zero vector.
     *
     * @param xyz Double3 object containing coordinates
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    /**
     * Adds another vector to this vector.
     *
     * @param other the other vector
     * @return a new Vector (this + other)
     */
    public Vector add(Vector other) {
        return new Vector(_xyz.add(other._xyz));
    }

    /**
     * Scales the vector by a scalar.
     *
     * @param scalar the scalar value
     * @return a new Vector scaled by the scalar
     */
    public Vector scale(double scalar) {
        return new Vector(_xyz.scale(scalar));
    }

    /**
     * Calculates the dot product (scalar product) between two vectors.
     *
     * @param other the other vector
     * @return the dot product result (double)
     */
    public double dotProduct(Vector other) {
        return _xyz._d1() * other._xyz._d1() +
                _xyz._d2() * other._xyz._d2() +
                _xyz._d3() * other._xyz._d3();
    }

    /**
     * Calculates the cross product (vector product) between two vectors.
     *
     * @param other the other vector
     * @return a new Vector perpendicular to both
     */
    public Vector crossProduct(Vector other) {
        double ax = _xyz._d1(), ay = _xyz._d2(), az = _xyz._d3();
        double bx = other._xyz._d1(), by = other._xyz._d2(), bz = other._xyz._d3();

        return new Vector(
                ay * bz - az * by,
                az * bx - ax * bz,
                ax * by - ay * bx
        );
    }

    /**
     * Calculates the squared length of the vector.
     *
     * @return length squared
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Calculates the length of the vector.
     *
     * @return length
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector (changes length to 1 without changing direction).
     *
     * @return a new normalized Vector
     */
    public Vector normalize() {
        double len = length();
        return new Vector(_xyz.divide(len));
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "Vector: " + super.toString();
    }
}