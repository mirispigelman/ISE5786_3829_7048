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
    /**
     * Generates a beam of rays from this ray's origin towards a target area (Blackboard).
     * This infrastructure supports super-sampling features like Soft Shadows and Glossy Surfaces.
     * * @param blackboard the 2D sampling blackboard configuration
     * @param distance the distance from the ray's origin to the target area plane
     * @return a list of rays forming the beam
     */
    public java.util.List<Ray> generateBeam(Blackboard blackboard, double distance) {
        java.util.List<Ray> beam = new java.util.ArrayList<>();

        // 1. Get the 2D sample offsets from the blackboard configuration
        java.util.List<Point2D> samples2D = blackboard.generate2DSamples();

        // 2. If the blackboard returns only the center point (feature off), return this single ray
        if (samples2D.size() <= 1) {
            beam.add(this);
            return beam;
        }

        // 3. Calculate the central target point on the target plane
        // TargetPoint = p0 + distance * dir
        Point targetCenter = this.getPoint(distance);

        // 4. Construct an orthonormal basis (local X and Y axes) for the target plane
        // Vector vX must be orthogonal to the ray's direction (_direction)
        Vector vX;
        double x = this._direction._xyz._d1();
        double y = this._direction._xyz._d2();

        // Avoid cross product with a parallel vector by checking components
        if (Util.isZero(x) && Util.isZero(y)) {
            vX = new Vector(1, 0, 0); // Ray points along Z axis, pick X axis
        } else {
            vX = new Vector(-y, x, 0).normalize(); // General orthogonal vector in XY plane
        }

        // Vector vY is orthogonal to both _direction and vX
        Vector vY = this._direction.crossProduct(vX).normalize();
        // 5. Map each 2D sample point to a 3D target point and construct the ray
        for (Point2D sample : samples2D) {
            double sX = sample.getX();
            double sY = sample.getY();

            Point targetPoint = targetCenter;

            // Move the target point along the local X axis if the offset is not zero
            if (!Util.isZero(sX)) {
                targetPoint = targetPoint.add(vX.scale(sX));
            }
            // Move the target point along the local Y axis if the offset is not zero
            if (!Util.isZero(sY)) {
                targetPoint = targetPoint.add(vY.scale(sY));
            }

            // Create a new ray from the original source pointing towards the 3D target point
            // Direction = TargetPoint - p0
            Vector beamDir = targetPoint.subtract(this._origin);

            // Ensure we don't automatically include the central ray unless it is part of the pattern
            beam.add(new Ray(this._origin, beamDir));
        }

        return beam;
    }
}