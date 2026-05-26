package primitives;

import java.util.List;
import java.util.Objects;

import geometries.api.Intersectable.Intersection;

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

    private static final double DELTA = 0.1;
    /**
     * Constructor for Ray.
     * The direction vector is normalized automatically.
     *
     * @param origin    the starting point
     * @param direction the direction vector
     */

    /**
     * Constructor for Ray that automatically shifts the ray head along the normal
     * to avoid self-shading and self-intersection bugs.
     * @param point original intersection point
     * @param direction direction vector of the new ray
     * @param normal normal vector of the geometry at the intersection point
     */
    public Ray(Point point, Vector direction, Vector normal) {
        // Calculate the dot product between the normal and the ray direction
        double nv = normal.dotProduct(direction);

        // Shift the point along the normal by DELTA or -DELTA based on the sign of nv
        Vector deltaVector = normal.scale(nv > 0 ? DELTA : -DELTA);
        this._origin = point.add(deltaVector);
        this._direction = direction.normalize();
    }
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
    /**
     * Find the closest point to the ray origin
     * @param points list of points
     * @return the closest point
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null ? null
                : findClosestIntersection(
                points.stream()
                        .map(point -> new Intersection(null, point)) // Convert points to intersections [cite: 71, 378]
                        .toList()
        ).point;
    }
    /**
     * Find the closest intersection point to the ray origin
     * @param intersections list of intersections
     * @return the closest intersection point
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }

        Intersection closest = null;
        double minDistanceSq = Double.POSITIVE_INFINITY;

        for (Intersection intersection : intersections) {
            // We use distanceSquared to save the expensive square root calculation
            double distSq = _origin.distanceSquared(intersection.point);
            if (distSq < minDistanceSq) {
                minDistanceSq = distSq;
                closest = intersection;
            }
        }
        return closest;
    }
}