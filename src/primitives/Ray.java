package primitives;

import java.util.Objects;

/**
 * Class Ray represents a half-line in 3D space, defined by a starting point
 * and a normalized direction vector.
 *
 * @author Your Name
 */
public class Ray {
    /**
     * The starting point of the ray
     */
    private final Point _origin;
    /**
     * The direction of the ray (always normalized)
     */
    private final Vector _direction;

    /**
     * Constructor for Ray.
     * The direction vector is normalized automatically.
     *
     * @param origin    the starting point
     * @param direction the direction vector
     */
    public Ray(Point origin, Vector direction) {
        _origin = origin;
        _direction = direction.normalize();
    }

    /**
     * Getter for the origin point.
     *
     * @return the origin point
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Getter for the direction vector.
     *
     * @return the direction vector
     */
    public Vector direction() {
        return _direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ray ray = (Ray) o;
        return _origin.equals(ray._origin) && _direction.equals(ray._direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }

    @Override
    public String toString() {
        return "Ray: origin=" + _origin + ", direction=" + _direction;
    }
}