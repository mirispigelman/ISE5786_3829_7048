package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * Class Triangle represents a two-dimensional triangle in 3D space.
 * Inherits from Polygon.
 *
 * @author Your Name
 */
public class Triangle extends Polygon {

    /**
     * Constructor for Triangle receiving three points.
     * Passes the points to the Polygon constructor.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public Vector getNormal(Point point) {
        return super.getNormal(point);
    }

    @Override
    public String toString() {
        return "Triangle: " + super.toString();
    }
}