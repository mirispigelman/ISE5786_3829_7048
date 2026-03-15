package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a finite cylinder in 3D space.
 */
public class Cylinder extends Tube {
    private final double _height;

    /**
     * Constructor for Cylinder.
     *
     * @param radius The radius
     * @param axis   The axis ray
     * @param height The height
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this._height = height;
    }

    @Override
    public Vector getNormal(Point point) {
        return null;
    }

    @Override
    public String toString() {
        return "Cylinder: height=" + _height + ", " + super.toString();
    }
}