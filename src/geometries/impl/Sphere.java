package geometries.impl;

import java.util.List;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Sphere represents a sphere in 3D space.
 * * @author Naama Shafer
 *
 * @author Miri Shpigelman
 */
public final class Sphere extends RadialGeometry {
    private final Point _center;

    /**
     * Constructor for Sphere.
     *
     * @param radius The radius
     * @param center The center point
     */
    public Sphere(Point center, double radius) {
        super(radius);
        this._center = center;
    }


    @Override
    public Vector getNormal(Point p) {
        // n = normalize(P - Center)
        return p.subtract(_center).normalize();
    }

    @Override
    public String toString() {
        return "Sphere: center=" + _center + ", radius=" + _radius;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // If the ray starts at the center of the sphere
        if (p0.equals(_center)) {
            return List.of(ray.getPoint(_radius));
        }

        Vector l = _center.subtract(p0);
        double tm = v.dotProduct(l);
        double dSquared = l.lengthSquared() - tm * tm;
        double rSquared = _radius * _radius;

        // Using isZero/alignZero to check if d >= r
        if (primitives.Util.alignZero(dSquared - rSquared) >= 0) {
            return null;
        }

        double th = Math.sqrt(rSquared - dSquared);
        double t1 = primitives.Util.alignZero(tm - th);
        double t2 = primitives.Util.alignZero(tm + th);

        // Returning only points that are in front of the ray (t > 0)
        // We use getPoint as required for Refactoring
        if (t1 > 0 && t2 > 0) {
            return List.of(ray.getPoint(t1), ray.getPoint(t2));
        }
        if (t1 > 0) {
            return List.of(ray.getPoint(t1));
        }
        if (t2 > 0) {
            return List.of(ray.getPoint(t2));
        }

        return null;
    }
}