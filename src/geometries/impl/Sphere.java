package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * Class Sphere represents a sphere in 3D space.
 */
public class Sphere extends RadialGeometry {
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
    public Vector getNormal(Point point) {
        // בשלב זה מחזירים null לפי ההוראות
        return null;
    }

    @Override
    public String toString() {
        return "Sphere: center=" + _center + ", radius=" + _radius;
    }
}