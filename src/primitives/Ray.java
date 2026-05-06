package primitives;

import java.util.List;
import java.util.Objects;
import static primitives.Util.*;

/**
 * Class Ray represents a half-line in 3D space, defined by a starting point
 * and a normalized direction vector.
 *
 * @author Naama Shafer
 * @author Miri Shpigelman
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

    /**
     * Calculates a point on the ray at a distance t from the head
     *
     * @param t distance from the ray head
     * @return the point P = _origin + t*v
     */
    public Point getPoint(double t) {
        try {
            // Trying to calculate: head + t * direction
            return _origin.add(_direction.scale(t));
        } catch (Exception e) {
            // If t is zero or so small that scale(t) results in a zero vector exception,
            // we return the head of the ray as the "safest" point.
            return _origin;
        }
    }
    /**
     * Finds the closest point to the ray's head from a list of points.
     * @param points list of points to check
     * @return the closest point, or null if the list is empty
     */
    public Point findClosestPoint(List<Point> points) {
        if (points == null || points.isEmpty()) //
            return null;

        Point closestPoint = null;
        double minDistanceSq = Double.POSITIVE_INFINITY; //

        for (Point p : points) {
            // Calculate squared distance to avoid expensive square root operation[cite: 3]
            double distanceSq = p.distanceSquared(_origin);
            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                closestPoint = p;
            }
        }
        return closestPoint;
    }
}