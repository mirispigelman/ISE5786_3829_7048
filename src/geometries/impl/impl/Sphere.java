package geometries.impl.impl;

import primitives.Point;
import primitives.Vector;

/**
 * Class Sphere represents a sphere in 3D space.
 * * @author Naama Shafer
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
}