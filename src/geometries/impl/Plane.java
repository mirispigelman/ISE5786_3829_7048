package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Class Plane represents a flat plane in 3D space.
 */
public class Plane extends Geometry {
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
        this._normal = null;
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
}