package geometries.impl;

import geometries.api.Geometry;
import java.util.List;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.*;

/**
 * Class Plane represents a flat plane in 3D space.
 * * @author Naama Shafer
 *
 * @author Miri Shpigelman
 */
public final class Plane extends Geometry {
    private final Point _point;
    private final Vector _normal;

    /**
     * Constructor from 3 points on the plane.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        this._point = p1;

        // 1. Create two vectors on the plane starting from the same point
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        // 2. Their cross product results in a vector orthogonal to the plane (the normal)
        // Note: If the points are collinear, crossProduct will automatically throw an
        // exception because the result is a zero vector.
        Vector n = v1.crossProduct(v2);

        // 3. Normalize the normal vector (to ensure its length is 1) and store it
        this._normal = n.normalize();
    }

    /**
     * Constructor from a point and a normal vector.
     *
     * @param point  a point on the plane
     * @param normal the normal vector (will be normalized)
     */
    public Plane(Point point, Vector normal) {
        this._point = point;
        this._normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    @Override
    public String toString() {
        return "Plane: point=" + _point + ", normal=" + _normal;
    }


    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Vector n = _normal;

        // denominator: n * v
        double nv = n.dotProduct(v);

        // ray is parallel to the plane (nv == 0) - no intersections
        if (isZero(nv)) {
            return null;
        }

        // ray origin is on the plane
        if (_point.equals(p0)) {
            return null;
        }

        // numerator: n * (Q0 - P0)
        Vector p0Q0 = _point.subtract(p0);
        double nP0Q0 = n.dotProduct(p0Q0);

        // t = (n * (Q0 - P0)) / (n * v)
        double t = alignZero(nP0Q0 / nv);

        // intersection is behind the ray origin or at the origin
        if (t <= 0) {
            return null;
        }

        // return the intersection wrapped in an Intersection object with this plane
        return List.of(new Intersection(this, ray.getPoint(t)));
    }
}