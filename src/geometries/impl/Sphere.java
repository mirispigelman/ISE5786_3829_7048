package geometries.impl;

import geometries.api.BoundingBox;
import java.util.List;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;

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
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // If the ray starts at the center of the sphere
        if (p0.equals(_center)) {

            return List.of(new Intersection(this, ray.getPoint(_radius)));
        }

        Vector l = _center.subtract(p0);
        double tm = v.dotProduct(l);
        double dSquared = l.lengthSquared() - tm * tm;
        double rSquared = _radius * _radius;

        // Using alignZero to check if d >= r
        if (alignZero(dSquared - rSquared) >= 0) {
            return null;
        }

        double th = Math.sqrt(rSquared - dSquared);
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);


        if (t1 > 0 && t2 > 0) {
            return List.of(
                    new Intersection(this, ray.getPoint(t1)),
                    new Intersection(this, ray.getPoint(t2))
            );
        }

        if (t1 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t1)));
        }

        if (t2 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t2)));
        }

        return null;
    }
    @Override
    public void getOrCreateBox() {
        // If the bounding box is already calculated, do not recalculate
        if (this.box != null) return;

        // Extract center coordinates using dot product with axis vectors
        primitives.Vector centerVec = _center.equals(primitives.Point.ZERO) ? null : _center.subtract(primitives.Point.ZERO);
        double cx = centerVec == null ? 0 : centerVec.dotProduct(primitives.Vector.AXIS_X);
        double cy = centerVec == null ? 0 : centerVec.dotProduct(primitives.Vector.AXIS_Y);
        double cz = centerVec == null ? 0 : centerVec.dotProduct(primitives.Vector.AXIS_Z);

        // Calculate box boundaries: center minus radius and center plus radius for each axis
        double minX = cx - _radius;
        double maxX = cx + _radius;
        double minY = cy - _radius;
        double maxY = cy + _radius;
        double minZ = cz - _radius;
        double maxZ = cz + _radius;

        // Create the bounding box and save it in the protected field inherited from Intersectable
        this.box = new BoundingBox(minX, maxX, minY, maxY, minZ, maxZ);
    }
}